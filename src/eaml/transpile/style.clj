(ns eaml.transpile.style
  (:require [eaml.util :refer :all]))

(defn- mk-item
  [{name :name value :value}]
  [:item {:name name} value])

(defn- mk-style-res
  [id parent? attrs]
  (let [style-attrs {:name id}
        style-attrs (if parent?
                      (assoc style-attrs :parent parent?)
                      style-attrs)
        items (map mk-item attrs)]
    (cons* items :style style-attrs)))

(defn style?
  [id]
  (= :style id))

(defn transpile-style
  [{id :id parent :parent attrs :attrs}]
  (let [by-config (group-by :config attrs)]
    (loop [configs (keys by-config)
           result {}]
      (if (empty? configs)
        result
        (let [config (first configs)
              tail (rest configs)
              attrs (get by-config config)]
          (recur tail (assoc result config
                             (mk-style-res id parent attrs))))))))

