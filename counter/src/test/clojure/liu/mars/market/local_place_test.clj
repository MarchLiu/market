(ns liu.mars.market.local-place-test
  (:require [clojure.test :refer :all]
            [liu.mars.market.config :as config])
  (:import (akka.actor ActorSystem)
           (akka.testkit.javadsl TestKit)
           (liu.mars.market PlaceActor)
           (liu.mars.market.messages LimitAsk LimitBid MarketAsk MarketBid Cancel)
           (java.util.function Supplier Function)))

(testing "orders counter tests"
  (let [system (ActorSystem/create "test")
        test-kit (TestKit. system)
        self (.getRef test-kit)
        await #(.awaitCond test-kit (reify Supplier (get [this] (.msgAvailable test-kit))))
        seq-url (:sequences @config/conf)
        counter (.actorOf system (PlaceActor/props seq-url))
        sym "btcusdt"
        limit-ask (doto (LimitAsk.)
                    (.setSymbol sym)
                    (.setPrice 5000M)
                    (.setQuantity 100))
        limit-bid (doto (LimitBid.)
                    (.setSymbol sym)
                    (.setPrice 3000M)
                    (.setQuantity 100))
        market-ask (doto (MarketAsk.)
                     (.setSymbol sym)
                     (.setQuantity 10))
        market-bid (doto (MarketBid.)
                     (.setSymbol sym)
                     (.setQuantity 10))
        cancel (doto (Cancel.)
                 (.setSymbol sym)
                 (.setOrderId 2))
        last-id (atom 0)
        db @config/db]
    (.tell counter limit-ask self)
    (await)
    (.expectMsgPF test-kit "first message need be saved"
                  (reify Function
                    (apply [this msg]
                      (is (instance? Long msg))
                      (reset! last-id msg))))
    (.tell counter limit-bid self)
    (await)
    (.expectMsgPF test-kit "second message should get more id"
                  (reify Function
                    (apply [this msg]
                      (is (instance? Long msg))
                      (is (= (swap! last-id inc) msg)))))
    (.tell counter market-ask self)
    (await)
    (.expectMsgPF test-kit "third message increment 1"
                  (reify Function
                    (apply [this msg]
                      (is (instance? Long msg))
                      (is (= (swap! last-id inc) msg)))))
    (.tell counter market-bid self)
    (await)
    (.expectMsgPF test-kit ""
                  (reify Function
                    (apply [this msg]
                      (is (instance? Long msg))
                      (is (= (swap! last-id inc) msg)))))
    (.tell counter cancel self)
    (await)
    (.expectMsgPF test-kit "first message need be saved"
                  (reify Function
                    (apply [this msg]
                      (is (instance? Long msg))
                      (is (= (swap! last-id inc) msg)))))
    (TestKit/shutdownActorSystem system)))
