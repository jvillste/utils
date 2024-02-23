(ns core
  (:require [clojure.string :as string]
            [babashka.process :as process]
            clipboard))

(defn notify-completion! []
  (process/process "afplay /System/Library/Sounds/Ping.aiff")
  (process/process "osascript -e 'display notification \"ready\""))

(defn convert-clipboard-to-plain-text []
  (clipboard/spit-clipboard (string/trim (clipboard/slurp-clipboard)))
  (notify-completion!))

(def commands [#'convert-clipboard-to-plain-text])

(defn -main [& command-line-arguments]
  (let [[command-name & arguments] command-line-arguments]
    (if-let [command (first (filter (fn [command]
                                      (= command-name
                                         (name (:name (meta command)))))
                                    commands))]
      (try (apply command arguments)
           (finally Exception
                    (.flush *out*)
                    (shutdown-agents)))

      (do (println "Usage:")
          (println "------------------------")
          (println (->> commands
                        (map (fn [command-var]
                               (str (:name (meta command-var))
                                    ": "
                                    (:arglists (meta command-var))
                                    (when-let [doc (:doc (meta command-var))]
                                      (str "\n" doc)))))
                        (string/join "\n------------------------\n")))))))
