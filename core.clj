(ns core
  (:require
   [clipboard]
   [clojure.string :as string]
   [common]
   [jira :as jira]))

(defn convert-clipboard-to-plain-text []
  (clipboard/spit-plain-text-to-clipboard (string/trim (clipboard/slurp-plain-text-from-clipboard)))
  (common/notify-completion!))

(def commands [#'convert-clipboard-to-plain-text
               #'jira/clean-up-jira-issue-title-in-clipboard
               #'jira/jira-issue-title-to-branch-name-in-clipboard])

(defn -main [& command-line-arguments]
  (try (let [[command-name & arguments] command-line-arguments]
         (if-let [command (first (filter (fn [command]
                                           (= command-name
                                              (name (:name (meta command)))))
                                         commands))]
           (apply command arguments)


           (do (println "Usage:")
               (println "------------------------")
               (println (->> commands
                             (map (fn [command-var]
                                    (str (:name (meta command-var))
                                         ": "
                                         (:arglists (meta command-var))
                                         (when-let [doc (:doc (meta command-var))]
                                           (str "\n" doc)))))
                             (string/join "\n------------------------\n"))))))
       (finally (.flush *out*)
                (shutdown-agents)
                (clipboard/exit))))
