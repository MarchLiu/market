;; gorilla-repl.fileformat = 1

;; **
;;; # Try to find way to implement abstract to actor
;;; 
;; **

;; @@
(defmulti receive :category)

(defmethod receive :limit-ask [message]
  (println "limit ask make new asks"))

(defmethod receive :limit-bid [message]
  (println "limit bid make new bids"))

(defmethod receive :limit-ask [message]
  (println "market ask take from bids"))

(defmethod receive :limit-bid [message]
  (println "market bid take from asks"))
;; @@
;; =>
;;; {"type":"html","content":"<span class='clj-unkown'>#multifn[receive 0x663d5e95]</span>","value":"#multifn[receive 0x663d5e95]"}
;; <=

;; @@
(receive {:category :limit-ask})
;; @@

;; @@

;; @@
