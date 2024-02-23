(ns clipboard
  (:import [java.awt.datatransfer DataFlavor StringSelection]
           [java.awt Toolkit]))

(defn slurp-clipboard []
  (.getTransferData (.getContents (.getSystemClipboard (Toolkit/getDefaultToolkit))
                                  nil)
                    (DataFlavor/stringFlavor)))

(defn spit-clipboard [text]
  (.setContents (.getSystemClipboard (Toolkit/getDefaultToolkit))
                (StringSelection. text)
                nil))
