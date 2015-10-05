(ns bestbuytopicreviews.routes.products
  (:require [clj-http.client :as client]))

(defn searchforproducts [product]
  (((client/get (str "http://api.remix.bestbuy.com/v1/products(longDescription=" product "*&customerReviewCount>300)?show=name,sku,url,image,manufacturer,regularPrice,onSale,percentSavings,salePrice,longDescription&pageSize=30&page=1&apiKey=5qxg5sxjbbxa9maxpfrqvqjw&format=json")
                                 {:form-params "body"
                                  :content-type :json
                                  :as :json}) :body) :products))
