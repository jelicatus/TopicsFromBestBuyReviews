(ns startingproject.app 
  (use (compojure handler [core :only (GET POST defroutes)])) ;handler za ovo je defroutes 
  (require ;[ring.adapter.jetty :as jetty] ;jetty je server
           [org.httpkit.server :as httpserver]
           [ring.util.response :as response] ;util je za response
           [net.cgrand.enlive-html :as en] ;za formatiranje - pritupanje html elemntima, aributima
           [compojure.route] ;za definisanje ruta
           [clojure.data.json :as json] ;za parsiranje json-a
           [clj-http.client :as client] ;za skidanje jsona
           [clojure.string :as string])
;  (import jml.clustering.NMF)
  )

;(httpserver/run-server #'app {:port 8080 :ip "localhost" :join? false})


(def products (atom ""))


;(en/deftemplate homepage
;  (en/xml-resource "index.html")
;  [request]
;  [:#listing :li]
;  (en/clone-for [[id url] @mapasajtova] [:a] (en/content (str id  " " url)) 
;                                        [:a] (en/set-attr :href (str \/ id))
;  ))

(en/deftemplate probanje 
  (en/xml-resource "index.html")
  [request]
;  (response/redirect "http://api.remix.bestbuy.com/v1/reviews(sku=1780275)?format=json&apiKey=5qxg5sxjbbxa9maxpfrqvqjw&show=comment")
  )

(en/deftemplate fill-products 
  (en/xml-resource "index.html")
  [productss]
  [:#products] 
  (en/clone-for[product  productss] 
     [:h2.name] (en/content (str (product :name)))
     [:img.product-image] (en/set-attr :src (str (product :image)))
     [:p.manufacturer] (en/content (str (product :manufacturer)))
     [:p.regularPrice] (en/content (str "regularPrice " (product :regularPrice) " "))
     [:p.onSale] (en/content (str "onSale " (product :onSale) " "))
     [:p.percentSavings] (en/content (str "percentSavingse " (product :percentSavings) " "))
     [:p.salePrice] (en/content (str "salePrice " (product :salePrice) " "))
     [:p.long-description] (en/content (str (product :longDescription) " "))                        
     [:a.url] (en/set-attr :href (str (product :url)))
     [:input.sku] (en/set-attr :value (str (product :sku)))
     
     )
 )
  


;(defn napunimapu
 ; [url]
  ;(let [id (swap! counter inc) id (java.lang.Long/toString id 36)]
  ;(swap! mapasajtova assoc id url)
  ;)  
  ;)

(defn searchforproducts [product]
   (reset! products
    {:data   (((client/get (str "http://api.remix.bestbuy.com/v1/products(name=" product "*&categoryPath.name=%22Cell%20Phones%22)?show=name,sku,url,image,manufacturer,regularPrice,onSale,percentSavings,salePrice,longDescription&pageSize=30&page=30&apiKey=5qxg5sxjbbxa9maxpfrqvqjw&format=json")
           {:form-params "body"
            :content-type :json
            :as :json}
            ) :body) :products) } )
    ;     ) 
;@products   
 )



(defn malaproba [request] (probanje request))

;server se startuje na sledeci nacin:
;(jetty/run-jetty #'app {:port 8080 :join? false})





;(client/put "http://api.remix.bestbuy.com/v1/reviews(sku=1780275)?format=json&apiKey=5qxg5sxjbbxa9maxpfrqvqjw&show=comment"
;  {:form-params "body"
;   :content-type :json
;   :throw-exceptions false
;   :as :json})


(((client/get "https://api.remix.bestbuy.com/v1/products((categoryPath.id=pcmcat241600050001))?apiKey=5qxg5sxjbbxa9maxpfrqvqjw&sort=regularPrice.asc&show=sku,color,customerReviewAverage,customerReviewCount,description,details.name,details.value,image,inStoreAvailability,inStoreAvailabilityText,longDescription,manufacturer,mobileUrl,name,onSale,percentSavings,regularPrice,salePrice,shortDescription,thumbnailImage,url&pageSize=10&format=json"
           {:form-params "body"
            :content-type :json
            :as :json}
            ) :body) :products) ;getting the products

(((client/get "http://api.remix.bestbuy.com/v1/products(name=Samsung%20Galaxy*&categoryPath.name=%22Cell%20Phones%22)?show=name,image,subclass,department,class,categoryPath.id,categoryPath.name,manufacturer,regularPrice,onSale,percentSavings,salePrice,longDescription&pageSize=30&page=30&apiKey=5qxg5sxjbbxa9maxpfrqvqjw&format=json"
           {:form-params "body"
            :content-type :json
            :as :json}
            ) :body) :products) ;drugi nacin

(def forbidden-words [":)" "the" "a" "an" "to" "that" "was" "is" "will" "on" "u" "you" "this"  
                            "can" "could" "my" "his" "has" "from" "each" "of" "one" "our" "we"
                            "he" "and" "for" "us" "her" "\"FaceTime\"" "it" "but" "when" "its" "cery" "don't"
                             "with" "it" "she" "i'm" "in" "if" "i" "no" "yet" "at"]) ;define more words later

(def punctuation-marks ["." "!" ":" "," "/"])

(defn tf-idf [tf_ij N df_i] (* tf_ij (java.lang.Math/log (/ N df_i)))) ;function for getting w_ij 

(def nmfopt (jml.options.NMFOptions.))
(set! (.epsilon nmfopt) (- java.lang.Math/E 5) )
(set! (.calc_OV nmfopt) false)
(set! (.verbose nmfopt) true)
(set! (.maxIter nmfopt) 50)
(set! (.nClus nmfopt) 2)

(def nmfclustering (jml.clustering.NMF. nmfopt))

(defn feed-data [nmfclustering data] (. nmfclustering (feedData data)))
(defn set-clustering [nmfclustering param] (. nmfclustering (clustering param)))


(defn remove-words-from-sentence
  [sentence words]
  (let [pattern (->> (for [w words] (str "\\b\\Q" w "\\E\\b"))
                     (string/join "|")
                     (format "(%s)\\s*"))]
    (.trim (.replaceAll sentence pattern "")))) ;function for removing forbidden words


(defn get-reviews [sku] 
  (let [reviews-body (((client/get (str "http://api.remix.bestbuy.com/v1/reviews(sku=" sku ")?format=json&apiKey=5qxg5sxjbbxa9maxpfrqvqjw&show=comment")
        {:form-params "body"
         :content-type :json
         :as :json} :headers) :body) :reviews),
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
        step1NMF (feed-data nmfclustering data),
        step2NMF (set-clustering nmfclustering nil),
        matrix  (. nmfclustering (getIndicatorMatrix)),
        t-matrix (apply mapv vector (mapv #(vec (.getRow matrix %)) (range (.getRowDimension matrix)))),
        topics-with-all-tokens (map #(zipmap tokens %) t-matrix ),
        topics-with-sorted-weight-of-tokens (for [i (range (count topics-with-all-tokens))](sort-by val > (nth topics-with-all-tokens i)))        
        ] 
     (def words-for-topics (map #(take 15 %) topics-with-sorted-weight-of-tokens)) ))


(defn findouttopics [sku]
  (get-reviews sku)  
  )


;defnisanje ruta
(defroutes app*
  ;(GET "/" request (homepage request))
  (GET "/" request (probanje request))
  (GET "/proba" request (probanje request))
  (POST "/searchforproducts" [product]       
        (searchforproducts  product)
           (fill-products (into () (:data @products)))
       )
  (POST "/findouttopics" request (findouttopics ((request :params) :sku))
       )
;  (POST "/napunimapu" request (napunimapu (-> request :params :url)) (response/redirect "/") )
  )


(def app (compojure.handler/site app*)) ;middleware xD


(httpserver/run-server #'app {:port 8080 :ip "localhost" :join? false})
