(ns re-gent.facts
  "re-cog facts collector"
  (:require
   [re-share.schedule :refer (watch halt! seconds)]
   [re-cog.facts.datalog :refer (populate)]))

(defn stop
  []
  (halt!))

(defn setup []
  (populate))

(defn start
  "Start facts update schedule"
  []
  (watch :facts (rest (seconds 120)) (fn [] (populate))))
