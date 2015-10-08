(ns eaml.error)

(defn raise!
  "Throws a RuntimeException with the given message as argument"
  [message]
  (throw (new RuntimeException message)))

