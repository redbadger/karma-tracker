(ns karma-tracker.cli)

(def commands #{"update" "report" "api"})

(defmulti parse-args first)

(defmethod parse-args :default [[command]]
  [:unknown command])

(defmethod parse-args "update" [_]
  [:update])

(defmethod parse-args "api" [_]
  [:api])

(defmulti execute
  (fn [_ [command] & args]
    command))

(defmethod execute :default [execution-fn command]
  (-> command execution-fn))

(defmethod execute :unknown [_ [_ command]]
  [:error (str "Command " command " not valid")])

(defmethod execute :error [_ command]
  command)


(defmulti show-result
  (fn [[event-name] & args]
    event-name))

(defmethod show-result :default [_])

(defmethod show-result :events-updated [_]
  (println "Events updated succesfully"))

(defmethod show-result :error [[_ message]]
  (println "Error:" message))

(defn run [execution-fn args]
  (->> args
       parse-args
       (execute execution-fn)
       show-result))
