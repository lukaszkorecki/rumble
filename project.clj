(defproject org.clojars.lukaszkorecki/rumble "0.1.0-SNAPSHOT"
  :description "(n)REPL helpers and tools"
  :url "https://github.com/lukaszkorecki/rumble"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}

  :deploy-repositories [["releases" :clojars]
                        ["snapshots" :clojars]]
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/tools.namespace "1.1.0"]
                 [lambdaisland/kaocha "1.60.945"]]
  :repl-options {:init-ns rumble.repl})
