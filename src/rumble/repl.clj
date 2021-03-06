(ns ^{:clojure.tools.namespace.repl/load false} rumble.repl
  (:refer-clojure :exclude [find-ns])
  (:require
    [clojure.pprint]
    [clojure.repl]
    [clojure.string :as str]
    [clojure.tools.namespace.find :as ns.find]
    [clojure.tools.namespace.repl :as ns.repl]
    [kaocha.repl])
  (:import
    (java.io
      File)))


(ns.repl/disable-reload! *ns*)


(defn pp
  "Alias for pprint, but returns passed in data"
  [thing]
  (clojure.pprint/pprint thing)
  thing)


(defn help [& _n]
  (println (str ";; in ns " 'rumble.repl))
  (->> (ns-publics  'rumble.repl)
       (sort-by (fn [[_ v]] (str v)))
       (mapv (fn [[_k v]]
               (printf ";; %s - %s %s\n" (.replaceAll (str v) "#'" "")
                       (:arglists (meta v))
                       (:doc (meta v))))))
  ::ok)


(defn- init!
  "Initialize the helper namespace"
  []
  (ns.repl/disable-reload! *ns*)
  (ns.repl/set-refresh-dirs "src" "test")
  (help))


(defn list-ns
  "Return list of symbols of namespaces found in src dir. Default: ./src"
  ([root]
   (ns.find/find-namespaces-in-dir (File. root)))
  ([]
   (list-ns "./src/")))


(defn find-ns
  "Find namespace vars by a regex"
  [re]
  (let [nss (vec (filter #(re-find re (str %)) (list-ns)))]
    (printf ";; found %s ns\n" (count nss))
    (when (<= (count nss) 10)
      (for [n nss]
        (printf ";; %s\n" n)))
    nss))


(defn find-test-ns
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


(def ^:private system-status (atom {}))


(defn safe-to-refresh?
  "Check if refresh is safe, by verifying that application system is not running"
  []
  (or (empty? @system-status)
      (= #{false} (-> @system-status vals set))))


(defn  refresh
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


(defn start-system!
  "Given a namespace, usually some-service, do the following:
  - find some-service.user namespace (by convention)
  - refresh
  - require the user ns e.g. some-service.user
  - start  system, invoking somer-service.user/start
  Warning: best if the system is not running, or things will go south

  Example: (rumble.repl/start-system! 'foo.user)"
  ([]
   ;; automagically guess the <app>.user namespace
   (let [an-ns (-> *ns*
                   str
                   (str/replace #"\..+" ".user")
                   symbol)]
     (require an-ns)
     (start-system! an-ns)))
  ([an-ns]
   (printf ";; Starting %s\n" an-ns)
   (when (= "rumble.repl" (str an-ns))
     (throw (ex-info "nope" {:ns (str an-ns)})))
   (if (get @system-status an-ns)
     (println ";; System possibly running" an-ns)
     (do
       (println ";; Refreshing and reloading " an-ns)
       (remove-ns an-ns)
       (refresh)
       (require [an-ns] :reload)
       (when-let [f (ns-resolve an-ns 'start)]
         (f)
         (swap! system-status (fn [s] (assoc s an-ns true))))))))


(defn stop-system!
  "Given a namespace, usually some-service.user, stop the system. If not passed, stops currently running system"
  ([]
   (stop-system! (first (keys @system-status))))
  ([an-ns]
   (let [f (ns-resolve an-ns 'stop)]
     (f)
     (swap! system-status (fn [s] (assoc s an-ns false))))))


(defn restart-system!
  "Restarts the system with an optiona reload"
  []
  (when (stop-system!)
    (start-system!)))


(defn sys
  "Pull out the system for passing around"
  []
  (var-get (ns-resolve (first (keys @system-status)) 'SYS)))


(defn c
  "Pul out a compont from a running system, pass keyword for the component name"
  [component-name]
  (let [sys (sys)]
    (get sys component-name)))


(def ^:private kaocha-conf  {:config (System/getenv "KAOCHA_CONFIG")})


(defn t
  "Run tests via kaocha - either all or a list of vars. WILL NOT REFRESH"
  ([]
   (kaocha.repl/run :unit kaocha-conf))
  ([ns-list]
   (apply kaocha.repl/run (flatten [ns-list [kaocha-conf]]))))


(defn t!
  "Run tests via kaocha, but refresh first - runs all tests or a list (or one) of ns vars"
  ([]
   (refresh)
   (kaocha.repl/run :unit kaocha-conf))
  ([& ns-list]
   (refresh)
   (apply kaocha.repl/run (flatten [ns-list [kaocha-conf]]))))


(defn clear-aliases
  "Reset aliases for given ns or current if no args given"
  ([]
   (clear-aliases *ns*))
  ([an-ns]
   (mapv #(ns-unalias an-ns %) (keys (ns-aliases an-ns)))))


;; Tap helpers

(def ^:private tap-log (atom []))
(def ^:private tap-ref (atom nil))


(defn tap-log-init!
  "Initialize a tap> listener and store the ref to it"
  []
  (reset! tap-ref (add-tap (fn [input]
                             (swap! tap-log conj input)))))


(defn tap-log-get
  "Return tap logged data"
  []
  @tap-log)


(defn tap-log-reset!
  "Clear the log"
  []
  (reset! tap-log []))


(defn tap-log-stop!
  "Clear tap log and remove the listener"
  []
  (remove-tap @tap-ref)
  (tap-log-reset!)
  (reset! tap-ref nil))


(init!)
