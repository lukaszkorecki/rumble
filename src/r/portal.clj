(ns r.portal
  (:require
   [clojure.java.browse :as browse]
   [clojure.tools.namespace.repl :as ns.repl]
   [portal.browse.cljfx] ;; register cljfx launcher
   [portal.api :as portal]
   [portal.colors :as pc]))

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

(defn foo [] (println "r.portal loaded"))

(pc/register! ::pc/github
              {::pc/text "#24292f" ; Standard text color
               ::pc/background "#ffffff" ; Main background (white)
               ::pc/background2 "#f6f8fa" ; Secondary background (lighter gray)
               ::pc/boolean "#0550ae" ; Blue for booleans
               ::pc/string "#0a3069" ; Dark blue for strings
               ::pc/keyword "#8250df" ; Purple for keywords
               ::pc/namespace "#0550ae" ; Blue for namespaces
               ::pc/tag "#116329" ; Green for tags
               ::pc/symbol "#24292f" ; Standard text for symbols
               ::pc/number "#0550ae" ; Blue for numbers
               ::pc/uri "#0969da" ; Link blue for URIs
               ::pc/border "#d0d7de" ; Border gray
               ::pc/package "#0969da" ; Link blue for packages
               ::pc/exception "#cf222e" ; Red for exceptions
               ::pc/diff-add "#1a7f37" ; Green for additions
               ::pc/diff-remove "#cf222e"} ; Red for removals
              )

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
                                     :theme ::pc/github
                                     :launcher launcher}))
         url (portal.api/url a-portal)]
     (reset! instance a-portal)
     (reset! the-tap (add-tap submit!))

     (when browse?
       (browse/browse-url url))
     url)))

(defn get-selected []
  @@instance)

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
