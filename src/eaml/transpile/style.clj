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
  "Return a map of config => style XmlStruct"
  [{id :id parent :parent attrs :attrs}]
  (if (empty? attrs)
    ;; if there are no attrs, return an empty style
    (mk-style-res id parent [])
    (let [by-config (group-by :config attrs)
          ;; obtain a list of all configurations
          configs (keys by-config)
          ;; get the 'default' config attrs
          default-attrs (:default by-config)
          _ (println default-attrs)
          ;; merge the default config attrs into other configs.
          ;; Why? default config attrs should be present by default.
          by-config (reduce-kv (fn [by-config config attrs]
                                 (assoc by-config config
                                        (merge-lists default-attrs attrs :name)))
                               {} by-config)]
      ;; iterate over all configs and construct
      ;; a map that assocs a config key to the XmlStruct
      ;; for the attrs that should be rendered in that config
      (loop [config-map {}
             configs configs]
        (if (empty? configs)
          config-map
          (let [config (first configs)
                current-attrs (get by-config config)]
            (recur (assoc config-map config
                          (mk-style-res id parent current-attrs))
                   (rest configs))))))))



