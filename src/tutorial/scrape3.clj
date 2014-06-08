(ns tutorial.scrape3
  (:require [net.cgrand.enlive-html :as html]
            [clojure.string :as str]))

(def ^:dynamic *base-url* "http://nytimes.com/")
(def ^:dynamic *user-agent*
  "Mozilla/5.0 (fake User-Agent)")

(def ^:dynamic *story-selector*
     [[:div.story
       (html/but :.advertisement)
       (html/but :.autosStory)
       (html/but :.adCreative)]])

(def ^:dynamic *headline-selector*
     #{[html/root :> :h2 :a],
       [html/root :> :h3 :a]
       [html/root :> :h5 :a]})

(def ^:dynamic *byline-selector* [html/root :> :.byline])

(def ^:dynamic *summary-selector* [html/root :> :.summary])

(defn fetch-url [url]
  (with-open [inputstream (-> (java.net.URL. url)
                              .openConnection
                              (doto (.setRequestProperty "User-Agent"
                                                         *user-agent*))
                              .getContent)]
    (html/html-resource inputstream)))

(defn stories []
  (html/select (fetch-url *base-url*) *story-selector*))

(defn extract [node]
  (let [headline (first (html/select [node] *headline-selector*))
        byline   (first (html/select [node] *byline-selector*))
        summary  (first (html/select [node] *summary-selector*))
        result   (map html/text [headline byline summary])]
    (zipmap [:headline :byline :summary] (map #(str/replace % #"\n" "") result))))

(defn empty-story? [node]
  (every? (fn [[k v]] (= v "")) node))

(defn check [story key default]
  (let [v (key story)]
   (if (not= v "") v default)))

(defn print-story [story]
  (println)
  (println (check story :headline "No headline"))
  (println "\t" (check story :byline "No byline"))
  (println "\t" (check story :summary "No summary")))

(defn print-stories []
  (doseq [story (remove empty-story? (map extract (stories)))]
    (print-story story)))

