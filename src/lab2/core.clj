(ns lab2.core
  (require [clojure.java.io :as io]
           [org.httpkit.client :as http]
           )
  (:gen-class)
  )

(defn readFile
  [fileName]
  (with-open [inFile (io/reader fileName)]
      (doall (line-seq inFile))
    )
  )

(defn getHrefs
  [page]
  (re-seq #"(?i)<a[^>]+href=\"([^\"]+)\"[^>]*>" page)
  )

(defn crawl
  [urls depth maxDepth]
  (doall(map 
    #(let [{:keys [status headers body error] :as resp} @(http/get % {:follow-redirects false})]
      (print %)
      (print " ")
      (if error
        (println "bad")
        (if (= status 301) (println "redir")
          (if (= status 301) (println "redir")
            (if (= status 307) (println "redir")
              (let [hrefs (getHrefs (.toString body))]
                (println (count hrefs))
                (if (> maxDepth depth) (crawl (map last hrefs) (+ 1 depth) maxDepth))
          ))))))
    urls))
  )

(defn -main
  [fileWithURLs depth]
  (let [depth (Integer/parseInt depth)]
    (crawl (readFile fileWithURLs) 0 (- depth 1))
    )
  )
