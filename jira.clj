(ns jira
  (:require [clojure.string :as string]
            clipboard
            [clojure.test :refer [deftest is]]
            common))

(defn- clean-up-jira-issue-title [text]
  (let [lines (string/split-lines text)]
    (str (first lines)
         " "
         (last lines))))

(deftest test-clean-up-jira-issue-title
  (is (= "IT-123 We have a big issue"
         (clean-up-jira-issue-title "IT-123\n\n\nWe have a big issue"))))

(defn href-from-jira-issue-html [html]
  (first (rest (re-find #"href=\"([^\"]+)" html))))

(deftest test-href-from-jira-issue-html
  (is (= "https://example.atlassian.net/browse/DET-715"
         (href-from-jira-issue-html "issue.item\" href=\"https://example.atlassian.net/browse/DET-715\" tabindex=\"0\""))))

(defn clean-up-jira-issue-title-in-clipboard []
  (clipboard/spit-html-to-clipboard (str "<a href=\"" (href-from-jira-issue-html (clipboard/slurp-html-from-clipboard)) "\">"
                                         (clean-up-jira-issue-title (clipboard/slurp-plain-text-from-clipboard))
                                         "</a>")
                                    (clean-up-jira-issue-title (clipboard/slurp-plain-text-from-clipboard)))
  (common/notify-completion!)
  (System/exit 0))

(defn jira-issue-title-to-branch-name [jira-issue-title]
  (let [cleaned-up-jira-issue-title (clean-up-jira-issue-title jira-issue-title)]
    (str (subs cleaned-up-jira-issue-title 0 3)
         (-> cleaned-up-jira-issue-title
             (subs 3)
             (string/replace #"[ \"\(\)\.]" "-")
             (string/replace #"'" "")
             (string/replace #"-{2,}" "-")
             (string/lower-case)))))

(deftest test-jira-issue-title-to-branch-name
  (is (= "DET-815-some-test-issue-name-e-a"
         (jira-issue-title-to-branch-name "DET-815\n2\nSome(test) \"issue\" - name.e.a'"))))

(defn jira-issue-title-to-branch-name-in-clipboard []
  (clipboard/spit-plain-text-to-clipboard (jira-issue-title-to-branch-name (clipboard/slurp-plain-text-from-clipboard)))
  (common/notify-completion!)
  (System/exit 0))
