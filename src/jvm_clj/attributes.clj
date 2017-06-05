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
  (:require
    [camel-snake-kebab.core :refer :all]
    ))

;;; http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7
(defmacro attributes-def
  "Attributes definition"
  []
  `{:constant-value                          {:java   45.3
                                              :type   #{:field}
                                              :struct :u2}   ; (:const :cst-integer :cst-float :cst-double :cst-long :cst-string)}

    :code                                    {:java   45.3
                                              :type   #{:method}
                                              :struct [:max-stack :u2
                                                       :max-local :u2
                                                       :code :code
                                                       :exceptions (:array :u2 (:struct :exceptions))
                                                       :attributes (:array :u2 (:code
                                                                                 :u2 ; (:const :utf8)
                                                                                 (:struct-length :u4 :attribute :type :code)))]}

    :stack-map-table                         {:java   50.0
                                              :type   #{:code}
                                              :struct nil}   ; later, this is awfull

    :exceptions                              {:java   45.3
                                              :type   #{:method}
                                              :struct (:array :u2 (:struct :throws))}

    :inner-classes                           {:java   45.3
                                              :type   #{:class}
                                              :struct (:array :u2 (:struct :inner-class))}

    :enclosing-method                        {:java   49.0
                                              :type   #{:class}
                                              :struct [:class :u2 ; (:const :cst-class)
                                                       :method :u2]} ; (:const :cst-method)]}

    :synthetic                               {:java   45.3
                                              :type   #{:class :field :method}
                                              :struct nil}

    :signature                               {:java   45.3
                                              :type   #{:class :field :method}
                                              :struct :u2}   ; (:const :cst-utf8)}

    :source-file                             {:java   45.3
                                              :type   #{:class}
                                              :struct :u2}   ; (:const :cst-utf8)}

    :source-debug-extension                  {:java   49.0
                                              :type   #{:class}
                                              :struct nil}

    :line-number-table                       {:java   45.3
                                              :type   #{:code}
                                              :struct (:array :u2 (:struct :line-numbers))}

    :local-variable-table                    {:java   45.3
                                              :type   #{:code}
                                              :struct (:array :u2 (:struct :local-variables))}

    :local-variable-type-table               {:java   49.0
                                              :type   #{:code}
                                              :struct (:array :u2 (:struct :local-variables-types))}

    :deprecated                              {:java 45.3
                                              :type #{:class :field :method}}

    :runtime-visible-annotations             {:java   49.0
                                              :type   #{:class :field :method}
                                              :struct (:array :u2 (:struct :annotations))}

    :runtime-invisible-annotations           {:java   49.0
                                              :type   #{:class :field :method}
                                              :struct (:array :u2 (:struct :annotations))}

    :runtime-visible-parameter-annotations   {:java   49.0
                                              :type   #{:method}
                                              :struct (:array :u1 (:struct :annotations))}

    :runtime-invisible-parameter-annotations {:java   49.0
                                              :type   #{:method}
                                              :struct (:array :u1 (:struct :annotations))}

    :annotation-default                      {:java   49.0
                                              :type   #{:method}
                                              :struct (:struct :element-value)}

    :bootstrap-methods                       {:java 51.0}
   })

