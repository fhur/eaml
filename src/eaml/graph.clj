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

  (:require [eaml.util :refer :all]))




(defn bfs
  "Perform a BFS in the given graph.
  (next-nodes-fn node graph) => takes a node as input and returns all nodes connected to that
  node in the graph."
  [next-nodes-fn initial-node graph]
  (loop [queue [initial-node]
         visited #{}
         result []]
    (if (empty? queue)
      result
      (let [node (first queue)
            queue-tail (subvec queue 1)]
        (if (visited node)
          (recur queue-tail visited result)
          (recur (conj-all queue-tail (next-nodes-fn node graph))
                 (conj visited node)
                 (conj result node)))))))


(defn get-node-by-id
  "Obtain a node in the graph given it's id"
  [id graph]
  (get graph id))


(defn direct-parents
  "Obtain a lazy list that references all the direct parents of a given node
  in the graph"
  [node graph]
  (map get-node-by-id (:parents node)))


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
          {}))

(defn configurations
  "Obtain a list of all configurations supported by a given node"
  [node]
  (vec (recur (fn [configs attr]
                (conj configs (:config attr)))
              #{})))


(defn merge-nodes
  "Merge two nodes' attributes. Preference on node1 whenever there is a conflict."
  [node1 node2]
  (loop [attrs1 (attributes node1)
         attrs2 (attributes node2)
         merged-attrs []
         visited #{}]
    (if (and (empty? attrs1) (empty? attrs2))
      (assoc node1 :attrs merged)
      (let [next1 (first attrs1)
            next2 (first attrs2)
            name1 (:name next1)
            name2 (:name next2)
            rest1 (rest attrs1)
            rest2 (rest attrs2)]

        (cond (and (visited name1) (visited name2))
                (recur rest1 rest2 merged-attrs visited)

              (and (visited name1) (not (visited name2)))
                (recur rest1 rest2 (conj merged-attrs next2) (conj visited name2))

              (and (not (visited name1) (visited name2)))
                (recur rest1 rest2 (conj merged-attrs next1) (conj visited name1))

              (and (not (visited name1) (not (visited name2))))
                (recur rest1 attrs2 (conj merged-attrs next1) (conj visited name1)))))))


(defn solve-inheritance
  [node graph]
  (->> (parent-path node graph)
       (reduce merge-nodes)))


#_(

style RedButton < Button {
  android:textColor: "#f00"
  android:background: "btn_red"
  config:[v21] {
    android:background: "btn_red_ripple"
  }
}

{:node "style"
 :id "Button"
 :parents []
 :attrs [{:name "android:textSize" :value "12sp" :config :default}]}

{:node "style"
 :id "RedButton"
 :parents "Button"
 :attrs [{:name "android:textColor" :value "#f00" :config :default}
         {:name "android:textColor" :value "#f00" :config "v21"}
         {:name "android:background" :value "btn_red" :config :default}
         {:name "android:background" :value "btn_red_ripple" :config "v21"}]}

{:id "RedButton"
 :config :default
 :attrs [{:name "android:textSize" :value "12sp"}
         {:name "android:textColor" :value "#f00"}
         {:name "android:background" :value "btn_red"}]}

{:id "RedButton"
 :config "v21"
 :attrs [{:name "android:textSize" :value "12sp"}
         {:name "android:textColor" :value "#f00"}
         {:name "android:background" :value "btn_red_ripple"}]}
)
