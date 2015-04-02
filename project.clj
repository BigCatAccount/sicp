(defproject com.pechorin/sicp "0.1.0-SNAPSHOT"
  :description "SICP course and book exercises"
  :dependencies [[org.clojure/clojure "1.6.0"]]
  :profiles {:dev {:dependencies [[midje "1.6.3"]]}}
  :test-paths ["test"]
  :source-paths ["src"])