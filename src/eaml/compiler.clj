(ns eaml.compiler
  (:require [eaml.error :refer :all]
            [eaml.util :refer :all]
            [eaml.xml :as xml]
            [eaml.scope :as scope]
            [eaml.node :as node]
            [eaml.graph :as graph]
            [eaml.parser :as parser]))

(defn- resolve-scope
  [scope node]
  (update-in node [:attrs]
             (fn [attrs]
               (map (fn [attr]
                      (update-in attr [:value] #(scope/obtain scope %)))
                    attrs))))


(defn- create-writer-specs
  [scope style-nodes]
  (map #(resolve-scope scope %) style-nodes))


(defn transpile
  "Transpiling high level overview

  Transpiling works as follows:
  input: an abstract syntax tree (AST)
  The AST is actually a list of parser nodes, some of them
  will be colors, dimens, styles, etc.
  1. Node structure is documented in eaml.nodes
  2. Generate and validate the scope. (scope/create)
  3. Generate and validate the inheritance graph (graph/create)
  4. Solve the inheritance graph for each node
  5. Solve the scope for each node in the graph.

  ast => a list of nodes
  writer-fn => a function which takes a list of nodes
  as argument and writes them. See dir-writer for an example
  implementation."
  [ast writer-fn]
  (let [nodes ast
        style-nodes (node/filter-styles nodes)
        scope (scope/create nodes)
        writer-specs (create-writer-specs scope style-nodes)]
    (writer-fn writer-specs)))

(defn dir-writer
  [dir]
  (fn [nodes]
    (xml/write-to-file! dir nodes)))

(defn str-writer
  []
  (fn [nodes] (xml/write-str nodes)))

(defn transpile-str
  [string]
  (transpile (parser/parse-str string)
             (str-writer)))


