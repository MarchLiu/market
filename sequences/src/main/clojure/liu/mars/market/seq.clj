(ns liu.mars.market.seq
  (:require [clojure.java.jdbc :as jdbc])
  (:require [jaskell.sql :refer [select from as]]))

(def db-spec
  {:dbtype "postgresql"
   :dbname "sequences"})

(defn create-seq
  [^String seq-name]
  (jdbc/execute! db-spec (str "create sequence " seq-name)))

(defn list-seq
  []
  (let [sql (-> (select ["sequencename" " COALESCE(last_value, start_value)" as :last_value] from :pg_sequences)
                (.script))]
    (jdbc/query db-spec [sql])))

(defn next-val
  [seq-name]
  (->
    (jdbc/query db-spec ["select nextval(?)" seq-name])
    first
    (:nextval)))

(defn drop-seq
  [seq-name]
  (jdbc/execute! db-spec (str "drop sequence " seq-name)))