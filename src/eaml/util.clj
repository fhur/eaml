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


