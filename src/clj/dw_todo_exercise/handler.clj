(ns dw-todo-exercise.handler
  (:require [compojure.core :refer [GET POST PUT DELETE OPTIONS defroutes
                                    routes context]]
            [compojure.route :refer [not-found resources]]
            [hiccup.page :refer [include-js include-css html5]]
            [dw-todo-exercise.middleware :refer [wrap-site-middleware
                                                 wrap-api-middleware]]
            [config.core :refer [env]]
            [dw-todo-exercise.todos :as todos]
            [clojure.edn :as edn]
            [ring.middleware.cors :as cors]
            [clojure.string :as str]))

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

(defn allow-all-cors [req]
  {:status 200
   :headers {"Access-Control-Allow-Methods" "GET POST PUT DELETE"
             "Access-Control-Allow-Origin" (get-in req [:headers "origin"])
             "Access-Control-Allow-Credentials" "true"}
   :body "preflight complete"})

(defn wrap-allow-all-cors [handler]
  (fn [req]
    (let [resp (handler req)
          cors-headers (:headers (allow-all-cors req))]
      (update resp :headers merge cors-headers))))

(defn wrap-edn [handler]
  (fn [req]
    (let [content-type (get-in req [:headers "content-type"])
          response (if (and content-type (str/starts-with? content-type
                                                           "application/edn"))
                     (-> req
                         (update :body (comp edn/read-string slurp))
                         handler)
                     (handler req))]
      (-> response
          (update :body pr-str)
          (assoc-in [:headers "Content-Type"]
                    "application/edn; charset=UTF-8")))))

(defroutes site-routes
  (GET "/" [] (loading-page))
  (GET "/about" [] (loading-page))
  (resources "/")
  (not-found "Not Found"))

(defroutes api-routes
  (context "/api" []
    (OPTIONS "/*" req (allow-all-cors req))
    (GET "/todos" [] (todos/http-get-all))
    (POST "/todos" req (todos/http-create req))
    (PUT "/todos/:id" [id :as req] (todos/http-update (keyword id) req))
    (DELETE "/todos/:id" [id] (todos/http-delete (keyword id)))
    (GET "/todos/:id" [id] (todos/http-get (keyword id)))
    (not-found {:error :not-found})))

(def app-handler
  (-> #'site-routes
      wrap-site-middleware))

(def api-handler
  (-> #'api-routes
      wrap-allow-all-cors
      wrap-edn
      wrap-api-middleware))

(def app
  (fn [req]
    (if (str/starts-with? (:uri req) "/api/")
      (api-handler req)
      (app-handler req))))



