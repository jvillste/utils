(ns jira
  (:require [clojure.string :as string]
            clipboard
            [clojure.test :refer [deftest is]]
            common))

(defn- clean-up-jira-issue-title [text]
  (string/join " " (rest (re-find #"([^\s]+)\s+(.*)" text))))

(deftest test-clean-up-jira-issue-title
  (is (= "IT-123 We have a big issue"
         (clean-up-jira-issue-title "IT-123\n\n\nWe have a big issue"))))

(defn href-from-jira-issue-html [html]
  (first (rest (re-find #"href=\"([^\"]+)" html))))

(deftest test-href-from-jira-issue-html
  (is (= "https://postinext.atlassian.net/browse/DET-715"
         (href-from-jira-issue-html "issue.item\" href=\"https://postinext.atlassian.net/browse/DET-715\" tabindex=\"0\""))))

(defn clean-up-jira-issue-title-in-clipboard []
  (clipboard/spit-html-to-clipboard (str "<a href=\"" (href-from-jira-issue-html (clipboard/slurp-html-from-clipboard)) "\">"
                                         (clean-up-jira-issue-title (clipboard/slurp-plain-text-from-clipboard))
                                         "<\\a>")
                                    (clean-up-jira-issue-title (clipboard/slurp-plain-text-from-clipboard)))
  (common/notify-completion!)
  (System/exit 0))
