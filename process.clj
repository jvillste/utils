(ns process
  (:require
   [babashka.process :as process]))

(defn execute [command & [options]]

  (let [result @(process/process ['bash '-c command]
                                 (merge {:out :inherit
                                         :err :inherit
                                         :in :inherit
                                         ;; :out *out*
                                         ;; :out System/out
                                         }
                                        options))]

    (if (= 0 (:exit result))
      result
      (throw (ex-info "command failed"
                      {:command command
                       :options options})))))
