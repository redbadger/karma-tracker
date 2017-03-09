(ns karma-tracker-ui.effects
  (:require [re-frame.core :as re-frame]
            [karma-tracker-ui.routes :as routes]))

(re-frame/reg-fx
 :redirect
 (fn [route]
   (routes/set! route)))
