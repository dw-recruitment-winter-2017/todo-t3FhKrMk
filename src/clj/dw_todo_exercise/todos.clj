(ns dw-todo-exercise.todos
  (:require [dw-todo-exercise.util :as util]
            [dw-todo-exercise.http :as http])
  (:refer-clojure :exclude [get]))

(defonce db (atom (sorted-map)))

(defn get [db id]
  (when-let [todo (clojure.core/get db id)]
    (assoc todo :id id)))

(defn get-all [db]
  (map #(get db %) (keys db)))

(defn http-get-all []
  (http/success-response (get-all @db)))

(defn update! [db id todo]
  (-> db
      (swap! update id merge todo)
      (get id)))

(def create! update!)

(defn http-create [req]
  (try
    (let [todo-params (:body req)
          todo (create! db (util/title->id (:title todo-params)) todo-params)]
      (http/created-response todo (str "/api/todos/" (name (:id todo)))))
    (catch Exception e
      (http/server-error (.getMessage e)))))

(defn http-update [id req]
  (try
    (if (get @db id)
      (let [todo (update! db id (:body req))]
        (http/success-response todo))
      http/not-found-error)
    (catch Exception e
      (http/server-error (.getMessage e)))))

(defn delete! [id]
  (swap! db dissoc id)
  id)

(defn http-delete [id]
  (try
    (if (get @db id)
      (http/success-response {:deleted (delete! id)})
      http/not-found-error)
    (catch Exception e
      (http/server-error (.getMessage e)))))

(defn http-get [id]
  (try
    (if-let [todo (get @db id)]
      (http/success-response todo)
      http/not-found-error)
    (catch Exception e
      (http/server-error (.getMessage e)))))
