(ns re-gent.zero.functions
  (:require 
    [me.raynes.fs :refer :all]))


(defmacro with-ns
  "Evaluates body in another namespace. ns is either a namespace
   object or a symbol. This makes it possible to define functions in
   namespaces other than the current one."
  [ns & body]
  `(binding [*ns* (the-ns ~ns)]
     ~@(map (fn [form] `(eval '~form)) body)))
