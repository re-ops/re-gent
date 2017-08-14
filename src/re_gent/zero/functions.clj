(ns re-gent.zero.functions
  (:require
    [re-share.metrics :refer (read-metrics)]
    [clojure.java.shell :refer [sh]]
    [serializable.fn :refer :all]
    [me.raynes.fs :refer :all]))

(comment
  (clojure.pprint/pprint (read-metrics))
  )
