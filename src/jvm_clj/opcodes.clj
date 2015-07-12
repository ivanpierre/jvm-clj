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

(ns jvm-clj.opcodes
  (:require [jvm-clj.utils :refer [swap-keys]]))

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

