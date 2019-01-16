(ns liu.mars.market.config
  (:require [clojure.java.io :refer [resource]])
  (:require [clojure.edn :as edn]
            [clj-postgresql.core :as pg]))

(def default-config
  {:db-spec {:dbtype "postgresql"
             :dbname "match"}
   :sequences "akka://sequences/user/sequences"
   :quotations "akka://market/user/quotations"
   :peek "akka://counter/user/peek"
   :status "akka://status/user/status"})

(def conf (delay
            (if-let [url (resource "config.edn")]
              (-> url
                  slurp
                  (edn/read-string)
                  (#(merge default-config %)))
              default-config)))

(defn sequences []
  (:sequences @conf))

(defn status []
  (:status @conf))

(defn quotations
  []
  (:quotations @conf))

(defn peek-selection
  []
  (:peek @conf))

(def db
  (delay
    (->> @conf
         :db-spec
         (apply concat)
         (apply pg/pool))))