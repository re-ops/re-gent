(ns re-gent.zero.management
  "Client registration/processing"
  (:require
   [serializable.fn :as s]
   [clojure.core.match :refer  [match]]
   [taoensso.timbre :refer (refer-timbre)]
   [re-gent.metrics :refer (read-metrics)]
   [re-gent.zero.client :refer (send-)]))

(refer-timbre)

(defn run-fn
  "Run a function from the server"
  [f args name]
  (info "executing" f args)
  (binding [*ns* (find-ns 're-gent.zero.functions)]
    (let [r (apply (eval f) args)]
      (send- {:reply :execute :result r :name name}))))

(defn process
  "process server requests"
  [request]
  (debug "processing..")
  (match [request]
    [{:request :metrics}] (send- {:reply :metrics :content (read-metrics)})
    [{:request :execute :fn f :args args :name name}] (run-fn f args name)
    [{:response :ok :on {:request :register}}] (info "registered successfuly")
    :else (info "no handler found for" request)))

(defn register []
  (send- {:request :register})
  (info "registering"))

(defn unregister []
  (send- {:request :unregister}))
