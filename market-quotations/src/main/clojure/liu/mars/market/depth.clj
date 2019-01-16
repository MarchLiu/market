(ns liu.mars.market.depth
  (:require [clojure.math.numeric-tower :refer [abs]])
  (:import (liu.mars.market.dash Level)))

(defn steps
  [orders step]
  (let [head (first orders)
        tail (last orders)]
    (-> (- (.getPrice tail) (.getPrice head))
        (/ (count orders))
        (abs)
        (* step))))

(defmulti merge-depth
          (fn [step _] step))

(defmethod merge-depth 0 [_ orders]
  (map #(Level/from %) orders))

(defmethod merge-depth 1 [_ orders]
  (into []
        (comp
          (partition-by #(quot (.getPrice %) (steps orders 2)))
          (map #(Level/accumulate %)))
        orders))

(defmethod merge-depth 2 [_ orders]
  (into []
        (comp
          (partition-by #(quot (.getPrice %) (steps orders 5)))
          (map #(Level/accumulate %)))
        orders))

(defmethod merge-depth 3 [_ orders]
  (into []
        (comp
          (partition-by #(-> % (.getPrice) (Math/sqrt) int))
          (map #(Level/accumulate %)))
        orders))

(defmethod merge-depth 4 [_ orders]
  (into []
        (comp
          (partition-by #(-> % (.getPrice) (Math/log) int))
          (map #(Level/accumulate %)))
        orders))

(defmethod merge-depth 5 [_ orders]
  (into []
        (comp
          (partition-by #(-> % (.getPrice) (Math/log10) int))
          (map #(Level/accumulate %)))
        orders))