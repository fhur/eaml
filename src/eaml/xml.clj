(ns eaml.xml
  (:require [clojure.string :refer [join]]
            [eaml.error :refer :all]
            [eaml.file :refer [path-concat mkdirs!]])
  (:import [java.io StringWriter]))

(defn- render-items
  [items]
  (map (fn [{name :name value :value}]
         (str "<item name=\"" name "\">"
              value
              "</item>"))
       items))

(defn render-style-node
  "Expects a structure with the following form:
   {:name <style name>
    :item [{:name <attr name> :value <attr value>}, ... ]}
  returns the given style node as an xml string"
  [style-name items parent]
  (str "<style name=\"" style-name "\""
       (if (nil? parent) "" (str " parent=\"" parent "\""))
       ">"
       (->> (render-items items)
            (join "\n"))
       "</style>"))

(defn write-to-writer!
  [nodes writer]
  ;; Initialize the writer
  (with-open [writer writer]

    ;; Write the header
    (.write writer "<resources>\n")

    ;; Write the body of the file by converting each
    ;; node to its xml representation
    (doseq [{name :id attrs :attrs parent :parent} nodes]
      (.write writer (render-style-node name attrs parent)))

    ;; Write the footer
    (.write writer "\n</resources>")))

(defn write-str
  [nodes]
  (let [writer (StringWriter. )]
    (write-to-writer! nodes writer)
    (.toString writer)))

(defn string-writer
  "Initializes a new StringWriter"
  [] (StringWriter. ))

(declare render-xml-node-start
         render-xml-node-body
         render-xml-node-end)

(defn render-xml
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

(defn render-xml-string
  [nodes]
  (let [writer (string-writer)]
    (render-xml nodes writer)
    (.toString writer)))
