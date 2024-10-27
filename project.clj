(defproject re-gent "0.6.5"
  :description "A distributed agent for running remote Clojure functions using ZeroMQ curve sockets"
  :url "https://github.com/re-ops/re-gent"
  :license  {:name "Apache License, Version 2.0" :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [
     [org.clojure/clojure "1.10.1"]

     [org.clojure/core.incubator "0.1.4"]
     [me.raynes/conch "0.8.0"]
     [com.rpl/specter "1.1.3"]

     ; logging
     [com.taoensso/timbre "5.1.0"]
     [com.fzakaria/slf4j-timbre "0.3.20"]

     ; repl
     [serializable-fn "1.1.4"]
     [org.clojure/tools.namespace "1.1.0"]

     ; zeromq
     [org.zeromq/jeromq "0.5.2"]

     ; serialization
     [com.taoensso/nippy "2.14.0"]
     [org.clojure/data.codec "0.1.1"]
     [cheshire "5.10.0"]

     ; processing
     [org.clojure/core.match "1.0.0"]

     ; provisioning
     [me.raynes/fs "1.4.6"]

     ; checksumming
     [digest "1.4.10"]

     ; common utilities and shared functions
     [re-share "0.18.0"]
     [re-cog "0.6.7"]
     [re-scan "0.2.1"]
   ]

   :plugins [
     [lein-cljfmt "0.5.6"]
     [lein-ancient "0.6.15" :exclusions [org.clojure/clojure]]
     [lein-tag "0.1.0"]
     [lein-set-version "0.3.0"]]

   :profiles {
     :dev {
       :source-paths  ["dev"]
       :set-version {
         :updates [
            {:path "src/re_gent/core.clj" :search-regex #"\"\d+\.\d+\.\d+\""}
            {:path "bin/binary.sh" :search-regex #"\d+\.\d+\.\d+"}
            {:path "README.md" :search-regex #"\d+\.\d+\.\d+"}
          ]}

     }

    :aot [re-gent.core]
    }

   :resource-paths  ["resources"]

   :jvm-opts ^:replace ["-Djava.library.path=/usr/lib:/usr/local/lib"]

   :repl-options {
     :init-ns user
     :prompt (fn [ns] (str "\u001B[35m[\u001B[34m" ns "\u001B[35m]\u001B[33mλ:\u001B[m " ))
     :welcome (println "Welcome to re-gent!" )
    }

  :aliases {
     "travis" [
        "do" "clean," "compile," "cljfmt" "check"
     ]
   }


   :main re-gent.core
)
