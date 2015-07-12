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

(ns jvm-clj.attributes
  (:require [clojure.string :refer [lower-case]]))

;;; http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7
(def attributes-def
  "Attributes definition"
  {"ConstantValue"                        {:java   45.3
                                           :type   #{:field}
                                           :struct :u2}     ; (:const :cst-integer :cst-float :cst-double :cst-long :cst-string)}

   "Code"                                 {:java   45.3
                                           :type   #{:method}
                                           :struct [:max-stack :u2
                                                    :max-local :u2
                                                    :code :code
                                                    :exceptions (:array :u2 (:struct :exceptions))
                                                    :attributes (:array :u2 (:code
                                                                              :u2 ; (:const :utf8)
                                                                              (:struct-length :u4 :attribute :type :code)))]}

   "StackMapTable"                        {:java   50.0
                                           :type   #{:code}
                                           :struct nil}     ; later, this is awfull

   "Exceptions"                           {:java   45.3
                                           :type   #{:method}
                                           :struct (:array :u2 (:struct :throws))}

   "InnerClasses"                         {:java   45.3
                                           :type   #{:class}
                                           :struct (:array :u2 (:struct :inner-class))}

   "EnclosingMethod"                      {:java   49.0
                                           :type   #{:class}
                                           :struct [:class :u2 ; (:const :cst-class)
                                                    :method :u2]} ; (:const :cst-method)]}

   "Synthetic"                            {:java   45.3
                                           :type   #{:class :field :method}
                                           :struct nil}

   "Signature"                            {:java   45.3
                                           :type   #{:class :field :method}
                                           :struct :u2}     ; (:const :cst-utf8)}

   "SourceFile"                           {:java   45.3
                                           :type   #{:class}
                                           :struct :u2}     ; (:const :cst-utf8)}

   "SourceDebugExtension"                 {:java   49.0
                                           :type   #{:class}
                                           :struct nil}

   "LineNumberTable"                      {:java   45.3
                                           :type   #{:code}
                                           :struct (:array :u2 (:struct :line-numbers))}

   "LocalVariableTable"                   {:java   45.3
                                           :type   #{:code}
                                           :struct (:array :u2 (:struct :local-variables))}

   "LocalVariableTypeTable"               {:java   49.0
                                           :type   #{:code}
                                           :struct (:array :u2 (:struct :local-variables-types))}

   "Deprecated"                           {:java 45.3
                                           :type #{:class :field :method}}

   "RuntimeVisibleAnnotations"            {:java   49.0
                                           :type   #{:class :field :method}
                                           :struct (:array :u2 (:struct :annotations))}

   "RuntimeInvisibleAnnotations"          {:java   49.0
                                           :type   #{:class :field :method}
                                           :struct (:array :u2 (:struct :annotations))}

   "RuntimeVisibleParameterAnnotations"   {:java   49.0
                                           :type   #{:method}
                                           :struct (:array :u1 (:struct :annotations))}

   "RuntimeInvisibleParameterAnnotations" {:java   49.0
                                           :type   #{:method}
                                           :struct (:array :u1 (:struct :annotations))}

   "AnnotationDefault"                    {:java   49.0
                                           :type   #{:method}
                                           :struct (:struct :element-value)}

   "BootstrapMethods"                     {:java 51.0}

   :else                                  {:java   45.3
                                           :type   #{:code :field :method :class}
                                           :struct nil}
   })

(defn attribute-name-to-keyword
  "Change attribute string name to keyword"
  [name]
  (keyword (lower-case name)))

(def attributes-by-code
  "Each element is: {code {:key key :java vers :type type :struct args}}"
  (reduce
    #(into %1
           {(first %2)
            (conj (second %2)
                  {:key (attribute-name-to-keyword (first %2))})})
    {} attributes-def))

(def
  attributes-by-key
  "Each element is: {key {:code code :java vers :type type :struct args}}"
  (reduce
    #(into %1
           {(attribute-name-to-keyword (first %2))
            (into (second %2)
                  {:code (first %2)})})
    {} attributes-def))

