(ns liu.mars.market.seq-test
  (:require [liu.mars.market.seq :refer :all])
  (:require [clojure.test :refer :all]))

(testing "sequences functions should list"
  (testing "list a empty sequences list"
    (is (empty? (list-seq))))
  (testing "create a new sequence and use it and then drop it"
    (create-seq "test")
    (let [sequences (list-seq)
          aim (atom 0)]
      (is 1 (count sequences))
      (is (= "test" (-> sequences
                        first
                        :name)))
      (is (= 1 (-> sequences
                   first
                   :last-value)))
      (dotimes [_ 100]
        (is (= (swap! aim inc) (next-val "test"))))
      (is (= 100 (-> (list-seq)
                     first
                     :last-value))))
    (drop-seq "test")))