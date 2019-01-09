(ns liu.mars.market.order
  (:require [clojure.java.jdbc :as j])
  (:require [clj-postgresql.core :as pg])
  (:require [liu.mars.market.config :as config])
  (:require [jaskell.sql :refer [select from where join with f p as on]])
  (:import (liu.mars.market.messages LimitAsk LimitBid MarketAsk MarketBid Cancel OrderNotFound OrderNoMore)))

(def db (delay @config/db))

(defn save [order]
  (j/insert! @db :order_flow {:id         (:id order)
                              :price      (:price order)
                              :account_id (:account-id order)
                              :symbol     (:symbol order)
                              :content    (if-some [_ (:price order)]
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

(def find-last-by-id
  (-> (with [:last as
             (select (f :min :id) :as :id
                     from :order_flow
                     where :id :> (p 0))]
            select [:order_flow.id :content :price]
            from :order_flow
            join :last on :order_flow.id := :last.id)
      (.cache)))

(def find-last-query
  (-> (with [:last as
             (select (f :min :id) :as :id
                     from :order_flow
                     where :id :> (p 0) :and :content :->> "'symbol'" := (p 1))]
            select [:order_flow.id :content :price]
            from :order_flow
            join :last on :order_flow.id := :last.id)
      (.cache)))

(defmulti load-order (fn [data] (get-in data [:content "category"])))

(defmethod load-order "limit-ask" [data]
  (doto (LimitAsk.)
    (.setId (:id data))
    (.setPrice (:price data))
    (.setSymbol (get-in data [:content "symbol"]))
    (.setQuantity (get-in data [:content "quantity"]))
    (.setCompleted (get-in data [:content "completed"]))
    (.setAccountId (get-in data [:content "account-id"]))))

(defmethod load-order "limit-bid" [data]
  (doto (LimitBid.)
    (.setId (:id data))
    (.setPrice (:price data))
    (.setSymbol (get-in data [:content "symbol"]))
    (.setQuantity (get-in data [:content "quantity"]))
    (.setCompleted (get-in data [:content "completed"]))
    (.setAccountId (get-in data [:content "account-id"]))))

(defmethod load-order "market-ask" [data]
  (doto (MarketAsk.)
    (.setId (:id data))
    (.setSymbol (get-in data [:content "symbol"]))
    (.setQuantity (get-in data [:content "quantity"]))
    (.setCompleted (get-in data [:content "completed"]))
    (.setAccountId (get-in data [:content "account-id"]))))

(defmethod load-order "market-bid" [data]
  (doto (MarketBid.)
    (.setId (:id data))
    (.setSymbol (get-in data [:content "symbol"]))
    (.setQuantity (get-in data [:content "quantity"]))
    (.setCompleted (get-in data [:content "completed"]))
    (.setAccountId (get-in data [:content "account-id"]))))

(defmethod load-order "cancel" [data]
  (doto (Cancel.)
    (.setId (:id data))
    (.setSymbol (get-in data [:content "symbol"]))
    (.setAccountId (get-in data [:content "account-id"]))
    (.setOrderId (get-in data [:content "order-id"]))))

(defn find-by
  ([order-id]
   (if-some [data (j/get-by-id @db :order_flow order-id)]
     (load-order data)
     (doto (OrderNotFound.)
       (.setId order-id))))
  ([order-id sym]
    (let [data (j/find-by-keys @db :order_flow {:id order-id :symbol sym})]
      (if (not-empty data)
        (load-order (first data))
        (doto (OrderNotFound.)
          (.setPositionId order-id)
          (.setSymbol sym))))))

(defn find-next
  ([from-id]
   (let [data (j/query @db [(.script find-last-by-id) from-id])]
     (if (not-empty data)
       (load-order (first data))
       (doto (OrderNoMore.)
         (.setPositionId from-id)))))
  ([from-id sym]
    (let [data (j/query @db [(.script find-last-query) from-id sym])]
      (if (not-empty data)
        (load-order (first data))
        (doto (OrderNoMore.)
          (.setPositionId from-id)
          (.setSymbol sym))))))
