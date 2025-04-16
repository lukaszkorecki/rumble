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

(def tap-log (atom []))

(defn ^:private log [] @tap-log)

(defn submit! [msg]
  (swap! tap-log conj msg)
  (portal.api/submit msg))

;;; Experimental stuff

(defn start!
  "Start portal instance and optionally open it in a browser"
  ([]
   (start! {:browse? false :launcher :emacs}))
  ([{:keys [browse? launcher]
     :or {launcher :emacs}
     :as opts}]
   (let [a-portal (portal.api/open (merge
                                    (dissoc opts :browse?)
                                    {:window-title "monroe portal"
                                     :theme :portal.colors/nord-light
                                     :launcher launcher}))
         url (portal.api/url a-portal)]
     (reset! instance a-portal)
     (reset! the-tap (add-tap submit!))

     (when browse?
       (browse/browse-url url))
     url)))

(defn get-selected []
  @instance)

(defn clear!
  "Clear current portal session view"
  []
  (reset! tap-log [])
  (portal.api/clear))

(defn stop!
  "Stop portal session"
  []
  (swap! the-tap remove-tap)
  (portal.api/close @instance))
