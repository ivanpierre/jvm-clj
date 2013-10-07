(ns jvm-clj.class-struct-test
  (:require [clojure.test :refer :all])
  (:use midje.sweet jvm-clj.class-struct ))

(use 'clojure.pprint)

(pprint (swap-keys :code  :key constants-by-key))

constants-by-key

(swap-keys :code  :key constants-by-key)

opcodes-by-key


opcodes-by-code

(get-elem-key opcodes-by-code 132 :struct)
(-> opcodes-by-code)
(pprint (swap-keys :code  :key opcodes-by-key))

attributes-by-code
attributes-by-key

