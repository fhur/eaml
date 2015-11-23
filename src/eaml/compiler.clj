(ns eaml.compiler
  (:require [eaml.error :refer :all]
            [eaml.util :refer :all]
            [eaml.xml :as xml]
            [eaml.scope :as scope]
            [eaml.node :as node]
            [eaml.transpile.simple-res :refer :all]
            [eaml.transpile.style :refer :all]
            [eaml.parser :as parser]))

(defn- transpile-node
  [scope node]
  (let [type (:node node)]
    (cond (style? type)
            (transpile-style node)
          (simple-res? type)
            (transpile-simple-res scope node)
          :else (raise! "Unknown node type: " node))))

(defn- insert-resources-top-level
  [config-map]
  (loop [result-map config-map
         remaining-keys (keys result-map)]
    (if (empty? remaining-keys)
      result-map
      (let [key (first remaining-keys)
            value (get config-map key)]
        (recur (assoc result-map key (cons* value :resources {}))
               (rest remaining-keys))))))

(defn transpile-str
  [string]
  (let [ast (parser/parse-str string)
        scope (scope/create ast)
        transpile-scoped #(transpile-node scope %)]
    (->> (map transpile-scoped ast)
         (group-maps)
         (insert-resources-top-level))))

(defn transpile
  [ast]
  (let [scope (scope/create ast)
        transpile-scoped #(transpile-node scope %)]
    (->> (map transpile-scoped ast)
         (group-maps)
         (insert-resources-top-level))))

