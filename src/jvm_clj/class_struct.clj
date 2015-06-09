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

(ns jvm-clj.class-struct
  "Definition of JVM Class model
  Here are defined the tree structure of a JVM class according to 'Chapter 4. The class File Format'
  of the Oracle 'Java Virtual Machine Specification'

  http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html

  Every element of the tree is associated with a type definition used to call the transformation
  functions for class reading and cration. The root element of the tree is the :class element of the
  structures map. These definition are ment to be constant even if some data are calculated to create
  more efficient access structures. So there is no treatement here, only definitions and "
  (:require [clojure.string :refer [lower-case]]))


(def structures
  "Definition of class structures"
  '{
    ;; http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1
    :class                 {:magic         :u4
                            :minor-version :u2
                            :major-version :u2
                            :constant-pool (:array :cp (:code :u1 (:constants)))

                            :access-flags  :u1              ; (:access-flags :class)
                            :this-class    :u2              ; (:const        :cst-class)
                            :super-class   :u2              ; (:const        :cst-class)

                            }

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
                                 :name-and-type :u2]}       ; (:const :cst-name-and-type)]}
    })

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


(def constants-by-code
  "Constant pool structs by code"
  (swap-keys :code :key constants-by-key))

(def access-flags
  "Access modifiers definition
  Class http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.1-200-E.1
  Field http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.5-200-A.1
  Method http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.6-200-A.1"
  '{:public       {:code 0x0001 :type #{:class :field :method}}
    :private      {:code 0x0002 :type #{:class :field :method}}
    :protected    {:code 0x0004 :type #{:class :field :method}}
    :static       {:code 0x0008 :type #{:field :method}}
    :final        {:code 0x0010 :type #{:class :field :method}}
    :synchronized {:code 0x0020 :type #{:method}}
    :volatile     {:code 0x0040 :type #{:field}}
    :bridge       {:code 0x0040 :type #{:method}}
    :varargs      {:code 0x0080 :type #{:method}}
    :transient    {:code 0x0080 :type #{:field}}
    :native       {:code 0x0100 :type #{:method}}
    :interface    {:code 0x0200 :type #{:class}}
    :abstract     {:code 0x0400 :type #{:class :method}}
    :strict       {:code 0x0800 :type #{:method}}
    :synthetic    {:code 0x1000 :type #{:class :field :method}}
    :annotation   {:code 0x2000 :type #{:class}}})

(defn good-type?
  "Test type for access"
  [access type]
  (get-in ((access-flags access) :type) type))

(defn good-type-and-flag?
  "Test type and flags for access"
  [access flags type]
  (let [access (get access-flags access)]
    (and (get (:type access) type)
         (not= 0 (bit-and (:code access) flags)))))

(defn get-access-set
  "Give back the set of access for given flags and type of object"
  [flags type]
  (set (filter
         #(good-type-and-flag? % flags type)
         (keys access-flags))))

(defn get-access-flag
  "Give back flag from access set"
  [access-set type]
  (reduce +
          (map
            #(first (get access-flags %))
            (filter
              #(good-type? % type)
              access-set))))

;;; http://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.7
(def attributes
  "Attributes definition"
  `{"ConstantValue"                        {:java   45.3 :type #{:field}
                                            :struct :u2}    ; (:const :cst-integer :cst-float :cst-double :cst-long :cst-string)}

    "Code"                                 {:java   45.3 :type #{:method}
                                            :struct [:max-stack :u2
                                                     :max-local :u2
                                                     :code :code
                                                     :exceptions (:array :u2 (:struct :exceptions))
                                                     :attributes (:array :u2 (:code
                                                                               :u2 ; (:const :utf8)
                                                                               (:struct-length :u4 :attribute :type :code)))]}

    "StackMapTable"                        {:java 50.0 :type #{:code} :struct nil} ; later, this is awfull

    "Exceptions"                           {:java 45.3 :type #{:method} :struct (:array :u2 (:struct :throws))}

    "InnerClasses"                         {:java   45.3 :type #{:class}
                                            :struct (:array :u2 (:struct :inner-class))}

    "EnclosingMethod"                      {:java   49.0 :type #{:class}
                                            :struct [:class :u2 ; (:const :cst-class)
                                                     :method :u2]} ; (:const :cst-method)]}

    "Synthetic"                            {:java   45.3 :type #{:class :field :method}
                                            :struct nil}

    "Signature"                            {:java   45.3 :type #{:class :field :method}
                                            :struct :u2}    ; (:const :cst-utf8)}

    "SourceFile"                           {:java   45.3 :type #{:class}
                                            :struct :u2}    ; (:const :cst-utf8)}

    "SourceDebugExtension"                 {:java   49.0 :type #{:class}
                                            :struct nil}

    "LineNumberTable"                      {:java   45.3 :type #{:code}
                                            :struct (:array :u2 (:struct :line-numbers))}

    "LocalVariableTable"                   {:java   45.3 :type #{:code}
                                            :struct (:array :u2 (:struct :local-variables))}

    "LocalVariableTypeTable"               {:java   49.0 :type #{:code}
                                            :struct (:array :u2 (:struct :local-variables-types))}

    "Deprecated"                           {:java 45.3 :type #{:class :field :method}}

    "RuntimeVisibleAnnotations"            {:java   49.0 :type #{:class :field :method}
                                            :struct (:array :u2 (:struct :annotations))}

    "RuntimeInvisibleAnnotations"          {:java   49.0 :type #{:class :field :method}
                                            :struct (:array :u2 (:struct :annotations))}

    "RuntimeVisibleParameterAnnotations"   {:java   49.0 :type #{:method}
                                            :struct (:array :u1 (:struct :annotations))}

    "RuntimeInvisibleParameterAnnotations" {:java   49.0 :type #{:method}
                                            :struct (:array :u1 (:struct :annotations))}

    "AnnotationDefault"                    {:java   49.0 :type #{:method}
                                            :struct (:struct :element-value)}

    "BootstrapMethods"                     {:java 51.0}

    :else                                  {:java   45.3 :type #{:code :field :method :class}
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
    {} attributes))

(def
  attributes-by-key
  "Each element is: {key {:code code :java vers :type type :struct args}}"
  (reduce
    #(into %1
           {(attribute-name-to-keyword (first %2))
            (into (second %2)
                  {:code (first %2)})})
    {} attributes))

(def
  opcodes-by-key
  "Each element is: {key {:code code :struct args}}"
  `{:nop             {:code 0}
    :aconst_null     {:code 1}                              ;                                 :exec #(push nil)}
    :iconst_m1       {:code 2}                              ;                                 :exec #(push (int -1)}
    :iconst_0        {:code 3}                              ;                                 :exec #(push (int 0))}
    :iconst_1        {:code 4}                              ;                                 :exec #(push (int 1)}
    :iconst_2        {:code 5}                              ;                                 :exec #(push (int 2))}
    :iconst_3        {:code 6}                              ;                                 :exec #(push (int 3)}
    :iconst_4        {:code 7}                              ;                                 :exec #(push (int 4)}
    :iconst_5        {:code 8}                              ;                                 :exec #(push (int 5)}
    :lconst_0        {:code 9}                              ;                                 :exec #(lpush (long 0)}
    :lconst_1        {:code 10}                             ;                                :exec #(lpush (long 1)}
    :fconst_0        {:code 11}                             ;                                :exec #(push (float 0))}
    :fconst_1        {:code 12}                             ;                                :exec #(push (float 1))}
    :fconst_2        {:code 13}                             ;                                :exec #(push (float 2))}
    :dconst_0        {:code 14}                             ;                                :exec #(lpush (double 0))}
    :dconst_1        {:code 15}                             ;                                :exec #(lpush (double 1))}
    :bipush          {:code 16 :struct :s1}                 ;                  :exec #(push (byte %)}
    :sipush          {:code 17 :struct :s2}                 ;                  :exec #(push (single %))}
    :ldc             {:code 18 :struct :u2}                 ; (:const )};              :exec #(push '(const %))}
    :ldc_w           {:code 19 :struct :u2}                 ; (:const :cst-long :cst-double)};  :exec #(lpush '(const %))}
    :ldc2_w          {:code 20 :struct :u4}                 ; (:lconst :cst-long :cst-double)}; :exec #(lpush '(lconst %))}
    :iload           {:code 21 :struct :u}                  ; (:local :u)};           :exec #(push '(local %))}
    :lload           {:code 22 :struct :u}                  ; (:local :u)};           :exec #(lpush '(llocal %))}
    :fload           {:code 23 :struct :u}                  ; (:local :u)}
    :dload           {:code 24 :struct :u}                  ; (:local :u)}
    :aload           {:code 25 :struct :u}                  ; (:local :u)}
    :iload_0         {:code 26}
    :iload_1         {:code 27}
    :iload_2         {:code 28}
    :iload_3         {:code 29}
    :lload_0         {:code 30}
    :lload_1         {:code 31}
    :lload_2         {:code 32}
    :lload_3         {:code 33}
    :fload_0         {:code 34}
    :fload_1         {:code 35}
    :fload_2         {:code 36}
    :fload_3         {:code 37}
    :dload_0         {:code 38}
    :dload_1         {:code 39}
    :dload_2         {:code 40}
    :dload_3         {:code 41}
    :aload_0         {:code 42}
    :aload_1         {:code 43}
    :aload_2         {:code 44}
    :aload_3         {:code 45}
    :iaload          {:code 46}
    :laload          {:code 47}
    :faload          {:code 48}
    :daload          {:code 49}
    :aaload          {:code 50}
    :baload          {:code 51}
    :caload          {:code 52}
    :saload          {:code 53}
    :istore          {:code 54 :struct :u}                  ; (:local :u)}
    :lstore          {:code 55 :struct :u}                  ; (:local :u)}
    :fstore          {:code 56 :struct :u}                  ; (:local :u)}
    :dstore          {:code 57 :struct :u}                  ; (:local :u)}
    :astore          {:code 58 :struct :u}                  ; (:local :u)}
    :istore_0        {:code 59}
    :istore_1        {:code 60}
    :istore_2        {:code 61}
    :istore_3        {:code 62}
    :lstore_0        {:code 63}
    :lstore_1        {:code 64}
    :lstore_2        {:code 65}
    :lstore_3        {:code 66}
    :fstore_0        {:code 67}
    :fstore_1        {:code 68}
    :fstore_2        {:code 69}
    :fstore_3        {:code 70}
    :dstore_0        {:code 71}
    :dstore_1        {:code 72}
    :dstore_2        {:code 73}
    :dstore_3        {:code 74}
    :astore_0        {:code 75}
    :astore_1        {:code 76}
    :astore_2        {:code 77}
    :astore_3        {:code 78}
    :iastore         {:code 79}
    :lastore         {:code 80}
    :fastore         {:code 81}
    :dastore         {:code 82}
    :aastore         {:code 83}
    :bastore         {:code 84}
    :castore         {:code 85}
    :sastore         {:code 86}
    :pop             {:code 87}
    :pop2            {:code 88}
    :dup             {:code 89}
    :dup_x1          {:code 90}
    :dup_x2          {:code 91}
    :dup2            {:code 92}
    :dup2_x1         {:code 93}
    :dup2_x2         {:code 94}
    :swap            {:code 95}
    :iadd            {:code 96}
    :ladd            {:code 97}
    :fadd            {:code 98}
    :dadd            {:code 99}
    :isub            {:code 100}
    :lsub            {:code 101}
    :fsub            {:code 102}
    :dsub            {:code 103}
    :imul            {:code 104}
    :lmul            {:code 105}
    :fmul            {:code 106}
    :dmul            {:code 107}
    :idiv            {:code 108}
    :ldiv            {:code 109}
    :fdiv            {:code 110}
    :ddiv            {:code 111}
    :irem            {:code 112}
    :lrem            {:code 113}
    :frem            {:code 114}
    :drem            {:code 115}
    :ineg            {:code 116}
    :lneg            {:code 117}
    :fneg            {:code 118}
    :dneg            {:code 119}
    :ishl            {:code 120}
    :lshl            {:code 121}
    :ishr            {:code 122}
    :lshr            {:code 123}
    :iushr           {:code 124}
    :lushr           {:code 125}
    :iand            {:code 126}
    :land            {:code 127}
    :ior             {:code 128}
    :lor             {:code 129}
    :ixor            {:code 130}
    :lxor            {:code 131}
    :iinc            {:code 132 :struct [:local :u          ; (:local :u)
                                         :increment :u2]}   ; (:const :cst-integer)]}
    :i2l             {:code 133}
    :i2f             {:code 134}
    :i2d             {:code 135}
    :l2i             {:code 136}
    :l2f             {:code 137}
    :l2d             {:code 138}
    :f2i             {:code 139}
    :f2l             {:code 140}
    :f2d             {:code 141}
    :d2i             {:code 142}
    :d2l             {:code 143}
    :d2f             {:code 144}
    :i2b             {:code 145}
    :i2c             {:code 146}
    :i2s             {:code 147}
    :lcmp            {:code 148}
    :fcmpl           {:code 149}
    :fcmpg           {:code 150}
    :dcmpl           {:code 151}
    :dcmpg           {:code 152}
    :ifeq            {:code 153 :struct :s4}                ; :branch}
    :ifne            {:code 154 :struct :s4}                ; :branch}
    :iflt            {:code 155 :struct :s4}                ; :branch}
    :ifge            {:code 156 :struct :s4}                ; :branch}
    :ifgt            {:code 157 :struct :s4}                ; :branch}
    :ifle            {:code 158 :struct :s4}                ; :branch}
    :if_icmpeq       {:code 159 :struct :s4}                ; :branch}
    :if_icmpne       {:code 160 :struct :s4}                ; :branch}
    :if_icmplt       {:code 161 :struct :s4}                ; :branch}
    :if_icmpge       {:code 162 :struct :s4}                ; :branch}
    :if_icmpgt       {:code 163 :struct :s4}                ; :branch}
    :if_icmple       {:code 164 :struct :s4}                ; :branch}
    :if_acmpeq       {:code 165 :struct :s4}                ; :branch}
    :if_acmpne       {:code 166 :struct :s4}                ; :branch}
    :goto            {:code 167 :struct :s4}                ; :branch}
    :jsr             {:code 168 :struct :s4}                ; :branch}
    :ret             {:code 169}
    :tableswitch     {:code 170 :struct [:padding :padding
                                         :default :s4
                                         :offsets (:array (:range :s4 :s4) :s4)]}
    :lookupswitch    {:code 171 :struct [:padding :padding
                                         :default :s4
                                         :pairs (:array :u4 (:struct :case))]}
    :ireturn         {:code 172}
    :lreturn         {:code 173}
    :freturn         {:code 174}
    :dreturn         {:code 175}
    :areturn         {:code 176}
    :return          {:code 177}
    :getstatic       {:code 178 :struct :u2}                ; (:const :cst-field)}
    :putstatic       {:code 179 :struct :u2}                ; (:const :cst-field)}
    :getfield        {:code 180 :struct :u2}                ; (:const :cst-field)}
    :putfield        {:code 181 :struct :u2}                ; (:const :cst-field)}
    :invokevirtual   {:code 182 :struct :u2}                ; (:const :cst-method)}
    :invokespecial   {:code 183 :struct :u2}                ; (:const :cst-method)}
    :invokestatic    {:code 184 :struct :u2}                ; (:const :cst-method)}
    :invokeinterface {:code 185 :struct [:method :u2        ; (:const :cst-interface)
                                         :type :u1
                                         :zero :u1]}
    :xxxunusedxxx1   {:code 186}
    :new             {:code 187 :struct :u2}                ; (:const :cst-class)}
    :newarray        {:code 188 :struct :u1}                ; :atype}
    :anewarray       {:code 189 :struct :u2}                ; (:const :cst-utf8)}
    :arraylength     {:code 190}
    :athrow          {:code 191}
    :checkcast       {:code 192 :struct :u2}                ; (:const :cst-class)}
    :instanceof      {:code 193 :struct :u2}                ; (:const :cst-class)}
    :monitorenter    {:code 194}
    :monitorexit     {:code 195}
    :wide            {:code 196 :struct :wide-opcode}
    :multianewarray  {:code 197 :struct [:type :u2          ; (:const :cst-type)
                                         :nb-dim :u1]}
    :ifnull          {:code 198 :struct :s4}                ; :branch}
    :ifnonnull       {:code 199 :struct :s4}                ; :branch}
    :goto_w          {:code 200 :struct :s8}                ; :branch_w}
    :jsr_w           {:code 201 :struct :s8}                ; :branch_w}
    :breakpoint      {:code 202}
    :impdep1         {:code 254}
    :impdep2         {:code 255}})

(def
  opcodes-by-code
  "Each element is: {code {:key key ...}}"
  (swap-keys :code :key opcodes-by-key))

(comment opcodes-by-code)

;;; 
(defn get-elem-key
  "Gives the key value of the element value of map table"
  [table elem key]
  (-> table
      (get-in elem key)))


 
