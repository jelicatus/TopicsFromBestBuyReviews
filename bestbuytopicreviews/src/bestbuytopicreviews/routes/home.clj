(ns bestbuytopicreviews.routes.home
  (:require [bestbuytopicreviews.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [clojure.java.io :as io]
            [bestbuytopicreviews.routes.utils :as util]))

(defn index-page
  ([] (layout/render "homepage.html"))
  ([product] (layout/render "homepage.html" {:results (util/searchforproducts product)})))

(defroutes home-routes
  (GET "/" [] (index-page))
  (POST "/searchforproducts" [product] (index-page product))
  ;should put request as parameter, will do after filtering products with 100+ reviews
  (POST "/findouttopics" [request] (util/get-reviews 7619002)))

;za definisanje ruta i hendleri za requestove (post get)
