(ns dw-todo-exercise.prod
  (:require [dw-todo-exercise.core :as core]))

;;ignore println statements in prod
(set! *print-fn* (fn [& _]))

(core/init!)
