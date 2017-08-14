(ns re-gent.zero.management
  "Client registration/processing"
  (:require
   [re-gent.zero.functions]
   [serializable.fn :as s]
   [clojure.core.match :refer  [match]]
   [taoensso.timbre :refer (refer-timbre)]
   [re-gent.zero.client :refer (send-)]))

(refer-timbre)

(defn run-fn
  "Run a function from the server"
  [f args name uuid]
  (info "executing" f args)
  (binding [*ns* (find-ns 're-gent.zero.functions)]
    (try
      (let [r (apply (eval f) args)]
        (send- {:reply :execute :result r :name name :uuid uuid}))
      (catch Throwable e
        (send- {:reply :execute :result :failed :name name :uuid uuid :message (.getMessage e)})
        (error "failed to call f" e)))))

(defn process
  "process server requests"
  [request]
  (debug "processing..")
  (match [request]
    [{:request :execute :fn f :args args :name name :uuid uuid}] (run-fn f args name uuid)
    [{:response :ok :on {:request :register}}] (info "registered successfuly")
    :else (info "no handler found for" request)))

(defn register []
  (send- {:request :register})
  (info "registering"))

(defn unregister []
  (send- {:request :unregister}))
