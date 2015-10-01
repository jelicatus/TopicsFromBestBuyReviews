(ns bestbuytopicreviews.routes.reviews
  (use 'criterium.core)
  (:require [clojure.string :as string]
            [clj-http.client :as client]
            [bestbuytopicreviews.routes.nmf :as nmf-util]
            [bestbuytopicreviews.routes.tagcloud :as tag-cloud-util]))

;function for Term-Frequency word weighting scheme
;calculated according to http://archimedes.fas.harvard.edu/presentations/2002-03-09/img13.html
(defn tf-idf [tf_ij N df_i]
  (* tf_ij (java.lang.Math/log (/ N df_i))))

;function for removing forbidden words
(defn remove-words-from-sentence [sentence words]
  (let [pattern (->> (for [w words] (str "\\b\\Q" w "\\E\\b"))
                  (string/join "|")
                  (format "(%s)\\s*"))]
    (.trim (.replaceAll sentence pattern ""))))

(defn take-total-number-of-pages [sku]
  (((client/get (str "http://api.remix.bestbuy.com/v1/reviews(sku=" sku ")?format=json&apiKey=5qxg5sxjbbxa9maxpfrqvqjw&show=comment&pageSize=100&page=1")
                                    {:form-params "body"
                                     :content-type :json
                                     :as :json} :headers) :body) :totalPages))

(defn request-reviews [sku page-no]
  (((client/get (str "http://api.remix.bestbuy.com/v1/reviews(sku=" sku ")?format=json&apiKey=5qxg5sxjbbxa9maxpfrqvqjw&show=comment&pageSize=100&page=" page-no)
                                    {:form-params "body"
                                     :content-type :json
                                     :as :json} :headers) :body) :reviews))

(defn collect-all-reviews [total-page-number sku]
  (let [reviews []]
        (for [i (range 1 total-page-number)] (conj reviews (request-reviews sku i)))))

;reviews-body - gets the reviews attribute of returned json object using BestBuy API
;sequence-of-reviews - returns a sequence whose elements are reviews
;pre-pre-tokens - takes out the forbidden words from reviews
;pre-tokens - takes out the punctuation marks from reviews
;tokens - returns a sequence that contains tokens
;tokenized-reviews - returns a sequence where element i contains sequence of tokens that appear in review i
;frequencies-of-words-reviews - returns a map where element i is a map that represent review i; keys of review i are tokens that appear in review i and values are frequencies of those tokens in review i
;frequencies-matrix - matrix where element ij represents occurences of token j in review i
;sequence-of-frequencies - sequence where element i represents the number of reviews that contains token i
;number-of-reviews
;w-matrix - matrix we get after performing tf-idf
;data - turning w-matrix into array of doubles
;t-matrix - making indicator matrics usable for performing further clojure functions
;topics-with-all-tokens - returns a sequence containg a map where element i represents topic i; topics i is also a map whose keys are all tokens and values weights of those tokens
;topics-with-sorted-weight-of-tokens - returns a sequence with sorted weight of tokens for each topic
;words-for-topics - returns a sequence with 15 most relevant words for each topic
(defn get-reviews [sku]
  (let [total-page-number (take-total-number-of-pages sku)
         reviews-body (flatten (collect-all-reviews total-page-number sku)),
         forbidden-words (string/split (slurp "resources/stopwords.txt") #", "),
         punctuation-marks (string/split (slurp "resources/punctuationmarks.txt") #" "),
         sequence-of-reviews (map string/lower-case (map :comment reviews-body)),
         pre-pre-tokens (map #(remove-words-from-sentence % forbidden-words) sequence-of-reviews),
         pre-tokens (for [s pre-pre-tokens] (-> s ((apply comp (for [s punctuation-marks] #(.replace %1 s "")))))),
         tokens (distinct (flatten (map #(string/split % #" ") pre-tokens))),
         tokenized-reviews (distinct (map #(string/split % #" ") pre-tokens)),
         frequencies-of-words-reviews (map frequencies tokenized-reviews),
         frequencies-matrix (for [i (range (count frequencies-of-words-reviews))] (for [j (range (count tokens))]
                                                                                    (if (= ((nth frequencies-of-words-reviews i) (nth tokens j)) nil) 0 ((nth frequencies-of-words-reviews i) (nth tokens j))))),
         sequence-of-frequencies (apply map + (for [i (range (count frequencies-of-words-reviews))] (for [j (range (count tokens))]
                                                                                                      (if (= ((nth frequencies-of-words-reviews i) (nth tokens j)) nil) 0  1)))),
         number-of-reviews (count sequence-of-reviews),
         w-matrix (for [i (range (count frequencies-matrix))] (for [j (range (count sequence-of-frequencies))]
                                                                (tf-idf (nth (nth frequencies-matrix i) j) number-of-reviews (nth sequence-of-frequencies j)))),
         data (into-array (map double-array w-matrix)),
         t-matrix (nmf-util/perform-nmf data),
         topics-with-all-tokens (map #(zipmap tokens %) t-matrix ),
         topics-with-sorted-weight-of-tokens (for [i (range (count topics-with-all-tokens))](sort-by val > (nth topics-with-all-tokens i))),
         tag-vectors (map #(take 15 %) topics-with-sorted-weight-of-tokens)
         ]
   (map tag-cloud-util/tag-cloud tag-vectors)) )
