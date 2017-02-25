(ns dw-todo-exercise.http
  (:require [dw-todo-exercise.db :as db]
            [dw-todo-exercise.util :as util])
  (:refer-clojure :exclude [get update]))

(defn server-error [message]
  {:status 500
   :headers {}
   :body {:error message}})

(def not-found-error
  {:status 404
   :headers {}
   :body {:error :not-found}})

(defn success-response [body]
  {:status 200
   :headers {}
   :body body})

(defn created-response [body path]
  {:status 201
   :headers {"Location" path}
   :body body})

(defn get
  "Takes an `id` of a todo, map with a :db key, and wraps
  `dw-todo-exercise.todos/get` in an HTTP 200 OK response if a todo is found
  with that id. If none is found, returns a 404 error response map. If an
  exception is thrown, returns a 500 error response map."
  [id {:keys [db]}]
  (try
    (if-let [todo (db/get @db id)]
      (success-response todo)
      not-found-error)
    (catch Exception e
      (server-error (.getMessage e)))))

(defn get-all
  "Takes a map with a :db key and wraps calling `dw-todo-exercise.db/get-all`
  on a deref of the value in an HTTP success response."
  [{:keys [db]}]
  (success-response (db/get-all @db)))

(defn create
  "Takes an HTTP request map and pulls todo map from :body, a db from :db, and
  wraps the result of calling `dw-todo-exercise.db/create!` in an HTTP
  201 Created response map if successful, or a 500 error response map if an
  exception is thrown."
  [{:keys [body db]}]
  (try
    (let [todo (db/create! db body)]
      (created-response todo (str "/api/todos/" (name (:id todo)))))
    (catch Exception e
      (server-error (.toString e)))))

(defn update
  "Takes an `id` and an HTTP request map. Pulls todo map from the request's
  :body, the db from the request's :db, and wraps the result of calling
  `dw-todo-exercise.db/update!` in an HTTP 200 OK response if successful, or a
  500 error response map if an exception is thrown."
  [id {:keys [body db]}]
  (try
    (if (db/get @db id)
      (let [todo (db/update! db id body)]
        (success-response todo))
      not-found-error)
    (catch Exception e
      (server-error (.toString e)))))

(defn delete
  "Takes an `id` of a todo, a map with a :db key, and looks up a todo using
  `dw-todo-exercise.db/get`. If it finds a todo, it deletes it from the db and
  returns an HTTP 200 OK response map. If it doesn't find a todo, it returns a
  404 error response map. If an exception is thrown, it returns a 500 error
  response map."
  [id {:keys [db]}]
  (try
    (if (db/get @db id)
      (success-response {:deleted (db/delete! db id)})
      not-found-error)
    (catch Exception e
      (server-error (.toString e)))))
