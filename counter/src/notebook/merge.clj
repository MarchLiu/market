;; gorilla-repl.fileformat = 1

;; **
;;; # Gorilla REPL
;;; 
;;; Welcome to gorilla :-)
;;; 
;;; Shift + enter evaluates code. Hit ctrl+g twice in quick succession or click the menu icon (upper-right corner) for more commands ...
;;; 
;;; It's a good habit to run each worksheet in its own namespace: feel free to use the declaration we've provided below if you'd like.
;; **

;; @@
(require '[liu.mars.market.config :as conf])
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
@conf/db
;; @@
;; =>
;;; {"type":"list-like","open":"<span class='clj-map'>{</span>","close":"<span class='clj-map'>}</span>","separator":", ","items":[{"type":"list-like","open":"","close":"","separator":" ","items":[{"type":"html","content":"<span class='clj-keyword'>:datasource</span>","value":":datasource"},{"type":"html","content":"<span class='clj-unkown'>#object[com.jolbox.bonecp.BoneCPDataSource 0x61034516 &quot;JDBC URL = jdbc:postgresql:///counter, Username = mars, partitions = 1, max (per partition) = 10, min (per partition) = 0, idle max age = 60 min, idle test period = 240 min, strategy = DEFAULT&quot;]</span>","value":"#object[com.jolbox.bonecp.BoneCPDataSource 0x61034516 \"JDBC URL = jdbc:postgresql:///counter, Username = mars, partitions = 1, max (per partition) = 10, min (per partition) = 0, idle max age = 60 min, idle test period = 240 min, strategy = DEFAULT\"]"}],"value":"[:datasource #object[com.jolbox.bonecp.BoneCPDataSource 0x61034516 \"JDBC URL = jdbc:postgresql:///counter, Username = mars, partitions = 1, max (per partition) = 10, min (per partition) = 0, idle max age = 60 min, idle test period = 240 min, strategy = DEFAULT\"]]"}],"value":"{:datasource #object[com.jolbox.bonecp.BoneCPDataSource 0x61034516 \"JDBC URL = jdbc:postgresql:///counter, Username = mars, partitions = 1, max (per partition) = 10, min (per partition) = 0, idle max age = 60 min, idle test period = 240 min, strategy = DEFAULT\"]}"}
;; <=

;; @@
(require '[clojure.java.jdbc :as j])
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
(require '[clj-postgresql.core :as pg])
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-nil'>nil</span>","value":"nil"}
;; <=

;; @@
(def remote (pg/spec :host "192.168.50.22" :dbname "counter" :user "market" :password "market"))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-var'>#&#x27;user/remote</span>","value":"#'user/remote"}
;; <=

;; @@
(->> (j/query remote ["select * from order_flow"])
     (j/insert-multi! @config/db :order_flow))
;; @@

