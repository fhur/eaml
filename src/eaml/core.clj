(ns eaml.core
  (:gen-class)
  (:require [eaml.compiler :as compiler]
            [eaml.xml :as xml]
            [eaml.util :refer [itp]]
            [eaml.parser :refer [parse-dir]]
            [clojure.string :refer [join]]
            [clojure.tools.cli :refer [parse-opts]]))

(defn eaml
  [in-dir out-dir]
  (println (itp "Transpiling from #{in-dir} to #{out-dir}"))
  (let [ast (parse-dir in-dir)
        config-map (compiler/transpile ast)]
    (doseq [[config xmlstruct] config-map]
      (with-open [writer (xml/config-writer out-dir config)]
        (println (itp "Transpiling done, writing to #{out-dir}"))
        (xml/render-xml xmlstruct writer)))))


(def cli-options
  [["-i" "--in DIR" "Input directory"]
   ["-o" "--out DIR" "Output directory"]
   ["-h" "--help" "Print a help message"]])

(defn exit
  "Print the given msg to stdout and terminate program with the given status code"
  [status msg]
  (println msg)
  (System/exit status))

(defn error-msg
  [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (join \newline errors)))

(defn usage
  [summary]
  (->> ["eaml transpiler"
        ""
        "Reads all .eaml files in the input directory and subdirectories, transpiles and writes XML to the output directory"
        ""
        "Usage:"
        ""
        "eaml [--in DIR] [--out DIR] [--help]"
        ""
        summary
        ""]
       (join \newline)))

(defn -main
  [& args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    ;; Handle help and error conditions
    (cond
      (:help options)
        (exit 0 (usage summary))
      errors
        (exit 1 (error-msg errors))
      (and (contains? options :in) (contains? options :out))
        (eaml (:in options) (:out options))
      :else
        (exit 1 (usage summary)))))
