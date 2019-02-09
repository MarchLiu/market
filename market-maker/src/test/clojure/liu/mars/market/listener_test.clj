(ns liu.mars.market.listener-test
  (:require [clojure.test :refer :all]
            [jaskell.handle :refer [supplier function]])
  (:import (akka.actor ActorSystem ActorPaths)
           (liu.mars.market DepthListenerActor)
           (akka.testkit.javadsl TestKit)
           (liu.mars.market.dash Depth)
           (akka.cluster.client ClusterClientReceptionist ClusterClientSettings ClusterClient ClusterClient$Send ClusterClient$SendToAll ClusterClient$Publish)
           (akka.cluster.pubsub DistributedPubSub DistributedPubSubMediator$Publish DistributedPubSubMediator DistributedPubSubMediator$Subscribe DistributedPubSubMediator$SubscribeAck)
           (akka.cluster Cluster)
           (java.util.function Function)))

(deftest direct-feedback-test
  "startup a inner actor and link to cluster, publish message and get feedback"
  (let [channel "btcusdt.depth.step0"
        system (ActorSystem/create "market")
        actor (.actorOf system (DepthListenerActor/props) "maker")
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
    (-> (DistributedPubSubMediator$Subscribe. channel self)
        (#(.tell mediator % self)))
    (await)
    (.expectMsgPF test-kit "subscribe ack"
                  (function [_ msg]
                    (is (instance? DistributedPubSubMediator$SubscribeAck msg))))
    (-> mediator
        (.tell (DistributedPubSubMediator$Publish. channel, empty-depth) self))
    (await)
    (.expectMsgPF test-kit "expect empty depth"
                  (function [_ msg]
                    (is (and (instance? Depth msg)
                             (= empty-depth msg)))))
    (.leave cluster (.selfAddress cluster))
    (TestKit/shutdownActorSystem system)))

;(defmulti service (fn [_ msg] (class msg)))
;(defmethod service Depth)

(deftest client-feedback-test
  "startup a inner actor and link to cluster, publish message and get feedback"
  (let [channel "btcusdt.depth.step0"
        system (ActorSystem/create "market")
        actor (.actorOf system (DepthListenerActor/props) "maker")
        test-kit (TestKit. system)
        self (.getRef test-kit)
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
                     #{(ActorPaths/fromString "akka.tcp://market@192.168.50.22:25580/system/receptionist")
                       (ActorPaths/fromString "akka.tcp://market@192.168.50.22:25581/system/receptionist")})
                   (ClusterClient/props)
                   (#(.actorOf system %)))
        mediator (-> system
                     (DistributedPubSub/get)
                     (.mediator))]
    (-> (DistributedPubSubMediator$Subscribe. channel self)
        (#(.tell mediator % self)))
    (await)
    (.expectMsgPF test-kit "subscribe ack"
                  (function [_ msg]
                    (is (instance? DistributedPubSubMediator$SubscribeAck msg))))
    (doto (ClusterClientReceptionist/get system)
      (.registerService actor)
      (.registerService self))
    (.tell client (ClusterClient$Publish. channel empty-depth) self)
    (await)
    (.expectMsgPF test-kit "expect empty depth from client"
                  (function [_ msg]
                    (is (and (instance? Depth msg)
                             (= empty-depth msg)))))

    (.leave cluster (.selfAddress cluster))
    (TestKit/shutdownActorSystem system)))