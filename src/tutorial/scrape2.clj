(ns tutorial.scrape2
  (:require [net.cgrand.enlive-html :as html]))

(def ^:dynamic *base-url* "https://news.ycombinator.com/")
(def ^:dynamic *user-agent*
  "Mozilla/5.0 (fake User-Agent)")

(defn fetch-url [url]
  (with-open [inputstream (-> (java.net.URL. url)
                              .openConnection
                              (doto (.setRequestProperty "User-Agent"
                                                         *user-agent*))
                              .getContent)]
    (html/html-resource inputstream)))

(defn hn-headlines-and-points []
  (map html/text
       (html/select (fetch-url *base-url*)
                    #{[:td.title :a] [:td.subtext html/first-child]})))

(defn print-headlines-and-points []
  (doseq [line (map (fn [[h s]] (str h " (" s ")"))
                    (partition 2 (hn-headlines-and-points)))]
    (println line)))
