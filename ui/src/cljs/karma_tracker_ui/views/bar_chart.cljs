(ns karma-tracker-ui.views.bar-chart)

(defn bar-style [value scale]
  {:width (-> value (* scale) (.toFixed 2) (str "%"))})
