(ns git
  (:require
   [process]
   [clojure.string :as string]))

(defn ancestory-path [from to]
  (string/split (:out (process/execute (str "git rev-list " from ".." to " --ancestry-path")
                                       {:out :string}))
                #"\n"))

(defn first-parents [from to]
  (string/split (:out (process/execute (str "git rev-list " from ".." to " --first-parent")
                                       {:out :string}))
                #"\n"))

(defn merge-commit [from to]
  (->> (first-parents from to)
       (filter (set (ancestory-path "ed89e71e9a2" "master")))
       (last)))

(defn summarize-commit [commit]
  (string/trim (:out (process/execute (str "git show --no-patch --format=\"%h %ad %an: %s\" --date=\"format:%Y-%m-%d %H:%M\" " commit)
                                      {:out :string}))))

(defn show-merge-commit [commit branch]
  (println (summarize-commit (merge-commit commit branch))))
