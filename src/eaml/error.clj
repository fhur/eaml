(ns eaml.error
  (:require [clojure.pprint :refer [pprint]]
            [clojure.string :refer [join]]))

(defn- ppr
  [obj]
  (with-out-str (clojure.pprint/pprint obj)))

(defn raise!
  "Throws a RuntimeException with the given message as argument"
  [& messages]
  (throw (new IllegalStateException (join "" (map ppr messages)))))


