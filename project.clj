(defproject jvm-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/ivanpierre/jvm-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.7.0-RC1"]
                 [camel-snake-kebab "0.3.2"]
                 [clojurewerkz/buffy "1.0.0-beta1"]]       ; https://github.com/clojurewerkz/buffy
  :main jvm-clj.core
  :profiles {:uberjar {:aot :all
                       :main jvm-clj.core}})
