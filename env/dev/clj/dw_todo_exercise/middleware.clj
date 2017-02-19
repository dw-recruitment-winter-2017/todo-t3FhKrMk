(ns dw-todo-exercise.middleware
  (:require [ring.middleware.defaults :refer [site-defaults wrap-defaults
                                              api-defaults]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn wrap-site-middleware [handler]
  (-> handler
      (wrap-defaults site-defaults)
      wrap-exceptions
      wrap-reload))

(defn wrap-api-middleware [handler]
  (-> handler
      (wrap-defaults api-defaults)
      wrap-reload))