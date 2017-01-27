(defproject karma-tracker "0.1.0-SNAPSHOT"
  :description "Open source contributions' tracker for organisations"
  :url "https://github.com/redbadger/karma-tracker"
  :license {:name "General Public License 3.0"
            :url "https://www.gnu.org/licenses/gpl-3.0.en.html"}
  :repositories [["jitpack" "https://jitpack.io"]]
  :dependencies [[org.clojure/clojure "1.9.0-alpha14"]
                 [com.github.raynes/tentacles "0e16d9f"]
                 [com.taoensso/nippy "2.12.2"]
                 [environ "1.1.0"]]
  :plugins [[lein-environ "1.1.0"]]
  :main ^:skip-aot karma-tracker.core
  :target-path "target/%s"
  :clean-targets [:target-path :compile-path [:env :cache-dir]]
  :env {:cache-dir ".karma-cache"}
  :profiles {:uberjar {:aot :all}
             :test {:env {:cache-dir "test/.karma-cache"}}})
