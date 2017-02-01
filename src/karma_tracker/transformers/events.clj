(ns karma-tracker.transformers.events)

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

(defn normalize [{:keys [type repo actor created_at] :as event}]
  (merge {:type       (get-type type)
          :created-at created_at
          :repo       (select-keys repo [:name :url])
          :user       (select-keys actor [:login :url])}))

(def transform
  (comp (filter valid?)
        (map normalize)))
