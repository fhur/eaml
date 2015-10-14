(ns eaml.parser-test
  (:require [eaml.parser :refer :all]
            [presto.core :refer :all]
            [clojure.test :refer :all]))

(defn parse-first
  [string]
  (->> (parser string)
       (normalize-nodes)
       (first)))


(defn parse-all
  [string]
  (->> (parser string)
       (normalize-nodes)))


(expected-when "parsing a single color" parse-first
  when ["color red: #ff0000;"]
     = {:id "red"
        :node :color
        :value [:literal "#ff0000"]}

  when ["color some_other_color: #f12;"]
     = {:id "some_other_color"
        :node :color
        :value [:literal "#f12"]})


(expected-when "parsing a single dimens" parse-first
  when ["dimen small_margins: 12dp;"]
     = {:id "small_margins"
        :node :dimen
        :value [:literal "12dp"]}

  when ["dimen large_text: 12sp;"]
     = {:id "large_text"
        :node :dimen
        :value [:literal "12sp"]})


(expected-when "parsing a single style" parse-first
  when ["style Foo {}"]
     = {:id "Foo"
        :node :style
        :parents []
        :attrs []}

  when ["style FooBar < Foo {}"]
     = {:id "FooBar"
        :node :style
        :parents ["Foo"]
        :attrs []}

  when ["style FooBar < Boo, Foo {}"]
     = {:id "FooBar"
        :node :style
        :parents ["Boo" "Foo"]
        :attrs []}

  when ["style FooBar123 < Foo, Bar, Baz {
           android:textColor: #ff0000;
           android:textSize: 12sp;
         }"]
     = {:id "FooBar123"
        :node :style
        :parents ["Foo" "Bar" "Baz"]
        :attrs [{:name "android:textColor" :value [:literal "#ff0000"] :config :default}
                {:name "android:textSize" :value [:literal "12sp"] :config :default}]}

  when ["style Foo < Bar {
          android:textColor: #123;
          android:background: @drawable/foo_drawable;
          android:textSize: small_text;
          android:text: \"some text\";
        }"]
     = {:id "Foo"
        :node :style
        :parents ["Bar"]
        :attrs [{:name "android:textColor" :value [:literal "#123"] :config :default}
                {:name "android:background" :value [:literal "@drawable/foo_drawable"] :config :default}
                {:name "android:textSize" :value [:pointer "small_text"] :config :default}
                {:name "android:text" :value [:literal "some text"] :config :default}]})


(expected-when "parsing several nodes" parse-all
  when ["color red: #f00;
         dimen normal_paddings: 12dp;
         style Foo < Bar {
           foo: 12dp;
         }"]
     = [{:id "red"
         :node :color
         :value [:literal "#f00"]}
        {:id "normal_paddings"
         :node :dimen
         :value [:literal "12dp"]}
        {:id "Foo"
         :node :style
         :parents ["Bar"]
         :attrs [{:name "foo" :value [:literal "12dp"] :config :default}]}])
