(ns karma-tracker-ui.db
  (:require [cljs.spec :as spec]
            [clojure.string :as string]
            [cljs-time.core :refer [date-time]])
  (:import goog.date.DateTime))

(spec/def ::date #(instance? goog.date.DateTime %))

(spec/def ::count nat-int?)

(spec/def ::change number?)

(spec/def ::contribution-total (spec/keys :req-un [::count] :opt-un [::change]))

(spec/def ::commits ::contribution-total)

(spec/def ::pull-requests ::contribution-total)

(spec/def ::issues ::contribution-total)

(spec/def ::comments ::contribution-total)

(spec/def ::total ::contribution-total)

(spec/def ::contribution-totals (spec/keys :req-un [::commits ::pull-requests ::issues ::comments ::total]))

(spec/def ::repository-name (spec/and string? #(string/includes? % "/")))

(spec/def ::repositories (spec/map-of ::repository-name ::contribution-totals))

(spec/def ::languages (spec/map-of string? ::count))

(spec/def ::username string?)

(spec/def ::avatar string?)

(spec/def ::contributor (spec/keys :req-un [::username ::avatar]))

(spec/def ::contributors (spec/coll-of ::contributor))

(spec/def ::db (spec/keys :opt-un [::date ::contribution-totals ::repositories ::languages ::contributors]))

(def default
  {:date (date-time 2017 12)

   :contribution-totals {:commits {:count 42, :change 0.095}
                         :pull-requests {:count 20, :change 0.00}
                         :issues {:count 15, :change -0.20}
                         :comments {:count 80, :change 1.20}
                         :total {:count 157, :change 0.50}}

   :repositories (array-map "redbadger/website-honestly" {:commits {:count 123}
                                                          :pull-requests {:count 10}
                                                          :issues {:count 5}
                                                          :comments {:count 14}
                                                          :total {:count 152}}
                            "reactjs/react-redux" {:commits {:count 5}
                                                   :pull-requests {:count 3}
                                                   :issues {:count 10}
                                                   :comments {:count 58}
                                                   :total {:count 76}})

   :languages (array-map "JavaScript" 9000
                         "Clojure" 5000)

   :contributors [{:username "gregstewart", :avatar "https://avatars1.githubusercontent.com/u/474076?v=3&s=240"}
                  {:username "haines", :avatar "https://avatars3.githubusercontent.com/u/785641?v=3&s=240"}
                  {:username "lazydevorg", :avatar "https://avatars3.githubusercontent.com/u/655705?v=3&s=240"}
                  {:username "mveritym", :avatar "https://avatars1.githubusercontent.com/u/1009524?v=3&s=240"}
                  {:username "Roisi", :avatar "https://avatars2.githubusercontent.com/u/8049847?v=3&s=240"}
                  {:username "sarigriffiths", :avatar "https://avatars2.githubusercontent.com/u/3176873?v=3&s=240"}
                  {:username "stevecottle", :avatar "https://avatars1.githubusercontent.com/u/11883999?v=3&s=240"}]})
