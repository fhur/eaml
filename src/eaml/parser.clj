(ns eaml.parser
  (:require [instaparse.core :as insta]
            [eaml.file :refer :all]
            [eaml.util :refer :all]
            [eaml.error :refer :all]))

(def parser (insta/parser (clojure.java.io/resource "grammar.bnf")
                          :auto-whitespace :standard))

(defn parse-dir
  [root-path]
  (->> (filter-tree root-path (extension-filter "eaml"))
       (map read-file)
       (map parser)
       (reduce conj)))


(defn parse-attr-value
  [attr]
  (let [[attr-type attr-value] attr]
    (vector (cond (#{:color-literal :dimen-literal
                     :native-pointer :string-literal} attr-type)
                    :literal
                  (#{:pointer} attr-type)
                    :pointer
                  :else (raise! "Unknown attr type " attr-type " in " attr))
            (cond (= :string-literal attr-type)
                    (.replaceAll attr-value "\"" "")
                  :else
                    attr-value))))


(defn parse-color-or-dimen
  [ast-node]
  (let [[_ node-type id attr] ast-node]
    {:node (keyword node-type)
     :id id
     :value (parse-attr-value attr)}))


(defn parse-parents
  [ast-node]
  (rest (find-first (fn [node]
                      (and (coll? node) (= :extends-expr (first node))))
                    ast-node [])))

[:attr-def "android:background" [:attr-value [:string-literal "\"@drawable/foo_drawable\""]]]
(defn parse-attr
  ([attr config]
   (if (= (first attr) :attr-def)
     (let [[_ attr-name [_ attr-value]] attr]
       {:name attr-name
        :value (parse-attr-value attr-value)
        :config config})
     (raise! "Unknown attr " attr)))
  ([attr]
   (parse-attr attr :default)))



(defn parse-attrs
  [ast-node]
  (->> (find-first #(and (coll? %) (= :attrs (first %))) ast-node [])
       (rest)
       (map parse-attr)))



(defn parse-style
  [ast-node]
  (let [[_ _ id & rst] ast-node]
    {:node :style
     :id id
     :parents (parse-parents ast-node)
     :attrs (parse-attrs ast-node)}))


(defn normalize-node
  [ast-node]
  (let [[_ ast-node-type id & other] ast-node]
    (cond (= ast-node-type "style")
            (parse-style ast-node)
          (#{"color" "dimen"} ast-node-type)
            (parse-color-or-dimen ast-node)
          :else
            (raise! "Unrecognized node type: '" ast-node-type "' in " ast-node))))

(defn normalize-nodes
  [ast-nodes]
  (for [ast-node ast-nodes]
    (normalize-node ast-node)))
