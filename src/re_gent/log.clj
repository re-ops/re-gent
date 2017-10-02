(ns re-gent.log
  "log setup"
  (:require
   [clojure.string :refer (join upper-case)]
   [taoensso.timbre.appenders.3rd-party.rolling :refer (rolling-appender)]
   [taoensso.timbre.appenders.core :refer (println-appender)]
   [clansi.core :refer (style)]
   [taoensso.timbre :refer (refer-timbre set-level! merge-config!)]
   [clojure.core.strint :refer (<<)]
   [clojure.java.io :refer (reader)]))

(refer-timbre)

(def level-color
  {:info :green :debug :blue :error :red :warn :yellow})

(defn output-fn
  "Timbre logger format function"
  ([data] (output-fn nil data))
  ([opts data] ; For partials
   (let [{:keys [level ?err #_vargs msg_ ?ns-str ?file hostname_ timestamp_ ?line]} data]
     (str (style (upper-case (name level)) (level-color level)) " " (force timestamp_) " [" (style ?file :bg-black) "] "  ": " (force msg_)))))

(defn disable-coloring
  "See https://github.com/ptaoussanis/timbre"
  []
  (merge-config!
   {:output-fn (partial output-fn  {:stacktrace-fonts {}})})
  (merge-config!  {:appenders {:println  (merge {:ns-whitelist ["re-gent.metrics"]} (println-appender {:stream :auto}))
                               :rolling (rolling-appender {:path "re-gent.log" :pattern :weekly})}}))

(defn setup-logging
  "Sets up logging configuration:
    - stale logs removale interval
    - steam collect logs
    - log level
  "
  [& {:keys [interval level] :or {interval 10 level :debug}}]
  (disable-coloring)
  (set-level! level))

(defn debug-on []
  (set-level! :debug))

(defn debug-off []
  (set-level! :info))
