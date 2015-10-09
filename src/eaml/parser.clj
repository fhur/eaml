(ns eaml.parser
  (:require [instaparse.core :as insta]
            [eaml.file :refer :all]))

(def parser (insta/parser (clojure.java.io/resource "grammar.bnf")
                          :auto-whitespace :standard))

(defn parse-dir
  [root-path]
  (->> (filter-tree root-path (extension-filter "eaml"))
       (map read-file)
       (map parser)
       (reduce conj)))
