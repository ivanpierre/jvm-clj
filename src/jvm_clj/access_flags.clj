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

(ns jvm-clj.access-flags)

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

