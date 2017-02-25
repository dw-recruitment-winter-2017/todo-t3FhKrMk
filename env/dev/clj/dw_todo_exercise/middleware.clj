(ns dw-todo-exercise.middleware
  (:require [dw-todo-exercise.db :as db]
            [dw-todo-exercise.example-todos :as examples]
            [ring.middleware.defaults :refer [site-defaults wrap-defaults
                                              api-defaults]]
            [prone.middleware :refer [wrap-exceptions]]
            [ring.middleware.reload :refer [wrap-reload]]))

(defn wrap-create-example-todos [handler]
  (fn [{:keys [db] :as req}]
    (when (empty? @db)
      (doseq [todo examples/this-project]
        (db/create! db todo)
        (Thread/sleep 1))) ; ensure created-at increments
    (handler req)))

(defn wrap-site-middleware [handler]
  (-> handler
      (wrap-defaults site-defaults)
      wrap-exceptions
      wrap-reload))

(defn wrap-api-middleware [handler]
  (-> handler
      (wrap-defaults api-defaults)
      wrap-reload
      wrap-create-example-todos))