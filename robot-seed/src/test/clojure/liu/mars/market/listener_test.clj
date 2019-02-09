(ns liu.mars.market.listener-test
  (:require [clojure.test :refer :all]
            [jaskell.handle :refer [supplier function]])
  (:import (akka.actor ActorSystem ActorPaths)
           (akka.testkit.javadsl TestKit)
           (liu.mars.market.dash Depth)
           (akka.cluster.client ClusterClientReceptionist ClusterClientSettings ClusterClient ClusterClient$Send ClusterClient$SendToAll ClusterClient$Publish)
           (akka.cluster.pubsub DistributedPubSub DistributedPubSubMediator$Publish DistributedPubSubMediator DistributedPubSubMediator$Subscribe DistributedPubSubMediator$SubscribeAck DistributedPubSubMediator$Unsubscribe)
           (akka.cluster Cluster)
           (java.util.function Function)
           (liu.mars ClojureActor)))

(defn initer [actor]
  (send (.getState actor)
        #(assoc % :feedback (-> self
                                (.path)
                                (.name)))))

(deftest direct-feedback-test
  "startup a inner actor and link to cluster, publish message and get feedback"
  (let [channel "btcusdt.depth.step0"
        system (ActorSystem/create "market")
        test-kit (TestKit. system)
        self (.getRef test-kit)
        empty-depth (doto (Depth.)
                      (.setChannel "btcusdt.depth.step0")
                      (.setVersion 0)
                      (.setAsk [])
                      (.setBid []))
        await #(.awaitCond test-kit (supplier [_] (.msgAvailable test-kit)))
        cluster (Cluster/get system)
        mediator (-> system
                     (DistributedPubSub/get)
                     (.mediator))]
    (try
      (-> (DistributedPubSubMediator$Subscribe. channel self)
          (#(.tell mediator % self)))
      (await)
      (.expectMsgPF test-kit "subscribe ack"
                    (function [msg]
                      (is (instance? DistributedPubSubMediator$SubscribeAck msg))))
      (-> mediator
          (.tell (DistributedPubSubMediator$Publish. channel, empty-depth) self))
      (await)
      (.expectMsgPF test-kit "expect empty depth"
                    (function [msg]
                      (is (and (instance? Depth msg)
                               (= empty-depth msg)))))
      (-> (DistributedPubSubMediator$Unsubscribe. channel self)
          (#(.tell mediator % self)))
      (await)
      (finally
        (do
          (.leave cluster (.selfAddress cluster))
          (TestKit/shutdownActorSystem system)
          (Thread/sleep 1000))))))

(defmulti service (fn [_ msg] (class msg)))
(defmethod service Depth [this msg]
  (-> this
      (.context)
      (.system)
      (.actorSelection (str "/system/" (:feedback @(.getState this))))
      (.tell msg (.self this))))

(deftest client-feedback-test
  "startup a inner actor and link to cluster, post messages from client and get feedback"
  (let [channel "btcusdt.depth.step0"
        system (ActorSystem/create "market")
        test-kit (TestKit. system)
        self (.getRef test-kit)
        port (-> system
                 (.provider)
                 (.getDefaultAddress)
                 (.port)
                 (.get))
        empty-depth (doto (Depth.)
                      (.setChannel "btcusdt.depth.step0")
                      (.setVersion 0)
                      (.setAsk [])
                      (.setBid []))
        await #(.awaitCond test-kit (supplier [_] (.msgAvailable test-kit)))
        cluster (Cluster/get system)
        client (-> system
                   (ClusterClientSettings/create)
                   (.withInitialContacts
                     #{(ActorPaths/fromString (format "akka.tcp://market@192.168.50.83:%d/system/receptionist" port))})
                   (ClusterClient/props)
                   (#(.actorOf system % "client")))
        service-actor (.actorOf system (ClojureActor/propsWithInit
                                         initer
                                         service) "service")]
    (try
      (doto (ClusterClientReceptionist/get system)
        (.registerSubscriber channel self)
        (.registerService service-actor))
      (Thread/sleep 100)
      (.tell client (ClusterClient$Publish. channel empty-depth) self)
      (await)
      (.expectMsgPF test-kit "expect empty depth from client publish"
                    (function [msg]
                      (is (and (instance? Depth msg)
                               (= empty-depth msg)))))
      (.tell client (ClusterClient$Send. "/user/service" empty-depth) self)
      (await)
      (.expectMsgPF test-kit "expect empty depth from client send"
                    (function [msg]
                      (is (and (instance? Depth msg)
                               (= empty-depth msg)))))
      (finally
        (do
          (doto (ClusterClientReceptionist/get system)
            (.unregisterService service-actor)
            (.unregisterSubscriber channel self))
          (.leave cluster (.selfAddress cluster))
          (TestKit/shutdownActorSystem system))))))


(deftest cross-feedback-test
  "startup a inner actor and link to cluster, publish message and get feedback"
  (let [channel "btcusdt.depth.step0"
        system (ActorSystem/create "market")
        test-kit (TestKit. system)
        self (.getRef test-kit)
        port (-> system
                 (.provider)
                 (.getDefaultAddress)
                 (.port)
                 (.get))
        empty-depth (doto (Depth.)
                      (.setChannel "btcusdt.depth.step0")
                      (.setVersion 0)
                      (.setAsk [])
                      (.setBid []))
        await #(.awaitCond test-kit (supplier [_] (.msgAvailable test-kit)))
        cluster (Cluster/get system)
        client (-> system
                   (ClusterClientSettings/create)
                   (.withInitialContacts
                     #{(ActorPaths/fromString (str "akka.tcp://market@192.168.50.83:" port "/system/receptionist"))})
                   (ClusterClient/props)
                   (#(.actorOf system % "client")))
        service-actor (.actorOf system (ClojureActor/propsWithInit
                                         initer
                                         service) "service")
        mediator (-> system
                     (DistributedPubSub/get)
                     (.mediator))]
    (try
      (-> (DistributedPubSubMediator$Subscribe. channel self)
          (#(.tell mediator % self)))
      (await)
      (.expectMsgPF test-kit "subscribe ack"
                    (function [msg]
                      (is (instance? DistributedPubSubMediator$SubscribeAck msg))))
      (doto (ClusterClientReceptionist/get system)
        (.registerService service-actor))
      (.tell client (ClusterClient$Publish. channel empty-depth) self)
      (await)
      (.expectMsgPF test-kit "expect empty depth from client publish"
                    (function [msg]
                      (is (and (instance? Depth msg)
                               (= empty-depth msg)))))
      (.tell client (ClusterClient$Send. "/user/service" empty-depth) self)
      (await)
      (.expectMsgPF test-kit "expect empty depth from client send"
                    (function [msg]
                      (is (and (instance? Depth msg)
                               (= empty-depth msg)))))
      (-> (DistributedPubSubMediator$Unsubscribe. channel self)
          (#(.tell mediator % self)))
      (await)
      (finally
        (do
          (doto (ClusterClientReceptionist/get system)
            (.unregisterService service-actor)
            (.unregisterSubscriber channel self))
          (.leave cluster (.selfAddress cluster))
          (TestKit/shutdownActorSystem system))))))