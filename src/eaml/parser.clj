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
  ([id parent attrs?]
  {:id id
   :node :style
   :parent parent
   :attrs (or-empty attrs?)})
  ([id attrs?]
   (normalize-style id nil attrs?)))

(defn normalize-mixin
  [id attrs?]
  {:id id
   :node :mixin
   :attrs (or-empty attrs?)})


(defn normalize-attr
  [id value]
  {:name id
   :value value
   :config :default})


(defn normalize-config-name
  [config-name]
  (keyword config-name))

(defn normalize-config-block
  [config-names & attrs]
  {:config-block (for [config-name config-names
                       attr attrs]
                   (assoc attr :config config-name))})

(defn normalize-style-attrs
  [& attrs]
  ;; TODO: rewrite this
  (reduce (fn [result attr]
            (if (:config-block attr)
              (into result (:config-block attr))
              (conj result attr)))
          [] attrs))

(defn normalize-mixin-attrs
  [& attrs]
  ;; TODO: rewrite this
  (apply normalize-style-attrs attrs))

(defn normalize-mixin-call
  [mixin-id & args?]
  {:mixin mixin-id
   :config :default
   :args (or-empty args?)})


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
                    :config-names (fn [& args] args)
                    :simple-res-def normalize-simple-res-def
                    :simple-res-configs (fn [& args] args)
                    :simple-res-config normalize-simple-res-config
                    :simple-res-single-config normalize-simple-res-single-config
                    :config-block normalize-config-block
                    :mixin-call normalize-mixin-call
                    :extends-expr (fn [& args] args)
                    :mixin-def normalize-mixin
                    :style-def normalize-style
                    :attrs normalize-style-attrs
                    :mixin-attrs normalize-mixin-attrs
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
