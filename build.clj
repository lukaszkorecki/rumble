(ns build
  (:require [clojure.tools.build.api :as b]
            [deps-deploy.deps-deploy :as dd]))

(def lib 'org.clojars.lukaszkorecki/rumble)
(def version-stable (format "1.0.0.%s" (b/git-count-revs nil)))
(defn version-snapshot [suffix] (format "%s-SNAPSHOT-%s" version-stable suffix))

(def class-dir "target/classes")
(defn jar-file [version] (format "target/%s-%s.jar" (name lib) version))
(def target "target")
;; delay to defer side effects (artifact downloads)
(def basis (delay (b/create-basis {:project "deps.edn"})))

(def ^:private pom-template
  [[:description "A very thin wrapper around clojure.tools.logging which adds MDC support"]
   [:url "https://github.com/lukaszkorecki/rumble"]
   [:licenses
    [:license
     [:name "MIT"]
     [:url "https://opensource.org/license/mit/"]]]
   [:scm
    [:url "https://github.com/lukaszkorecki/rumble"]
    [:connection "scm:git:git://github.com/lukaszkorecki/rumble.git"]
    [:developerConnection "scm:git:ssh://git@github.com/lukaszkorecki/rumble.git"]]])

(defn ^:private jar-opts
  [{:keys [version] :as opts}]
  (assoc opts
         :lib lib
         :version version
         :jar-file (jar-file version)
         :basis (b/create-basis)
         :class-dir class-dir
         :target target
         :src-dirs ["src"]
         :pom-data pom-template))

;; Tasks

(defn clean [_]
  (b/delete {:path target}))

(defn jar
  [{:keys [snapshot] :as _args}]
  (let [{:keys [jar-file] :as opts} (jar-opts {:version (if snapshot
                                                          (version-snapshot snapshot)
                                                          version-stable)})]
    (println (format "Cleaning '%s'..." target))
    (b/delete {:path "target"})
    (println (format "Writing 'pom.xml'..."))
    (b/write-pom opts)
    (println (format "Copying source files to '%s'..." class-dir))
    (b/copy-dir {:src-dirs ["src" "resources"] :target-dir class-dir})
    (println (format "Building JAR to '%s'..." jar-file))
    (b/jar opts)
    (println "Finished.")))

(defn install
  [{:keys [snapshot]}]
  (let [{:keys [jar-file] :as opts} (jar-opts {:version (if snapshot
                                                          (version-snapshot snapshot)
                                                          version-stable)})]
    (dd/deploy {:installer :local
                :artifact (b/resolve-path jar-file)
                :pom-file (b/pom-path (select-keys opts [:lib :class-dir]))})))

(defn publish
  [{:keys [snapshot]}]
  (let [{:keys [jar-file] :as opts} (jar-opts {:version (if snapshot
                                                          (version-snapshot snapshot)
                                                          version-stable)})]
    (dd/deploy {:installer :remote
                :artifact (b/resolve-path jar-file)
                :pom-file (b/pom-path (select-keys opts [:lib :class-dir]))})))
