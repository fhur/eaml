(ns eaml.compiler
  (:require [eaml.error :refer :all]
            [eaml.xml :refer :all]))

(defn- build-name-map
  [styles-list]
  (group-by :name styles-list))

(defn resolve-attr-value
  [[val-type value]]
  (if (= val-type "literal")
    value
    (raise (str "unsupported type " val-type))))

(defn transpile-struct
  [style-struct name-map]
  (let [xml (style-struct->xml style-struct name-map)]
    (for [config (:configs style-struct)]
      {:path (str "values-" config "/")
       :xml xml})))

(defn transpile
  "Given a list of style structs as argument, returns an
  object containing the relative path and transpiled xml
  of each style struct."
  [style-list]
  (let [name-map (build-name-map style-list)]
    (for [style-struct style-list]
      (->> (transpile-struct style-struct name-map)))))

(def test-structs
  [{:name "Button"
    :configs ["v21"]
    :attrs [{:name "android:textColor" :value ["literal" "#ff0000"]}
            {:name "android:background" :value ["literal" "@nil"]}]}

   {:name "FooStyle"
    :configs ["default" "land"]
    :attrs [{:name "qux" :value ["literal" "#ff0000"]}
            {:name "foo:bar" :value ["literal" "@nil"]}]}])

(println (transpile test-structs))
