(ns eaml.parser
  (:require [instaparse.core :as insta]
            [eaml.nodes :refer :all]))

(def parser (insta/parser (clojure.java.io/resource "grammar.bnf")
                          :auto-whitespace :standard))


(defn parse-color
  [nodes]
  {:node :color
   :configs (get-configs nodes)
   :id (get-id nodes)
   :val (get-color-val nodes)})

(defn parse-style
  [nodes]
  {:node :style
   :id (get-id nodes)
   :configs (get-configs nodes)
   :opts (get-opts nodes)
   :body (get-style-body nodes)})

(defn- parse-nodes
  [parse-tree]
  (map (fn [node]
         (cond (color-def-node? node) (parse-color node)
               (style-def-node? node) (parse-style node)))
       parse-tree))


(defn parse
  [string]
  (->> (parser string)
       rest ;; ignore the S node
       parse-nodes))

[:S [:color-def [:configs "v21"] [:identifier "btn_color"] [:res-pointer [:literal "'#123'"]]]
    [:color-def [:identifier "btn_color"] [:res-pointer [:literal "'#123'"]]]
    [:color-def [:identifier "primary_color"] [:res-pointer [:identifier "btn_color"]]]
    [:style-def [:configs "v21"] [:identifier "Button"] [:addit-opts [:assignment "parent='BaseButton'"]] [:exprs [:assignment "android:layout_height='80dp'"] [:assignment "android:layout_width='wrap_content'"] [:assignment "android:textColor=btn_color"]]]
    [:style-def [:identifier "RedButton"] [:exprs [:invocation [:identifier "Button"]] [:invocation [:identifier "BaseButton"]] [:assignment "android:background='#f00'"] [:assignment "android:textColor='#fff'"]]]]
