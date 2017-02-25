(ns dw-todo-exercise.db
  "Contains the database manipulation functions for the TODO app. Note that
  the read functions take map-like db args (typically deref'd atoms containing
  maps). The write functions, on the other hand, take args that behave like
  atoms containing maps. They will call things like `swap!` on them.

  So generally you'll create your own `(atom {})` to serve as the db and deref
  it yourself before passing it to the read functions, but pass the atom itself
  to the write functions. This allows you to use the read functions on the
  return values of the write functions (a property that the write functions
  themselves take advantage of)."
  (:refer-clojure :exclude [get])
  (:require [dw-todo-exercise.util :as util])
  (:import (java.util Date)))

(defn get
  "Takes a `db` and `id` arg and returns the todo with the given id from the
  db, if it exists (nil otherwise). The todo should be retrievable from the db
  via `clojure.core/get` using the id as the key."
  [db id]
  (when-let [todo (clojure.core/get db id)]
    (assoc todo :id id)))

(defn get-all
  "Takes a `db` and returns all todos from it using
  `dw-todo-exercise.todos/get`. The db will be deref'd before it is accessed."
  [db]
  (vec (sort-by :created-at (map #(get db %) (keys db)))))

(defn update!
  "Takes a `db`, `id`, and `todo` map args and updates the db by upserting the
  todo using the id as the key. Returns the new / updated todo.

  The todo arg should look like this:
  {:title \"Do a thing\", :complete false}"
  [db id todo]
  (-> db
      (swap! update id merge todo)
      (get id)))

(defn create!
  "Takes a `db` and a `todo` map and inserts it into the db using
  `dw-todo-exercise.db/update!`. Returns the new todo."
  [db todo]
  (let [id (util/title->id (:title todo))
        created-at (Date.)]
    (update! db id (assoc todo :created-at created-at))))

(defn delete!
  "Takes a `db` and an `id` of a todo and deletes the todo with that id from
  the db if it exists (using `clojure.core/dissoc`. Returns the id."
  [db id]
  (swap! db dissoc id)
  id)