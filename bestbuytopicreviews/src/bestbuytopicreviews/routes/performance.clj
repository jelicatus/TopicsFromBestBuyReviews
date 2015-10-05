(ns bestbuytopicreviews.routes.performance
  (:require [criterium.core :as criterium]
            [clojure.string :as string]
            [bestbuytopicreviews.routes.products :as products]
            [bestbuytopicreviews.routes.reviews :as reviews]
            [bestbuytopicreviews.routes.tagcloud :as tag-cloud]))


;products

;(criterium/with-progress-reporting (criterium/bench (products/searchforproducts "iphone") :verbose))

;reviews
;(criterium/with-progress-reporting (criterium/bench (reviews/tf-idf 1 3 1) :verbose))

;(def forbidden-words (string/split (slurp "resources/stopwords.txt") #", "))
;(criterium/with-progress-reporting (criterium/bench (reviews/remove-words-from-sentence "Extra smooth, ladies love the silver ring. Used to have a 5 and the ladies would turn me away. With this 3D force touch ladies come running like I'm using Jedi mind tricks. If you want your son to be the #1 boy at school pick him up one of these bad boys." forbidden-words) :verbose))

;(criterium/with-progress-reporting (criterium/bench (reviews/get-reviews-body 7618003) :verbose))

;can not be called - limit of calls per second is 5 and this takes more than 1h
;(criterium/with-progress-reporting (criterium/bench (reviews/get-reviews 7457059) :verbose))
