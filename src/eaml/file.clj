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

