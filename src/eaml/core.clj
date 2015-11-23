(ns eaml.core
  (:require [eaml.compiler :as compiler]
            [eaml.xml :as xml]
            [eaml.parser :refer [parse-dir]]))

(defn eaml
  [in-dir out-dir]
  (let [ast (parse-dir in-dir)
        config-map (compiler/transpile ast)]
    (doseq [[config xmlstruct] config-map]
      (with-open [writer (xml/config-writer out-dir config)]
        (xml/render-xml xmlstruct writer)))))
