(ns eaml.graph-test
  (:require [eaml.graph :refer :all]
            [presto.core :refer :all]
            [clojure.test :refer :all]))

(defn attr [name value]
  {:name name :value [:literal value] :config :default})

(defn attrs
  [& name-value-pairs]
  (map (fn [[name value]]
         (attr name value))
       (partition 2 name-value-pairs)))

(defn attrs-eq?
  "presto comparator function. First argument is a node, second argument
  is a list of attr-name attr-value as in attrs.
  Tests that the attrs in node are equivalent to name-value-pairs"
  [node name-value-pairs]
  (= (:attrs node)
     (apply attrs name-value-pairs)))


(def node1
     {:id "1"
      :node :style
      :parents []
      :attrs (attrs "color" "red"
                    "size" "small")})

(def node2
     {:id "2"
      :node :style
      :parents ["1"]
      :attrs (attrs "margins" "big")})

(def node3
     {:id "3"
      :node :style
      :parents ["2"]
      :attrs (attrs "margins" "small"
                    "color" "blue"
                    "paddings" "12dp")})

(def node4
     {:id "4"
      :node :style
      :parents ["2"]
      :attrs (attrs "paddings" "10dp")})

(def node5
     {:id "5"
      :node :style
      :parents ["4" "3"]
      :attrs (attrs "border" "red")})

(def graph (create [node1 node2 node3 node4 node5]))

(expected-when "get-node-by-id returns a node in the graph given its id"
               get-node-by-id
  when ["1" graph] = node1
  when ["2" graph] = node2
  when ["3" graph] = node3
  when ["4" graph] = node4
  when ["foo" graph] = nil)

(expected-when "direct-parents returns a list of nodes that are direct parents of the given node"
               direct-parents
  when [node1 graph] = []
  when [node2 graph] = [node1]
  when [node3 graph] = [node2]
  when [node4 graph] = [node2]
  when [node5 graph] = [node4 node3])

(expected-when "parent-path constructs the full ancestry graph as a list"
               parent-path
  when [node1 graph] = [node1]
  when [node2 graph] = [node2 node1]
  when [node3 graph] = [node3 node2 node1]
  when [node4 graph] = [node4 node2 node1]
  when [node5 graph] = [node5 node4 node3 node2 node1])

(expected-when "solve-inheritance for simple single inheritance" solve-inheritance

  when      [node1 graph]
  attrs-eq? ["color" "red"
             "size" "small"]

  when      [node2 graph]
  attrs-eq? ["margins" "big"
             "color" "red"
             "size" "small"]

  when      [node3 graph]
  attrs-eq? ["margins" "small"
             "color" "blue"
             "paddings" "12dp"
             "size" "small"]

  when      [node5 graph]
  attrs-eq? ["border" "red"
             "paddings" "10dp"
             "margins" "small"
             "color" "blue"
             "size" "small"])

