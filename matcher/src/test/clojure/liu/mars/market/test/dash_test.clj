(ns liu.mars.market.test.dash_test
  (:require [clojure.test :refer [testing is]])
  (:import (akka.actor ActorSystem Props)
           (akka.testkit.javadsl TestKit)
           (liu.mars.actor StatusActorMock StatusActorMock$PrepareEmpty DashActor)
           (java.util.function Function Supplier)
           (liu.mars.message QueryDepth LimitBid LimitAsk MarketAsk MarketBid)
           (liu.mars.trade Trade)
           (liu.mars.dash Depth)))

(testing "dash basic tests"
  (let [system (ActorSystem/create "test")
        testKit (TestKit. system)
        world-ref (.getRef testKit)
        sym "btceth"
        _ (.actorOf system (Props/create StatusActorMock (to-array [sym])) "status")
        await #(.awaitCond testKit (reify Supplier (get [this] (.msgAvailable testKit))))
        await-in #(.awaitCond testKit % (reify Supplier (get [this] (.msgAvailable testKit))))
        status-url (str "akka://" (.name system) "/user/status")
        dash-actor (.actorOf system (Props/create DashActor (to-array [sym status-url])) (str "dash:" sym))
        depth-query (doto (QueryDepth.)
                      (.setStep 0))
        limit-ask (doto (LimitAsk.)
                    (.setId 2)
                    (.setSymbol sym)
                    (.setPrice 5000M)
                    (.setQuantity 100))
        limit-bid (doto (LimitBid.)
                    (.setId 1)
                    (.setSymbol sym)
                    (.setPrice 3000M)
                    (.setQuantity 100))
        market-ask (doto (MarketAsk.)
                     (.setId 3)
                     (.setSymbol sym)
                     (.setQuantity 10))
        market-bid (doto (MarketBid.)
                     (.setId 4)
                     (.setSymbol sym)
                     (.setQuantity 10))]
    (Thread/sleep 1000)
    (.tell dash-actor depth-query world-ref)
    (await)
    (.expectMsgPF testKit "dash should empty"
                  (reify Function
                    (apply [this msg]
                      (is (= 0 (.getVersion msg)))
                      (is (.isEmpty (.getAsk msg)))
                      (is (.isEmpty (.getBid msg))))))
    (.tell dash-actor limit-bid world-ref)
    (await)
    (.expectMsgPF testKit "now one limit bid should become a maker"
                  (reify Function
                    (apply [this msg]
                      (is (= Trade (.getClass msg)))
                      (is (= 0 (count (.getTradeItems msg)))))))
    (.tell dash-actor depth-query world-ref)
    (await)
    (.expectMsgPF testKit "now one maker order should in dash"
                  (reify Function
                    (apply [this msg]
                      (is (= Depth (.getClass msg)))
                      (is (= 0 (count (.getAsk msg))))
                      (is (= 1 (count (.getBid msg))))
                      (let [item (first (.getBid msg))]
                        (is (= (.getPrice limit-bid) (.getPrice item)))
                        (is (= (.getQuantity limit-bid) (.getQuantity item)))))))
    (.tell dash-actor limit-ask world-ref)
    (await)
    (.expectMsgPF testKit "now one limit ask should become a maker"
                  (reify Function
                    (apply [this msg]
                      (is (= Trade (.getClass msg)))
                      (is (= 0 (count (.getTradeItems msg)))))))
    (.tell dash-actor depth-query world-ref)
    (await)
    (.expectMsgPF testKit "now two maker orders should in dash"
                  (reify Function
                    (apply [this msg]
                      (is (= Depth (.getClass msg)))
                      (is (= 1 (count (.getAsk msg))))
                      (is (= 1 (count (.getBid msg))))
                      (let [item (first (.getBid msg))]
                        (is (= (.getPrice limit-bid) (.getPrice item)))
                        (is (= (.getQuantity limit-bid) (.getQuantity item))))
                      (let [item (first (.getAsk msg))]
                        (is (= (.getPrice limit-ask) (.getPrice item)))
                        (is (= (.getQuantity limit-ask) (.getQuantity item)))))))

    (.tell dash-actor market-ask world-ref)
    (await)
    (.expectMsgPF testKit "now one market ask order should be traded"
                  (reify Function
                    (apply [this msg]
                      (is (= Trade (.getClass msg)))
                      (is (= 1 (count (.getTradeItems msg))))
                      (is (= (.getQuantity market-ask) (.getTurnover (first (.getTradeItems msg))))))))
    (.tell dash-actor depth-query world-ref)
    (await)
    (.expectMsgPF testKit "now two maker orders should in dash else but something have been bid"
                  (reify Function
                    (apply [this msg]
                      (is (= Depth (.getClass msg)))
                      (is (= 1 (count (.getAsk msg))))
                      (is (= 1 (count (.getBid msg))))
                      (let [bid (first (.getBid msg))]
                        (is (= (.getPrice limit-bid) (.getPrice bid)))
                        (is (= (.getQuantity limit-bid) (+ (.getQuantity market-ask) (.getQuantity bid))))
                        (is (= (.getQuantity bid) (- (.getQuantity limit-bid) (.getQuantity market-ask))))))))
    (.tell dash-actor market-bid world-ref)
    (await)
    (.expectMsgPF testKit "now one market bid should be traded"
                  (reify Function
                    (apply [this msg]
                      (is (= Trade (.getClass msg)))
                      (is (= 1 (count (.getTradeItems msg))))
                      (let [bid (first (.getTradeItems msg))]
                        (is (= (.getPrice limit-ask) (.getPrice bid)))
                        (is (= (.getTurnover bid) (.getQuantity market-bid)))))))
    (.tell dash-actor depth-query world-ref)
    (await)
    (.expectMsgPF testKit "now two maker orders should in dash"
                  (reify Function
                    (apply [this msg]
                      (is (= Depth (.getClass msg)))
                      (is (= 1 (count (.getAsk msg))))
                      (is (= 1 (count (.getBid msg))))
                      (let [bid (first (.getBid msg))]
                        (is (= (.getPrice limit-bid) (.getPrice bid)))
                        (is (= (.getQuantity limit-bid) (+ (.getQuantity market-ask) (.getQuantity bid)))))
                      (let [ask (first (.getAsk msg))]
                        (is (= (.getPrice limit-ask) (.getPrice ask)))
                        (is (= (.getQuantity limit-ask) (+ (.getQuantity market-bid) (.getQuantity ask))))))))
    (TestKit/shutdownActorSystem system)))