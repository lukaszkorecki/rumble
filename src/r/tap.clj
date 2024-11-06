(ns r.tap)

;; Tap helpers

(def ^:private tap-log (atom []))
(def ^:private tap-ref (atom nil))

(defn log-init!
  "Initialize a `tap>` listener and store the ref to it"
  []
  (reset! tap-ref (add-tap (fn [input]
                             (swap! tap-log conj input)))))
;; muscle memory...
#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(def init-log! log-init!)

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn log-get
  "Return tap logged data"
  []
  @tap-log)

(defn log-clear!
  "Clear the log"
  []
  (reset! tap-log []))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn log-stop!
  "Clear tap log and remove the listener"
  []
  (remove-tap @tap-ref)
  (log-clear!)
  (reset! tap-ref nil))
