(ns eaml.xml-test
  (:require [eaml.xml :refer :all]
            [eaml.file :refer [read-file]]
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
          [:second {:name "second"} [:foo {:hi "ho"}]]
          [:third {:name "third"} "third-val"]]]
  xml=? "<nested foo=\"bar\">
          <first name=\"first\">first-val</first>
          <second name=\"second\">
            <foo hi=\"ho\"></foo>
          </second>
          <third name=\"third\">third-val</third>
        </nested>"

  when  [[:a {:x "y"}
          [:b {:z "w"}
           [:c {:foo "bar"}]]]]
  xml=? "<a x=\"y\">
          <b z=\"w\">
            <c foo=\"bar\"></c>
          </b>
         </a>")

(deftest test-config-writer
  (let [text (str (rand-int 100) "text")]
    (with-open [writer (config-writer "tmp" :foo)]
      (.write writer text))
    (is (= (str text "\n")
           (read-file "tmp/values-foo/values.xml")))))
