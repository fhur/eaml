(ns eaml.util-test
  (:require [eaml.util :refer :all]
            [presto.core :refer :all]
            [clojure.test :refer :all]))

(defn weak=
  [expected actual]
  (= (set expected) (set actual)))

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


(expected-when "group-maps joins values with the same key over different maps" group-maps
  when [[{:foo "foozie"}
         {:bar "bazie"
          :qux ["a" "list"]}
         {:foo "fun"
          :qux "quxie"}]]
     = {:foo ["foozie" "fun"]
        :bar ["bazie"]
        :qux [["a" "list"] "quxie"]})

(expected-when "cons* test" cons*
  when [[] 1 2] = [1 2]
  when [[3 4] 1 2] = [1 2 3 4]
  when [(list 3 4) 1 2] = [1 2 3 4]
  when [(seq [3 4]) 1 2] = [1 2 3 4]
  when [[:foo :bar] :baz] = [:baz :foo :bar])

(expected-when "case-match test" case-match
  when ["foobar" #"\Afoo" :foo] = :foo
  when ["foobar" #"\Abar" :bar] = nil
  when ["foobar" #"\Afoo" :foo
                 #"\Afoobar" :foobar] = :foo)

(expected-when "merge-lists test" merge-lists
  when [[] [1 2 3] identity] weak= [1 2 3]
  when [[1 2 3] [] identity] weak= [1 2 3]
  when [[] [] identity] = []
  when [[1 2 3] [3 4 5] identity] weak= [1 2 3 4 5]
  when  [[{:a 1 :b 2} {:a 2 :b 3} {:foo :bar}]
         [{:a 1 :b 3} {:a 2 :b 6} {:a 4 :b 5}] :a]
  weak= [{:a 1 :b 3} {:a 2 :b 6} {:a 4 :b 5} {:foo :bar}])

(expected-when "find-first finds the first element that matches a predicate" find-first
  when [[1 2 3 4 5] #(> % 5)] = nil
  when [[1 2 3 4 5] #(> % 3)] = 4
  when [[] (fn [x] true)] = nil) 
