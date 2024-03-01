(ns common
  (:require [babashka.process :as process]))

(defn notify-completion! []
  (process/process "afplay /System/Library/Sounds/Ping.aiff")
  (process/process "osascript -e 'display notification \"ready\""))
