(ns eaml.graph-test
  (:require [eaml.graph :refer :all]
            [presto.core :refer :all]
            [clojure.test :refer :all]))

(defn mknode
  [id parents]
  {:id id :parents parents})

(defn mknodes
  [& id-parent-pair]
  (->> (partition 2 id-parent-pair)
       (map #(apply mknode %))))

(def test-graph (mknodes :A [:B :C]
                    :B [:C :D]
                    :C []
                    :D [:F]
                    :F []
                    :G [:D :H]
                    :H []))

(expected-when "test conj-all" conj-all
  when [[1 2 3] [4 5 6]] = [1 2 3 4 5 6]
  when [[] []] = []
  when [[] [1]] = [1]
  when [(subvec [1 2 3 4] 1) [5 6 7 8]] = [2 3 4 5 6 7 8])

(defn test-parent-path
  [node]
  (parent-path node test-graph))

(expected-when "test parent-path" test-parent-path
  when []
