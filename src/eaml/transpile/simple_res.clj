(ns eaml.transpile.simple-res
  (:require [eaml.scope :as scope]))

(defn- mk-simple-res
  [nodetype id value]
  [nodetype {:name id} value])

(defn simple-res?
  "true if the given node id is a color, dimen, bool, integer or string"
  [id]
  (#{:color :dimen :bool :integer :string} id))

(defn transpile-simple-res
  "Given a simple res node, returns a map of config => [node {:name name} value]
  Example:
  {:node :dimen
   :id 'margins'
   :vals [{:config 'tablet' :value '24dp'
           :config 'default' :value '12dp'}]}

  Will return:
  {'default' [:color {:name 'margins'} 12dp]
   'tablet'  [:color {:name 'margins'} 24dp]}"
  [scope simple-res-node]
  (let [{vals :vals id :id nodetype :node} simple-res-node]
    (reduce (fn [config-map {config :config value :value}]
              (let [scoped-value (scope/resolve-expr :any value scope)]
                (assoc config-map config
                       (mk-simple-res nodetype id scoped-value))))
            {} vals)))
