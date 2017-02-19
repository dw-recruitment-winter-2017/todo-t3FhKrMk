(ns dw-todo-exercise.todo
  (:require [reagent.core :as reagent]
            [clojure.string :as str]
            [dw-todo-exercise.api-client :as client]
            [dw-todo-exercise.util :as util]
            [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:refer-clojure :exclude [list update]))

(enable-console-print!)

(def app-state (reagent/atom {}))

(defn update [id todo]
  (go
   (let [updated (<! (client/update id todo))]
     (if (instance? js/Error updated)
       (swap! app-state assoc :error (.-message updated))
       (swap! app-state update-in [:todo-list id] merge updated)))))

(defn item [todo]
  (let [checkbox-attrs {:type "checkbox"
                        :checked (:complete todo)
                        :on-change #(update
                                     (:id todo)
                                     {:complete (not (:complete todo))})}]
    [:li {:class (if (:complete todo)
                   "complete"
                   "incomplete")}
     [:input checkbox-attrs]
     (:title todo)]))

(defn load-list [state]
  (go
   (let [list (<! (client/get-all))]
     (if (instance? js/Error list)
       (swap! state assoc :error (.-message list))
       (swap! state assoc :todo-list
              (reduce (fn [m i] (assoc m (:id i) i))
                      {} list))))))=

(defn list []
  (when (empty? (get @app-state :todo-list))
    (load-list app-state))
  [:div {:id "todo-list"}
   (when-let [error-msg (:error @app-state)]
     [:div {:class "error"}
      [:h2 "Error"]
      [:p error-msg]])
   [:ol {:class "todo"}
    (for [todo (vals (:todo-list @app-state))]
      ^{:key (:id todo)} [item todo])]])

