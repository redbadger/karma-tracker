(defproject scraper "0.1.0-SNAPSHOT"
  :description "Scrapes GitHub for open-source contributions by an organisation's members"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :repositories [["jitpack" "https://jitpack.io"]]
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [com.github.raynes/tentacles "0e16d9f"]
                 [cheshire "5.7.0"]
                 [clj-time "0.13.0"]
                 [environ "1.1.0"]]
  :main ^:skip-aot scraper.core
  :target-path "target/%s"
  :profiles {:uberjar {:aot :all}})
