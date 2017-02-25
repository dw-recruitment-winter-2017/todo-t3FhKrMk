(ns dw-todo-exercise.handler
  (:require [dw-todo-exercise.handler.api :as api]
            [dw-todo-exercise.handler.app :as app]
            [clojure.string :as str]))

(defonce db (atom {}))

(def app
  (fn [req]
    (if (str/starts-with? (:uri req) "/api/")
      (api/handler (assoc req :db db))
      (app/handler req))))



