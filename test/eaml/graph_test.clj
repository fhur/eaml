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
      :attrs []})

(def node4
     {:id "4"
      :node :style
      :parents ["2"]
      :attrs []})

(def node5
     {:id "5"
      :node :style
      :parents ["4" "3"]
      :attrs []})

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


