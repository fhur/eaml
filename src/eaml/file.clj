(ns eaml.file
  (:require [clojure.java.io :refer :all])
  (:import [java.io File]))

(defn read-file
  "Reads the file located at file-path and returns the contents as a string"
  [file-path]
  (with-open [reader (clojure.java.io/reader file-path)]
    (loop [result ""
           line (.readLine reader)]
      (if (nil? line)
        result
        (recur (str result line "\n")
               (.readLine reader))))))


(defn extension-filter
  "Creates an anonymous function which takes a file as argument and returns
  true if the file has the given extension.
  Example: (extension-filter \"clj\") will match clojure files."
  [ext]
  (fn [file]
    (let [file-name (.getName file)
          match (re-find #".+\.(.+)" file-name)]
      (if (nil? match)
        false
        (= ext (last match))))))


(defn filter-tree
  "Reads a file tree recursively and returns a list of files that match the
  given filter function f. To match by extension see the extension-filter
  function."
  [root-dir-path f]
  (loop [files [(File. root-dir-path)]
         result []]
    (if (empty? files)
      result
      (let [file (first files)]
        (if (.isDirectory file)
          (recur (into (rest files) (.listFiles file)) result)
          (recur (rest files) (if (f file) (conj result file) result)))))))


(defn mkdirs!
  "Given a list of files or paths, creates any missing directories for every
  file or file-path."
  [file-paths]
  (for [file-path file-paths]
    (let [file (as-file file-path)]
      (println "Making dirs for " file)
      (.mkdirs file))))

(defn path-concat
  "Concatenates to file paths, the first one being a parent
  of the second one."
  [path1 path2]
  (let [clean-path1 (str (File. path1))
        clean-path2 (.replaceAll (str (File. path2)) "\\./" "")
        joined-path (str clean-path1 "/" clean-path2)]
    (str (File. joined-path))))

