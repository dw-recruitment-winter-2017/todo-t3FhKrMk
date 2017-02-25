(ns dw-todo-exercise.todo
  (:require [reagent.core :as reagent]
            [clojure.string :as str]
            [dw-todo-exercise.api-client :as client]
            [dw-todo-exercise.util :as util]
            [cljs.core.async :refer [<!]])
  (:require-macros [cljs.core.async.macros :refer [go]])
  (:refer-clojure :exclude [list update]))

(def app-state (reagent/atom {}))

(defn create [title]
  (go
   (let [created (<! (client/create title))]
     (if (instance? js/Error created)
       (swap! app-state assoc :error (.-message created))
       (swap! app-state clojure.core/update :todo-list assoc (:id created)
              created)))))

(defn update [id todo]
  (go
   (let [updated (<! (client/update id todo))]
     (if (instance? js/Error updated)
       (swap! app-state assoc :error (.-message updated))
       (swap! app-state update-in [:todo-list id] merge updated)))))

(defn delete [id]
  (go
   (let [deleted (<! (client/delete id))]
     (if (instance? js/Error deleted)
       (swap! app-state assoc :error (.-message deleted))
       (swap! app-state clojure.core/update :todo-list dissoc id)))))

(defn item [todo]
  (let [checkbox-attrs {:type "checkbox"
                        :checked (:complete todo)
                        :on-change #(update
                                     (:id todo)
                                     {:complete (not (:complete todo))})}]
    [:li {:class (str/join " " ["item" (if (:complete todo)
                                         "complete"
                                         "incomplete")])}
     [:input checkbox-attrs]
     [:span {:class "title"} (:title todo)]
     [:a {:class "delete"
          :on-click #(delete (:id todo))}
      "X"]]))

(defn load-list [state]
  (go
   (let [list (<! (client/get-all))]
     (if (instance? js/Error list)
       (swap! state assoc :error (.-message list))
       (swap! state assoc :todo-list
              (reduce (fn [m i] (assoc m (:id i) i))
                      {} list))))))

(defn list []
  (when (empty? (get @app-state :todo-list))
    (load-list app-state))
  [:div {:id "todo-list"}
   (when-let [error-msg (:error @app-state)]
     [:div {:class "error"}
      [:h2 "Error"]
      [:p error-msg]])
   [:ol {:class "todo"}
    (for [todo (sort-by :created-at (vals (:todo-list @app-state)))]
      ^{:key (:id todo)} [item todo])]
   [:form {:class "new-item"
           :on-submit (fn [e]
                        (let [new-item (.getElementById js/document "new-item")
                              new-item-title (.-value new-item)]
                          (create new-item-title)
                          (set! (.-value new-item) ""))
                        (.preventDefault e))}
    [:label {:for "new-item"} "Add TODO"]
    [:div {:class "row"}
     [:input {:id "new-item"
              :class "column column-90"
              :type "text"
              :name "title"}]
     [:input {:type "submit"
              :class "button column"
              :value "Add"}]]]])

