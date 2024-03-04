
# API Documentation



# `r`
> <sup>`src/r.clj`</sup>












<details>
  <summary>Functions, macros & vars</summary>

- [ppn](#r/ppn)

- [pp](#r/pp)

- [-&gt;pp](#r/-&gt;pp)

- [-&gt;&gt;pp](#r/-&gt;&gt;pp)

- [list-ns](#r/list-ns)

- [find-ns](#r/find-ns)

- [tests](#r/tests)

- [find-tests](#r/find-tests)

- [find-test-ns](#r/find-test-ns)

- [describe-ns](#r/describe-ns)

- [safe-to-refresh?](#r/safe-to-refresh?)

- [refresh](#r/refresh)

- [refresh-all](#r/refresh-all)

- [system-ns](#r/system-ns)

- [start-system!](#r/start-system!)

- [stop-system!](#r/stop-system!)

- [restart-system!](#r/restart-system!)

- [sys](#r/sys)

- [c](#r/c)

- [t](#r/t)

- [t!](#r/t!)

- [clear-aliases](#r/clear-aliases)

- [-&gt;tap&gt;](#r/-&gt;tap&gt;)

- [-&gt;&gt;tap&gt;](#r/-&gt;&gt;tap&gt;)

- [tap-log-init!](#r/tap-log-init!)

- [tap-log-get](#r/tap-log-get)

- [tap-log-clear!](#r/tap-log-clear!)

- [tap-log-stop!](#r/tap-log-stop!)

- [portal-tap](#r/portal-tap)

- [portal-instance](#r/portal-instance)

- [portal-start!](#r/portal-start!)

- [portal-clear!](#r/portal-clear!)

- [portal-stop!](#r/portal-stop!)

- [help](#r/help)

</details>

<hr />




## <a name="r/ppn">`r/ppn`</a> <sup>var</sup>
> 





> 


Pretty print given thing


<details>
  <summary><sub>Source: <code>src/r.clj:21</code></p></summary>

```clojure

(def ^{:doc "Pretty print given thing"} ppn clojure.pprint/pprint)

```

</details>



## <a name="r/pp">`r/pp`</a> <sup>function</sup>
> 





> 
Like `ppn`, but returns passed in data. Useful for debugging threaded calls


<details>
  <summary><sub>Source: <code>src/r.clj:24</code></p></summary>

```clojure

(defn pp
  "Like `ppn`, but returns passed in data. Useful for debugging threaded calls"
  [thing]
  (ppn thing)
  thing)

```

</details>



## <a name="r/-&gt;pp">`r/->pp`</a> <sup>function</sup>
> 





> 
Pretty print in `->` threading macro. Optionally tag the thing with `:tag` to pp a hash map of `{tag thing}`


<details>
  <summary><sub>Source: <code>src/r.clj:30</code></p></summary>

```clojure

(defn ->pp
  "Pretty print in `->` threading macro. Optionally tag the thing with `:tag` to pp a hash map of `{tag thing}`"
  [thing tag]
  (pp {tag thing})
  thing)

```

</details>



## <a name="r/-&gt;&gt;pp">`r/->>pp`</a> <sup>function</sup>
> 





> 
Pretty print in `->>` threading macro. Optionally tag the thing with `:tag` to pp a hash map of `{tag thing}`


<details>
  <summary><sub>Source: <code>src/r.clj:36</code></p></summary>

```clojure

(defn ->>pp
  "Pretty print in `->>` threading macro. Optionally tag the thing with `:tag` to pp a hash map of `{tag thing}`"
  [tag thing]
  (pp {tag thing})
  thing)

```

</details>



## <a name="r/list-ns">`r/list-ns`</a> <sup>function</sup>
> 





> 
Return list of symbols of namespaces found in a dir. Default: `./src`


<details>
  <summary><sub>Source: <code>src/r.clj:43</code></p></summary>

```clojure

(defn list-ns
  "Return list of symbols of namespaces found in a dir. Default: `./src`"
  ([root]
   (ns.find/find-namespaces-in-dir (File. ^String root)))
  ([]
   (list-ns "./src/")))

```

</details>



## <a name="r/find-ns">`r/find-ns`</a> <sup>function</sup>
> 





> 
Find namespace vars by a regex


<details>
  <summary><sub>Source: <code>src/r.clj:50</code></p></summary>

```clojure

(defn find-ns
  "Find namespace vars by a regex"
  [re]
  (let [nss (vec (filter #(re-find re (str %)) (list-ns)))]
    (printf ";; found %s ns\n" (count nss))
    (when (<= (count nss) 20)
      (for [n nss]
        (printf ";; %s\n" n)))
    nss))

```

</details>



## <a name="r/tests">`r/tests`</a> <sup>function</sup>
> 





> 
Find test namespace vars by a regex


<details>
  <summary><sub>Source: <code>src/r.clj:60</code></p></summary>

```clojure

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

```

</details>



## <a name="r/find-tests">`r/find-tests`</a> <sup>var</sup>
> 





> 


alias for `r/tests`


<details>
  <summary><sub>Source: <code>src/r.clj:74</code></p></summary>

```clojure

(def ^{:doc "alias for `r/tests`"} find-tests tests)

```

</details>



## <a name="r/find-test-ns">`r/find-test-ns`</a> <sup>var</sup>
> 





> 


alias for `r/tests`


<details>
  <summary><sub>Source: <code>src/r.clj:75</code></p></summary>

```clojure

(def ^{:doc "alias for `r/tests`"} find-test-ns tests)

```

</details>



## <a name="r/describe-ns">`r/describe-ns`</a> <sup>function</sup>
> 





> 
Describes given namespace by listing **public** symbols, optionally filters down via `:s <search>`
and can optionally add the doc string with `:docs?` true


<details>
  <summary><sub>Source: <code>src/r.clj:77</code></p></summary>

```clojure

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

```

</details>



## <a name="r/system-status">`r/system-status`</a> <sup>var</sup>
> 

**Private**





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r.clj:102</code></p></summary>

```clojure

(def ^:private system-status (atom {}))

```

</details>



## <a name="r/safe-to-refresh?">`r/safe-to-refresh?`</a> <sup>function</sup>
> 





> 
Check if refresh is safe, by verifying that application system is not running


<details>
  <summary><sub>Source: <code>src/r.clj:104</code></p></summary>

```clojure

(defn safe-to-refresh?
  "Check if refresh is safe, by verifying that application system is not running"
  []
  (or (empty? @system-status)
      (= #{false} (-> @system-status vals set))))

```

</details>



## <a name="r/refresh">`r/refresh`</a> <sup>function</sup>
> 





> 
Refresh changed namespaces, only if its safe


<details>
  <summary><sub>Source: <code>src/r.clj:110</code></p></summary>

```clojure

(defn refresh
  "Refresh changed namespaces, only if its safe"
  []
  (if (safe-to-refresh?)
    (ns.repl/refresh)
    ::system-running!))

```

</details>



## <a name="r/refresh-all">`r/refresh-all`</a> <sup>function</sup>
> 





> 
Refresh everything, only if its safe


<details>
  <summary><sub>Source: <code>src/r.clj:117</code></p></summary>

```clojure

(defn refresh-all
  "Refresh everything, only if its safe"
  []
  (if (safe-to-refresh?)
    (ns.repl/refresh-all)
    ::system-running!))

```

</details>



## <a name="r/system-ns">`r/system-ns`</a> <sup>function</sup>
> 





> 
Default finder for location of the system namespace.
It extracts the first segment of the namspace tree and appends `.user` to it.



<details>
  <summary><sub>Source: <code>src/r.clj:124</code></p></summary>

```clojure

(defn system-ns
  "Default finder for location of the system namespace.
  It extracts the first segment of the namspace tree and appends `.user` to it.
  "
  []
  (-> *ns*
      str
      (str/replace #"\..+" ".user")
      symbol))

```

</details>



## <a name="r/start-system!">`r/start-system!`</a> <sup>function</sup>
> 



> [!WARNING]
> This function is deprecated.




> 
Given a namespace, usually some-service, do the following:
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

Example: `(r/start-system! 'foo.user)`


<details>
  <summary><sub>Source: <code>src/r.clj:134</code></p></summary>

```clojure

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

```

</details>



## <a name="r/stop-system!">`r/stop-system!`</a> <sup>function</sup>
> 



> [!WARNING]
> This function is deprecated.




> 
Given a namespace, usually some-service.user, stop the system. If not passed, stops currently running system


<details>
  <summary><sub>Source: <code>src/r.clj:173</code></p></summary>

```clojure

(defn ^:deprecated stop-system!
  "Given a namespace, usually some-service.user, stop the system. If not passed, stops currently running system"
  ([]
   (stop-system! (first (keys @system-status))))
  ([dev-sys-ns]
   (let [f (ns-resolve dev-sys-ns 'stop)]
     (f)
     (swap! system-status (fn [s] (assoc s dev-sys-ns false))))))

```

</details>



## <a name="r/restart-system!">`r/restart-system!`</a> <sup>function</sup>
> 



> [!WARNING]
> This function is deprecated.




> 
Restarts the system with an optional reload. If the system is not running, it will start it


<details>
  <summary><sub>Source: <code>src/r.clj:182</code></p></summary>

```clojure

(defn ^:deprecated restart-system!
  "Restarts the system with an optional reload. If the system is not running, it will start it"
  []
  (when (first (keys @system-status))
    (stop-system!))
  (start-system!))

```

</details>



## <a name="r/sys">`r/sys`</a> <sup>function</sup>
> 





> 
Get the running system map


<details>
  <summary><sub>Source: <code>src/r.clj:189</code></p></summary>

```clojure

(defn sys
  "Get the running system map"
  []
  (var-get (ns-resolve (first (keys @system-status)) 'SYS)))

```

</details>



## <a name="r/c">`r/c`</a> <sup>function</sup>
> 





> 
Get a component from the running system, e.g (r/c :postgres)


<details>
  <summary><sub>Source: <code>src/r.clj:194</code></p></summary>

```clojure

(defn c
  "Get a component from the running system, e.g (r/c :postgres)"
  [component-key]
  (when-let [sys (sys)]
    (get sys component-key)))

```

</details>



## <a name="r/t">`r/t`</a> <sup>function</sup>
> 





> 
Run tests via kaocha - either all or a list of vars.
> [!NOTE]
> It will not refresh any code - use `t!` for that


<details>
  <summary><sub>Source: <code>src/r.clj:202</code></p></summary>

```clojure

(defn t
  "Run tests via kaocha - either all or a list of vars.
  > [!NOTE]
  > It will not refresh any code - use `t!` for that"
  ([]
   (kaocha.repl/run-all))
  ([ns-list]
   (apply kaocha.repl/run ns-list)))

```

</details>



## <a name="r/t!">`r/t!`</a> <sup>function</sup>
> 





> 
Run tests via kaocha, but refresh first - runs all tests or a list (or one) of ns vars.

> ![NOTE]
> Refresh happens only if it's safe to do so e.g. dev system is not running



<details>
  <summary><sub>Source: <code>src/r.clj:211</code></p></summary>

```clojure

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

```

</details>



## <a name="r/clear-aliases">`r/clear-aliases`</a> <sup>function</sup>
> 





> 
Reset aliases for given ns or current one if no args given


<details>
  <summary><sub>Source: <code>src/r.clj:224</code></p></summary>

```clojure

(defn clear-aliases
  "Reset aliases for given ns or current one if no args given"
  ([]
   (clear-aliases *ns*))
  ([an-ns]
   (mapv #(ns-unalias an-ns %) (keys (ns-aliases an-ns)))))

```

</details>



## <a name="r/tap-log">`r/tap-log`</a> <sup>var</sup>
> 

**Private**





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r.clj:233</code></p></summary>

```clojure

(def ^:private tap-log (atom []))

```

</details>



## <a name="r/tap-ref">`r/tap-ref`</a> <sup>var</sup>
> 

**Private**





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r.clj:234</code></p></summary>

```clojure

(def ^:private tap-ref (atom nil))

```

</details>



## <a name="r/-&gt;tap&gt;">`r/->tap>`</a> <sup>function</sup>
> 





> 
Like `tap>` but returns input, and is designed for threading macros. Optionally tag the thing with `:tag` to tap a hash map of `{tag thing}`


<details>
  <summary><sub>Source: <code>src/r.clj:236</code></p></summary>

```clojure

(defn ->tap>
  "Like `tap>` but returns input, and is designed for threading macros. Optionally tag the thing with `:tag` to tap a hash map of `{tag thing}`"
  ([thing]
   (->tap> thing :->tap>))
  ([thing tag]
   (tap> {tag thing})
   thing))

```

</details>



## <a name="r/-&gt;&gt;tap&gt;">`r/->>tap>`</a> <sup>function</sup>
> 





> 
Like `tap>` but returns input, and is designed for threading macros. Optionally tag the thing with `:tag` to tap a hash map of `{tag thing}`


<details>
  <summary><sub>Source: <code>src/r.clj:244</code></p></summary>

```clojure

(defn ->>tap>
  "Like `tap>` but returns input, and is designed for threading macros. Optionally tag the thing with `:tag` to tap a hash map of `{tag thing}`"
  ([thing]
   (->>tap> thing :->>tap>))
  ([tag thing]
   (tap> {tag thing})
   thing))

```

</details>



## <a name="r/tap-log-init!">`r/tap-log-init!`</a> <sup>function</sup>
> 





> 
Initialize a `tap>` listener and store the ref to it


<details>
  <summary><sub>Source: <code>src/r.clj:252</code></p></summary>

```clojure

(defn tap-log-init!
  "Initialize a `tap>` listener and store the ref to it"
  []
  (reset! tap-ref (add-tap (fn [input]
                             (swap! tap-log conj input)))))

```

</details>



## <a name="r/tap-log-get">`r/tap-log-get`</a> <sup>function</sup>
> 





> 
Return tap logged data


<details>
  <summary><sub>Source: <code>src/r.clj:258</code></p></summary>

```clojure

(defn tap-log-get
  "Return tap logged data"
  []
  @tap-log)

```

</details>



## <a name="r/tap-log-clear!">`r/tap-log-clear!`</a> <sup>function</sup>
> 





> 
Clear the log


<details>
  <summary><sub>Source: <code>src/r.clj:263</code></p></summary>

```clojure

(defn tap-log-clear!
  "Clear the log"
  []
  (reset! tap-log []))

```

</details>



## <a name="r/tap-log-stop!">`r/tap-log-stop!`</a> <sup>function</sup>
> 





> 
Clear tap log and remove the listener


<details>
  <summary><sub>Source: <code>src/r.clj:268</code></p></summary>

```clojure

(defn tap-log-stop!
  "Clear tap log and remove the listener"
  []
  (remove-tap @tap-ref)
  (tap-log-clear!)
  (reset! tap-ref nil))

```

</details>



## <a name="r/portal-tap">`r/portal-tap`</a> <sup>var</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r.clj:275</code></p></summary>

```clojure

(def portal-tap (atom nil))

```

</details>



## <a name="r/portal-instance">`r/portal-instance`</a> <sup>var</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r.clj:276</code></p></summary>

```clojure

(def portal-instance (atom nil))

```

</details>



## <a name="r/portal-start!">`r/portal-start!`</a> <sup>function</sup>
> 





> 
Start portal instance and optionally open it in a browser


<details>
  <summary><sub>Source: <code>src/r.clj:278</code></p></summary>

```clojure

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

```

</details>



## <a name="r/portal-clear!">`r/portal-clear!`</a> <sup>function</sup>
> 





> 
Clear current portal session view


<details>
  <summary><sub>Source: <code>src/r.clj:293</code></p></summary>

```clojure

(defn portal-clear!
  "Clear current portal session view"
  []
  (portal.api/clear))

```

</details>



## <a name="r/portal-stop!">`r/portal-stop!`</a> <sup>function</sup>
> 





> 
Stop portal session


<details>
  <summary><sub>Source: <code>src/r.clj:298</code></p></summary>

```clojure

(defn portal-stop!
  "Stop portal session"
  []
  (swap! portal-tap remove-tap)
  (portal.api/close @portal-instance))

```

</details>



## <a name="r/help">`r/help`</a> <sup>function</sup>
> 





> 
Get help about all `r` functionality


<details>
  <summary><sub>Source: <code>src/r.clj:307</code></p></summary>

```clojure

(defn help
  "Get help about all `r` functionality"
  []
  (describe-ns 'r :doc true))

```

</details>



## <a name="r/init!">`r/init!`</a> <sup>function</sup>
> 

**Private**





> 
Initialize `r`umble helpers


<details>
  <summary><sub>Source: <code>src/r.clj:312</code></p></summary>

```clojure

(defn- init!
  "Initialize `r`umble helpers"
  []
  (ns.repl/disable-reload! *ns*)
  (ns.repl/set-refresh-dirs "src" "test")
  (println "Rumble loaded, use (r/help) to get started"))

```

</details>






# `r.kaocha-hooks`
> <sup>`src/r/kaocha_hooks.clj`</sup>












<details>
  <summary>Functions, macros & vars</summary>

- [results](#r.kaocha-hooks/results)

- [unpack-failure](#r.kaocha-hooks/unpack-failure)

- [report*](#r.kaocha-hooks/report*)

- [reporter](#r.kaocha-hooks/reporter)

</details>

<hr />




## <a name="r.kaocha-hooks/results">`r.kaocha-hooks/results`</a> <sup>var</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r/kaocha_hooks.clj:7</code></p></summary>

```clojure

(def results (atom []))

```

</details>



## <a name="r.kaocha-hooks/unpack-failure">`r.kaocha-hooks/unpack-failure`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r/kaocha_hooks.clj:9</code></p></summary>

```clojure

(defn unpack-failure [testable]
  (let [{:keys [actual expected :kaocha/testable]} testable]
    :x))

```

</details>



## <a name="r.kaocha-hooks/report*">`r.kaocha-hooks/report*`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r/kaocha_hooks.clj:13</code></p></summary>

```clojure

(defn report* [{:keys [type] :as testable}]
  (let [reportable (case type
                     :begin-test-suite (do
                                         (tap> results)
                                         (reset! results []))
                     :summary (r.portal/table (select-keys testable [:error :fail :pass :pending :test]))
                     :begin-test-ns (r.portal/hiccup
                                     [:strong (str (-> testable :kaocha/testable :kaocha.testable/id))])
                     :begin-test-var (r.portal/hiccup
                                      [:em (str (-> testable :kaocha/testable :kaocha.testable/id))])
                     :pass {:pass testable}
                     :fail {:fail testable}
                     :error {:error testable}
                     (:end-test-suite :end-test-var :end-test-ns) nil
                     #_else type)]

    (when reportable
      (swap! results conj reportable))
    #_(tap> reportable))
  testable)

```

</details>



## <a name="r.kaocha-hooks/reporter">`r.kaocha-hooks/reporter`</a> <sup>var</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r/kaocha_hooks.clj:34</code></p></summary>

```clojure

(def reporter
  [report* kaocha.report/result])

```

</details>






# `r.portal`
> <sup>`src/r/portal.clj`</sup>












<details>
  <summary>Functions, macros & vars</summary>

- [v](#r.portal/v)

- [table](#r.portal/table)

- [hiccup](#r.portal/hiccup)

- [test-report](#r.portal/test-report)

- [diff](#r.portal/diff)

</details>

<hr />




## <a name="r.portal/v">`r.portal/v`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r/portal.clj:3</code></p></summary>

```clojure

(defn v [type thing]
  (with-meta
    thing
    {:portal.viewer/default type}))

```

</details>



## <a name="r.portal/table">`r.portal/table`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r/portal.clj:8</code></p></summary>

```clojure

(defn table [thing]
  (v :portal.viewer/table thing))

```

</details>



## <a name="r.portal/hiccup">`r.portal/hiccup`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r/portal.clj:11</code></p></summary>

```clojure

(defn hiccup [thing]
  (v :portal.viewer/hiccup thing))

```

</details>



## <a name="r.portal/test-report">`r.portal/test-report`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r/portal.clj:14</code></p></summary>

```clojure

(defn test-report [thing]
  (v :portal.viewer/test-report thing))

```

</details>



## <a name="r.portal/diff">`r.portal/diff`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r/portal.clj:17</code></p></summary>

```clojure

(defn diff [thing]
  (v :portal.viewer/diff thing))

```

</details>






# `r.sql`
> <sup>`src/r/sql.clj`</sup>












<details>
  <summary>Functions, macros & vars</summary>

- [formatter](#r.sql/formatter)

- [format-sql](#r.sql/format-sql)

- [query-view](#r.sql/query-view)

- [debug-query-&gt;portal](#r.sql/debug-query-&gt;portal)

- [query-execution-error-&gt;portal](#r.sql/query-execution-error-&gt;portal)

- [with-debug](#r.sql/with-debug)

- [query](#r.sql/query)

</details>

<hr />




## <a name="r.sql/formatter">`r.sql/formatter`</a> <sup>var</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r/sql.clj:8</code></p></summary>

```clojure

(def formatter ^SqlFormatter (SqlFormatter/of "postgresql"))

```

</details>



## <a name="r.sql/format-sql">`r.sql/format-sql`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r/sql.clj:10</code></p></summary>

```clojure

(defn format-sql [sql-str]
  (.format ^SqlFormatter formatter ^String sql-str))

```

</details>



## <a name="r.sql/query-view">`r.sql/query-view`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r/sql.clj:13</code></p></summary>

```clojure

(defn query-view [sql-vec]
  (r.p/hiccup [:div
               [:h2 "Query"]
               [:portal.viewer/code (format-sql (first sql-vec))]
               (when-let [params (->> sql-vec
                                      rest
                                      seq
                                      (map-indexed (fn [i p] {:i i :p p})))]
                 [:div
                  [:h3 "Params"]
                  [:ul
                   (for [{:keys [i p]} params]
                     [:li [:strong (str "$" i)] " " p])]])]))

```

</details>



## <a name="r.sql/debug-query-&gt;portal">`r.sql/debug-query->portal`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r/sql.clj:27</code></p></summary>

```clojure

(defn debug-query->portal
  [{:keys [sql-vec result]}]
  (tap> (query-view sql-vec))
  (tap> (r.p/table result))
  sql-vec)

```

</details>



## <a name="r.sql/query-execution-error-&gt;portal">`r.sql/query-execution-error->portal`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r/sql.clj:33</code></p></summary>

```clojure

(defn query-execution-error->portal [e sql-vec]
  (let [msg (.getMessage ^Exception e)
        position? (->> msg
                       (str/split-lines)
                       (filter #(re-find #"Position:" %))
                       (first))
        position (when position?
                   (-> position?
                       (str/split #": ")
                       (last)
                       parse-long))
        sql-string (if position
                     (str
                      (subs (first sql-vec) 0 (dec position))
                      "/* < FAILED HERE > */"
                      (subs (first sql-vec) (dec position)))
                     (first sql-vec))
        sql-vec (concat [sql-string] (rest sql-vec))]
    (tap> {:error e
           :details (r.p/hiccup [:div
                                 [:h1 "Query exception"]
                                 [:div
                                  [:h2 "Error"]
                                  [:portal.viewer/exception msg]]
                                 (query-view sql-vec)])})))

```

</details>



## <a name="r.sql/with-debug">`r.sql/with-debug`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r/sql.clj:59</code></p></summary>

```clojure

(defn with-debug [execute-fn conn sql-vec]
  (try
    (let [result (execute-fn conn sql-vec)]
      (debug-query->portal {:sql-vec sql-vec :result result})
      result)
    (catch Throwable e
      (query-execution-error->portal e sql-vec)
      (throw e))))

```

</details>



## <a name="r.sql/query">`r.sql/query`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>src/r/sql.clj:70</code></p></summary>

```clojure

(defn query [db]
    (->> {:select [:*] :from :table}
         (sql/format)
         (r.sql/with-debug jdbc/execute! db)))

```

</details>






# `r`
> <sup>`target/classes/r.clj`</sup>












<details>
  <summary>Functions, macros & vars</summary>

- [ppn](#r/ppn)

- [pp](#r/pp)

- [-&gt;pp](#r/-&gt;pp)

- [-&gt;&gt;pp](#r/-&gt;&gt;pp)

- [list-ns](#r/list-ns)

- [find-ns](#r/find-ns)

- [tests](#r/tests)

- [find-test-ns](#r/find-test-ns)

- [describe-ns](#r/describe-ns)

- [safe-to-refresh?](#r/safe-to-refresh?)

- [refresh](#r/refresh)

- [refresh-all](#r/refresh-all)

- [system-ns](#r/system-ns)

- [start-system!](#r/start-system!)

- [stop-system!](#r/stop-system!)

- [restart-system!](#r/restart-system!)

- [sys](#r/sys)

- [c](#r/c)

- [t](#r/t)

- [t!](#r/t!)

- [clear-aliases](#r/clear-aliases)

- [-&gt;tap&gt;](#r/-&gt;tap&gt;)

- [-&gt;&gt;tap&gt;](#r/-&gt;&gt;tap&gt;)

- [tap-log-init!](#r/tap-log-init!)

- [tap-log-get](#r/tap-log-get)

- [tap-log-clear!](#r/tap-log-clear!)

- [tap-log-stop!](#r/tap-log-stop!)

- [portal-tap](#r/portal-tap)

- [portal-instance](#r/portal-instance)

- [portal-start!](#r/portal-start!)

- [portal-clear!](#r/portal-clear!)

- [portal-stop!](#r/portal-stop!)

- [help](#r/help)

</details>

<hr />




## <a name="r/ppn">`r/ppn`</a> <sup>var</sup>
> 





> 


Pretty print given thing


<details>
  <summary><sub>Source: <code>target/classes/r.clj:21</code></p></summary>

```clojure

(def ^{:doc "Pretty print given thing"} ppn clojure.pprint/pprint)

```

</details>



## <a name="r/pp">`r/pp`</a> <sup>function</sup>
> 





> 
Like ppn, but returns passed in data. Useful for debugging threaded calls


<details>
  <summary><sub>Source: <code>target/classes/r.clj:24</code></p></summary>

```clojure

(defn pp
  "Like ppn, but returns passed in data. Useful for debugging threaded calls"
  [thing]
  (ppn thing)
  thing)

```

</details>



## <a name="r/-&gt;pp">`r/->pp`</a> <sup>function</sup>
> 





> 
Pretty print in -> threading macro. Optionally tag the thing with :tag to pp a hash map of {tag thing}


<details>
  <summary><sub>Source: <code>target/classes/r.clj:30</code></p></summary>

```clojure

(defn ->pp
  "Pretty print in -> threading macro. Optionally tag the thing with :tag to pp a hash map of {tag thing}"
  [thing tag]
  (pp {tag thing})
  thing)

```

</details>



## <a name="r/-&gt;&gt;pp">`r/->>pp`</a> <sup>function</sup>
> 





> 
Pretty print in ->> threading macro. Optionally tag the thing with :tag to pp a hash map of {tag thing}


<details>
  <summary><sub>Source: <code>target/classes/r.clj:36</code></p></summary>

```clojure

(defn ->>pp
  "Pretty print in ->> threading macro. Optionally tag the thing with :tag to pp a hash map of {tag thing}"
  [tag thing]
  (pp {tag thing})
  thing)

```

</details>



## <a name="r/list-ns">`r/list-ns`</a> <sup>function</sup>
> 





> 
Return list of symbols of namespaces found in src dir. Default: ./src


<details>
  <summary><sub>Source: <code>target/classes/r.clj:43</code></p></summary>

```clojure

(defn list-ns
  "Return list of symbols of namespaces found in src dir. Default: ./src"
  ([root]
   (ns.find/find-namespaces-in-dir (File. ^String root)))
  ([]
   (list-ns "./src/")))

```

</details>



## <a name="r/find-ns">`r/find-ns`</a> <sup>function</sup>
> 





> 
Find namespace vars by a regex


<details>
  <summary><sub>Source: <code>target/classes/r.clj:50</code></p></summary>

```clojure

(defn find-ns
  "Find namespace vars by a regex"
  [re]
  (let [nss (vec (filter #(re-find re (str %)) (list-ns)))]
    (printf ";; found %s ns\n" (count nss))
    (when (<= (count nss) 20)
      (for [n nss]
        (printf ";; %s\n" n)))
    nss))

```

</details>



## <a name="r/tests">`r/tests`</a> <sup>function</sup>
> 





> 
Find test namespace vars by a regex


<details>
  <summary><sub>Source: <code>target/classes/r.clj:60</code></p></summary>

```clojure

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

```

</details>



## <a name="r/find-test-ns">`r/find-test-ns`</a> <sup>var</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r.clj:75</code></p></summary>

```clojure

(def find-test-ns tests)

```

</details>



## <a name="r/describe-ns">`r/describe-ns`</a> <sup>function</sup>
> 





> 
Describes given namespace by listing PUBLIC symbols, optionally filters down via :s <search>
and can optionally add the doc string with :doc true


<details>
  <summary><sub>Source: <code>target/classes/r.clj:77</code></p></summary>

```clojure

(defn describe-ns
  "Describes given namespace by listing PUBLIC symbols, optionally filters down via :s <search>
  and can optionally add the doc string with :doc true"
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

```

</details>



## <a name="r/system-status">`r/system-status`</a> <sup>var</sup>
> 

**Private**





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r.clj:102</code></p></summary>

```clojure

(def ^:private system-status (atom {}))

```

</details>



## <a name="r/safe-to-refresh?">`r/safe-to-refresh?`</a> <sup>function</sup>
> 





> 
Check if refresh is safe, by verifying that application system is not running


<details>
  <summary><sub>Source: <code>target/classes/r.clj:104</code></p></summary>

```clojure

(defn safe-to-refresh?
  "Check if refresh is safe, by verifying that application system is not running"
  []
  (or (empty? @system-status)
      (= #{false} (-> @system-status vals set))))

```

</details>



## <a name="r/refresh">`r/refresh`</a> <sup>function</sup>
> 





> 
Refresh changed namespaces, only if its safe


<details>
  <summary><sub>Source: <code>target/classes/r.clj:110</code></p></summary>

```clojure

(defn refresh
  "Refresh changed namespaces, only if its safe"
  []
  (if (safe-to-refresh?)
    (ns.repl/refresh)
    ::system-running!))

```

</details>



## <a name="r/refresh-all">`r/refresh-all`</a> <sup>function</sup>
> 





> 
Refresh everything, only if its safe


<details>
  <summary><sub>Source: <code>target/classes/r.clj:117</code></p></summary>

```clojure

(defn refresh-all
  "Refresh everything, only if its safe"
  []
  (if (safe-to-refresh?)
    (ns.repl/refresh-all)
    ::system-running!))

```

</details>



## <a name="r/system-ns">`r/system-ns`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r.clj:124</code></p></summary>

```clojure

(defn system-ns []
  (-> *ns*
      str
      (str/replace #"\..+" ".user")
      symbol))

```

</details>



## <a name="r/start-system!">`r/start-system!`</a> <sup>function</sup>
> 





> 
Given a namespace, usually some-service, do the following:
- find some-service.user namespace (by convention)
- refresh
- require the user ns e.g. some-service.user
- start  system, invoking somer-service.user/start
Warning: best if the system is not running, or things will go south

Example: (r/start-system! 'foo.user)


<details>
  <summary><sub>Source: <code>target/classes/r.clj:130</code></p></summary>

```clojure

(defn start-system!
  "Given a namespace, usually some-service, do the following:
  - find some-service.user namespace (by convention)
  - refresh
  - require the user ns e.g. some-service.user
  - start  system, invoking somer-service.user/start
  Warning: best if the system is not running, or things will go south

  Example: (r/start-system! 'foo.user)"
  ([]
   ;; automagically guess the <app>.user namespace
   (let [dev-sys-ns (system-ns)]
     (require dev-sys-ns)
     (start-system! dev-sys-ns)))
  ([dev-sys-ns]
   (printf ";; Starting %s\n" dev-sys-ns)
   (when (= "r" (str dev-sys-ns))
     (throw (ex-info "nope" {:ns (str dev-sys-ns)})))
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

```

</details>



## <a name="r/stop-system!">`r/stop-system!`</a> <sup>function</sup>
> 





> 
Given a namespace, usually some-service.user, stop the system. If not passed, stops currently running system


<details>
  <summary><sub>Source: <code>target/classes/r.clj:159</code></p></summary>

```clojure

(defn stop-system!
  "Given a namespace, usually some-service.user, stop the system. If not passed, stops currently running system"
  ([]
   (stop-system! (first (keys @system-status))))
  ([dev-sys-ns]
   (let [f (ns-resolve dev-sys-ns 'stop)]
     (f)
     (swap! system-status (fn [s] (assoc s dev-sys-ns false))))))

```

</details>



## <a name="r/restart-system!">`r/restart-system!`</a> <sup>function</sup>
> 





> 
Restarts the system with an optional reload. If the system is not running, it will start it


<details>
  <summary><sub>Source: <code>target/classes/r.clj:168</code></p></summary>

```clojure

(defn restart-system!
  "Restarts the system with an optional reload. If the system is not running, it will start it"
  []
  (when (first (keys @system-status))
    (stop-system!))
  (start-system!))

```

</details>



## <a name="r/sys">`r/sys`</a> <sup>function</sup>
> 





> 
Get the running system map


<details>
  <summary><sub>Source: <code>target/classes/r.clj:175</code></p></summary>

```clojure

(defn sys
  "Get the running system map"
  []
  (var-get (ns-resolve (first (keys @system-status)) 'SYS)))

```

</details>



## <a name="r/c">`r/c`</a> <sup>function</sup>
> 





> 
Get a component from the running system, e.g (r/c :postgres)


<details>
  <summary><sub>Source: <code>target/classes/r.clj:180</code></p></summary>

```clojure

(defn c
  "Get a component from the running system, e.g (r/c :postgres)"
  [component-key]
  (when-let [sys (sys)]
    (get sys component-key)))

```

</details>



## <a name="r/kaocha-dummy-conf">`r/kaocha-dummy-conf`</a> <sup>var</sup>
> 

**Private**





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r.clj:188</code></p></summary>

```clojure

(def ^:private kaocha-dummy-conf {:config nil})

```

</details>



## <a name="r/t">`r/t`</a> <sup>function</sup>
> 





> 
Run tests via kaocha - either all or a list of vars. WILL NOT REFRESH ANY CODE


<details>
  <summary><sub>Source: <code>target/classes/r.clj:190</code></p></summary>

```clojure

(defn t
  "Run tests via kaocha - either all or a list of vars. WILL NOT REFRESH ANY CODE"
  ([]
   (kaocha.repl/run :unit kaocha-dummy-conf))
  ([ns-list]
   (apply kaocha.repl/run (flatten [ns-list [kaocha-dummy-conf]]))))

```

</details>



## <a name="r/t!">`r/t!`</a> <sup>function</sup>
> 





> 
Run tests via kaocha, but refresh first - runs all tests or a list (or one) of ns vars


<details>
  <summary><sub>Source: <code>target/classes/r.clj:197</code></p></summary>

```clojure

(defn t!
  "Run tests via kaocha, but refresh first - runs all tests or a list (or one) of ns vars"
  ([]
   (println (refresh))
   (kaocha.repl/run :unit kaocha-dummy-conf))
  ([& ns-list]
   (println (refresh))
   (apply kaocha.repl/run (flatten [ns-list [kaocha-dummy-conf]]))))

```

</details>



## <a name="r/clear-aliases">`r/clear-aliases`</a> <sup>function</sup>
> 





> 
Reset aliases for given ns or current one if no args given


<details>
  <summary><sub>Source: <code>target/classes/r.clj:206</code></p></summary>

```clojure

(defn clear-aliases
  "Reset aliases for given ns or current one if no args given"
  ([]
   (clear-aliases *ns*))
  ([an-ns]
   (mapv #(ns-unalias an-ns %) (keys (ns-aliases an-ns)))))

```

</details>



## <a name="r/tap-log">`r/tap-log`</a> <sup>var</sup>
> 

**Private**





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r.clj:215</code></p></summary>

```clojure

(def ^:private tap-log (atom []))

```

</details>



## <a name="r/tap-ref">`r/tap-ref`</a> <sup>var</sup>
> 

**Private**





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r.clj:216</code></p></summary>

```clojure

(def ^:private tap-ref (atom nil))

```

</details>



## <a name="r/-&gt;tap&gt;">`r/->tap>`</a> <sup>function</sup>
> 





> 
Like tap> but returns input, and is designed for threading macros. Optionally tag the thing with :tag to tap a hash map of {tag thing}


<details>
  <summary><sub>Source: <code>target/classes/r.clj:218</code></p></summary>

```clojure

(defn ->tap>
  "Like tap> but returns input, and is designed for threading macros. Optionally tag the thing with :tag to tap a hash map of {tag thing}"
  ([thing]
   (->tap> thing :->tap>))
  ([thing tag]
   (tap> {tag thing})
   thing))

```

</details>



## <a name="r/-&gt;&gt;tap&gt;">`r/->>tap>`</a> <sup>function</sup>
> 





> 
Like tap> but returns input, and is designed for threading macros. Optionally tag the thing with :tag to tap a hash map of {tag thing}


<details>
  <summary><sub>Source: <code>target/classes/r.clj:226</code></p></summary>

```clojure

(defn ->>tap>
  "Like tap> but returns input, and is designed for threading macros. Optionally tag the thing with :tag to tap a hash map of {tag thing}"
  ([thing]
   (->>tap> thing :->>tap>))
  ([tag thing]
   (tap> {tag thing})
   thing))

```

</details>



## <a name="r/tap-log-init!">`r/tap-log-init!`</a> <sup>function</sup>
> 





> 
Initialize a tap> listener and store the ref to it


<details>
  <summary><sub>Source: <code>target/classes/r.clj:234</code></p></summary>

```clojure

(defn tap-log-init!
  "Initialize a tap> listener and store the ref to it"
  []
  (reset! tap-ref (add-tap (fn [input]
                             (swap! tap-log conj input)))))

```

</details>



## <a name="r/tap-log-get">`r/tap-log-get`</a> <sup>function</sup>
> 





> 
Return tap logged data


<details>
  <summary><sub>Source: <code>target/classes/r.clj:240</code></p></summary>

```clojure

(defn tap-log-get
  "Return tap logged data"
  []
  @tap-log)

```

</details>



## <a name="r/tap-log-clear!">`r/tap-log-clear!`</a> <sup>function</sup>
> 





> 
Clear the log


<details>
  <summary><sub>Source: <code>target/classes/r.clj:245</code></p></summary>

```clojure

(defn tap-log-clear!
  "Clear the log"
  []
  (reset! tap-log []))

```

</details>



## <a name="r/tap-log-stop!">`r/tap-log-stop!`</a> <sup>function</sup>
> 





> 
Clear tap log and remove the listener


<details>
  <summary><sub>Source: <code>target/classes/r.clj:250</code></p></summary>

```clojure

(defn tap-log-stop!
  "Clear tap log and remove the listener"
  []
  (remove-tap @tap-ref)
  (tap-log-clear!)
  (reset! tap-ref nil))

```

</details>



## <a name="r/portal-tap">`r/portal-tap`</a> <sup>var</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r.clj:257</code></p></summary>

```clojure

(def portal-tap (atom nil))

```

</details>



## <a name="r/portal-instance">`r/portal-instance`</a> <sup>var</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r.clj:258</code></p></summary>

```clojure

(def portal-instance (atom nil))

```

</details>



## <a name="r/portal-start!">`r/portal-start!`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r.clj:260</code></p></summary>

```clojure

(defn portal-start!
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

```

</details>



## <a name="r/portal-clear!">`r/portal-clear!`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r.clj:274</code></p></summary>

```clojure

(defn portal-clear! []
  (portal.api/clear))

```

</details>



## <a name="r/portal-stop!">`r/portal-stop!`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r.clj:277</code></p></summary>

```clojure

(defn portal-stop! []
  (swap! portal-tap remove-tap)
  (portal.api/close @portal-instance))

```

</details>



## <a name="r/help">`r/help`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r.clj:285</code></p></summary>

```clojure

(defn help []
  (describe-ns 'r :doc true))

```

</details>



## <a name="r/init!">`r/init!`</a> <sup>function</sup>
> 

**Private**





> 
Initialize the helper namespace


<details>
  <summary><sub>Source: <code>target/classes/r.clj:288</code></p></summary>

```clojure

(defn- init!
  "Initialize the helper namespace"
  []
  (ns.repl/disable-reload! *ns*)
  (ns.repl/set-refresh-dirs "src" "test")
  (println "Rumble loaded, use (r/help) to get started"))

```

</details>






# `r.kaocha-hooks`
> <sup>`target/classes/r/kaocha_hooks.clj`</sup>












<details>
  <summary>Functions, macros & vars</summary>

- [results](#r.kaocha-hooks/results)

- [unpack-failure](#r.kaocha-hooks/unpack-failure)

- [report*](#r.kaocha-hooks/report*)

- [reporter](#r.kaocha-hooks/reporter)

</details>

<hr />




## <a name="r.kaocha-hooks/results">`r.kaocha-hooks/results`</a> <sup>var</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r/kaocha_hooks.clj:7</code></p></summary>

```clojure

(def results (atom []))

```

</details>



## <a name="r.kaocha-hooks/unpack-failure">`r.kaocha-hooks/unpack-failure`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r/kaocha_hooks.clj:9</code></p></summary>

```clojure

(defn unpack-failure [testable]
  (let [{:keys [actual expected :kaocha/testable]} testable]
    :x))

```

</details>



## <a name="r.kaocha-hooks/report*">`r.kaocha-hooks/report*`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r/kaocha_hooks.clj:13</code></p></summary>

```clojure

(defn report* [{:keys [type] :as testable}]
  (let [reportable (case type
                     :begin-test-suite (do
                                         (tap> results)
                                         (reset! results []))
                     :summary (r.portal/table (select-keys testable [:error :fail :pass :pending :test]))
                     :begin-test-ns (r.portal/hiccup
                                     [:strong (str (-> testable :kaocha/testable :kaocha.testable/id))])
                     :begin-test-var (r.portal/hiccup
                                      [:em (str (-> testable :kaocha/testable :kaocha.testable/id))])
                     :pass {:pass testable}
                     :fail {:fail testable}
                     :error {:error testable}
                     (:end-test-suite :end-test-var :end-test-ns) nil
                     #_else type)]

    (when reportable
      (swap! results conj reportable))
    #_(tap> reportable))
  testable)

```

</details>



## <a name="r.kaocha-hooks/reporter">`r.kaocha-hooks/reporter`</a> <sup>var</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r/kaocha_hooks.clj:34</code></p></summary>

```clojure

(def reporter
  [report* kaocha.report/result])

```

</details>






# `r.portal`
> <sup>`target/classes/r/portal.clj`</sup>












<details>
  <summary>Functions, macros & vars</summary>

- [v](#r.portal/v)

- [table](#r.portal/table)

- [hiccup](#r.portal/hiccup)

- [test-report](#r.portal/test-report)

- [diff](#r.portal/diff)

</details>

<hr />




## <a name="r.portal/v">`r.portal/v`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r/portal.clj:3</code></p></summary>

```clojure

(defn v [type thing]
  (with-meta
    thing
    {:portal.viewer/default type}))

```

</details>



## <a name="r.portal/table">`r.portal/table`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r/portal.clj:8</code></p></summary>

```clojure

(defn table [thing]
  (v :portal.viewer/table thing))

```

</details>



## <a name="r.portal/hiccup">`r.portal/hiccup`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r/portal.clj:11</code></p></summary>

```clojure

(defn hiccup [thing]
  (v :portal.viewer/hiccup thing))

```

</details>



## <a name="r.portal/test-report">`r.portal/test-report`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r/portal.clj:14</code></p></summary>

```clojure

(defn test-report [thing]
  (v :portal.viewer/test-report thing))

```

</details>



## <a name="r.portal/diff">`r.portal/diff`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r/portal.clj:17</code></p></summary>

```clojure

(defn diff [thing]
  (v :portal.viewer/diff thing))

```

</details>






# `r.sql`
> <sup>`target/classes/r/sql.clj`</sup>












<details>
  <summary>Functions, macros & vars</summary>

- [formatter](#r.sql/formatter)

- [format-sql](#r.sql/format-sql)

- [query-view](#r.sql/query-view)

- [debug-query-&gt;portal](#r.sql/debug-query-&gt;portal)

- [query-execution-error-&gt;portal](#r.sql/query-execution-error-&gt;portal)

- [with-debug](#r.sql/with-debug)

- [query](#r.sql/query)

</details>

<hr />




## <a name="r.sql/formatter">`r.sql/formatter`</a> <sup>var</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r/sql.clj:8</code></p></summary>

```clojure

(def formatter ^SqlFormatter (SqlFormatter/of "postgresql"))

```

</details>



## <a name="r.sql/format-sql">`r.sql/format-sql`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r/sql.clj:10</code></p></summary>

```clojure

(defn format-sql [sql-str]
  (.format ^SqlFormatter formatter ^String sql-str))

```

</details>



## <a name="r.sql/query-view">`r.sql/query-view`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r/sql.clj:13</code></p></summary>

```clojure

(defn query-view [sql-vec]
  (r.p/hiccup [:div
               [:h2 "Query"]
               [:portal.viewer/code (format-sql (first sql-vec))]
               (when-let [params (->> sql-vec
                                      rest
                                      seq
                                      (map-indexed (fn [i p] {:i i :p p})))]
                 [:div
                  [:h3 "Params"]
                  [:ul
                   (for [{:keys [i p]} params]
                     [:li [:strong (str "$" i)] " " p])]])]))

```

</details>



## <a name="r.sql/debug-query-&gt;portal">`r.sql/debug-query->portal`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r/sql.clj:27</code></p></summary>

```clojure

(defn debug-query->portal
  [{:keys [sql-vec result]}]
  (tap> (query-view sql-vec))
  (tap> (r.p/table result))
  sql-vec)

```

</details>



## <a name="r.sql/query-execution-error-&gt;portal">`r.sql/query-execution-error->portal`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r/sql.clj:33</code></p></summary>

```clojure

(defn query-execution-error->portal [e sql-vec]
  (let [msg (.getMessage ^Exception e)
        position? (->> msg
                       (str/split-lines)
                       (filter #(re-find #"Position:" %))
                       (first))
        position (when position?
                   (-> position?
                       (str/split #": ")
                       (last)
                       parse-long))
        sql-string (if position
                     (str
                      (subs (first sql-vec) 0 (dec position))
                      "/* < FAILED HERE > */"
                      (subs (first sql-vec) (dec position)))
                     (first sql-vec))
        sql-vec (concat [sql-string] (rest sql-vec))]
    (tap> {:error e
           :details (r.p/hiccup [:div
                                 [:h1 "Query exception"]
                                 [:div
                                  [:h2 "Error"]
                                  [:portal.viewer/exception msg]]
                                 (query-view sql-vec)])})))

```

</details>



## <a name="r.sql/with-debug">`r.sql/with-debug`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r/sql.clj:59</code></p></summary>

```clojure

(defn with-debug [execute-fn conn sql-vec]
  (try
    (let [result (execute-fn conn sql-vec)]
      (debug-query->portal {:sql-vec sql-vec :result result})
      result)
    (catch Throwable e
      (query-execution-error->portal e sql-vec)
      (throw e))))

```

</details>



## <a name="r.sql/query">`r.sql/query`</a> <sup>function</sup>
> 





> 
> *no doc*


<details>
  <summary><sub>Source: <code>target/classes/r/sql.clj:70</code></p></summary>

```clojure

(defn query [db]
    (->> {:select [:*] :from :table}
         (sql/format)
         (r.sql/with-debug jdbc/execute! db)))

```

</details>





Generated 04/03/2024 08:19
