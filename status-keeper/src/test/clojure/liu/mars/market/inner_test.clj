(ns liu.mars.market.inner-test
  (:require [clojure.test :refer :all])
  (:require [liu.mars.market.mock-actor :as mock])
  (:require [liu.mars.actor :refer [! ??]])
  (:require [clojure.java.jdbc :as j]
            [liu.mars.market.config :as config])
  (:import (akka.actor ActorSystem)
           (liu.mars ClojureActor)
           (liu.mars.market StatusApp)
           (akka.testkit.javadsl TestKit)
           (java.util.function Supplier)
           (liu.mars.market.directive StatusQuery LoadStatus)
           (liu.mars.market.status DashStatus)))

(deftest basic-test
  (let [system (ActorSystem/create "test")
        mock-actor (.actorOf system (ClojureActor/propsWithInit mock/init mock/match-mock) "btcusdt")
        status-actor (.actorOf system (StatusApp/props) "status")
        query (doto (StatusQuery.)
                (.setSymbol "btcusdt"))]
    (try
      (j/delete! @config/db :status ["id > ?" 1])
      (is (= 1 (-> @config/db
                   (j/query ["select count(*) as c from status"])
                   first
                   :c)))
      (! mock-actor query status-actor)
      (Thread/sleep 1000)
      (is (= 2 (-> @config/db
                   (j/query ["select count(*) as c from status"])
                   first
                   :c)))
      (finally
        (TestKit/shutdownActorSystem system)))))

(deftest local-test
  (let [system (ActorSystem/create "test")
        mock-actor (.actorOf system (ClojureActor/propsWithInit mock/init mock/match-mock) "btcusdt")
        status-actor (.actorOf system (StatusApp/props) "status")
        query (doto (StatusQuery.)
                (.setSymbol "btcusdt"))]
    (try
      (j/delete! @config/db :status ["id > ?" 1])
      (is (= 1 (-> @config/db
                   (j/query ["select count(*) as c from status"])
                   first
                   :c)))
      (! mock-actor query status-actor)
      (Thread/sleep 1000)
      (is (= 2 (-> @config/db
                   (j/query ["select count(*) as c from status"])
                   first
                   :c)))
      (testing "test in system inner"
        (let [load (doto (LoadStatus.)
                     (.setSymbol "btcusdt"))
              status (?? status-actor load)]
          (is (instance? DashStatus status))
          (is (= (.getLatestOrderId status)
                 (-> @config/db
                     (j/query ["select max((content ->> 'latest-order-id')::bigint) as lid from status"])
                     first
                     :lid)))))
      (testing "run test from select"
        (let [status-selection (.actorSelection system "akka://test/user/status")
              load (doto (LoadStatus.)
                     (.setSymbol "btcusdt"))
              status (?? status-selection load)]
          (is (instance? DashStatus status))
          (is (= (.getLatestOrderId status)
                 (-> @config/db
                     (j/query ["select max((content ->> 'latest-order-id')::bigint) as lid from status"])
                     first
                     :lid)))))
      (testing "run test from select"
        (let [status-selection (.actorSelection system "akka.tcp://test@127.0.0.1:2554/user/status")
              load (doto (LoadStatus.)
                     (.setSymbol "btcusdt"))
              status (?? status-selection load)]
          (is (instance? DashStatus status))
          (is (= (.getLatestOrderId status)
                 (-> @config/db
                     (j/query ["select max((content ->> 'latest-order-id')::bigint) as lid from status"])
                     first
                     :lid)))))
      (finally
        (TestKit/shutdownActorSystem system)))))