(ns karma-tracker-ui.db
  (:require [cljs.spec :as spec]
            [clojure.string :as string]
            [cljs-time.core :refer [date-time]])
  (:import goog.date.DateTime))

(spec/def ::state #{:initializing :loading :ready :error})

(spec/def ::date #(instance? goog.date.DateTime %))

(spec/def ::count nat-int?)

(spec/def ::change number?)

(spec/def ::contribution-total (spec/keys :req-un [::count]
                                          :opt-un [::change]))

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

(spec/def ::db (spec/keys :req-un [::state]
                          :opt-un [::date ::contribution-totals ::repositories ::languages ::contributors]))

(def default
  {:state :initializing})
