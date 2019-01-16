(ns liu.mars.market.config
  (:require [clojure.java.io :refer [resource]])
  (:require [clojure.edn :as edn]
            [clj-postgresql.core :as pg]))

(def default-config
  {:db-spec    {:dbtype "postgresql"
                :dbname "status"}
   :matcher    "akka://matcher/user/matcher"
   :query-rate 60})

(def conf (delay
            (if-let [url (resource "config.edn")]
              (-> url
                  slurp
                  (edn/read-string)
                  (#(merge default-config %)))
              default-config)))

(defn query-rate
  []
  (:query-rate @conf))

(defn matcher
  [sym]
  (-> @conf
      :matcher
      sym))

(def db
  (delay
    (->> @conf
         :db-spec
         (apply concat)
         (apply pg/pool))))
