(defproject karma-tracker "0.1.0-SNAPSHOT"
  :description "Open source contributions' tracker for organisations"
  :url "https://github.com/redbadger/karma-tracker"
  :license {:name "General Public License 3.0"
            :url  "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :repositories [["jitpack" "https://jitpack.io"]]
  :dependencies [[com.github.raynes/tentacles "0e16d9f"]
                 [com.github.michaelklishin/monger "6e333bb"]
                 [clj-time "0.13.0"]
                 [environ "1.1.0"]
                 [org.clojure/clojure "1.9.0-alpha14"]
                 [cljstache "2.0.0"]]
  :plugins [[lein-environ "1.1.0"]]
  :main ^:skip-aot karma-tracker.core
  :target-path "target/%s"
  :env {:organisation "redbadger"
        :mongodb-uri "mongodb://127.0.0.1:27017/karma-tracker"}
  :profiles {:uberjar {:aot :all}
             :test {:resource-paths ["resources-test"]
                    :env {:mongodb-uri "mongodb://127.0.0.1:27017/karma-tracker-test"}}
             :ci {:plugins [[lein-test-report-junit-xml "0.2.0"]]}})
