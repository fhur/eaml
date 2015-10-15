(ns eaml.graph
  "Inheritance in eaml is modeled by an acyclic directed graph.
  G = (N, E) where N is the set of all nodes in the graph and
  E is the set of edges.

  Notation:
  Let a, b in N and [a,b] in E. This can also be expressed
  as 'a < b'. As the graph is acyclic this does not imply that
  'b < a'. We can also write a < b,c,d as a shorthand for
  a < b and a < c and a < d

  Example 1:
  let N = (BigButton, RedButton, Button, BigRedButton) and
  - BigButton < Button
  - RedButton < Button
  - BigRedButton < BigButton, RedButton

               BigRedButton
                |       |
                V       V
          BigButton    RedButton
                 |      |
                 V      V
                  Button

  Define 'Parent Path: parent-path(node)'
  The parent path of a given node is defined as the subgraph
  that results from a BFS starting in in the node.
  In Ex.1
  - parent-path(Button) = [Button]
  - parent-path(BigButton) = [BigButton Button]
  - parent-path(BigRedButton) = [BigRedButton, BigButton, RedButton, Button]"

  (:require [eaml.util :refer :all]
            [eaml.error :refer :all]
            [eaml.node :as node]))


(defn get-node-by-id
  "Obtain a node in the graph given it's id"
  [id graph]
  (get graph id))


(defn direct-parents
  "Obtain a lazy list that references all the direct parents of a given node
  in the graph"
  [node graph]
  (map #(get-node-by-id % graph) (:parents node)))


(defn parent-path
  "Obtains the parent path given a node in the graph. Read the ns doc to get
  a better understanding of what the parent-path is.

  The resulting parent path is guaranteed to be ordered such that
  a parent path of [a b c] implies a < b, c"
  [node graph]
  (bfs #(direct-parents %1 %2) node graph))


(defn create
  "Create a new graph given a list of nodes"
  [nodes]
  (reduce (fn [graph node]
            (assoc graph (:id node) node))
          {} nodes))

(defn- visited?
  "Used in merge-nodes. A node is considered visited if it is either nil or
  contained in the visited-set"
  [visited-set node]
  (or (nil? node) (visited-set (:name node))))

(defn- add-visited
  [visited-set node]
  (conj visited-set (:name node)))

(def not-visited? (complement visited?))

(defn merge-nodes
  "Merge two nodes' attributes. Preference on node1 whenever there is a conflict."
  [node1 node2]
  (loop [attrs1 (node/attributes node1)
         attrs2 (node/attributes node2)
         merged-attrs []
         visited #{}]
    (if (and (empty? attrs1) (empty? attrs2))
      (assoc node1 :attrs merged-attrs)
      (let [next1 (first attrs1)
            next2 (first attrs2)
            rest1 (rest attrs1)
            rest2 (rest attrs2)]

        (cond (and (visited? visited next1) (visited? visited next2))
                (recur rest1 rest2 merged-attrs visited)

              (and (visited? visited next1) (not-visited? visited next2))
                (recur rest1 rest2 (conj merged-attrs next2) (add-visited visited next2))

              (and (not-visited? visited next1) (visited? visited next2))
                (recur rest1 rest2 (conj merged-attrs next1) (add-visited visited next1))

              (and (not-visited? visited next1) (not-visited? visited next2))
                (recur rest1 attrs2 (conj merged-attrs next1) (add-visited visited next1)))))))


(defn solve-inheritance
  [node graph]
  (->> (parent-path node graph)
       (reduce merge-nodes)))



