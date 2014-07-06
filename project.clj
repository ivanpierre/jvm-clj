(defproject jvm-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/ivanpierre/jvm-clj"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [typed "0.1.6"]
                 [clojurewerkz/buffy "1.0.0-beta1"]]       ; https://github.com/clojurewerkz/buffy
  :main jvm-clj.core
  :profiles {:uberjar {:aot :all
                       :main jvm-clj.core}})
