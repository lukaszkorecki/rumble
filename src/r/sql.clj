(ns r.sql
  (:require
   [r.portal :as r.p]
   [clojure.string :as str])
  (:import
   (com.github.vertical_blank.sqlformatter SqlFormatter)))

(def formatter ^SqlFormatter (SqlFormatter/of "postgresql"))

(defn format-sql [sql-str]
  (.format ^SqlFormatter formatter ^String sql-str))

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

(defn debug-query->portal
  [{:keys [sql-vec result]}]
  (tap> (query-view sql-vec))
  (tap> (r.p/table result))
  sql-vec)

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
                      (subs (first sql-vec) position))
                     (first sql-vec))
        sql-vec (concat [sql-string] (rest sql-vec))]
    (tap> {:error e
           :details (r.p/hiccup [:div
                                 [:h1 "Query exception"]
                                 [:div
                                  [:h2 "Error"]
                                  [:portal.viewer/exception msg]]
                                 (query-view sql-vec)])})))

(defn with-debug [execute-fn conn sql-vec]
  (try
    (let [result (execute-fn conn sql-vec)]
      (debug-query->portal {:sql-vec sql-vec :result result})
      result)
    (catch Exception e
      (query-execution-error->portal e sql-vec)
      (throw e))))

(comment
  (->> {:select [:*] :from :table}
       (sql/format)
       (r.sql/with-debug next.jdbc/execute! db)))
