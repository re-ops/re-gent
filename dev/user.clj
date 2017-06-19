(ns user
  (:require
    [clojure.java.io :as io]
    [clojure.repl :refer :all]
    [clojure.tools.namespace.repl :refer (refresh refresh-all)]
    [re-gent.core :refer :all])
  )

(def system nil)

(defn init
  "Constructs the current development system."
  []
  #_(alter-var-root #'system (constantly (launch/setup))))

(defn start
  "Starts the current development system."
  []
  #_(alter-var-root #'system launch/start))

(defn stop
  "Shuts down and destroys the current development system."
  []
  #_(alter-var-root #'system launch/stop))

(declare go)

(defn go
  "Initializes the current development system and starts it running."
  [& {:keys [watch]}]
  (init)
  (start))

(defn reset []
  (stop)
  (refresh :after 'user/go))


(defn clear []
 "clean repl"
 (print (str (char 27) "[2J"))
 (print (str (char 27) "[;H")))
