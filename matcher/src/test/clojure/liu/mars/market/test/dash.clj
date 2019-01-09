(ns liu.mars.market.test.dash
  (:require [cheshire.core :refer :all])
  (:import [com.fasterxml.jackson.databind ObjectMapper]
           (liu.mars.status DashStatus)))

(defn orderbook-0
  [status sym]
  {:id              1
   :latest-order-id 100
   :status          status
   :symbol          sym
   :asks            [{:order-id 6 :price "20001.002005" :quantity 9000 :symbol "btcusdt"}
                     {:order-id 1 :price "19008.0023" :quantity 5400 :symbol "btcusdt" :completed 0}
                     {:order-id 18 :price "17000.0005" :quantity 2341 :symbol "btcusdt"}
                     {:order-id 2 :price "16056.002005" :quantity 1000 :symbol "btcusdt" :completed 800}
                     {:order-id 17 :price "15090.02005" :quantity 4290 :symbol "btcusdt"}
                     {:order-id 16 :price "9000.0005" :quantity 6220 :symbol "btcusdt"}
                     {:order-id 15 :price "8000.002005" :quantity 2220 :symbol "btcusdt"}
                     {:order-id 14 :price "7000.08005" :quantity 3720 :symbol "btcusdt"}
                     {:order-id 12 :price "6905.002005" :quantity 920 :symbol "btcusdt"}
                     {:order-id 13 :price "6500.0905" :quantity 322 :symbol "btcusdt" :completed 1000}]
   :bids            [{:order-id 20 :price "101.232005" :quantity 1320020 :symbol "btcusdt"}
                     {:order-id 11 :price "4000.0005" :quantity 32320 :symbol "btcusdt"}
                     {:order-id 19 :price "4300.002005" :quantity 33220 :symbol "btcusdt"}
                     {:order-id 25 :price "4400.00205" :quantity 320 :symbol "btcusdt"}
                     {:order-id 3 :price "4500.00095" :quantity 220 :symbol "btcusdt"}
                     {:order-id 9 :price "4900.00005" :quantity 2520 :symbol "btcusdt"}
                     {:order-id 8 :price "5900.002005" :quantity 320 :symbol "btcusdt"}
                     {:order-id 5 :price "6000.00905" :quantity 322 :symbol "btcusdt"}
                     {:order-id 4 :price "6200.002105" :quantity 3224 :symbol "btcusdt" :completed 10}
                     {:order-id 10 :price "6497.00105" :quantity 30000 :symbol "btcusdt" :completed 1000}]})

(defn empty-orderbook
  [status sym]
  {:id              0
   :latest-order-id 0
   :status          status
   :symbol          sym
   :asks            []
   :bids            []})

(defn to-status
  [orderbook]
  (let [^ObjectMapper mapper (new ObjectMapper)]
    (->> orderbook
         generate-string
         (#(.readValue mapper % DashStatus)))))

(defn empty-status
  [status sym]
  (-> (empty-orderbook status sym)
      to-status))

(defn status-0
  [status sym]
  (-> (orderbook-0 status sym)
      to-status))
