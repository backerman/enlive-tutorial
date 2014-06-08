(ns tutorial.scrape1
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

(defn hn-headlines []
  (map html/text (html/select (fetch-url *base-url*) [:td.title :a])))

(defn hn-points []
  (map html/text (html/select (fetch-url *base-url*) [:td.subtext html/first-child])))

(defn print-headlines-and-points []
  (doseq [line (map #(str %1 " (" %2 ")") (hn-headlines) (hn-points))]
    (println line)))
