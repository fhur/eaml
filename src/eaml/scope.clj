(ns eaml.scope
  "This namespace provides the build-scope function whose
  purpose is two build the global scope which will later be used
  by the transpiler to resolve variables and type checking"
  (:require [eaml.error :refer :all]))


(defn scopable?
  [node]
  (#{:color :dimen "color" "dimen"} (:node node)))


(defn create
  "Create the scope"
  [nodes]
  (reduce (fn [scope node]
            (let [id (:id node)]
              (if (contains? scope id)
                (raise! (str "Scope already contains a mapping for "id": "(scope id)))
                (if (scopable? node)
                  (assoc scope id node)
                  scope))))
          nodes))


(defn literal?
  [attr-value]
  (#{"literal" :literal} (first attr-value)))

(defn get-literal
  [attr-value]
  (second attr-value))


(defn pointer?
  [attr-value]
  (#{"pointer" :pointer} (first attr-value)))

(defn get-pointer
  [attr-value]
  (get scope (second attr-value)))


(defn has?
  [scope attr-value]
  (or (literal? attr-value)
      (contains? scope id)))

(defn obtain
  "Obtain the current mapping for a given attr-value"
  [scope attr-value]
  (cond (literal? attr-value)
          (get-literal attr-value)
        (pointer? attr-value)
          (get scope id))


