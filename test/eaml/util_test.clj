(ns eaml.util-test
  (:require [eaml.util :refer :all]
            [presto.core :refer :all]
            [clojure.test :refer :all]))

(defn- gt [arg] #(> % arg))

(expected-when "test find-first" find-first
  when [(gt 0) [-1 -2 -3 9 12] :foo] = 9
  when [(gt 10) [1 2 3 4] :foo ] = :foo
  when [nil? [1 :foo nil :bar] :not-nil] = nil
  when [(gt 0) [] :foo ] = :foo
  when [(gt 0) [-1 -2 -3]] = nil)


(expected-when "test conj-all" conj-all
  when [[1 2 3] [4 5 6]] = [1 2 3 4 5 6]
  when [[] []] = []
  when [[] [1]] = [1]
  when [(subvec [1 2 3 4] 1) [5 6 7 8]] = [2 3 4 5 6 7 8])


(def test-graph
  {:a [:b :c]
   :b []
   :c [:d :e]
   :d [:a :b :c]
   :e [:f]
   :f [:g]})

(expected-when "bfs performs a BFS over a graph"
               (fn [initial-node]
                 (bfs (fn [node graph] (get graph node))
                      initial-node test-graph))
  when [:b] = [:b]
  when [:e] = [:e :f :g]
  when [:a] = [:a :b :c :d :e :f :g])


(expected-when "flat-coll flattens a coll of colls" flat-coll
  when [[]] = []
  when [[[1] [2]]] = [1 2]
  when [[[] [1 2 3] [4 5]]] = [1 2 3 4 5])
