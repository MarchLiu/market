(ns liu.mars.market.status
  (:require [clojure.java.jdbc :as j])
  (:require [liu.mars.market.config :as config])
  (:require [cheshire.core :as c]))

(def db (delay @config/db))

(defn empty-status
  [sym]
  {:symbol          sym
   :asks            []
   :bids            []
   :latest-order-id 0})

(defn dump
  ([data]
   (-> data
       first
       :content
       c/generate-string))
  ([data sym]
   (-> data
       first
       :content
       (#(if (contains? % :symbol)
           %
           (assoc % :symbol sym)))
       c/generate-string)))

(defn load-latest
  ([]
   (-> @db
       (j/query ["select content from status where id=(select max(id) from status)"])
       dump))
  ([sym]
   (let [result (-> @db
                    (j/query
                      [(str "select content "
                            "from status "
                            "where id=(select max(id) from status where meta ->> 'symbol' = ?)")
                       sym]))]
     (if (empty? result)
       (c/generate-string (empty-status sym))
       (dump result sym)))))

(defn save
  ([status]
   (let [data (c/parse-string status true)
         sym (:symbol data)]
     (j/execute! @db ["insert into status(meta, content) values(?, ?)"
                      {:symbol sym}
                      data])))
  ([status source]
   (let [data (c/parse-string status true)
         sym (:symbol data)]
     (j/execute! @db ["insert into status(meta, content) values(?, ?)"
                      {:symbol sym :source source}
                      data]))))
