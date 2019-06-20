(ns re-gent.zero.functions
  (:require
   [re-share.oshi :refer (read-metrics os get-processes)]
   [re-scan.core :refer [nmap into-ports into-hosts]]
   [clojure.java.shell :refer [sh]]
   [cheshire.core :refer :all]
   [serializable.fn :refer :all]
   [re-cog.common :refer [require-constants require-functions bind-bash]]))

(require-functions)
(require-constants)
(bind-bash)

(comment
  (clojure.pprint/pprint (read-metrics)))
