(ns re-gent.log
  "log setup"
  (:require
   [re-share.log :as log]
   [taoensso.timbre :refer (refer-timbre set-level!)]))

(refer-timbre)

(defn setup-logging
  "Sets up logging configuration:
    - steam collect logs
    - log level
  "
  [& {:keys [level] :or {level :info}}]
  (log/setup "re-gent" [] ["re-gent.metrics"])
  (set-level! level))

(defn debug-on []
  (set-level! :debug))

(defn debug-off []
  (set-level! :debug))
