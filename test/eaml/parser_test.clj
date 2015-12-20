(ns eaml.parser-test
  (:require [eaml.parser :refer :all]
            [presto.core :refer :all]
            [clojure.test :refer :all]))

(defn parse-first
  [string]
  (first (parse-str string)))

(expected-when "parsing a single color" parse-first
  when ["color red: #ff0000;"]
     = {:id "red"
        :node :color
        :vals [{:config :default
                :value "#ff0000"}]}

  when ["color some_other_color: #f12;"]
     = {:id "some_other_color"
        :node :color
        :vals [{:config :default
                :value "#f12"}]}

  when ["color multi_config_color {
           default: #ff0000;
           v21: @drawable/btn_red_ripple;
         }"]
     = {:id "multi_config_color"
        :node :color
        :vals [{:config :default
                :value "#ff0000"}
               {:config :v21
                :value "@drawable/btn_red_ripple"}]}

  when ["color some_color: some_other;"]
     = {:id "some_color"
        :node :color
        :vals [{:config :default
                :value "some_other"}]})

(expected-when "parsing a boolean" parse-first
  when ["bool is_foo: true;"]
     = {:id "is_foo"
        :node :bool
        :vals [{:config :default
                :value "true"}]})

(expected-when "parsing a string" parse-first
  when ["string a_string: 'foobar';"]
     = {:id "a_string"
        :node :string
        :vals [{:config :default
                :value "'foobar'"}]})

(expected-when "parsing an integer" parse-first
  when ["integer foo: 123;"]
     = {:id "foo"
        :node :integer
        :vals [{:config :default
                :value "123"}]})


(expected-when "parsing a single dimens" parse-first
  when ["dimen small_margins: 12dp;"]
     = {:id "small_margins"
        :node :dimen
        :vals [{:config :default
                :value "12dp"}]}

  when ["dimen large_text: 12sp;"]
     = {:id "large_text"
        :node :dimen
        :vals [{:config :default
                :value "12sp"}]})


(expected-when "parsing a single style" parse-first
  when ["style Foo {}"]
     = {:id "Foo"
        :node :style
        :parent nil
        :attrs []}

  when ["style FooBar < Foo.Bar123.Baz {}"]
     = {:id "FooBar"
        :node :style
        :parent "Foo.Bar123.Baz"
        :attrs []}

  when ["style FooBar123 < Foo {
           colorPrimaryDark: #ff0000;
           android:textSize: 12sp;
           android:background: @null;
           foo: @android:foo/bar;
           bar123: @style/Text.Label2;
         }"]
     = {:id "FooBar123"
        :node :style
        :parent "Foo"
        :attrs [{:name "colorPrimaryDark" :value "#ff0000" :config :default}
                {:name "android:textSize" :value "12sp" :config :default}
                {:name "android:background" :value "@null" :config :default}
                {:name "foo" :value "@android:foo/bar" :config :default}
                {:name "bar123" :value "@style/Text.Label2" :config :default}]}

  when ["style Foo < Bar {
          android:textColor: #123;
          android:background: @drawable/foo_drawable;
          fooBar();
          android:textSize: small_text;
          android:text: \"some text\";
          qux();
        }"]
     = {:id "Foo"
        :node :style
        :parent "Bar"
        :attrs [{:name "android:textColor" :value "#123" :config :default}
                {:name "android:background" :value "@drawable/foo_drawable" :config :default}
                {:mixin "fooBar" :args [] :config :default}
                {:name "android:textSize" :value "small_text" :config :default}
                {:name "android:text" :value "\"some text\"" :config :default}
                {:mixin "qux" :args [] :config :default}]})

(expected-when "Parsing a single mixin" parse-first
  when ["mixin Foo {}"]
     = {:id "Foo"
        :node :mixin
        :attrs []}

  when ["mixin FooBarBaz {
          android:textSize: 12sp;
          android:textColor: #123456;
          android:numFoo: 4;
          android:text: @string/a_string;
          &:foo-bar {
            android:textSize: 14sp;
            android:extraAttr: 'extra';
          }
        }"]
    = {:id "FooBarBaz"
       :node :mixin
       :attrs [{:name "android:textSize" :value "12sp" :config :default}
               {:name "android:textColor" :value "#123456" :config :default}
               {:name "android:numFoo" :value "4" :config :default}
               {:name "android:text" :value "@string/a_string" :config :default}
               {:name "android:textSize" :value "14sp" :config :foo-bar}
               {:name "android:extraAttr" :value "'extra'" :config :foo-bar}]})


(expected-when "parsing several nodes" parse-str
  when ["color red: #f00;
         dimen normal_paddings: 12dp;
         style Foo < Bar {
           foo: 12dp;
           &:land { foo: 12dp; }
         }"]
     = [{:id "red"
         :node :color
         :vals [{:config :default
                 :value "#f00"}]}
        {:id "normal_paddings"
         :node :dimen
         :vals [{:config :default
                 :value "12dp"}]}
        {:id "Foo"
         :node :style
         :parent "Bar"
         :attrs [{:name "foo" :value "12dp" :config :default}
                 {:name "foo" :value "12dp" :config :land}]}])
