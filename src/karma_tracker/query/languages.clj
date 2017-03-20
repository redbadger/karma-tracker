(ns karma-tracker.query.languages
  (:require [clojure.string :as s]
            [karma-tracker
             [cache :as cache]
             [github :as gh]]
            [karma-tracker.query.utils :refer :all]))

(defn- fetch-and-save-languages [cache github-conn repo-name]
  (let [languages (->> (s/split repo-name #"\/")
                       (apply gh/repo-languages github-conn))]
    (if-not (gh/error-response? languages)
      (cache/write cache repo-name languages)
      languages)))

(defn- load-or-fetch-languages [cache github-conn repo-name]
  (if-let [languages (cache/read cache repo-name)]
    languages
    (fetch-and-save-languages cache github-conn repo-name)))

(defn- load-repos-languages [cache github-conn repos]
  (->> repos
       (map (fn [repo]
              [repo (s/split repo #"\/")]))
       (map (fn [[full-name [user repo]]]
              [full-name (load-or-fetch-languages cache github-conn full-name)]))
       (remove (fn [[full-name response]]
                 (gh/error-response? response)))
       (mapcat identity)
       (apply hash-map)))

(defn- repos [events]
  "Aggregates events into a set of repositories names"
  (->> events (map :repo) set))

(defn repos-languages [cache github-conn events]
  (->> events
       (repos)
       (load-repos-languages cache github-conn)))

(defn languages [cache github-conn events]
  (->> events
       (repos-languages cache github-conn)
       vals
       (mapcat keys)
       frequencies
       map-vals->percentage
       map-vals->rank
       (map (fn [[language values]]
              [(name language) values]))
       rank->maps))
