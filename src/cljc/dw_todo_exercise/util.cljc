(ns dw-todo-exercise.util
  (:require [clojure.string :as str]))

(defn title->id
  "Converts TODO item titles to id's. It wouldn't be too hard to find id
  collisions with this. But for a simple TODO list, it does provide nice
  human-readable id's and resource URLs."
  [title]
  (-> title
      (str/replace #"\s+" "-")
      (str/replace #"[^\w\-]" "")
      str/lower-case
      keyword))
