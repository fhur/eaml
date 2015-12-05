(ns eaml.transpile.style
  (:require [eaml.util :refer :all]
            [eaml.error :refer :all]
            [eaml.scope :refer [resolve-expr]]))

(defn- mk-item
  [scope {name :name value :value}]
  [:item {:name name} (resolve-expr :any value scope)])

(defn- mk-style-res
  [scope id parent? attrs]
  (let [style-attrs {:name id}
        style-attrs (if parent?
                      (assoc style-attrs :parent parent?)
                      style-attrs)
        items (map #(mk-item scope %) attrs)]
    (cons* items :style style-attrs)))

(defn style?
  [id]
  (= :style id))

(defn- mixin-attr?
  [attr]
  (contains-all? attr :mixin :args :config))

(defn regular-attr?
  [attr]
  (contains-all? attr :name :value :config))

(defn- expand-mixin
  [scope mixin]
  (:attrs (get scope (:mixin mixin))))

(defn- expand-mixins
  "Invokes all mixins and replaces them with the mixin's contents. Also removes
  any duplicate attributes that would have resulted by mixin expansion."
  [scope attrs]
  (->> (map (fn [attr]
              (cond (mixin-attr? attr)
                      (expand-mixin scope attr)
                    (regular-attr? attr)
                      [attr]
                    :else (raise! (itp "Unrecognized attr: #{attr}"))))
            attrs)
       (flat-coll)))


(defn transpile-style
  "Return a map of config => style XmlStruct"
  ;; TODO: handle repeated attrs correctly!
  [scope {id :id parent :parent attrs :attrs}]
  (if (empty? attrs)
    ;; if there are no attrs, return an empty style
    (mk-style-res id parent [])
    (let [attrs (expand-mixins scope attrs)
          by-config (group-by :config attrs)
          ;; obtain a list of all configurations
          configs (keys by-config)
          ;; get the 'default' config attrs
          default-attrs (:default by-config)
          ;; merge the default config attrs into other configs.
          ;; Why? default config attrs should be present by default.
          by-config (reduce-kv (fn [by-config config attrs]
                                 (assoc by-config config
                                        (merge-lists default-attrs attrs :name)))
                               {} by-config)]
      ;; iterate over all configs and construct
      ;; a map that assocs a config key to the XmlStruct
      ;; for the attrs that should be rendered in that config
      (reduce (fn [config-map config]
                (assoc config-map config
                       (mk-style-res scope id parent (get by-config config))))
              {} configs))))


