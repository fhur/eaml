(ns eaml.xml
  (:require [clojure.string :refer [join]]
            [eaml.util :refer :all]
            [eaml.error :refer :all]
            [clojure.java.io :refer [writer]]
            [eaml.file :refer [path-concat mkdirs!]])
  (:import [java.io StringWriter]))

(declare render-xml-node-start
         render-xml-node-body
         render-xml-node-end)

(defn string-writer
  "Initializes a new StringWriter"
  [] (StringWriter. ))

(defn config-writer
  [root config]
  (let [config-name (name config)
        file-path (itp "#{root}/#{config-name}/values.xml")]
    (mkdirs! (itp "#{root}/#{config-name}"))
    (writer file-path)))

(defn render-xml
  "Writes a tree of nodes to the given writer.
  node must be in the following form:
  [:node {:arg1 'val1' :arg2 'val2' ...}
   [:subnode1 {:arg1 'val1'} ...]
   [:subnode2 {:arg1 'val1'} ...]
   ...]

  Note: this function does not handle closing of the writer.
  You must do this yourself.

  Example:
  [:resources {}
    [:style {name 'Button' parent 'BaseButton'}
     [:item {name 'android:textSize'} '12sp']]]

  Will write
  <resources>
    <style name='Button' parent='BaseButton'>
      <item name='android:textSize'>12sp</item>
    </style>
  </resources>"
  [node writer]
  (let [writer writer]
    (loop [node-queue (list node)]
      (if (empty? node-queue)
        writer
        (let [head (first node-queue)
              tail (rest node-queue)]
          (render-xml-node-start head writer)
          (render-xml-node-body head writer)
          (render-xml-node-end head writer)
          (recur tail))))))

(defn render-xml-string
  "Writes a tree of nodes to a string and returns that string.
  Use this method only for debbuging.
  Equivalent to render-xml using string-writer as a writer"
  [nodes]
  (let [writer (string-writer)]
    (render-xml nodes writer)
    (.toString writer)))

(defn- render-xml-node-start
  [[node-type attrs & subnodes] writer]
  (.write writer "<")
  (.write writer (name node-type))
  (.write writer " ")
  (.write writer (->> (map (fn [[k v]]
                             (str (name k) "=\"" v \"))
                           attrs)
                      (join " ")))
  (.write writer ">"))

(defn- render-xml-node-body
  [[node-type attrs & subnodes] writer]
  (when (seq subnodes)
    (doseq [node subnodes]
      (if (coll? node)
        (render-xml node writer)
        (.write writer node)))))

(defn- render-xml-node-end
  [[node-type & _] writer]
  (.write writer "</")
  (.write writer (name node-type))
  (.write writer ">"))

