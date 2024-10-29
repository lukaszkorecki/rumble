(ns r.portal
  (:require
   [clojure.java.browse :as browse]
   [clojure.tools.namespace.repl :as ns.repl]
   [portal.api :as portal]
   [portal.colors]))

(set! *warn-on-reflection* true)

(ns.repl/disable-reload! *ns*)

(defn v [type thing]
  (with-meta
    thing
    {:portal.viewer/default type}))

(defn table [thing]
  (v :portal.viewer/table thing))

(defn hiccup [thing]
  (v :portal.viewer/hiccup thing))

(defn test-report [thing]
  (v :portal.viewer/test-report thing))

(defn diff [thing]
  (v :portal.viewer/diff thing))

(defn exc [thing]
  (v :portal.viewer/ex thing))

(def the-tap (atom nil))
(def instance (atom nil))

;;; Experimental stuff

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn start!
  "Start portal instance and optionally open it in a browser"
  ([]
   (start! {:browse? false}))
  ([{:keys [browse?] :as opts}]
   (let [instance (portal.api/open (merge
                                    (dissoc opts :browse?)
                                    {:window-title "monroe portal"
                                     :theme :portal.colors/nord-light
                                     :launcher :emacs}))
         url (portal.api/url instance)]
     (reset! instance instance)
     (reset! the-tap (add-tap #'portal.api/submit))
     (when browse?
       (browse/browse-url url))
     url)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn clear!
  "Clear current portal session view"
  []
  (portal.api/clear))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn stop!
  "Stop portal session"
  []
  (swap! the-tap remove-tap)
  (portal.api/close @instance))
