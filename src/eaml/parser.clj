(ns eaml.parser
  (:require [instaparse.core :as insta]
            [eaml.file :refer :all]
            [eaml.util :refer :all]
            [eaml.error :refer :all]))

(defn or-empty
  [coll-or-nil]
  (if (nil? coll-or-nil)
    [] coll-or-nil))

(defn normalize-literal
  [literal-value]
  literal-value)

(defn normalize-pointer
  [value]
  value)

(defn normalize-string
  [value]
  value)

(defn normalize-simple-res-def
  [res-type id value]
  {:node (keyword res-type)
   :id id
   :vals value})

(defn normalize-simple-res-single-config
  [value]
  [{:config :default
    :value value}])

(defn normalize-simple-res-config
  [config value]
  {:value value
   :config config})

(defn normalize-style
  ([id parents? attrs?]
  {:id id
   :node :style
   :parent (or-empty parents?)
   :attrs (or-empty attrs?)})
  ([id attrs?]
  {:id id
   :node :style
   :parent nil
   :attrs (or-empty attrs?)}))


(defn normalize-attr
  [id value]
  {:name id
   :value value
   :config :default})


(defn normalize-config-name
  [config-name]
  (keyword config-name))

(defn normalize-nodes
  [ast-nodes]
  (insta/transform {:color-literal normalize-literal
                    :dimen-literal normalize-literal
                    :string-literal normalize-string
                    :native-pointer normalize-literal
                    :boolean-literal normalize-literal
                    :integer-literal normalize-literal
                    :pointer normalize-pointer
                    :config-name normalize-config-name
                    :simple-res-def normalize-simple-res-def
                    :simple-res-configs (fn [& args] args)
                    :simple-res-config normalize-simple-res-config
                    :simple-res-single-config normalize-simple-res-single-config

                    :extends-expr (fn [& args] args)
                    :style-def normalize-style
                    :attrs (fn [& args] args)
                    :attr-def normalize-attr}
                   ast-nodes))

(def parser (insta/parser (clojure.java.io/resource "grammar.bnf")
                          :auto-whitespace :standard))

(defn parse-str
  "Parses and normalizes a string. Raises an error if it is not possible to parse
  the given string."
  [string]
  (let [parsed (parser string)]
    (if (insta/failure? parsed)
      (do (instaparse.failure/pprint-failure (insta/get-failure parsed))
          (raise! "Error while parsing"))
      (normalize-nodes parsed))))


(defn parse-dir
  [root-path]
  (->> (filter-tree root-path (extension-filter "eaml"))
       (map read-file)
       (map parse-str)
       (flat-coll)))
