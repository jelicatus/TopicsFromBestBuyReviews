(ns bestbuytopicreviews.routes.tagcloud
  (:require [hiccup.core :as hc]))

;idea from http://briancarper.net/blog/426/
(defn tag-cloud [tag-vector]
  (let [counts (map last tag-vector)
        max-count (apply max counts)
        min-count  (apply min counts)
        min-size 90.0
        max-size 200.0
        color-fn (fn [v]
                   (let [b (min (- 255 (Math/round (* v 255))) 200)]
                    (str "rgb(" b "," b "," b ")")))
        tag-fn (fn [[tag c]]
                 (let [b (rand-int 100)
                       weight (/ (- (Math/log c) (Math/log min-count))
                                 (- (Math/log max-count) (Math/log min-count)))
                       size (+ min-size (Math/round (* weight
                                                       (- max-size min-size))))
                       color (color-fn (* weight 1.0))]
                   [:a {;:href (:url tag)
                        :style (str "font-size: " size "%;"  " position: relative;" " top: " (+ b c)"px; "  " left: -" (+ b c) "px;"  "color:" color)}
                    (str tag)]))]
    (hc/html [:div.tag-cloud
              {:style (str "margin-left: 15%")}
              (hc/html (map tag-fn tag-vector))])))
