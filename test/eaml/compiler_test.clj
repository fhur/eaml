(ns eaml.compiler-test
  (:require [eaml.compiler :refer :all]
            [eaml.parser :refer [parse-str]]
            [presto.core :refer :all]
            [clojure.test :refer :all]
            [clojure.string :refer [join split-lines]]))

(defn cleanup-xml
  [xml-string]
  (->> (split-lines xml-string)
       (map #(.trim %))
       (join)))


(defn matches?
  [expected actual]
  (= (cleanup-xml expected)
     (cleanup-xml actual)))

(expected-when "test transpile" transpile-str
  when     ["style Foo {
              foo: 12dp;
             }"]
  matches? "<resources>
              <style name=\"Foo\">
                <item name=\"foo\">12dp</item>
              </style>
            </resources>"

  when     ["color red: #f00;
             style Bar {
               foo: red;
            }"]
  matches? "<resources>
              <style name=\"Bar\">
                <item name=\"foo\">#f00</item>
              </style>
            </resources>"

  when     ["color blue: #f00;
             color color_primary: blue;
             dimen text_large: 20sp;

             style Button {
               foo: color_primary;
             }
             style BigButton < Button {
               size: text_large;
             }"]
  matches? "<resources>
              <style name=\"Button\">
                <item name=\"foo\">#f00</item>
              </style>
              <style name=\"BigButton\">
                <item name=\"size\">20sp</item>
                <item name=\"foo\">#f00</item>
              </style>
            </resources>")
