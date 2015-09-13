(ns eaml.nodes)

(defn find-first
  ([func coll default]
  (loop [coll coll]
    (if (empty? coll)
      default
      (if (func (first coll))
        (first coll)
        (recur (rest coll))))))
  ([func coll]
   (find-first func coll nil)))

(defn is-node-name?
  [node node-name]
  (and (coll? node)
       (= node-name (first node))))

(defmacro def-is-nodes
  "Defines {node name}-node? functions using the
  is-node-name? function"
  [& name-kws]
  (cons `do
        (for [kw name-kws]
          `(defn ~(symbol (str (name kw) "-node?"))
             [node#]
             (is-node-name? node# ~kw)))))

(def-is-nodes :color-def :configs :identifier
  :style-def :res-pointer :literal :opts
  :invocation :exprs :assignment)

(defn get-configs
  [nodes]
  (rest (find-first configs-node? nodes [])))

(defn get-id
  [nodes]
  (last (find-first identifier-node? nodes)))

(defn get-color-val
  [nodes]
  (last (find-first res-pointer-node? nodes)))

(defn get-opts
  [nodes]
  (rest (find-first opts-node? nodes [])))

(defn get-style-body
  [nodes]
  (rest (find-first exprs-node? nodes [])))

