(ns ^:figwheel-no-load dw-todo-exercise.dev
  (:require [dw-todo-exercise.core :as core]
            [figwheel.client :as figwheel :include-macros true]))

(figwheel/watch-and-reload
  :websocket-url "ws://localhost:3449/figwheel-ws"
  :jsload-callback core/mount-root)

(core/init!)
