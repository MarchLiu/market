(ns liu.mars.market.inner-find-test
  (:require [clojure.java.jdbc :as j])
  (:require [liu.mars.market.order :refer :all])
  (:require [clojure.test :refer :all])
  (:require [liu.mars.market.test-data :as data]
            [liu.mars.market.config :as cfg])
  (:import (liu.mars.market.messages LimitAsk LimitBid MarketAsk MarketBid)))

(testing "inner testing for order module"
  (j/delete! @cfg/db :order_flow ["id < ?" 26])
  (testing "tests for limit ask orders save and reload"
    (doseq [item (:limit-ask data/note-paper)]
      (let [data (assoc item :completed 0 :category "limit-ask")]
        (save data)
        (let [order (find-by (:id data))]
          (is (instance? LimitAsk order))
          (is (= (:id data) (.getId order)))
          (is (= 0 (:completed data) (.getCompleted order)))
          (is (= (:quantity data) (.getQuantity order)))
          (is (= (:account-id data) (.getAccountId order)))
          (is (= (:symbol data)) (.getSymbol order))
          (is (= (:price data) (.getPrice order)))))))
  (testing "tests for limit bid orders save and reload"
    (doseq [item (:limit-bid data/note-paper)]
      (let [data (assoc item :completed 0 :category "limit-bid")]
        (save data)
        (let [order (find-by (:id data))]
          (is (instance? LimitBid order))
          (is (= (:id data) (.getId order)))
          (is (= 0 (:completed data) (.getCompleted order)))
          (is (= (:quantity data) (.getQuantity order)))
          (is (= (:account-id data) (.getAccountId order)))
          (is (= (:symbol data)) (.getSymbol order))
          (is (= (:price data) (.getPrice order)))))))
  (testing "tests for market ask orders save and reload"
    (doseq [item (:market-ask data/note-paper)]
      (let [data (assoc item :completed 0 :category "market-ask")]
        (save data)
        (let [order (find-by (:id data))]
          (is (instance? MarketAsk order))
          (is (= (:id data) (.getId order)))
          (is (= 0 (:completed data) (.getCompleted order)))
          (is (= (:quantity data) (.getQuantity order)))
          (is (= (:account-id data) (.getAccountId order)))
          (is (= (:symbol data)) (.getSymbol order))))))
  (testing "tests for market bid orders save and reload"
    (doseq [item (:market-bid data/note-paper)]
      (let [data (assoc item :completed 0 :category "market-bid")]
        (save data)
        (let [order (find-by (:id data))]
          (is (instance? MarketBid order))
          (is (= (:id data) (.getId order)))
          (is (= 0 (:completed data) (.getCompleted order)))
          (is (= (:account-id data) (.getAccountId order)))
          (is (= (:quantity data) (.getQuantity order)))
          (is (= (:symbol data)) (.getSymbol order)))))))