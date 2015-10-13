(ns eaml.util)

(defn conj-all
  "Returns the result of conjoining all elements in coll2 into coll1"
  [coll1 coll2]
  (reduce conj coll1 coll2))

(defn flat-coll
  "Given a coll of collections, flattens it by one level.
  Example: [[1] [2 3] [] [4 5 6]] will result in [1 2 3 4 5 6]"
  [colls]
  (apply concat colls))

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


(defn find-first
  "Iterates over coll and returns the first item such that (func item)
  is truthy. If no item is found then default is returned (defaults to nil)."
  ([func coll default]
   (loop [coll coll]
     (if (empty? coll)
       default
       (if (func (first coll))
         (first coll)
         (recur (rest coll))))))
  ([func coll]
   (find-first func coll nil)))
