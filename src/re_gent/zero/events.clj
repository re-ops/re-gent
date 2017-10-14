(ns re-gent.zero.events
  (:require
   [re-gent.zero.management :refer (register)]
   [taoensso.timbre :refer (refer-timbre)]))

(refer-timbre)

(defn handle [e-type event]
  (when (= e-type :connected)
    (register)))

