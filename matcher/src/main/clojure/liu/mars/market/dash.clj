(ns liu.mars.market.dash
  (:import (akka.actor AbstractActor)
           (scala PartialFunction Function1)))

(deftype SampleActor [sequences status quotations trades])

(proxy [AbstractActor] []
  (createReceive []
    (proxy [PartialFunction] []
      (apply [message]
        ))))