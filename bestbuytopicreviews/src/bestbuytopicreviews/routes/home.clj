(ns bestbuytopicreviews.routes.home
  (:require [bestbuytopicreviews.layout :as layout]
            [compojure.core :refer [defroutes GET POST]]
            [ring.util.http-response :refer [ok]]
            [clojure.java.io :as io]
            [bestbuytopicreviews.routes.utils :as util]
            [bestbuytopicreviews.routes.products :as products]
            [bestbuytopicreviews.routes.reviews :as reviews]))

(defn index-page
  ([] (layout/render "homepage.html"))
  ;([product] (layout/render "homepage.html" {:results (util/searchforproducts product)}))
  ([product] (layout/render "homepage.html" {:results (products/searchforproducts product)})))

(defn cloud-page [sku]
  ;(layout/render "homepage.html" {:clouds  (util/get-reviews sku)})
  (layout/render "homepage.html" {:clouds  (reviews/get-reviews sku)}))

(defroutes home-routes
  (GET "/" [] (index-page))
  (POST "/searchforproducts" [product] (index-page product))
  ;should put request as parameter, will do after filtering products with 100+ reviews
  (POST "/findouttopics" [sku] (cloud-page sku)))

;za definisanje ruta i hendleri za requestove (post get)
