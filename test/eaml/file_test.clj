(ns eaml.file-test
  (:require [eaml.file :refer :all]
            [clojure.java.io :refer [as-file]]
            [presto.core :refer :all]
            [clojure.test :refer :all]))


(expected-when "path-concat concatenates two paths" path-concat
  when ["foo" "bar"] = "foo/bar"
  when ["./foo" "./bar"] = "./foo/bar"
  when ["./" "bar"] = "./bar"
  when ["path" "foo/bar.baz"] = "path/foo/bar.baz"
  when ["foo/" "bar/"] = "foo/bar"
  when ["./foo/bar/" "./baz/"] = "./foo/bar/baz")

(expected-when "filter-tree iterates recursively over a file system tree and
               returns all files that match the given filter" filter-tree
  when ["test/res" (extension-filter "eaml")]
     = [(as-file "test/res/nested/example2.eaml")
        (as-file "test/res/example1.eaml")]
  when ["test/res" (extension-filter "foo")]
     = [])



