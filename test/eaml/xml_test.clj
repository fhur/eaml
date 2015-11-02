(ns eaml.xml-test
  (:require [eaml.xml :refer :all]
            [clojure.string :refer [split-lines join]]
            [presto.core :refer :all]
            [clojure.test :refer :all]))

(defn cleanup-xml
  [xml-string]
  (->> (split-lines xml-string)
       (map #(.trim %))
       (join)))


(defn matches?
  [expected actual]
  (= (cleanup-xml expected)
     (cleanup-xml actual)))


(expected-when "test render-style-node" render-style-node
  :when ["Foo" [] nil] matches? "<style name=\"Foo\"></style>"

  :when ["Foo" [] "Bar"] matches? "<style name=\"Foo\" parent=\"Bar\"></style>"

  :when ["Foo" [{:name "foo"
                 :value "bar"}
                {:name "android:textSize"
                 :value "12sp"}] nil] matches?
        "<style name=\"Foo\">
          <item name=\"foo\">bar</item>
          <item name=\"android:textSize\">12sp</item>
        </style>")


