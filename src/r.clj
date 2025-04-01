(ns ^{:clojure.tools.namespace.repl/load false} r
  (:refer-clojure :exclude [find-ns])
  (:require
   [babashka.process :as proc]
   [clojure.java.browse]
   [clojure.pprint]
   [clojure.repl]
   [clojure.string :as str]
   [clojure.tools.namespace.find :as ns.find]
   [clojure.tools.namespace.repl :as ns.repl]
   [utility-belt.component.system :as util.system]
   [kaocha.repl])
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

(def system-store
  (atom
   {:sys-map-fn nil
    :status nil}))

(defn safe-to-refresh?
  "Check if refresh is safe, by verifying that application system is not running"
  []
  (not (= ::running (-> @system-store :status))))

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

(declare start-sys' stop-sys' restart-sys' get-sys')

(defn start-system!
  [& [component-map-fn]]
  (swap! system-store (fn [{:keys [status sys-map-fn] :as state}]
                        (if sys-map-fn
                          (if (= ::running status)
                            (do
                              (println "System running")
                              state)

                            (do
                              (println "Starting system")
                              (start-sys')))

                          (do
                            (println "Initializing and starting system")
                            (assert (qualified-symbol? component-map-fn) "Need a valid component map fn")
                            (let [{:keys [start-system stop-system get-system]} (util.system/setup-for-dev
                                                                                 {:component-map-fn component-map-fn
                                                                                  :reloadable? true})]

                              #_{:clj-kondo/ignore [:inline-def]}
                              (def get-sys' get-system)
                              #_{:clj-kondo/ignore [:inline-def]}
                              (def start-sys' start-system)
                              #_{:clj-kondo/ignore [:inline-def]}
                              (def restart-sys' (fn [] (stop-system) (start-system)))
                              #_{:clj-kondo/ignore [:inline-def]}
                              (def stop-sys' stop-system))

                            (start-sys')

                            (assoc state
                                   :status ::running
                                   :sys-map-fn component-map-fn))))))

(defn stop-system!
  "Given a namespace, usually some-service.user, stop the system. If not passed, stops currently running system"
  []
  (swap! system-store (fn [{:keys [status sys-map-fn] :as state}]
                        (if sys-map-fn
                          (if (= ::running status)
                            (do
                              (println "Stopping system")
                              (stop-sys')
                              (assoc status :state ::stopped))

                            (do
                              (println "System not running")
                              state))

                          (do
                            (println "System machinery not initialized")
                            state)))))

(defn restart-system!
  "Restarts the system with an optional reload. If the system is not running, it will start it"
  []
  (restart-sys'))

(defn sys
  "Get the running system map"
  []
  (get-sys'))

(defn c
  "Get a component from the running system, e.g (r/c :postgres)"
  [component-key]
  (when-let [sys' (sys)]
    (get sys' component-key)))

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
   (apply kaocha.repl/run (flatten ns-list))))

(defn clear-aliases
  "Reset aliases for given ns or current one if no args given"
  ([]
   (clear-aliases *ns*))
  ([an-ns]
   {:pre [(symbol? an-ns)]}
   (mapv #(ns-unalias an-ns %) (keys (ns-aliases an-ns)))))

(defn tap->
  "Like `tap>` but returns input, and is designed for threading macros. Optionally tag the thing with `:tag` to tap a hash map of `{tag thing}`"
  ([thing]
   (tap-> thing :tap->))
  ([thing tag]
   (tap> {tag thing})
   thing))

(defn tap->>
  "Like `tap>` but returns input, and is designed for threading macros. Optionally tag the thing with `:tag` to tap a hash map of `{tag thing}`"
  ([thing]
   (tap->> thing :tap->>))
  ([tag thing]
   (tap> {tag thing})
   thing))

(defn fmt-all!
  "Format all Clojure files in the project using `clojure-lsp` binary"
  []
  (let [git-root (-> (proc/sh "git" "rev-parse" "--show-toplevel") :out str/trim)
        fmt-cmd (str git-root "/node_modules/.bin/clojure-lsp --lint --fix --lint-ns \"src/\"")]
    (println "Formatting all Clojure files in the project")
    (println fmt-cmd)
    (proc/sh "sh" "-c" fmt-cmd)))

(defn classpath->vec
  "Return class path as vector of absolute paths"
  []
  (->> (java.lang.System/getProperty "java.class.path")
       (re-seq #"[^;]+")
       (mapcat #(clojure.string/split % #":"))
       vec))

(defn help
  "Get help about all `r` functionality"
  []
  (describe-ns 'r :doc true))

(defn- init!
  "Initialize `r`umble helpers"
  []
  (ns.repl/disable-reload! *ns*)
  (ns.repl/disable-unload! *ns*)
  (ns.repl/set-refresh-dirs "src" "test")
  (require 'r.portal)
  (println "Rumble loaded, use (r/help) to get started"))

(init!)
