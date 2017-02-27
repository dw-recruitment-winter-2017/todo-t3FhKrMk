(ns dw-todo-exercise.handler.api
  "API ring handlers and middleware"
  (:require [clojure.string :as str]
            [clojure.edn :as edn]
            [compojure.core :refer [GET POST PUT DELETE OPTIONS defroutes
                                    routes context]]
            [compojure.route :refer [not-found]]
            [dw-todo-exercise.middleware :refer [wrap-api-middleware]]
            [dw-todo-exercise.http :as http]))

(defn allow-all-cors
  "Simple handler for allowing all CORS requests. Used for responding to
  OPTIONS preflight requests."
  [req]
  {:status 200
   :headers {"Access-Control-Allow-Methods" "GET POST PUT DELETE"
             "Access-Control-Allow-Origin" (get-in req [:headers "origin"])
             "Access-Control-Allow-Credentials" "true"}
   :body "preflight complete"})

(defn wrap-allow-all-cors
  "Ring middleware that allows all CORS requests. Obviously not appropriate
  for anything real, but gets this coding exercise app up and running. Uses
  `allow-all-cors` to generate the appropriate response headers."
  [handler]
  (fn [req]
    (let [resp (handler req)
          cors-headers (:headers (allow-all-cors req))]
      (update resp :headers merge cors-headers))))

(defn wrap-edn
  "Parses EDN request bodies and renders response bodies back into EDN (and
  also sets the Content-Type header to application/edn). Allows all other
  handlers to deal with request and response bodies as Clojure values instead
  of parsing and rendering."
  [handler]
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

(defroutes api-routes
  (context "/api" []
    (OPTIONS "/*" req (allow-all-cors req))
    (GET "/todos" req (http/get-all req))
    (POST "/todos" req (http/create req))
    (PUT "/todos/:id" [id :as req] (http/update (keyword id) req))
    (DELETE "/todos/:id" [id :as req] (http/delete (keyword id) req))
    (GET "/todos/:id" [id :as req] (http/get (keyword id) req))
    (not-found {:error :not-found})))

(def ring-handler
  (-> #'api-routes
      wrap-allow-all-cors
      wrap-edn
      wrap-api-middleware))