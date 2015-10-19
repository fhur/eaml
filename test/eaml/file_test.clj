(ns eaml.file-test
  (:require [eaml.file :refer :all]
            [presto.core :refer :all]
            [clojure.test :refer :all]))


(expected-when "path-concat concatenates two paths" path-concat
  when ["foo" "bar"] = "foo/bar"
  when ["./foo" "./bar"] = "./foo/bar"
  when ["./" "bar"] = "./bar"
  when ["path" "foo/bar.baz"] = "path/foo/bar.baz"
  when ["foo/" "bar/"] = "foo/bar"
  when ["./foo/bar/" "./baz/"] = "./foo/bar/baz")
