(defproject karma-tracker "0.1.0-SNAPSHOT"
  :description "Open source contributions' tracker for organisations"
  :url "https://github.com/redbadger/karma-tracker"
  :license {:name "General Public License 3.0"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :repositories [["jitpack" "https://jitpack.io"]]
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [com.github.raynes/tentacles "0e16d9f"]]
  :main ^:skip-aot karma-tracker.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}
             :test {:resource-paths ["resources-test"]}})
