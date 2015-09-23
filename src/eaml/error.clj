(ns eaml.error)

(defn raise
  [message]
  (throw (new RuntimeException message)))

