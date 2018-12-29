(ns liu.mars.market.config
  (:require [clojure.java.io :refer [resource]])
  (:require [clojure.edn :as edn]))

(def default-config
  {:db-spec {:dbtype "postgresql"
             :dbname "sequences"}})

(def conf (delay
            (if-let [url (resource "config.edn")]
              (-> url
                  slurp
                  (edn/read-string)
                  (#(merge default-config %)))
              default-config)))

(defn db-spec []
  (:db-spec @conf))
