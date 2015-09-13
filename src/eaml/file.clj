(ns eaml.file
  (:require [clojure.java.io :refer :all]))

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
