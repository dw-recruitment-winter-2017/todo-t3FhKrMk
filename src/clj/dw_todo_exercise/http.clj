(ns dw-todo-exercise.http)

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