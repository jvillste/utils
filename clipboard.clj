(ns clipboard
  (:import
   (java.awt Toolkit)
   (java.awt.datatransfer DataFlavor StringSelection)
   (javafx.application Platform)
   (javafx.scene.input Clipboard ClipboardContent)))

(try (Platform/startup (fn []))
     (catch Exception _exception))

(defn exit []
  (Platform/exit))

(defn slurp-plain-text-from-clipboard []
  (.getTransferData (.getContents (.getSystemClipboard (Toolkit/getDefaultToolkit))
                                  nil)
                    (DataFlavor/stringFlavor)))

(defn slurp-html-from-clipboard []
  (.getTransferData (.getContents (.getSystemClipboard (Toolkit/getDefaultToolkit))
                                  nil)
                    (DataFlavor/allHtmlFlavor)))

(defn spit-plain-text-to-clipboard [text]
  (.setContents (.getSystemClipboard (Toolkit/getDefaultToolkit))
                (StringSelection. text)
                nil))

(defn spit-html-to-clipboard [html plain-text]
  (Platform/runLater (fn []
                       (.setContent (Clipboard/getSystemClipboard)
                                    (doto (ClipboardContent.)
                                      (.putHtml html)
                                      (.putString plain-text))))))
