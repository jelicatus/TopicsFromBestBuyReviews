(ns bestbuytopicreviews.routes.nmf
  (:import [jml.clustering.NMF]))

(defn feed-data [nmfclustering data]
  (. nmfclustering (feedData data)))

(defn set-clustering [nmfclustering param]
  (. nmfclustering (clustering param)))

;step1NMF - insert the data (a.k.a the w-matrix) needed to perform NMF
;step2NMF - we set clustering; If null, KMeans will be used for initialization
;matrix - represents the indicator matrix of NMF, in our case matrix where rows represent the topics
(defn perform-nmf [data]
  (let [nmfopt (jml.options.NMFOptions. 3 true 50),
       nmfclustering (jml.clustering.NMF. nmfopt),
       step1NMF (feed-data nmfclustering data),
       step2NMF (set-clustering nmfclustering nil),
       matrix  (. nmfclustering (getIndicatorMatrix))
        ]
  (apply mapv vector (mapv #(vec (.getRow matrix %)) (range (.getRowDimension matrix))))))
