(defproject jvm-clj "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [typed "0.1.6"]]
  :plugins [[lein-midje "3.0.0"]]
  :main jvm-clj.core
  :profiles {:uberjar {:aot :all
                       :main jvm-clj.core}
             :dev {:dependencies [[midje "1.5.1"]]}})
