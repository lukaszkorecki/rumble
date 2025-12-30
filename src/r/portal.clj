(ns r.portal
  (:require
   [clojure.java.browse :as browse]
   [clojure.tools.namespace.repl :as ns.repl]
   [portal.browse.cljfx] ;; register cljfx launcher
   [portal.api :as portal]
   [portal.colors :as pc]))

(set! *warn-on-reflection* true)

(ns.repl/disable-reload! *ns*)

(defn ->v [type thing]
  (with-meta
    thing
    {:portal.viewer/default type}))

(defmulti view (fn [type _thing] type))

;; fallback viewer, tries to convert type to :portal.viewer/<type>
(defmethod view :default [type thing]
  (->v (keyword "portal.viewer" (name type)) thing))

;; special cases
(defmethod view :markdown [_type thing]
  (->v :portal.viewer/hiccup [:portal.viewer/markdown thing]))

(defmethod view :html [_type thing]
  (->v :portal.viewer/hiccup [:portal.viewer/html thing]))

#_{:clojure-lsp/ignore [:clojure-lsp/unused-public-var]}
(defn all-viewers []
  (let [viewer-kws (sort (map keyword (keys (ns-publics 'portal.viewer))))]

    (remove #(= :for %) viewer-kws)))

(def the-tap (atom nil))
(def instance (atom nil))

(def tap-log (atom []))

#_{:clj-kondo/ignore [:unused-private-var]}
(defn ^:private log [] @tap-log)

(defn submit! [msg]
  (swap! tap-log conj msg)
  (portal.api/submit msg))

#_{:clojure-lsp/ignore [:clojure-lsp/unused-public-var]}
(defn start!
  "Start portal instance and optionally open it in a browser"
  ([]
   (start! {:browse? false :launcher :cljfx}))
  ([{:keys [browse? launcher]
     :or {launcher :cljfx}
     :as opts}]
   (let [a-portal (portal.api/open (merge
                                    (dissoc opts :browse?)
                                    {:window-title "Portal"
                                     :theme ::pc/nord-light
                                     :launcher launcher}))
         url (portal.api/url a-portal)]
     (reset! instance a-portal)
     (reset! the-tap (add-tap submit!))

     (when browse?
       (browse/browse-url url))
     url)))

#_{:clojure-lsp/ignore [:clojure-lsp/unused-public-var]}
(defn get-selected []
  @@instance)

#_{:clojure-lsp/ignore [:clojure-lsp/unused-public-var]}
(defn clear!
  "Clear current portal session view"
  []
  (reset! tap-log [])
  (portal.api/clear))

#_{:clojure-lsp/ignore [:clojure-lsp/unused-public-var]}
(defn stop!
  "Stop portal session"
  []
  (swap! the-tap remove-tap)
  (portal.api/close @instance))
