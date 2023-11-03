(ns r.kaocha-hooks
  (:require
   [lambdaisland.deep-diff2 :as ddiff]
   r.portal
   kaocha.report))

(def results (atom []))

(defn unpack-failure [testable]
  (let [{:keys [actual expected :kaocha/testable]} testable]
    :x))

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

(def reporter
  [report* kaocha.report/result])
