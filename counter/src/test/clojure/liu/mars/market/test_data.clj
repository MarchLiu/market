(ns liu.mars.market.test-data)

(def sym "btcusdt")

(def note-paper
  {:limit-ask  [{:id 1 :symbol sym :price 34522M :quantity 1 :account-id 3223421}
                {:id 2 :symbol sym :price 34512M :quantity 10001 :account-id 3223421}
                {:id 3 :symbol sym :price 34525M :quantity 10020 :account-id 34223421}
                {:id 4 :symbol sym :price 34562M :quantity 1000 :account-id 3422341}
                {:id 5 :symbol sym :price 44522M :quantity 10000 :account-id 34223421}]
   :limit-bid  [{:id 6 :symbol sym :price 24522M :quantity 1 :account-id 34223421}
                {:id 7 :symbol sym :price 3412M :quantity 10001 :account-id 34223421}
                {:id 8 :symbol sym :price 32525M :quantity 10020 :account-id 34223421}
                {:id 9 :symbol sym :price 31562M :quantity 1000 :account-id 34223421}
                {:id 10 :symbol sym :price 1522M :quantity 9999 :account-id 34223421}]
   :market-ask [{:id 11 :symbol sym :quantity 1 :account-id 34223421}
                {:id 12 :symbol sym :quantity 10001 :account-id 3422342}
                {:id 13 :symbol sym :quantity 10020 :account-id 34223421}
                {:id 14 :symbol sym :quantity 1000 :account-id 3423421}
                {:id 15 :symbol sym :quantity 10000 :account-id 34223421}]
   :market-bid [{:id 16 :symbol sym :quantity 12433 :account-id 34223421}
                {:id 17 :symbol sym :quantity 10001 :account-id 34223421}
                {:id 18 :symbol sym :quantity 10020 :account-id 34223421}
                {:id 19 :symbol sym :quantity 1000 :account-id 34223421}
                {:id 20 :symbol sym :quantity 9999 :account-id 3422421}]
   :cancel     [{:id 21 :symbol sym :account-id 4223421 :order-id 23341}
                {:id 22 :symbol sym :account-id 3423421 :order-id 23342}
                {:id 23 :symbol sym :account-id 3422321 :order-id 2341}
                {:id 24 :symbol sym :account-id 3423421 :order-id 23}
                {:id 25 :symbol sym :account-id 3423421 :order-id 9}]})