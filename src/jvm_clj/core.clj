(ns jvm-clj.core
  (:use jvm-clj.class-file)
  (:gen-class))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!")
  args)
