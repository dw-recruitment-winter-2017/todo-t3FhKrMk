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
     (println "Response:" (pr-str response))
     (:body response))))

(defn get [id]
  (go
   (->> id
        (str api-root "/todos/")
        http/get
        <!
        :body
        edn/read-string)))

(defn create [todo]
  (go (<! (http/post (str api-root "/todos"
                          {:edn-params todo})))))

(defn update [id todo]
  (http/put (str api-root "/todos/" (name id))
            {:edn-params todo}))

(defn delete [id]
  (go (<! (http/delete (str api-root "/todos/" id)))))

