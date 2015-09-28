(ns eaml.xml
  (:require [clojure.string :refer [join]]))

(defn- render-items
  [items]
  (map (fn [item]
         (str "<item name=\"" (:name item) "\">"
              (:value item)
              "</item>"))
       items))

(defn render-style-node
  "Expects a structure with the following form:
   {:name <style name>
    :item [{:name <attr name> :value <attr value>}, ... ]}
  returns the given style node as an xml string"
  [node]
  (str "<style name=\"" (:name node) "\">"
       (->> (render-items (:items node))
            (join "\n"))
       "</style>"))
