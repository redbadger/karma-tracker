(ns karma-tracker-ui.views.svg)

(defn icon [id class]
  [:svg {:class class}
   [:use {:xlinkHref (str "/assets/icons.svg#" id)}]])
