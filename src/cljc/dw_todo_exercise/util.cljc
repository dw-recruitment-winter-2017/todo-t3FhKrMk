(ns dw-todo-exercise.util
  (:require [clojure.string :as str]))

(defn title->id
  "Converts TODO item titles to id's"
  [title]
  (-> title
      (str/replace #"\s+" "-")
      str/lower-case
      keyword))
