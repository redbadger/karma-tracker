(defproject karma-tracker-ui "0.1.0-SNAPSHOT"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.9.229"]
                 [com.andrewmcveigh/cljs-time "0.4.0"]
                 [kibu/pushy "0.3.6"]
                 [reagent "0.6.0"]
                 [re-frame "0.9.2"]
                 [day8.re-frame/http-fx "0.1.3"]
                 [secretary "1.2.3"]]

  :plugins [[lein-cljsbuild "1.1.4"]]

  :min-lein-version "2.5.3"

  :source-paths ["src/clj"]

  :profiles {:dev {:dependencies [[binaryage/devtools "0.8.2"]]}}

  :cljsbuild {:builds [{:id "development"
                        :source-paths ["src/cljs"]
                        :compiler {:main karma-tracker-ui.core
                                   :output-to "target/js/app.js"
                                   :output-dir "target/js/out"
                                   :asset-path ""
                                   :source-map-timestamp true
                                   :preloads [devtools.preload]
                                   :external-config {:devtools/config {:features-to-install :all}}}}

                       {:id "production"
                        :source-paths ["src/cljs"]
                        :compiler {:main karma-tracker-ui.core
                                   :output-to "target/js/app.js"
                                   :optimizations :advanced
                                   :closure-defines {goog.DEBUG false
                                                     karma-tracker-ui.config.api-url "https://karma.red-badger.com/api"}
                                   :pretty-print false}}]})
