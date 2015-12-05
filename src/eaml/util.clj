(ns eaml.util
  (:require [clojure.set :refer [union intersection]]))

(defn flat-coll
  "Given a coll of collections, flattens it by one level.
  Example: [[1] [2 3] [] [4 5 6]] will result in [1 2 3 4 5 6]"
  [colls]
  (apply concat colls))


(defn group-maps
  "Takes a collection of maps and groups their k,v pairs by key"
  [maps]
  (loop [grouped {}
         maps maps]
    (if (empty? maps)
      grouped
      (let [m (first maps)]
        (recur (reduce-kv (fn [reduction k v]
                           (let [current-val (get reduction k)]
                             (if current-val
                               (assoc reduction k (conj current-val v))
                               (assoc reduction k [v]))))
                   grouped m)
               (rest maps))))))

(defn cons*
  "equivalent to calling cons over the values in reverse order.
  Example: (cons* [4 5 6] 1 2 3) == '(1 2 3 4 5 6)"
  [coll & values]
  (if (empty? values)
    coll
    (cons (first values)
          (apply cons* coll (rest values)))))

(defmacro itp
  "Interpolation macro. Similar to ruby interpolation.
  Does not provide any escaping mechanism.
  Syntax: (itp 'Hi #{name}, how are you?')
  Will expand to (str 'Hi ' name ', how are you?')"
  [string]
  (loop [string string
         result []]
    (let [[_ match-sym-anywhere] (re-find #"(.*?)#\{.*?\}" string)
          [regex-match match-symbol] (re-find #"\A#\{(.*?)\}" string)]
      (cond match-symbol
              (recur (.substring string (count regex-match))
                     (conj result (read-string match-symbol)))
            match-sym-anywhere
              (recur (.substring string (count match-sym-anywhere))
                     (conj result match-sym-anywhere))
            :else
              (cons `str (conj result string))))))

(defn case-match
  "Matches string agains the given regex, one by one until a match
  is found. Returns the matched form arg. Returns nil if there are no matches.
  form-pairs => [regex arg]"
  [string & form-pairs]
  (if (empty? form-pairs)
    nil
    (let [[regex form] (take 2 form-pairs)]
      (if (re-find regex string)
        form
        (apply case-match string (rest (rest form-pairs)))))))


(defn singleton?
  [coll]
  (= (count coll) 1))

(defn find-first
  "Return the first x in coll s.t. pred(x) = true"
  [coll pred]
  (first (filter pred coll)))


(defn merge-lists
  "Preconditions:
  - f is a 1-1 function
  - Every element on both l1 and l2 is unique

  Merges the two lists into one. If for some a in l1 and b in l2
  (= (f a) (f b)) then only b is added."
  [l1 l2 f]
  (let [mapped-set1 (set (map f l1))
        mapped-set2 (set (map f l2))
        mapped-intersection (intersection mapped-set1 mapped-set2)
        l1-filtered (filter (fn [x]
                              (if (contains? mapped-intersection (f x))
                                  false true))
                            l1)]
    (loop [l2 l2
           clashes []
           result []]
      (if (empty? l2)
        (concat l1-filtered result clashes)
        (let [x (first l2)
              tail (rest l2)
              fx (f x)]
          (if (contains? mapped-intersection fx)
            (recur tail (conj clashes x) result)
            (recur tail clashes (conj result x))))))))



