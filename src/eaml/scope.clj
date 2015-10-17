(ns eaml.scope
  "This namespace provides the build-scope function whose
  purpose is two build the global scope which will later be used
  by the transpiler to resolve variables and type checking

  attr-value: a tuple where the first element is the type and
  the second element is the value.

  Example: [:literal \"#ff0000\"] => an attr-value for a color
           [:]
  "
  (:require [eaml.error :refer :all]))


(defn scopable?
  "A node is 'scopable' if it is either a color or a dimen"
  [node]
  (#{:color :dimen "color" "dimen"} (:node node)))

(defn get-attr-value
  [node]
  (:value node))


(defn create
  "Create the scope"
  [nodes]
  (reduce (fn [scope node]
            (let [id (:id node)]
              (if (contains? scope id)
                (raise! (str "Scope already contains a mapping for "id": "(scope id)))
                (if (scopable? node)
                  (assoc scope id (get-attr-value node))
                  scope))))
          {} nodes))

(defn- get-id
  "Return the identifier of an attr-value"
  [attr-value]
  (second attr-value))

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
  [scope attr-value]
  (let [[_ id] attr-value
        resolved (get scope id)]
    (if (literal? resolved)
      (get-literal resolved)
      (get-pointer scope resolved))))


(defn has?
  "Returns true if the scope can resolve the given attr-value"
  [scope attr-value]
  (or (literal? attr-value) ;; literals can always be resolved
      (contains? scope (get-id attr-value)))) ;; check if the id is mapped to the scope

(defn obtain
  "Obtain the current mapping for a given attr-value"
  [scope attr-value]
  (cond (literal? attr-value)
          (get-literal attr-value)
        (pointer? attr-value)
          (get-pointer scope attr-value)))


