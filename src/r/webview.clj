(ns ^{:clojure.tools.namespace.repl/load false} r.webview
  (:require [cljfx.api :as fx]
            [cljfx.lifecycle :as lifecycle]
            [cljfx.mutator :as mutator]
            [cljfx.prop :as prop]
            [clojure.string :as str])
  (:import [javafx.scene.web WebView]))

(def java-major-version
  (-> (System/getProperty "java.version") (str/split #"\.") first parse-long))

;; we need this because JDK11 blows up on mac-os regardless of how it was installed
(when (< java-major-version 21)
  (println "WARNING: You are using a JDK version < 21, which may cause issues with the web-view on macOS. Consider using JDK 21 or later."))

;; Kick off, based on examples in clfx repo

(def web-view-with-ext-props
  (fx/make-ext-with-props
   {:on-location-changed (prop/make (mutator/property-change-listener
                                     #(.locationProperty (.getEngine ^WebView %)))
                                    lifecycle/change-listener)}))
;; A well known URL

(def *state (atom {::partial-url ""
                   ::current-url ""
                   ::title "a browser"}))

(defn top-pane [{:keys [state]}]
  {:fx/type :h-box
   :spacing 5
   :children [{:fx/type :text-field
               :h-box/hgrow :always
               :text (::partial-url state)
               :on-text-changed {:event/type ::url-typed}
               :on-action {:event/type ::url-submitted}}]})

;; The web-pane function returns the extended web-view that has the additional property :on-location-changed installed.
(defn web-pane [{:keys [state]}]
  {:fx/type web-view-with-ext-props
   :desc {:fx/type :web-view
          :pref-height 1000
          :pref-width 1500
          :url (::current-url state)}
   :props {:on-location-changed {:event/type ::url-changed}}})

(defn body-pane [{:keys [state]}]
  {:fx/type :v-box
   :padding 10
   :spacing 10
   :children [{:fx/type top-pane
               :state state}
              {:fx/type web-pane
               :state state}]})

(defn root [state]
  {:fx/type :stage
   :showing true
   :title (::title state)
   :scene {:fx/type :scene
           :root {:fx/type body-pane :state state}}})

(defn normalise [s]
  (cond
    (str/starts-with? s "http") s
    (str/starts-with? s "file:") s
    :else (str "http://" s)))

(defn uri->title [s]
  (let [{:keys [host rawPath]} (java.net.URI. s)]
    (format "%s/%s" host rawPath)))


(defn event-handler [{:keys [fx/event event/type]}]
  (case type
    ::url-typed (swap! *state assoc ::partial-url event)
    ::url-submitted (swap! *state #(assoc % ::current-url (-> % ::partial-url normalise)))
    ::url-changed (swap! *state assoc ::current-url event ::partial-url event)))

(def renderer
  (fx/create-renderer
   :middleware (fx/wrap-map-desc assoc :fx/type root)
   :opts {:fx.opt/map-event-handler event-handler}))

(def ui-state (atom nil))

(defn browse [url & {:keys [title] :or {title url}}]
  (fx/on-fx-thread
   (reset! *state {::current-url url
                   ::partial-url url
                   ::title (or title  (uri->title (normalise url)))})
   (swap! ui-state (fn [instance]
                     (if instance
                       instance
                       (fx/mount-renderer *state renderer))))))
