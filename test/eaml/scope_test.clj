(ns eaml.scope-test
  (:require [eaml.scope :refer :all]
            [eaml.parser :refer [parse-str]]
            [eaml.parser :refer [parse-str]]
            [presto.core :refer :all]
            [clojure.test :refer :all]))

(def nodes (parse-str "color red: #f00;
                       color main_color: red;
                       integer foo: 5;
                       string name: 'a name';
                       string my_name: name;
                       string other_name: my_name;
                       style Foo < Bar {
                         android:textColor: main_color;
                         android:foo: foo;
                         android:text: my_name;
                       }"))

(def scope (create nodes))

(expected-when "obtain-type returns the resolved type of an expression
               in the current scope" #(obtain-type % scope)
  when ["red"] = :color
  when ["main_color"] = :color
  when ["foo"] = :integer
  when ["name"] = :string
  when ["my_name"] = :string
  when ["other_name"] = :string
  when ["5"] = :integer
  when ["5123"] = :integer
  when ["true"] = :bool
  when ["false"] = :bool
  when ["'foo'"] = :string
  when ["\"\""] = :string
  when ["@string/foo"] = :string
  when ["@bool/foo"] = :bool
  when ["''"] = :string)

(expected-when "resolve-expr obtains the value of an expression in the
               current scope" #(resolve-expr :any % scope)
  when ["#f00"] = "#f00"
  when ["true"] = "true"
  when ["false"] = "false"
  when ["'foo'"] = "foo"
  when ["12sp"] = "12sp"
  when ["1234"] = "1234"
  when ["red"] = "@color/red"
  when ["main_color"] = "@color/main_color"
  when ["foo"] = "@integer/foo"
  when ["my_name"] = "@string/my_name")

(deftest obtain-type-throws-when-not-found
  (is (thrown? IllegalStateException (obtain-type "asdf" scope)))
  (is (thrown? IllegalStateException (obtain-type "a%1234" scope)))
  (is (thrown? IllegalStateException (obtain-type "#" scope)))
  (is (thrown? IllegalStateException (resolve-expr :string "main_color" scope)))
  (is (thrown? IllegalStateException (create (parse-str "integer foo: 1; color foo: #f00;")))))
