;   jvm-clj  JVM management library
;   Copyright (c) 2013 Ivan Pierre <me@ivanpierre.ch>. All rights reserved.
;
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns ^{:doc "Definition of JVM Class model"}
  jvm-clj.class-struct)

(defonce ^{:doc "Definition of class structure"}
  class-struct
  '(1 [:cla-magic         :u4]
      [:cla-minor-version :u2]
      [:cla-major-version :u2]
      [:cla-constant-pool :cp-info]
      [:cla-access-flags  :access-flags]
      [:vla-this-class    :u2]
      [:cla-super-class   :u2]
      [:cla-interfaces    :interface-info]
      [:cla-fields        :field-info]
      [:cla-attributes    :attribure-info]))

(defonce ^{:doc "Definition of constant structs by keywords"}
  constant-pool-keywords
   {:cst-utf9            '(1  [:utf8         :utf8])
    :cst-integer         '(3  [:value          :s4])
    :cst-float           '(4  [:value          :f4])
    :cst-long            '(5  [:value          :s8])
    :cst-double          '(6  [:value          :f8])
    :cst-class           '(7  [:name           :cst-utf8])
    :scr-string          '(8  [:sring          :cst-utf8])
    :cst-field           '(9  [:class          :cst-class]
                              [:name-and-type  :cst-name-and-type])
    :cst-method          '(10 [:class          :sct-class]
                              [:name-and-type  :cst-name-and-type])
    :cst-interface       '(11 [:class          :sct-clas]
                              [:name-and-type  :cst-name-and-type])
    :cst-name-and-type   '(12 [:name           :cst-utf8]
                              [:descriptor     :cst-utf8])
    :cst-handle          '(15 [:kind           :kind]
                              [:reference      :cst-reference])
    :cst-type            '(16 [:descriptor     :cst-utf8])
    :cst-dynamic         '(18 [:bootstrap      :bootstrap-index]
                              [:name-and-type  :cst-name-and-type])})

(def ^{:doc "Constant pool keyword by code"}
  constant-pool-codes
  (into {} (map (fn [[k v]] {(first v) k]} constant-pool-keyword)))

(defn constant-by-keyword
  "Constant pool by keyword"
  [key]
  (get constant-pool-keywords key))

(defn constant-keword-by-code
  "Constant pool structures by index"
  [code]
  (get constant-pool-codes code))

(defn constant-struct-by-code
  "Constant pool structures by index"
  [code]
  (rest (constant-by-keyword (constant-keyword-by-code code))))

(defn constant-struct-by-keyword
  "Constant pool structures by key"
  [key]
  (rest (constant-by-keyword key)))

(defn constant-code-by-keyword
  "Constant pool index by key"
  [key]
  (first (constant-by-keyword key)))


(defonce ^{:doc "Access modifiers definition"}
  access-flags
  {:public        [0x0001 #{:class :field :method}]
   :private       [0x0002 #{:class :field :method}]
   :protected     [0x0004 #{:class :field :method}]
   :static        [0x0008 #{:field :method}]
   :final         [0x0010 #{:class :field :method}]
   :synchronized  [0x0020 #{:method}]
   :volatile      [0x0040 #{:field}]
   :bridge        [0x0040 #{:method}]
   :varargs       [0x0080 #{:method}]
   :transient     [0x0080 #{:field}]
   :native        [0x0100 #{:method}]
   :interface     [0x0200 #{:class}]
   :abstract      [0x0400 #{:class :method}]
   :strict        [0x0800 #{:method}]
   :synthetic     [0x1000 #{:class :field :method}]
   :annotation    [0x2000 #{:class}]})


(defn good-type?
  "Test type for access"
  [access flags type]
  (get (get (get access-flags access) 1) type))

(defn good-type-and-flag?
  "Test type and flags for access"
  [access flags type]
  (let [access (get access-flags access)]
  (and (get (get access 1) type))
       (not= (bit-and (get access 0) flags)
             0)))

(defn get-access-set
  "Give back the set of access for given flags and type of object"
  [flags type]
  (set (filter #(good-type-and-flag? % flags type)
               (keys access-flags))))

(defn get-access-flag
  "Give back flag from access set"
  [access-set type]
  (reduce + (map #(first (get access-flags %))
                 (filter #(good-type? % type)
                         access-set))))

(defonce ^{:doc "Field structure"}
  field-struct
  '(1 [:access-flags   :field-modifier]
      [:name-index     :u2]
      [:descriptor     :u2]
      [:attributes     :attribute-info]))


(def
  ^{:doc "Each element is:
             {mnemonic [opcode args opstack-pop opstack-push]}"}
  opcodes-by-code
  {:nop             (0)
   :aconst_null     (1)
   :iconst_m1       (2)
   :iconst_0        (3)
   :iconst_1        (4)
   :iconst_2        (5)
   :iconst_3        (6)
   :iconst_4        (7)
   :iconst_5        (8)
   :lconst_0        (9)
   :lconst_1        (10)
   :fconst_0        (11)
   :fconst_1        (12)
   :fconst_2        (13)
   :dconst_0        (14)
   :dconst_1        (15)
   :bipush          (16  [:byte :s1])
   :sipush          (17  [:short :s2])
   :ldc             (18  [:const :u1])
   :ldc_w           (19  [:const-wide :u2])
   :ldc2_w          (20  [:const-wide :u2])
   :iload           (21  [:local :u]) ; :u is :u1 normally and :u2 if prefixed by wide op code
   :lload           (22  [:local :u])
   :fload           (23  [:local :u])
   :dload           (24  [:local :u])
   :aload           (25  [:local :u])
   :iload_0         (26)
   :iload_1         (27)
   :iload_2         (28)
   :iload_3         (29)
   :lload_0         (30)
   :lload_1         (31)
   :lload_2         (32)
   :lload_3         (33)
   :fload_0         (34)
   :fload_1         (35)
   :fload_2         (36)
   :fload_3         (37)
   :dload_0         (38)
   :dload_1         (39)
   :dload_2         (40)
   :dload_3         (41)
   :aload_0         (42)
   :aload_1         (43)
   :aload_2         (44)
   :aload_3         (45)
   :iaload          (46)
   :laload          (47)
   :faload          (48)
   :daload          (49)
   :aaload          (50)
   :baload          (51)
   :caload          (52)
   :saload          (53)
   :istore          (54  [:local :u])
   :lstore          (55  [:local :u])
   :fstore          (56  [:local :u])
   :dstore          (57  [:local :u])
   :astore          (58  [:local :u])
   :istore_0        (59)
   :istore_1        (60)
   :istore_2        (61)
   :istore_3        (62)
   :lstore_0        (63)
   :lstore_1        (64)
   :lstore_2        (65)
   :lstore_3        (66)
   :fstore_0        (67)
   :fstore_1        (68)
   :fstore_2        (69)
   :fstore_3        (70)
   :dstore_0        (71)
   :dstore_1        (72)
   :dstore_2        (73)
   :dstore_3        (74)
   :astore_0        (75)
   :astore_1        (76)
   :astore_2        (77)
   :astore_3        (78)
   :iastore         (79)
   :lastore         (80)
   :fastore         (81)
   :dastore         (82)
   :aastore         (83)
   :bastore         (84)
   :castore         (85)
   :sastore         (86)
   :pop             (87)
   :pop2            (88)
   :dup             (89)
   :dup_x1          (90)
   :dup_x2          (91)
   :dup2            (92)
   :dup2_x1         (93)
   :dup2_x2         (94)
   :swap            (95)
   :iadd            (96)
   :ladd            (97)
   :fadd            (98)
   :dadd            (99)
   :isub            (100)
   :lsub            (101)
   :fsub            (102)
   :dsub            (103)
   :imul            (104)
   :lmul            (105)
   :fmul            (106)
   :dmul            (107)
   :idiv            (108)
   :ldiv            (109)
   :fdiv            (110)
   :ddiv            (111)
   :irem            (112)
   :lrem            (113)
   :frem            (114)
   :drem            (115)
   :ineg            (116)
   :lneg            (117)
   :fneg            (118)
   :dneg            (119)
   :ishl            (120)
   :lshl            (121)
   :ishr            (122)
   :lshr            (123)
   :iushr           (124)
   :lushr           (125)
   :iand            (126)
   :land            (127)
   :ior             (128)
   :lor             (129)
   :ixor            (130)
   :lxor            (131)
   :iinc            (132 [:local :u]
                         [:const :u2])
   :i2l             (133)
   :i2f             (134)
   :i2d             (135)
   :l2i             (136)
   :l2f             (137)
   :l2d             (138)
   :f2i             (139)
   :f2l             (140)
   :f2d             (141)
   :d2i             (142)
   :d2l             (143)
   :d2f             (144)
   :i2b             (145)
   :i2c             (146)
   :i2s             (147)
   :lcmp            (148)
   :fcmpl           (149)
   :fcmpg           (150)
   :dcmpl           (151)
   :dcmpg           (152)
   :ifeq            (153 [:branch :s2])
   :ifne            (154 [:branch :s2])
   :iflt            (155 [:branch :s2])
   :ifge            (156 [:branch :s2])
   :ifgt            (157 [:branch :s2])
   :ifle            (158 [:branch :s2])
   :if_icmpeq       (159 [:branch :s2])
   :if_icmpne       (160 [:branch :s2])
   :if_icmplt       (161 [:branch :s2])
   :if_icmpge       (162 [:branch :s2])
   :if_icmpgt       (163 [:branch :s2])
   :if_icmple       (164 [:branch :s2])
   :if_acmpeq       (165 [:branch :s2])
   :if_acmpne       (166 [:branch :s2])
   :goto            (167 [:branch :s2])
   :jsr             (168 [:branch :s2])
   :ret             (169)
   :tableswitch     (170 [:align-pad       :padding]
                         [:default         :s4]
                         [:low             :s4]
                         [:high            :s4]
                         [:offsets         :offsets])
   :lookupswitch    (171 [:align-pad       :padding]
                         [:default         :s4]
                         [:n-pairs         :s4]
                         [:match-pairs     :pairs])
   :ireturn         (172)
   :lreturn         (173)
   :freturn         (174)
   :dreturn         (175)
   :areturn         (176)
   :return          (177)
   :getstatic       (178 [:const :u2])
   :putstatic       (179 [:const :u2])
   :getfield        (180 [:const :u2])
   :putfield        (181 [:const :u2])
   :invokevirtual   (182 [:const :u2])
   :invokespecial   (183 [:const :u2])
   :invokestatic    (184 [:const :u2])
   :invokeinterface (185 [:const :u2]
                         [:nargs :u1]
                         [:zero  :u1])
   :xxxunusedxxx1   (186)
   :new             (187 [:const :u2])
   :newarray        (188 [:atype :u1])
   :anewarray       (189 [:const :u2])
   :arraylength     (190)
   :athrow          (191)
   :checkcast       (192 [:const :u2])
   :instanceof      (193 [:const :u2])
   :monitorenter    (194)
   :monitorexit     (195)
   :wide            (196 [:wide-opcode :instruction])
   :multianewarray  (197 [:const :u2]
                         [:dimention :u1])
   :ifnull          (198 [:branch :u2])
   :ifnonnull       (199 [:branch :u2])
   :goto_w          (200 [:branch-wide])
   :jsr_w           (201 [:branch-wide])
   :breakpoint      (202)
   :impdep1         (254)
   :impdep2         (255)})

