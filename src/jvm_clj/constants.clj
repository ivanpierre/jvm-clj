;
;   jvm-clj  JVM management library
;   Copyright (c) 2013 Ivan Pierre <me@ivanpierre.ch>. All rights reserved.
;
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns jvm-clj.constants
  (:require [jvm-clj.utils :refer [swap-keys]]))

;;; http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.4
(def constants-by-key
  "Definition of constant structs by keywords"
  `{:cst-utf8          {:code   10
                        :struct :utf8}

    :cst-integer       {:code   3
                        :struct :s4}
    :cst-float         {:code   4
                        :struct :f4}

    :cst-long          {:code   5
                        :struct :s8}

    :cst-double        {:code   6
                        :struct :f8}

    :cst-class         {:code   7
                        :struct :u2}                        ; (:const :cst-utf8)}

    :cst-string        {:code   8
                        :struct :u2}                        ; (:const :cst-utf8)}

    :cst-field         {:code   9
                        :struct [:class :u2                 ; (:const :cst-class)
                                 :name-and-type :u2]}       ; (:const :cst-name-and-type)]}

    :cst-method        {:code   10
                        :struct [:class :u2                 ; (:const :cst-class)
                                 :name-and-type :u2]}       ; (:const :cst-name-and-type)]}

    :cst-interface     {:code   11
                        :struct [:class :u2                 ; (:const :sct-clas)
                                 :name-and-type :u2]}       ; (:const :cst-name-and-type)]}

    :cst-name-and-type {:code   12
                        :struct [:name :u2                  ; (:const :cst-utf8)
                                 :descriptor :u2]}          ; (:const :cst-utf8)]}

    :cst-handle        {:code   15
                        :struct [:kind :u1                  ; :kind
                                 :reference :u2]}           ; (:const :cst-field :cst-method :cst-interface)]}

    :cst-descriptor    {:code   16
                        :struct (:const :cst-utf8)}

    :cst-dynamic       {:code   18
                        :struct [:bootstrap :u2             ; (:bootstrap-index)
                                 :name-and-type :u2]}})     ; (:const :cst-name-and-type)]}
    


(def constants-by-code
  "Constant pool structs by code"
  (swap-keys :code :key constants-by-key))

