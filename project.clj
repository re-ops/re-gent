(defproject re-gent "0.4.4"
  :description "A distributed agent for running remote Clojure functions using ZeroMQ curve sockets"
  :url "https://github.com/re-ops/re-gent"
  :license  {:name "Apache License, Version 2.0" :url "http://www.apache.org/licenses/LICENSE-2.0.html"}
  :dependencies [
     [org.clojure/clojure "1.9.0"]

     [org.clojure/core.incubator "0.1.4"]
     [me.raynes/conch "0.8.0"]
     [org.clojure/core.async "0.3.443"]
     [narkisr/cliopatra "1.1.0"]
     [narkisr/clansi "1.2.0"]
     [com.rpl/specter "1.0.3"]

     ; logging
     [com.taoensso/timbre "4.10.0"]
     [com.fzakaria/slf4j-timbre "0.3.7"]
     [org.clojure/tools.trace "0.7.9"]

     ; repl
     [io.aviso/pretty "0.1.34"]
     [serializable-fn "1.1.4"]
     [org.clojure/tools.namespace "0.2.11"]

     ; configuration
     [clojure-future-spec "1.9.0-alpha15"]
     [formation "0.2.0"]

     ; zeromq
     [org.zeromq/jeromq "0.4.2"]

     ; serialization
     [com.taoensso/nippy "2.13.0"]
     [org.clojure/data.codec "0.1.0"]
     [cheshire "5.8.0"]

     ; processing
     [org.clojure/core.match "0.3.0-alpha4"]

     ; provisioning
     [me.raynes/fs "1.4.6"]

     ; common utilities and shared functions
     [re-share "0.6.6"]
     [re-scan "0.2.1"]
   ]

   :plugins [
     [jonase/eastwood "0.2.4"]
     [lein-cljfmt "0.5.6"]
     [lein-ancient "0.6.7" :exclusions [org.clojure/clojure]]
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

   :repositories  {"bintray"  "https://dl.bintray.com/content/narkisr/narkisr-jars"}

   :repl-options {
     :init-ns user
     :prompt (fn [ns] (str "\u001B[35m[\u001B[34m" ns "\u001B[35m]\u001B[33mλ:\u001B[m " ))
     :welcome (println "Welcome to re-gent!" )
    }

  :aliases {
     "travis" [
        "do" "clean," "compile," "cljfmt" "check," "eastwood" "{:exclude-namespaces [re-gent.zero.loop]}"
     ]
   }


   :main re-gent.core
)
