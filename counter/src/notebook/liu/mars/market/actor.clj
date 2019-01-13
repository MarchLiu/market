(ns liu.mars.market.actor
  (:import (akka.actor AbstractActor Props)
           (akka.japi Creator))
  (:gen-class
    :name liu.mars.market.MarketActor
    :extend akka.actor.AbstractActor
    :prefix "a-"))

(defn a-create-receive [this]
  (-> this
      .receiveBuilder
      .build))

(defn props []
  (Props/create liu.mars.market.MarketActor (reify Creator
                                              (create [this]
                                                (new liu.mars.market.MarketActor)))))