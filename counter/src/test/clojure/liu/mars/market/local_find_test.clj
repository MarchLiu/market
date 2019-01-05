(ns liu.mars.market.local-find-test
  (:require [clojure.test :refer :all])
  (:require [clojure.java.jdbc :as j])
  (:require [liu.mars.market.test-data :as data])
  (:require [liu.mars.market.config :as cfg])
  (:require [liu.mars.market.order :as o])
  (:import (akka.actor ActorSystem)
           (akka.testkit.javadsl TestKit)
           (liu.mars.market PeekActor)
           (liu.mars.market.messages FindOrder LimitAsk LimitBid MarketAsk MarketBid NextOrder Cancel)
           (java.util.function Supplier Function)))

(testing "tests for find action by actor in local system"
  (j/delete! @cfg/db :order_flow ["id < ?" 26])
  (let [system (ActorSystem/create "test")
        test-kit (TestKit. system)
        await #(.awaitCond test-kit (reify Supplier (get [this] (.msgAvailable test-kit))))
        self (.getRef test-kit)
        actor (.actorOf system (PeekActor/props))]
    (doseq [item (:limit-ask data/note-paper)]
      (let [data (assoc item :completed 0 :category "limit-ask")
            message (doto (FindOrder.)
                      (.setId (:id item)))]
        (o/save data)
        (.tell actor message self)
        (await)
        (.expectMsgPF test-kit "should get limit ask order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? LimitAsk msg))
                          (is (= (:id item) (.getId msg)))
                          (is (= 0 (:completed data) (.getCompleted msg)))
                          (is (= (:quantity data) (.getQuantity msg)))
                          (is (= (:account-id data) (.getAccountId msg)))
                          (is (= (:symbol data)) (.getSymbol msg))
                          (is (= (:price data) (.getPrice msg))))))))
    (doseq [item (:limit-bid data/note-paper)]
      (let [data (assoc item :completed 0 :category "limit-bid")
            message (doto (FindOrder.)
                      (.setId (:id item)))]
        (o/save data)
        (.tell actor message self)
        (await)
        (.expectMsgPF test-kit "should get limit bid order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? LimitBid msg))
                          (is (= (:id item) (.getId msg)))
                          (is (= 0 (:completed data) (.getCompleted msg)))
                          (is (= (:quantity data) (.getQuantity msg)))
                          (is (= (:symbol data)) (.getSymbol msg))
                          (is (= (:account-id data) (.getAccountId msg)))
                          (is (= (:price data) (.getPrice msg))))))))
    (doseq [item (:market-ask data/note-paper)]
      (let [data (assoc item :completed 0 :category "market-ask")
            message (doto (FindOrder.)
                      (.setId (:id item)))]
        (o/save data)
        (.tell actor message self)
        (await)
        (.expectMsgPF test-kit "should get market ask order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? MarketAsk msg))
                          (is (= (:id item) (.getId msg)))
                          (is (= 0 (:completed data) (.getCompleted msg)))
                          (is (= (:quantity data) (.getQuantity msg)))
                          (is (= (:account-id data) (.getAccountId msg)))
                          (is (= (:symbol data)) (.getSymbol msg)))))))
    (doseq [item (:market-bid data/note-paper)]
      (let [data (assoc item :completed 0 :category "market-bid")
            message (doto (FindOrder.)
                      (.setId (:id item)))]
        (o/save data)
        (.tell actor message self)
        (await)
        (.expectMsgPF test-kit "should get market bid order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? MarketBid msg))
                          (is (= (:id item) (.getId msg)))
                          (is (= 0 (:completed data) (.getCompleted msg)))
                          (is (= (:account-id data) (.getAccountId msg)))
                          (is (= (:quantity data) (.getQuantity msg)))
                          (is (= (:symbol data)) (.getSymbol msg)))))))
    (doseq [item (:cancel data/note-paper)]
      (let [data (assoc item :completed 0 :category "cancel")
            message (doto (FindOrder.)
                      (.setId (:id item)))]
        (o/save data)
        (.tell actor message self)
        (await)
        (.expectMsgPF test-kit "should get cancel order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? Cancel msg))
                          (is (= (:id item) (.getId msg)))
                          (is (= (:account-id data) (.getAccountId msg)))
                          (is (= :order-id) (.getOrderId msg))
                          (is (= (:symbol data)) (.getSymbol msg)))))))
    (TestKit/shutdownActorSystem system)))

(testing "tests for find next action by actor in local system"
  (j/delete! @cfg/db :order_flow ["id < ?" 26])
  (let [system (ActorSystem/create "test")
        test-kit (TestKit. system)
        await #(.awaitCond test-kit (reify Supplier (get [this] (.msgAvailable test-kit))))
        self (.getRef test-kit)
        actor (.actorOf system (PeekActor/props))]
    (doseq [item (:limit-ask data/note-paper)]
      (let [data (assoc item :completed 0 :category "limit-ask")
            message (doto (NextOrder.)
                      (.setPositionId (- (:id item) 1)))]
        (o/save data)
        (.tell actor message self)
        (await)
        (.expectMsgPF test-kit "should get limit ask order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? LimitAsk msg))
                          (is (= (:id item) (.getId msg)))
                          (is (= 0 (:completed data) (.getCompleted msg)))
                          (is (= (:account-id data) (.getAccountId msg)))
                          (is (= (:quantity data) (.getQuantity msg)))
                          (is (= (:symbol data)) (.getSymbol msg))
                          (is (= (:price data) (.getPrice msg))))))))
    (doseq [item (:limit-bid data/note-paper)]
      (let [data (assoc item :completed 0 :category "limit-bid")
            message (doto (NextOrder.)
                      (.setPositionId (- (:id item) 1)))]
        (o/save data)
        (.tell actor message self)
        (await)
        (.expectMsgPF test-kit "should get limit bid order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? LimitBid msg))
                          (is (= (:id item) (.getId msg)))
                          (is (= 0 (:completed data) (.getCompleted msg)))
                          (is (= (:account-id data) (.getAccountId msg)))
                          (is (= (:quantity data) (.getQuantity msg)))
                          (is (= (:symbol data)) (.getSymbol msg))
                          (is (= (:price data) (.getPrice msg))))))))
    (doseq [item (:market-ask data/note-paper)]
      (let [data (assoc item :completed 0 :category "market-ask")
            message (doto (NextOrder.)
                      (.setPositionId (- (:id item) 1)))]
        (o/save data)
        (.tell actor message self)
        (await)
        (.expectMsgPF test-kit "should get market ask order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? MarketAsk msg))
                          (is (= (:id item) (.getId msg)))
                          (is (= 0 (:completed data) (.getCompleted msg)))
                          (is (= (:account-id data) (.getAccountId msg)))
                          (is (= (:quantity data) (.getQuantity msg)))
                          (is (= (:symbol data)) (.getSymbol msg)))))))
    (doseq [item (:market-bid data/note-paper)]
      (let [data (assoc item :completed 0 :category "market-bid")
            message (doto (NextOrder.)
                      (.setPositionId (- (:id item) 1)))]
        (o/save data)
        (.tell actor message self)
        (await)
        (.expectMsgPF test-kit "should get market bid order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? MarketBid msg))
                          (is (= (:id item) (.getId msg)))
                          (is (= (:account-id data) (.getAccountId msg)))
                          (is (= 0 (:completed data) (.getCompleted msg)))
                          (is (= (:quantity data) (.getQuantity msg)))
                          (is (= (:symbol data)) (.getSymbol msg)))))))
    (doseq [item (:cancel data/note-paper)]
      (let [data (assoc item :completed 0 :category "cancel")
            message (doto (NextOrder.)
                      (.setPositionId (- (:id item) 1)))]
        (o/save data)
        (.tell actor message self)
        (await)
        (.expectMsgPF test-kit "should get cancel order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? Cancel msg))
                          (is (= (:id item) (.getId msg)))
                          (is (= (:account-id data) (.getAccountId msg)))
                          (is (= :order-id) (.getOrderId msg))
                          (is (= (:symbol data)) (.getSymbol msg)))))))
    (TestKit/shutdownActorSystem system)))