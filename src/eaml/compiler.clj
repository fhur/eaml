(ns eaml.compiler
  (:require [eaml.error :refer :all]
            [eaml.util :refer :all]
            [eaml.xml :refer xml]
            [eaml.scope :as scope]
            [eaml.nodes :as nodes]
            [eaml.graph :as graph]
            [eaml.parser :as parser]))


(defn resolve-scope
  [scope node]
  (update-in node :attrs
             (fn [attrs]
               (replace (fn [attr]
                          (update-in attr :value #(obtain scope %))
                          attrs)))))


(defn create-writer-specs
  [graph scope style-nodes]
  (map (fn [node]
         (->> (graph/solve-inheritance node graph)
              (resolve-scope scope)))))



(defn transpile
  "Transpiling high level overview

  Transpiling works as follows:
  input: an abstract syntax tree (AST)
  The AST is actually a list of parser nodes, some of them
  will be colors, dimens, styles, etc.
  1. Validate and obtain a list of nodes conformant
     with the eaml.node specs. (nodes/normalize-ast)
  2. Generate and validate the scope. (scope/create)
  3. Generate and validate the inheritance graph (graph/create)
  4. Loop through all nodes and obtain a WriterSpec
     (the writer spec is a simple datastructure which
     tells the transpiler where the file should be written
     to and describes the contents of the final xml.
  5. Pass the full list of WriterSpec to the xml ns which
     will then generate and write the appropriate xml
     at the appropriate file location"
  [ast]
  (let [nodes (node/normalize-ast ast)
        style-nodes (node/filter-styles nodes)
        scope (scope/create nodes)
        graph (graph/create style-nodes)
        writer-specs (create-writer-specs graph scope style-nodes)]
    (xml/write writer-specs)))

