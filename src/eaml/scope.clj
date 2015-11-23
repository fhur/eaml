(ns eaml.scope
  "This namespace provides the build-scope function whose
  purpose is two build the global scope which will later be used
  by the transpiler to resolve variables and type checking

  attr-value: a tuple where the first element is the type and
  the second element is the value.

  Example: [:literal \"#ff0000\"] => an attr-value for a color
           [:]
  "
  (:require [eaml.error :refer :all]
            [eaml.util :refer :all]))

(defn literal?
  [parsed]
  (#{:color :dimen :bool :integer :string} parsed))

(defn pointer?
  [parsed]
  (= parsed :pointer))

(defn native-res?
  [parsed]
  (= parsed :native-pointer))

(defn remove-quotes
  [string]
  (.replaceAll string "[\"']" ""))

(defn parse-expr
  [string]
  (case-match string
    #"\A@.*?/.*"          :native-pointer
    #"\A#[a-fA-F\d]{3}"   :color
    #"\A#[a-fA-F\d]{6}"   :color
    #"\A#[a-fA-F\d]{8}"   :color
    #"\A\d+(sp|px|dp)"    :dimen
    #"\Atrue|false"       :bool
    #"\A\d+"              :integer
    #"\A\".*?\""          :string
    #"\A'.*?'"            :string
    #"\A[a-zA-Z]\w*"      :pointer))

(defn create
  "Create the scope"
  [nodes]
  (reduce (fn [scope node]
            (let [id (:id node)]
              (if (contains? scope id)
                (raise! (itp "Scope already contains a mapping for #{id} => #{node}: #{(scope id)}"))
                (assoc scope id node))))
          {} nodes))

(defn obtain-type
  [expr scope]
  (let [parsed (parse-expr expr)]
    (cond (literal? parsed)
            parsed
          (native-res? parsed)
            (->> (re-find #"@(.*)/" parsed)
                 (last)
                 (keyword))
          (pointer? parsed)
            (if (contains? scope expr)
              (:node (get scope expr))
              (raise! (itp "No mapping found for '#{expr}' in scope")))
          :else
            (raise! (itp "Unkown parse result: '#{parsed}'")))))

(defn resolve-expr
  "Obtains the value for the given expr in the scope.
  If the resolved type is different from the expected type
  then return nil.
  If the given expr is not present in the scope, throw an exception.
  You may use :any as type to match any type"
  [type expr scope]
  (let [parsed (parse-expr expr)]
    (cond (or (literal? parsed) (native-res? parsed))
            (if (= :string parsed)
              (remove-quotes expr)
              expr)
          (pointer? parsed)
            (let [resolved-type (obtain-type expr scope)]
              (if (or (= type :any) (= type resolved-type))
                (case resolved-type
                  :dimen          (itp "@dimen/#{expr}")
                  :color          (itp "@color/#{expr}")
                  :bool           (itp "@bool/#{expr}")
                  :integer        (itp "@integer/#{expr}")
                  :string         (itp "@string/#{expr}"))
                (raise! "Type mismatch: expected #{type} but got #{resolved-type}"))))))

