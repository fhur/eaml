(ns eaml.error)

(defn raise!
  "Throws a RuntimeException with the given message as argument"
  [& messages]
  (throw (new RuntimeException (apply str messages))))

