(ns eaml.compiler-test
  (:require [eaml.compiler :refer :all]
            [presto.core :refer :all]
            [clojure.test :refer :all]))

(defn test-node
  [name configs extends & attr-name-val]
  (let [name-val-pairs (partition 2 attr-name-val)]
    {:name name
     :configs configs
     :extends extends
     :attrs (map (fn [[n v]]
                   {:name n
                    :value ["literal" v]})
                 name-val-pairs)}))

(def test-nodes
  [(test-node "Button" [] [] "textSize" "12sp")
   (test-node "RedButton" [] ["Button"] "textColor" "#f00")
   (test-node "BigButton" [] ["Button"] "padding" "12sp")
   (test-node "BigRedButton" [] ["RedButton" "BigButton"])])


(expected-when "test get-ascendents-branch" get-ascendents-branch
  when [(nth test-nodes 0) test-nodes] = []
  when [(nth test-nodes 1) test-nodes] =
       (nth test-nodes 0))
