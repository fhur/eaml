(ns eaml.compiler-test
  (:require [eaml.compiler :refer :all]
            [eaml.test-helpers :refer :all]
            [eaml.fixtures.simple-res :refer :all]
            [presto.core :refer :all]
            [clojure.test :refer :all]))

(expected-when "Transpiling simple resources" transpile-str
  when  [fix-simple-colors]
     =  {:default
         (resources
          (color "red" "#f00")
          (color "green" "#0f0")
          (color "blue" "#00f")
          (color "main_color" "@color/red"))}

  when  [fix-simple-dimen]
  = {:default
         (resources
          (dimen "small_margins" "8dp")
          (dimen "medium_margins" "12dp")
          (dimen "large_margins" "24dp")
          (dimen "default_margins" "@dimen/medium_margins"))}

  when  [fix-simple-strings]
  = {:default
         (resources
          (string "hello_world" "Hello World!")
          (string "name" "Pizza 123"))}

  when  [fix-simple-bools]
  = {:default
         (resources
          (bool "is_true" "true")
          (bool "aint_true" "false")
          (bool "a_boolean" "@bool/is_true"))})


(expected-when "Transpiling simple resources that support multiple configs" transpile-str
  when  [fix-simple-res-with-configs]
  = {:default   (resources
                  (dimen "padding" "12dp")
                  (string "supports_ripples" "nope")
                  (color "main_color" "#f00")
                  (color "button_color" "@color/main_color"))
     :v21       (resources
                  (dimen "padding" "24dp")
                  (string "supports_ripples" "yes")
                  (color "button_color" "@drawable/btn_ripple"))
     :land      (resources
                  (dimen "padding" "30dp"))})

(expected-when "Transpiling styles" transpile-str
  when  ["color foo: #fff;
          style Foo {
            foo: foo;
          }"]
     =  {:default (resources
                    (color "foo" "#fff")
                    (style {:name "Foo"}
                           (item "foo" "@color/foo")))}

  when  ["style Button < BaseButton {
            android:textSize: 12dp;
            android:background: @drawable/btn_back;
            &:v21,v22 {
              android:background: @drawable/btn_ripple;
            }
            &:land {
              android:textSize: 10dp;
            }
          }"]
     =  {:default (resources
                    (style {:name "Button" :parent "BaseButton"}
                           (item "android:textSize" "12dp")
                           (item "android:background" "@drawable/btn_back")))
         :v21     (resources
                    (style {:name "Button" :parent "BaseButton"}
                           (item "android:textSize" "12dp")
                           (item "android:background" "@drawable/btn_ripple")))
         :v22     (resources
                    (style {:name "Button" :parent "BaseButton"}
                           (item "android:textSize" "12dp")
                           (item "android:background" "@drawable/btn_ripple")))
         :land    (resources
                    (style {:name "Button" :parent "BaseButton"}
                           (item "android:background" "@drawable/btn_back")
                           (item "android:textSize" "10dp")))})


