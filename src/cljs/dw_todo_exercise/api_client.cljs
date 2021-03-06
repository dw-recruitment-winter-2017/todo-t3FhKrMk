(ns dw-todo-exercise.api-client
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:require [cljs-http.client :as http]
            [cljs.core.async :refer [<!]]
            [cljs.reader :as edn])
  (:refer-clojure :exclude [get update]))

(def api-root "/api")

(defn get-all []
  (go
   (let [response (<! (http/get (str api-root "/todos")))]
     (if (= 200 (:status response))
       (:body response)
       (js/Error. "todo list failed to load")))))

(defn create [title]
  (go
   (let [response (<! (http/post (str api-root "/todos")
                                 {:edn-params {:title title
                                               :complete false}}))]
     (if (= 201 (:status response))
       (:body response)
       (js/Error. (str "todo list item " title " failed to create"))))))

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

