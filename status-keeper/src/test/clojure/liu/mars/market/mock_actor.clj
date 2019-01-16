(ns liu.mars.market.mock-actor
  (:require [liu.mars.actor :refer [!]])
  (:import (liu.mars.market.directive StatusQuery)
           (liu.mars.market.status DashStatus)))

(defn init [this]
  (-> (.getState this)
      (send #(assoc % :id 0))))

(defmulti match-mock (fn [this msg] (class msg)))
(defmethod match-mock StatusQuery [this msg]
  (let [result (doto (DashStatus.)
                 (.setSymbol (.getSymbol msg))
                 (.setLatestOrderId (:id @(.getState this)))
                 (.setStatus "trading"))]
    (-> this
        .getState
        (send #(update % :id inc)))
    (! (.getSender this) result)))