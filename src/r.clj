(ns ^{:clojure.tools.namespace.repl/load false} r
  (:refer-clojure :exclude [find-ns])
  (:require
   [clojure.java.browse]
   [clojure.pprint]
   [clojure.repl]
   [clojure.string :as str]
   [clojure.tools.namespace.find :as ns.find]
   [clojure.tools.namespace.repl :as ns.repl]
   [kaocha.repl]
   [portal.api]
   [portal.colors])
  (:import
   (java.io
    File)))

(set! *warn-on-reflection* true)

(ns.repl/disable-reload! *ns*)

(def ^{:doc "Pretty print given thing"} ppn clojure.pprint/pprint)

;; debugging and stuff
(defn pp
  "Like `ppn`, but returns passed in data. Useful for debugging threaded calls"
  [thing]
  (ppn thing)
  thing)

(defn ->pp
  "Pretty print in `->` threading macro. Optionally tag the thing with `:tag` to pp a hash map of `{tag thing}`"
  [thing tag]
  (pp {tag thing})
  thing)

(defn ->>pp
  "Pretty print in `->>` threading macro. Optionally tag the thing with `:tag` to pp a hash map of `{tag thing}`"
  [tag thing]
  (pp {tag thing})
  thing)

;; finding things in a Clj project
(defn list-ns
  "Return list of symbols of namespaces found in a dir. Default: `./src`"
  ([root]
   (ns.find/find-namespaces-in-dir (File. ^String root)))
  ([]
   (list-ns "./src/")))

(defn find-ns
  "Find namespace vars by a regex"
  [re]
  (let [nss (vec (filter #(re-find re (str %)) (list-ns)))]
    (printf ";; found %s ns\n" (count nss))
    (when (<= (count nss) 20)
      (for [n nss]
        (printf ";; %s\n" n)))
    nss))

(defn tests
  "Find test namespace vars by a regex"
  [pattern]
  (let [re (cond
             (string? pattern) (re-pattern pattern)
             (= java.util.regex.Pattern (class pattern)) pattern
             :else (throw (ex-info "this is not a patternable thing" {:pattern pattern})))
        nss (vec (filter #(re-find re (str %)) (list-ns "./test/")))]
    (printf ";; found %s nss\n" (count nss))
    (when (<= (count nss) 10)
      (for [n nss]
        (printf ";; %s\n" n)))
    nss))

(def ^{:doc "alias for `r/tests`"} find-tests tests)
(def ^{:doc "alias for `r/tests`"} find-test-ns tests)

(defn describe-ns
  "Describes given namespace by listing **public** symbols, optionally filters down via `:s <search>`
  and can optionally add the doc string with `:docs?` true"
  [an-ns & {:keys [s docs?]}]
  (->> an-ns
       (ns-publics)
       (filter (fn [[sym _thing]]
                 (if s
                   (re-find (if (string? s)
                              (re-pattern s)
                              s)
                            (str sym))
                   true)))
       (sort-by #(str (first %)))
       (mapv (fn [[sym thing]]
               (format "> %s > %s > %s%s"
                       sym
                       (str thing)
                       (get (meta thing) :arglists)
                       (if docs?
                         (str "\n" (get (meta thing) :doc) "\n")
                         ""))))
       (clojure.string/join "\n")
       (println)))

(def ^:private system-status (atom {}))

(defn safe-to-refresh?
  "Check if refresh is safe, by verifying that application system is not running"
  []
  (or (empty? @system-status)
      (= #{false} (-> @system-status vals set))))

(defn refresh
  "Refresh changed namespaces, only if its safe"
  []
  (if (safe-to-refresh?)
    (ns.repl/refresh)
    ::system-running!))

(defn refresh-all
  "Refresh everything, only if its safe"
  []
  (if (safe-to-refresh?)
    (ns.repl/refresh-all)
    ::system-running!))

(defn system-ns
  "Default finder for location of the system namespace.
  It extracts the first segment of the namspace tree and appends `.user` to it.
  "
  []
  (-> *ns*
      str
      (str/replace #"\..+" ".user")
      symbol))

(defn ^:deprecated start-system!
  "Given a namespace, usually some-service, do the following:
  - find some-service.user namespace (by convention)
  - refresh
  - require the user ns e.g. some-service.user
  - start  system, invoking somer-service.user/start

  The namespace has to contain the following:

  - `start` function that starts the system
  - `stop` function that stops the system
  - `SYS` - var that contains the system map, it can be an atom - whatever can keep a state

  > [!WARNING]
  > best if the system is not running, or things will go south very quickly

  Example: `(r/start-system! 'foo.user)`"
  ([]
   ;; automagically guess the <app>.user namespace
   (let [dev-sys-ns (system-ns)]
     (require dev-sys-ns)
     (start-system! dev-sys-ns)))
  ([dev-sys-ns]
   (when (= "r" (str dev-sys-ns))
     (throw (ex-info "not allowed" {:ns (str dev-sys-ns)})))

   (printf ";; Starting %s\n" dev-sys-ns)

   (if (get @system-status dev-sys-ns)
     (println ";; System possibly running" dev-sys-ns)
     (do
       (println ";; Refreshing and reloading " dev-sys-ns)
       (remove-ns dev-sys-ns)
       (refresh)
       (require [dev-sys-ns] :reload)
       (when-let [f (ns-resolve dev-sys-ns 'start)]
         (f)
         (swap! system-status (fn [s] (assoc s dev-sys-ns true))))))))

(defn ^:deprecated stop-system!
  "Given a namespace, usually some-service.user, stop the system. If not passed, stops currently running system"
  ([]
   (stop-system! (first (keys @system-status))))
  ([dev-sys-ns]
   (let [f (ns-resolve dev-sys-ns 'stop)]
     (f)
     (swap! system-status (fn [s] (assoc s dev-sys-ns false))))))

(defn ^:deprecated restart-system!
  "Restarts the system with an optional reload. If the system is not running, it will start it"
  []
  (when (first (keys @system-status))
    (stop-system!))
  (start-system!))

(defn sys
  "Get the running system map"
  []
  (var-get (ns-resolve (first (keys @system-status)) 'SYS)))

(defn c
  "Get a component from the running system, e.g (r/c :postgres)"
  [component-key]
  (when-let [sys (sys)]
    (get sys component-key)))

;;; Test helpers

(defn t
  "Run tests via kaocha - either all or a list of vars.
  > [!NOTE]
  > It will not refresh any code - use `t!` for that"
  ([]
   (kaocha.repl/run-all))
  ([ns-list]
   (apply kaocha.repl/run ns-list)))

(defn t!
  "Run tests via kaocha, but refresh first - runs all tests or a list (or one) of ns vars.

  > ![NOTE]
  > Refresh happens only if it's safe to do so e.g. dev system is not running
  "
  ([]
   (println (refresh))
   (kaocha.repl/run-all))
  ([& ns-list]
   (println (refresh))
   (apply kaocha.repl/run ns-list)))

(defn clear-aliases
  "Reset aliases for given ns or current one if no args given"
  ([]
   (clear-aliases *ns*))
  ([an-ns]
   (mapv #(ns-unalias an-ns %) (keys (ns-aliases an-ns)))))

;; Tap helpers

(def ^:private tap-log (atom []))
(def ^:private tap-ref (atom nil))

(defn ->tap>
  "Like `tap>` but returns input, and is designed for threading macros. Optionally tag the thing with `:tag` to tap a hash map of `{tag thing}`"
  ([thing]
   (->tap> thing :->tap>))
  ([thing tag]
   (tap> {tag thing})
   thing))

(defn ->>tap>
  "Like `tap>` but returns input, and is designed for threading macros. Optionally tag the thing with `:tag` to tap a hash map of `{tag thing}`"
  ([thing]
   (->>tap> thing :->>tap>))
  ([tag thing]
   (tap> {tag thing})
   thing))

(defn tap-log-init!
  "Initialize a `tap>` listener and store the ref to it"
  []
  (reset! tap-ref (add-tap (fn [input]
                             (swap! tap-log conj input)))))
;; muscle memory...
(def init-tap-log! tap-log-init!)

(defn tap-log-get
  "Return tap logged data"
  []
  @tap-log)

(defn tap-log-clear!
  "Clear the log"
  []
  (reset! tap-log []))

(defn tap-log-stop!
  "Clear tap log and remove the listener"
  []
  (remove-tap @tap-ref)
  (tap-log-clear!)
  (reset! tap-ref nil))

(def portal-tap (atom nil))
(def portal-instance (atom nil))

;;; Experimental stuff

(defn portal-start!
  "Start portal instance and optionally open it in a browser"
  ([]
   (portal-start! {:browse? true}))
  ([{:keys [browse?]}]
   (let [instance (portal.api/open {:window-title "monroe portal"
                                    :theme ::missing
                                    :launcher false})
         url (portal.api/url instance)]
     (reset! portal-instance instance)
     (reset! portal-tap (add-tap #'portal.api/submit))
     (when browse?
       (clojure.java.browse/browse-url url))
     url)))

(defn portal-clear!
  "Clear current portal session view"
  []
  (portal.api/clear))

(defn portal-stop!
  "Stop portal session"
  []
  (swap! portal-tap remove-tap)
  (portal.api/close @portal-instance))

;; (defn portal-get []
;;   (portal.api/selected @portal-instance))

(defn help
  "Get help about all `r` functionality"
  []
  (describe-ns 'r :doc true))

(defn- init!
  "Initialize `r`umble helpers"
  []
  (ns.repl/disable-reload! *ns*)
  (ns.repl/set-refresh-dirs "src" "test")
  (println "Rumble loaded, use (r/help) to get started"))

(init!)
