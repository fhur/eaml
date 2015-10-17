(ns eaml.scope-test
  (:require [eaml.scope :refer :all]
            [eaml.parser :refer [parse-str]]
            [presto.core :refer :all]
            [clojure.test :refer :all]))

(defn with-id?
  [attr-value id]
  (= (second attr-value) id))

(def test-nodes (parse-str
  "color red1: #f00;
   color red2: #f11;
   color red3: #f22;
   dimen margins_small:  4dp;
   dimen margins_normal: margins_small;
   dimen margins_big:    12dp;"))

(def test-scope (create test-nodes))

(expected-when "obtain gets the value of an attr-val" obtain
  when [test-scope [:literal "foo"]] = "foo"
  when [test-scope [:pointer "red1"]] = "#f00"
  when [test-scope [:pointer "margins_big"]] = "12dp"
  when [test-scope [:pointer "margins_normal"]] = "4dp")


