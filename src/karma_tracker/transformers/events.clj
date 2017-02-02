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
          :repo       (:name repo)
          :user       (:login actor)
          :created-at created_at}))

(def transform
  (comp (filter valid?)
        (map normalize)))
