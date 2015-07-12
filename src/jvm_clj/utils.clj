(ns jvm-clj.utils)

;;; The internal constant table will have the following structure
;;; {{type: value:} index} to manage identical values and parsing source code
;;; and [{type: value:}] to get value from code
(defn swap-keys
  "Create new map from a map of maps. Making key a mapped value by new
  and addik key as value mapped by old.
  {k {new val ...}} -> {val {old k ...}}"
  [new old map]
  (reduce-kv
    (fn [r k v]
      (into r
            {(get v new)
             (dissoc
               (into v {old k})
               new)}))
    {} map))

