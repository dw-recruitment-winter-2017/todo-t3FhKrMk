(ns dw-todo-exercise.todo
  (:require [reagent.core :as reagent]
            [clojure.string :as str]))

(enable-console-print!)

(def todos
  (reagent/atom
   {"buy milk"   false
    "drink beer" false
    "watch TV"   false}))

(defn todo-item [item]
  (let [item-id (str/replace item #"\s+" "-")
        checkbox-attrs {:type "checkbox"
                        :checked (get @todos item)
                        :on-change #(swap! todos update item not)}]
    [:li {:class (if (get @todos item)
                   "complete"
                   "incomplete")}
     [:input checkbox-attrs]
     item]))

(defn todo-list []
  [:ol {:class "todo"}
   (for [todo (keys @todos)]
     ^{:key todo} [todo-item todo])])

