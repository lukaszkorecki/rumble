(ns r.portal)

(defn v [type thing]
  (with-meta
    thing
    {:portal.viewer/default type}))

(defn table [thing]
  (v :portal.viewer/table thing))

(defn hiccup [thing]
  (v :portal.viewer/hiccup thing))

(defn test-report [thing]
  (v :portal.viewer/test-report thing))

(defn diff [thing]
  (v :portal.viewer/diff thing))
