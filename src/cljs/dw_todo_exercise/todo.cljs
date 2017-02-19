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
  (swap! app-state update-in [:todo-list id] merge todo)
  (client/update id todo))

(defn item [todo]
  (println "Rendering item:" (pr-str todo))
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
     (swap! state assoc :todo-list
            (reduce (fn [m i] (assoc m (:id i) i))
                    {} list)))))

(defn list []
  (when (empty? (get @app-state :todo-list))
    (load-list app-state))
  [:ol {:class "todo"}
   (for [todo (vals (get @app-state :todo-list))]
     ^{:key (:id todo)} [item todo])])

