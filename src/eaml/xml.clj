(ns eaml.xml
  (:require [clojure.string :refer [join]]))

(defn style-struct-attr->xml
  [attr]
  (str "<item name=\"" (:name attr) "\">"
       (last (:value attr)) ;; TODO change this
       "</item>"))

(defn style-struct->xml
  [style-struct name-map]
  (str "<style name=\"" (:name style-struct) "\">"
       (->> (map style-struct-attr->xml (:attrs style-struct))
            (join "\n"))
       "</style>"))


