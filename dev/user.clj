(ns user
  (:require
   [re-share.log :refer (debug-on debug-off)]
   [clojure.java.io :as io]
   [clojure.repl :refer :all]
   [clojure.tools.namespace.repl :refer (refresh refresh-all)]
   [re-gent.core :as core]))

(def system nil)

(defn init
  "Constructs the current development system."
  []
  (alter-var-root #'system (constantly (core/setup))))

(defn start
  "Starts the current development system."
  []
  (alter-var-root #'system core/start))

(defn stop
  "Shuts down and destroys the current development system."
  []
  (alter-var-root #'system core/stop))

(declare go)

(defn go
  "Initializes the current development system and starts it running."
  [& {:keys [watch]}]
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))

(defn clear
  "clean repl"
  []
  (print (str (char 27) "[2J"))
  (print (str (char 27) "[;H")))
