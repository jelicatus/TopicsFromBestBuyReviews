(ns bestbuytopicreviews.routes.home
  (:require [bestbuytopicreviews.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [clojure.java.io :as io]
            [bestbuytopicreviews.routes.utils :as util]))

(defn home-page []
  (layout/render
    "home.html" {:docs (-> "docs/docs.md" io/resource slurp)}))

(defn index-page []
  (layout/render "homepage.html"))

(defn about [a]
  (layout/render "about.html" {:results (str a)}))

(defroutes home-routes
  (GET "/" [] (index-page))
  (POST "/searchforproducts" [product] (util/searchforproducts product))
  (POST "/findouttopics" [request] (util/get-reviews request))
  (POST "/bls" [a] (about a)))

;za definisanje ruta i hendleri za requestove (post get)
