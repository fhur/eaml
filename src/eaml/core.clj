(ns eaml.core
  (:require [eaml.compiler :as compiler]
            [eaml.parser :refer [parse-dir]]))

(defn transpile
  "Reads all .eaml files in the root-dir and subdirs and places
  the transpiled result in out-dir.
  out-dir should be your Android project's res/ folder."
  [root-dir out-dir]
  (let [ast (parse-dir root-dir)
        writer (compiler/dir-writer out-dir)]
    (compiler/transpile ast writer)))



