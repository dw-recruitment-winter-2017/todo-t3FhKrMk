(ns dw-todo-exercise.core
    (:require [reagent.core :as reagent :refer [atom]]
              [reagent.session :as session]
              [secretary.core :as secretary :include-macros true]
              [accountant.core :as accountant]
              [dw-todo-exercise.todo :as todo]))

;; -------------------------
;; Views

(defn home-page []
  [:div [:h2 "Welcome to DW TODO"]
   [:div [:a {:href "/about"} "about"]]
   [:div [todo/todo-list]]])

(defn about-page []
  [:div [:h2 "DW TODO"]
   [:p "This TODO application is for my job application to Democracy Works. It was fun!"]
   [:div [:a {:href "/"} "home"]]])

(defn current-page []
  [:div [(session/get :current-page)]])

;; -------------------------
;; Routes

(secretary/defroute "/" []
  (session/put! :current-page #'home-page))

(secretary/defroute "/about" []
  (session/put! :current-page #'about-page))

;; -------------------------
;; Initialize app

(defn mount-root []
  (reagent/render [current-page] (.getElementById js/document "app")))

(defn init! []
  (accountant/configure-navigation!
    {:nav-handler
     (fn [path]
       (secretary/dispatch! path))
     :path-exists?
     (fn [path]
       (secretary/locate-route path))})
  (accountant/dispatch-current!)
  (mount-root))
