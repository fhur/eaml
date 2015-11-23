(ns eaml.test-helpers
  "Place common helper functions for testing"
  (:require [clojure.string :refer [split-lines join]]
            [eaml.util :refer :all]))

(defn cleanup-xml
  [xml-string]
  (->> (split-lines xml-string)
       (map #(.trim %))
       (join)))


(defn xml=?
  "Tests that two xml strings are semantically equal.
  This does not include new lines, indentation, etc."
  [expected actual]
  (= (cleanup-xml expected)
     (cleanup-xml actual)))

(defmacro simpleres
  [type name value]
  [(keyword type) {:name name} value])

(defmacro resources
  [& nodes]
  (vec (cons* nodes :resources {})))

(defmacro color
  [name value]
  (simpleres :color name value))

(defmacro dimen
  [name value]
  (simpleres :dimen name value))

(defmacro string
  [name value]
  (simpleres :string name value))

(defmacro bool
  [name value]
  (simpleres :bool name value))

