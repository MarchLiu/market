(ns liu.mars.market.remote-test
  (:require [clojure.test :refer :all])
  (:require [liu.mars.market.config :refer [conf]])
  (:import (akka.testkit.javadsl TestKit)
           (akka.actor ActorSystem)
           (liu.mars.market.messages ListSequences)
           (java.util.function Supplier Function)))

(def ^String url (-> @conf
                     :remote
                     :sequences))

(testing "tests through akka tcp provider"
  (let [system (ActorSystem/create "test")
        test-kit (TestKit. system)
        self (.getRef test-kit)
        await #(.awaitCond test-kit (reify Supplier (get [this] (.msgAvailable test-kit))))
        remote (.actorSelection system url)
        seq-name "test"]
    (testing "init stat should't include test sequence"
      (.tell remote (ListSequences.) self)
      (await)
      (.expectMsgPF test-kit "message should't include test"
                    (reify Function
                      (apply [this msg]
                        (is (vec msg))
                        (println (str msg)))))
      (TestKit/shutdownActorSystem system))))