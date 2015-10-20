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
  [style-name items]
  (str "<style name=\"" style-name "\">"
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
    (doseq [{name :id attrs :attrs} nodes]
      (.write writer (render-style-node name attrs)))

    ;; Write the footer
    (.write writer "\n</resources>")))

(defn write-str
  [nodes]
  (let [writer (StringWriter. )]
    (write-to-writer! nodes writer)
    (.toString writer)))

(defn write-to-file!
  [dir nodes writer]
  (let [style-root (path-concat dir "values")
        styles-path (path-concat style-root "styles.xml")
        _ (.mkdirs style-root)
        writer (clojure.java.io/writer styles-path)]
    (write-to-writer! nodes writer)))


