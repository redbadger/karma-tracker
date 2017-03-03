(ns karma-tracker.events
  (:require [clojure.spec :as s]))

(def types {"PullRequestEvent"              :pull-request
            "PullRequestReviewEvent"        :pull-request-review
            "PullRequestReviewCommentEvent" :pull-request-review-comment
            "IssuesEvent"                   :issue
            "IssueCommentEvent"             :issue-comment
            "PushEvent"                     :push})

(defn get-type [type]
  (get types type))

(def valid?
  (let [valid-types (-> types keys set)]
    #(contains? valid-types (:type %))))

(defmulti additional-info :type)

(defmethod additional-info :default [event] nil)

(defmethod additional-info "PushEvent" [event]
  {:commits (-> event :payload :commits count)})

(s/def ::type (-> types vals set))
(s/def ::repo string?)
(s/def ::user string?)
(s/def ::created-at string?)
(s/def ::commits int?)
(s/def ::event (s/keys :req-un [::type ::repo ::user ::created-at]
                       :opt-un [::commits]))

(defn normalize [{:keys [type repo actor created_at] :as event}]
  ;; {:post [(s/valid? ::event %)]}
  (merge {:type       (get-type type)
          :repo       (:name repo)
          :user       (:login actor)
          :created-at created_at}
         (additional-info event)))

(def transform
  (comp (filter valid?)
        (map normalize)))
