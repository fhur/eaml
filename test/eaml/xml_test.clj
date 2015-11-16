(ns eaml.xml-test
  (:require [eaml.xml :refer :all]
            [eaml.test-helpers :refer :all]
            [presto.core :refer :all]
            [clojure.test :refer :all]))

(expected-when "test render-xml" render-xml-string

  when [[:foo]]
  xml=? "<foo ></foo>"

  when [[:foo {:name "bar" :age "12"} "a string value"]]
  xml=? "<foo age=\"12\" name=\"bar\">
          a string value
        </foo>"

  when  [[:nested {:foo "bar"}
          [:first {:name "first"} "first-val"]
          [:second {:name "second"} "second-val"]
          [:third {:name "third"} "third-val"]]]
  xml=? "<nested foo=\"bar\">
          <first name=\"first\">first-val</first>
          <second name=\"second\">second-val</second>
          <third name=\"third\">third-val</third>
        </nested>")



