(ns re-gent.zero.functions
  (:require
   [re-share.oshi :refer (read-metrics os get-processes)]
   [re-scan.core :refer (nmap into-ports into-hosts)]
   [clojure.java.shell :refer [sh]]
   [cheshire.core :refer :all]
   [serializable.fn :refer :all]
   [re-cog.common.functions :refer (require-functions require-resources)]
   [re-cog.scripts.common :refer (bind-bash)]
   [re-cog.common.constants :refer (require-constants)]))

(require-functions)
(require-resources)
(require-constants)
(bind-bash)

(comment
  (clojure.pprint/pprint (read-metrics)))
