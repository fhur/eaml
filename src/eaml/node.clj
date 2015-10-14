(ns eaml.node
  "Nodes
  This namespace describes the Node datastructure and variants

  Expression
  An expression is a tuple [type value]
  where type can be one of 'literal', 'pointer' or 'exp'. Currently only
  'literal' is supported.
  Example:
  - ['literal' '#ff0000']
  - ['pointer' 'some-other-resource-id']
  - ['exp' 'lighter(10%, some-other-resource-id)']

  Attr
  name <String> : the name of the attribute
  value <Expression>: : the value of the attribute
  config <String> : the list of all configurations where the attribute
                  will be applied to. :default implies the attribute
                  applies to the :default configuration.
                  Every attribute has only one configuration.

  Style Node
  id <String>: the node's id (i.e. 'RedButton')
  node 'style': the literal 'style' string.
  parents <list of id>: the list of node ids from which this node extends.
  attrs <list of Attr>: a list of attributes that describes this node.

  Example:
  {:id 'RedButton'
   :node 'style'
   :parents ['Button']
   :attrs [{:name 'android:textColor'
            :value ['literal' '#f00']}
            :config :default]}

  Color, dimen Node
  id <String>: the color's id
  node <'color' or 'dimen'>: the type of node
  value <Expression>: an expression that represents the node's value

  Example:
  {:id 'primary_color'
   :node 'color'
   :value ['literal' '#f00']}"

  (:require [eaml.error :refer :all]
            [eaml.util :refer :all]))


(defn is-attr-in-config
  "Returns true if the given attribute belongs to the given config"
  [attr config]
  (= (:config attr) config))


(defn attributes
  "The attributes of given node."
  [node]
    (:attrs node))


(defn style?
  "True if the given node is a style node"
  [node]
  (or (= (:node node) "style")
      (= (:node node) :style)))


(defn filter-styles
  "A filtered list of only style nodes"
  [nodes]
  (filter style? nodes))
