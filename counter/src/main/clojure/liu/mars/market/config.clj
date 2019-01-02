(ns liu.mars.market.config
  (:require [clojure.java.io :refer [resource]])
  (:require [clojure.edn :as edn]
            [clj-postgresql.core :as pg]))

(def default-config
  {:db-spec {:dbtype "postgresql"
             :dbname "counter"}})

(def conf (delay
            (if-let [url (resource "config.edn")]
              (-> url
                  slurp
                  (edn/read-string)
                  (#(merge default-config %)))
              default-config)))

(defn seq
  []
  (-> @conf
      :sequences))

(def db
  (delay
    (->> @conf
         :db-spec
         (apply concat)
         (apply pg/pool))))
