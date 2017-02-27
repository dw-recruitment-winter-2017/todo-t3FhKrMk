(ns dw-todo-exercise.handler.app
  "web app ring handlers"
  (:require [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found resources]]
            [config.core :refer [env]]
            [dw-todo-exercise.middleware :refer [wrap-site-middleware]]
            [hiccup.page :refer [include-js include-css html5]]))

(def mount-target
  [:div#app {:class "container"}
   [:h3 "ClojureScript has not been compiled!"]
   [:p "please run "
    [:b "lein figwheel"]
    " in order to start the compiler"]])

(defn head []
  [:head
   [:meta {:charset "utf-8"}]
   [:meta {:name "viewport"
           :content "width=device-width, initial-scale=1"}]
   [:link {:rel "stylesheet"
           :href "//fonts.googleapis.com/css?family=Roboto:300,300italic,700,700italic"}]
   [:link {:rel "stylesheet"
           :href "//cdn.rawgit.com/necolas/normalize.css/master/normalize.css"}]
   [:link {:rel "stylesheet"
           :href "//cdn.rawgit.com/milligram/milligram/master/dist/milligram.min.css"}]
   (include-css (if (env :dev) "/css/site.css" "/css/site.min.css"))])

(defn loading-page []
  (html5
   (head)
   [:body {:class "body-container"}
    mount-target
    (include-js "/js/app.js")]))

(defroutes site-routes
  (GET "/" [] (loading-page))
  (GET "/about" [] (loading-page))
  (resources "/")
  (not-found "Not Found"))

(def ring-handler
  (-> #'site-routes
      wrap-site-middleware))