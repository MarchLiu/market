(ns liu.mars.market.local-remote-test
  "run with local profile, place run `lein with-profile +local run` prepare the server in local"
  (:require [clojure.test :refer :all])
  (:require [clojure.java.jdbc :as j])
  (:require [liu.mars.market.test-data :as data])
  (:require [liu.mars.market.config :as cfg])
  (:require [liu.mars.market.order :as o])
  (:import (akka.actor ActorSystem)
           (akka.testkit.javadsl TestKit)
           (liu.mars.market.messages FindOrder LimitAsk LimitBid MarketAsk MarketBid NextOrder Cancel)
           (java.util.function Supplier Function)))

(testing "tests for place and find actions by actor"
  (j/execute! @cfg/db ["delete from order_flow"])
  (let [system (ActorSystem/create "test")
        test-kit (TestKit. system)
        await #(.awaitCond test-kit (reify Supplier (get [this] (.msgAvailable test-kit))))
        self (.getRef test-kit)
        host "192.168.50.22"
        place-actor (.actorSelection system (str "akka.tcp://counter@" host ":25530/user/place"))
        peek-actor (.actorSelection system (str "akka.tcp://counter@" host ":25530/user/peek"))]
    (doseq [item (:limit-ask data/note-paper)]
      (let [post (doto (LimitAsk.)
                   (.setPrice (:price item))
                   (.setQuantity (:quantity item))
                   (.setAccountId (:account-id item))
                   (.setSymbol (:symbol item)))
            get  (FindOrder.)]
        (.tell place-actor post self)
        (await)
        (.expectMsgPF test-kit "should get new order id from place actor"
                      (reify Function
                        (apply [this msg]
                          (is (instance? Long msg))
                          (.setId get msg))))
        (.tell peek-actor get self)
        (await)
        (.expectMsgPF test-kit "should get limit ask order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? LimitAsk msg))
                          (is (= (.getId get) (.getId msg)))
                          (is (= 0 (.getCompleted msg)))
                          (is (= (:quantity item) (.getQuantity msg)))
                          (is (= (:account-id item) (.getAccountId msg)))
                          (is (= (:symbol item)) (.getSymbol msg))
                          (is (= (:price item) (.getPrice msg))))))))
    (doseq [item (:limit-bid data/note-paper)]
      (let [post (doto (LimitBid.)
                   (.setPrice (:price item))
                   (.setQuantity (:quantity item))
                   (.setAccountId (:account-id item))
                   (.setSymbol (:symbol item)))
            get  (FindOrder.)]
        (.tell place-actor post self)
        (await)
        (.expectMsgPF test-kit "should get new order id from place actor"
                      (reify Function
                        (apply [this msg]
                          (is (instance? Long msg))
                          (.setId get msg))))
        (.tell peek-actor get self)
        (await)
        (.expectMsgPF test-kit "should get limit ask order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? LimitBid msg))
                          (is (= (.getId get) (.getId msg)))
                          (is (= 0 (.getCompleted msg)))
                          (is (= (:quantity item) (.getQuantity msg)))
                          (is (= (:account-id item) (.getAccountId msg)))
                          (is (= (:symbol item)) (.getSymbol msg))
                          (is (= (:price item) (.getPrice msg))))))))
    (doseq [item (:market-ask data/note-paper)]
      (let [post (doto (MarketAsk.)
                   (.setQuantity (:quantity item))
                   (.setAccountId (:account-id item))
                   (.setSymbol (:symbol item)))
            get  (FindOrder.)]
        (.tell place-actor post self)
        (await)
        (.expectMsgPF test-kit "should get new order id from place actor"
                      (reify Function
                        (apply [this msg]
                          (is (instance? Long msg))
                          (.setId get msg))))
        (.tell peek-actor get self)
        (await)
        (.expectMsgPF test-kit "should get limit ask order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? MarketAsk msg))
                          (is (= (.getId get) (.getId msg)))
                          (is (= 0 (.getCompleted msg)))
                          (is (= (:quantity item) (.getQuantity msg)))
                          (is (= (:account-id item) (.getAccountId msg)))
                          (is (= (:symbol item)) (.getSymbol msg)))))))
    (doseq [item (:market-bid data/note-paper)]
      (let [post (doto (MarketBid.)
                   (.setQuantity (:quantity item))
                   (.setAccountId (:account-id item))
                   (.setSymbol (:symbol item)))
            get  (FindOrder.)]
        (.tell place-actor post self)
        (await)
        (.expectMsgPF test-kit "should get new order id from place actor"
                      (reify Function
                        (apply [this msg]
                          (is (instance? Long msg))
                          (.setId get msg))))
        (.tell peek-actor get self)
        (await)
        (.expectMsgPF test-kit "should get limit ask order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? MarketBid msg))
                          (is (= (.getId get) (.getId msg)))
                          (is (= 0 (.getCompleted msg)))
                          (is (= (:quantity item) (.getQuantity msg)))
                          (is (= (:account-id item) (.getAccountId msg)))
                          (is (= (:symbol item)) (.getSymbol msg)))))))
    (doseq [item (:cancel data/note-paper)]
      (let [post (doto (Cancel.)
                   (.setAccountId (:account-id item))
                   (.setSymbol (:symbol item))
                   (.setOrderId (:order-id item)))
            get  (FindOrder.)]
        (.tell place-actor post self)
        (await)
        (.expectMsgPF test-kit "should get new order id from place actor"
                      (reify Function
                        (apply [this msg]
                          (is (instance? Long msg))
                          (.setId get msg))))
        (.tell peek-actor get self)
        (await)
        (.expectMsgPF test-kit "should get cancel order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? Cancel msg))
                          (is (= (.getId get) (.getId msg)))
                          (is (= (:order-id item) (.getOrderId msg)))
                          (is (= (:account-id item) (.getAccountId msg)))
                          (is (= (:symbol item)) (.getSymbol msg)))))))

    (doseq [item (:limit-ask data/note-paper)]
      (let [post (doto (LimitAsk.)
                   (.setPrice (:price item))
                   (.setQuantity (:quantity item))
                   (.setAccountId (:account-id item))
                   (.setSymbol (:symbol item)))
            get  (NextOrder.)]
        (.tell place-actor post self)
        (await)
        (.expectMsgPF test-kit "should get new order id from place actor"
                      (reify Function
                        (apply [this msg]
                          (is (instance? Long msg))
                          (.setPositionId get (- msg 1)))))
        (.tell peek-actor get self)
        (await)
        (.expectMsgPF test-kit "should get limit ask order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? LimitAsk msg))
                          (is (= (+ (.getPositionId get) 1) (.getId msg)))
                          (is (= 0 (.getCompleted msg)))
                          (is (= (:quantity item) (.getQuantity msg)))
                          (is (= (:account-id item) (.getAccountId msg)))
                          (is (= (:symbol item)) (.getSymbol msg))
                          (is (= (:price item) (.getPrice msg))))))))
    (doseq [item (:limit-bid data/note-paper)]
      (let [post (doto (LimitBid.)
                   (.setPrice (:price item))
                   (.setQuantity (:quantity item))
                   (.setAccountId (:account-id item))
                   (.setSymbol (:symbol item)))
            get  (NextOrder.)]
        (.tell place-actor post self)
        (await)
        (.expectMsgPF test-kit "should get new order id from place actor"
                      (reify Function
                        (apply [this msg]
                          (is (instance? Long msg))
                          (.setPositionId get (- msg 1)))))
        (.tell peek-actor get self)
        (await)
        (.expectMsgPF test-kit "should get limit ask order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? LimitBid msg))
                          (is (= (inc (.getPositionId get)) (.getId msg)))
                          (is (= 0 (.getCompleted msg)))
                          (is (= (:quantity item) (.getQuantity msg)))
                          (is (= (:account-id item) (.getAccountId msg)))
                          (is (= (:symbol item)) (.getSymbol msg))
                          (is (= (:price item) (.getPrice msg))))))))
    (doseq [item (:market-ask data/note-paper)]
      (let [post (doto (MarketAsk.)
                   (.setQuantity (:quantity item))
                   (.setAccountId (:account-id item))
                   (.setSymbol (:symbol item)))
            get  (NextOrder.)]
        (.tell place-actor post self)
        (await)
        (.expectMsgPF test-kit "should get new order id from place actor"
                      (reify Function
                        (apply [this msg]
                          (is (instance? Long msg))
                          (.setPositionId get (- msg 1)))))
        (.tell peek-actor get self)
        (await)
        (.expectMsgPF test-kit "should get limit ask order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? MarketAsk msg))
                          (is (= (+ (.getPositionId get) 1) (.getId msg)))
                          (is (= 0 (.getCompleted msg)))
                          (is (= (:quantity item) (.getQuantity msg)))
                          (is (= (:account-id item) (.getAccountId msg)))
                          (is (= (:symbol item)) (.getSymbol msg)))))))
    (doseq [item (:market-bid data/note-paper)]
      (let [post (doto (MarketBid.)
                   (.setQuantity (:quantity item))
                   (.setAccountId (:account-id item))
                   (.setSymbol (:symbol item)))
            get  (NextOrder.)]
        (.tell place-actor post self)
        (await)
        (.expectMsgPF test-kit "should get new order id from place actor"
                      (reify Function
                        (apply [this msg]
                          (is (instance? Long msg))
                          (.setPositionId get (- msg 1)))))
        (.tell peek-actor get self)
        (await)
        (.expectMsgPF test-kit "should get limit ask order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? MarketBid msg))
                          (is (= (+ (.getPositionId get) 1) (.getId msg)))
                          (is (= 0 (.getCompleted msg)))
                          (is (= (:quantity item) (.getQuantity msg)))
                          (is (= (:account-id item) (.getAccountId msg)))
                          (is (= (:symbol item)) (.getSymbol msg)))))))
    (doseq [item (:cancel data/note-paper)]
      (let [post (doto (Cancel.)
                   (.setAccountId (:account-id item))
                   (.setSymbol (:symbol item))
                   (.setOrderId (:order-id item)))
            get  (NextOrder.)]
        (.tell place-actor post self)
        (await)
        (.expectMsgPF test-kit "should get new order id from place actor"
                      (reify Function
                        (apply [this msg]
                          (is (instance? Long msg))
                          (.setPositionId get (- msg 1)))))
        (.tell peek-actor get self)
        (await)
        (.expectMsgPF test-kit "should get cancel order as save"
                      (reify Function
                        (apply [this msg]
                          (is (instance? Cancel msg))
                          (is (= (inc (.getPositionId get)) (.getId msg)))
                          (is (= (:order-id item) (.getOrderId msg)))
                          (is (= (:account-id item) (.getAccountId msg)))
                          (is (= (:symbol item)) (.getSymbol msg)))))))
    (TestKit/shutdownActorSystem system)))
