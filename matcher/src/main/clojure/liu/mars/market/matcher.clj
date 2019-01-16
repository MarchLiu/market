(ns liu.mars.market.matcher
  (:require [clojure.java.jdbc :as j]
            [liu.mars.market.config :as config]
            [cheshire.core :as c]))

(defn save [trade]
  (let [data (c/parse-string trade true)]
    (j/execute! @config/db {:meta    {:category (:category data)
                                      :symbol   (:symbol data)
                                      :taker-id (:taker-id data)}
                            :content data})))