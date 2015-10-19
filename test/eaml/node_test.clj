(ns eaml.node-test
  (:require [eaml.node :refer :all]
            [eaml.parser :refer [parse-str]]
            [presto.core :refer :all]
            [clojure.test :refer :all]))

(defn with-ids
  [expected-nodes ids]
  (let [id-set (set ids)]
    (->> (map :id expected-nodes)
         (map id-set)
         (reduce #(and %1 %2)))))

(def program
  "color red: #f00;
   dimen paddings: 12dp;
   dimen text_size_normal: 12sp;

   style Button {
     android:textColor: red;
     android:paddings: paddings;
     android:textSize: text_size_normal;
   }

   style LargeButton < Button {
     android:textSize: 14sp;
     android:layout_margins: 20dp;
   }
  ")

(def nodes (parse-str program))

(expected-when "filter-styles returns only style nodes" filter-styles
  when [nodes] with-ids ["Button" "LargeButton"])
