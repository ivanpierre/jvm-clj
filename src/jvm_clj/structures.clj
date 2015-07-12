;
;   jvm-clj  JVM management library
;   Copyright (c) 2015 Ivan Pierre <me@ivanpierre.ch>. All rights reserved.
;
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns jvm-clj.structures
  "Definition of JVM Class model
Here are defined the tree structure of a JVM class according to 'Chapter 4. The class File Format'
of the Oracle 'Java Virtual Machine Specification'

http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html

Every element of the tree is associated with a type definition used to call the transformation
functions for class reading and cration. The root element of the tree is the :class element of the
structures map. These definition are ment to be constant even if some data are calculated to create
more efficient access structures. So there is no treatement here, only definitions."
  )

(defmacro structures
  "Definition of class structures"
  []
  `{
    ;; http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1
    :class                 {:struct [:magic :u4
                                     :minor-version :u2
                                     :major-version :u2
                                     :constant-pool (:array :cp (:code :u1 (:constants)))
                                     :access-flags :u1
                                     :this-class :u2
                                     :super-class :u2]}     ; (:const        :cst-class)




    ;; http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.5
    :fields                {:struct [:access-flags :u1      ; (:acces-flags :field)
                                     :name-index :u2        ; :cst-utf8)
                                     :descriptor :u2        ; :cst-type)
                                     :attributes (:array :u2 (:code
                                                               :u2 ; (:const :utf8)
                                                               (:struct-length :u4 :attribute :type :field)))]
                            }

    ;; http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.6
    :methods               {:struct [:access-flags :u1      ; (:acces-flags :method)
                                     :name-index :u2        ; (:const :cst-utf8)
                                     :descriptor :u2        ; (:const :cst-type)
                                     :attributes (:array :u2 (:code
                                                               :u2 ; (:const :utf8)
                                                               (:struct-length :u4 :attribute :type :method)))]
                            }

    ;; In Code Attribute http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.3
    :exceptions            {:struct [:start-pc :u2
                                     :end-pc :u2
                                     :handler-pc :u2
                                     :catch-type :u2]       ; (:const :cst-class)]
                            }

    ;; In exceptions Attribute http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.5
    :throws                {:struct (:const :u2)}           ; :cst-class)}

    ;; In innerClass Atribute http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.6
    :inner-classes         {:struct [:inner-class :u2       ; (:const :cst-class)
                                     :outer-class :u2       ; (:const :cst-class)
                                     :inner-name :u2        ; (:const :cst-utf8)
                                     :access-flags :u1]     ; (:access-flags :class)]
                            }

    ;; In LineNumberTable Attribute http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.12
    :line-numbers          {:struct [:start-pc :u2
                                     :line-number :u2]
                            }

    ;; In LocalVariableTable http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.13
    :local-variables       {:struct [:start-pc :u2
                                     :length :u2
                                     :name :u2              ; (:const :cst-utf8)
                                     :descriptor :u2        ; (:const :cst-utf8)
                                     :index :u2]
                            }

    ;; In LocalVariableTypeTable http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.14
    :local-variables-types {:struct [:start-pc :u2
                                     :length :u2
                                     :name :u2              ; (:const :cst-utf8)
                                     :signature :u2         ; (:const :cst-utf8)
                                     :index :u2]
                            }

    ;; RuntimeVisibleAnnotations http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.16
    :annotations           {:struct [:type :u2              ; (:const     :cst-utf8)
                                     :value-pairs (:struct-array :value-pairs)]
                            }

    ;; RuntimeVisibleAnnotations http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7.16
    :value-pairs           {:struct [:element-name :u2      ; (:const     :cst-utf8)
                                     :element-value (:element-value)]
                            }                               ; element-value is more complicated,union

    :case                  {:struct [:value :s4
                                     :label :s4]
                            }})

