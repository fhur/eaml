(defproject eaml "0.4.0"
  :description "Describe your android styles expressively with eaml,
               an Android XML styles pre-processor."
  :main eaml.core
  :url "http://github.com/fhur/eaml"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins [[lein-cloverage "1.0.6"]]
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [instaparse "1.4.1"]
                 [presto "0.2.0"]
                 [org.clojure/tools.cli "0.3.3"]])


