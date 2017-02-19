(ns dw-todo-exercise.middleware
  (:require [ring.middleware.defaults :refer [site-defaults wrap-defaults
                                              api-defaults]]))

(defn wrap-site-middleware [handler]
  (wrap-defaults handler site-defaults))

(defn wrap-api-middleware [handler]
  (wrap-defaults handler api-defaults))