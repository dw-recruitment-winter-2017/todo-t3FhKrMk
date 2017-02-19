(ns dw-todo-exercise.api-client
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [cljs.reader :as edn])
  (:refer-clojure :exclude [get update]))

(enable-console-print!)

(def api-root "http://localhost:3449/api")

(defn get-all []
  (go
   (let [response (<! (http/get (str api-root "/todos")))]
     (if (= 200 (:status response))
       (:body response)
       (js/Error. "todo list failed to load")))))

(defn get [id]
  (go
   (->> id
        (str api-root "/todos/")
        http/get
        <!
        :body
        edn/read-string)))

(defn create [todo]
  (http/post (str api-root "/todos"
                  {:edn-params todo})))

(defn update [id todo]
  (go
    (let [response (<! (http/put (str api-root "/todos/" (name id))
                                 {:edn-params todo}))]
      (if (= 200 (:status response))
        (:body response)
        (js/Error. (str "todo list item " id " failed to update"))))))

(defn delete [id]
  (go
   (let [response (<! (http/delete (str api-root "/todos/" (name id))))]
     (if (= 200 (:status response))
       (:body response)
       (js/Error. (str "todo list item " id " failed to delete"))))))

