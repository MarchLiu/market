(ns liu.mars.market.order
  (:require [clojure.java.jdbc :as j])
  (:require [clj-postgresql.core :as pg])
  (:require [liu.mars.market.config :as config])
  (:import (liu.mars.market.messages LimitAsk LimitBid MarketAsk MarketBid Cancel)))

(def db (delay @config/db))

(defn save [order]
  (j/insert! @db :order_flow {:id      (:id order)
                              :price   (:price order)
                              :content (if-some [_ (:price order)]
                                         (update order :price str)
                                         order)}))

(defmulti place-order (fn [order] (.getClass order)))

(defmethod place-order LimitAsk [order]
  (save {:id         (.getId order)
         :category   "limit-ask"
         :account-id (.getAccountId order)
         :price      (.getPrice order)
         :quantity   (.getQuantity order)
         :completed  (.getCompleted order)
         :symbol     (.getSymbol order)}))

(defmethod place-order LimitBid [order]
  (save {:id         (.getId order)
         :category   "limit-bid"
         :account-id (.getAccountId order)
         :price      (.getPrice order)
         :quantity   (.getQuantity order)
         :completed  (.getCompleted order)
         :symbol     (.getSymbol order)}))

(defmethod place-order MarketAsk [order]
  (save {:id         (.getId order)
         :category   "market-ask"
         :account-id (.getAccountId order)
         :quantity   (.getQuantity order)
         :completed  (.getCompleted order)
         :symbol     (.getSymbol order)}))

(defmethod place-order MarketBid [order]
  (save {:id         (.getId order)
         :category   "market-bid"
         :account-id (.getAccountId order)
         :quantity   (.getQuantity order)
         :completed  (.getCompleted order)
         :symbol     (.getSymbol order)}))

(defmethod place-order Cancel [order]
  (save {:id         (.getId order)
         :category   "cancel"
         :order-id   (.getOrderId order)
         :account-id (.getAccountId order)
         :symbol     (.getSymbol order)}))
