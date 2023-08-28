(defproject org.clojars.lukaszkorecki/rumble "0.1.0-SNAPSHOT-7"
  :description "(n)REPL helpers and tools"
  :url "https://github.com/lukaszkorecki/rumble"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :deploy-repositories {"clojars" {:sign-releases false
                                   :username :env/clojars_username
                                   :password :env/clojars_password}}

  :dependencies [[org.clojure/clojure "1.11.1"]
                 [org.clojure/tools.namespace "1.4.4"]
                 [lambdaisland/kaocha "1.85.1342"]]
  :repl-options {:init-ns r})
