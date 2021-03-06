(defproject karma-tracker "0.1.0-SNAPSHOT"
  :description "Open source contributions' tracker for organisations"
  :url "https://github.com/redbadger/karma-tracker"
  :license {:name "General Public License 3.0"
            :url  "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :repositories [["jitpack" "https://jitpack.io"]]
  :dependencies [[com.github.raynes/tentacles "0e16d9f"]
                 [com.github.michaelklishin/monger "6e333bb" :exclusions [org.mongodb/mongodb-driver]]
                 [org.mongodb/mongodb-driver "3.4.2"]
                 [clj-time "0.13.0"]
                 [environ "1.1.0"]
                 [org.clojure/clojure "1.9.0-alpha14"]
                 [cljstache "2.0.0"]
                 [ring "1.5.1"]
                 [ring-cors "0.1.9"]
                 [compojure "1.5.2"]
                 [cheshire "5.7.0"]]
  :plugins [[lein-environ "1.1.0"]
            [lein-ring "0.11.0"]]
  :main ^:skip-aot karma-tracker.core
  :target-path "target/%s"
  :env {:organisation "redbadger"
        :mongodb-uri  "mongodb://127.0.0.1:27017/karma-tracker"}
  :ring {:handler karma-tracker.api/dev-handler}
  :profiles {:shared  {:source-paths   ["dev"]
                       :resource-paths ["resources-test"]
                       :dependencies   [[org.slf4j/slf4j-nop "1.7.12"]]}
             :uberjar {:aot :all}
             :ci      {:plugins [[lein-test-report-junit-xml "0.2.0"]]}
             :repl    [:shared]
             :test    [:shared {:env {:mongodb-uri "mongodb://127.0.0.1:27017/karma-tracker-test"}}]
             :dev     [:shared]}
  :aliases {"dev-api" ["with-profile" "repl" "ring" "server-headless"]})
