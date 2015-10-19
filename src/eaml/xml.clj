(ns eaml.xml
  (:require [clojure.string :refer [join]]
            [eaml.error :refer :all]
            [eaml.file :refer [path-concat mkdirs!]]))

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

(defn write-str
  [nodes]
  (for [node nodes]
    (render-style-node (:id node) (:attrs node))))

(defn write!
  [dir nodes]
  (let [style-root (path-concat dir "values")
        styles-path (path-concat style-root "styles.xml")]

    (println "Writing some shit to: " style-root "and" styles-path)

    ;; Create any missing directories
    (mkdirs! [style-root])

    (println "Files created, I think" (.mkdirs (java.io.File. style-root)))

    ;; Initialize the writer
    (with-open [writer (clojure.java.io/writer styles-path)]

      ;; Write the header
      (.write writer "<resources>\n")

      ;; Write the body of the file by converting each
      ;; node to its xml representation
      (doseq [{name :id attrs :attrs} nodes]
        (.write writer (render-style-node name attrs)))

      ;; Write the footer
      (.write writer "\n</resources>"))))
