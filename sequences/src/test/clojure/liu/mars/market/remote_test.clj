(ns liu.mars.market.remote-test
  (:require [clojure.test :refer :all])
  (:require [liu.mars.market.config :refer [conf]])
  (:import (akka.testkit.javadsl TestKit)
           (akka.actor ActorSystem)
           (liu.mars.market.messages ListSequences CreateSequence NextValue DropSequence)
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
    (testing "tests about a sequence lifetime"
      (.tell remote (ListSequences.) self)
      (await)
      (.expectMsgPF test-kit "message should't include test"
                    (reify Function
                      (apply [this msg]
                        (let [data (vec msg)]
                          (is (not-any? #(= (:name %) seq-name) data))))))
      (.tell remote
             (doto (CreateSequence.)
               (.setName seq-name))
             self)
      (await)
      (.expectMsgClass test-kit Integer)
      (.tell remote (ListSequences.) self)
      (await)
      (.expectMsgPF test-kit "message should include test"
                    (reify Function
                      (apply [this msg]
                        (let [data (vec msg)]
                          (is (some #(= (:name %) seq-name) data))))))
      (dotimes [n 100]
        (.tell remote (doto (NextValue.) (.setName "test")) self)
        (await)
        (.expectMsgPF test-kit "should get next value more than times"
                      (reify Function
                        (apply [this msg]
                          (is (= (inc n) msg))))))
      (.tell remote (doto (DropSequence.) (.setName seq-name)) self)
      (await)
      (.expectMsgClass test-kit Integer)
      (.tell remote (ListSequences.) self)
      (await)
      (.expectMsgPF test-kit "message should't include test"
                    (reify Function
                      (apply [this msg]
                        (let [data (vec msg)]
                          (is (not-any? #(= (:name %) seq-name) data))))))
      (TestKit/shutdownActorSystem system))))