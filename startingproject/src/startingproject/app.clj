(ns startingproject.app 
  (use (compojure handler [core :only (GET POST defroutes)])) ;handler za ovo je defroutes 
  (require [ring.adapter.jetty :as jetty] ;jetty je server
           [ring.util.response :as response] ;util je za response
           [net.cgrand.enlive-html :as en] ;za formatiranje - pritupanje html elemntima, aributima
           [compojure.route] ;za definisanje ruta
           [clojure.data.json :as json] ;za parsiranje json-a
           [clj-http.client :as client] ;za skidanje jsona
           [clojure.string :as string])
;  (import jml.clustering.NMF)
  )


(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))


(def a 10)

(def counter (atom 10000))

(def mapasajtova (atom {}))

(en/deftemplate homepage
  (en/xml-resource "sajtovi.html")
  [request]
  [:#listing :li]
  (en/clone-for [[id url] @mapasajtova] [:a] (en/content (str id  " " url)) 
                                        [:a] (en/set-attr :href (str \/ id))
  ))

(defn probanje
  [request]
  (response/redirect "http://api.remix.bestbuy.com/v1/reviews(sku=1780275)?format=json&apiKey=5qxg5sxjbbxa9maxpfrqvqjw&show=comment")
  )

(defn redirect 
  [id]
  (response/redirect (@mapasajtova id)) ;@ posto je atom
  )

(defn napunimapu
  [url]
  (let [id (swap! counter inc) id (java.lang.Long/toString id 36)]
  (swap! mapasajtova assoc id url)
  )  
  )

;server se startuje na sledeci nacin:
;(jetty/run-jetty #'app {:port 8080 :join? false})

;defnisanje ruta
(defroutes app*
  (GET "/" request (homepage request))
  (GET "/proba" request (probanje request))
  (GET "/:id" [id] (redirect id))
  (POST "/napunimapu" request (napunimapu (-> request :params :url)) (response/redirect "/") )
  )

(client/put "http://api.remix.bestbuy.com/v1/reviews(sku=1780275)?format=json&apiKey=5qxg5sxjbbxa9maxpfrqvqjw&show=comment"
  {:form-params "body"
   :content-type :json
   :throw-exceptions false
   :as :json})

(def app (compojure.handler/site app*)) ;middleware xD

; ((json/read-str dzejson :key-fn keyword) :a) --->prvo napravi mapu od dzejsona pa onda se normalno pristupa elementima mape :D

; (((client/get "http://api.remix.bestbuy.com/v1/reviews(sku=1780275)?format=json&apiKey=5qxg5sxjbbxa9maxpfrqvqjw&show=comment"
;     {:form-params "body"
;      :content-type :json
;      :as :json} :headers) :body) :reviews) ---->dobijanje review-ova


;******************************************************************

;(def reviews-body (((client/get "http://api.remix.bestbuy.com/v1/reviews(sku=1780275)?format=json&apiKey=5qxg5sxjbbxa9maxpfrqvqjw&show=comment"
;     {:form-params "body"
;      :content-type :json
;      :as :json} :headers) :body) :reviews))

;(def sequence-of-reviews (map string/lower-case (for [i (range(count reviews-body))] ((reviews-body i) :comment))))

(let [reviews-body (((client/get "http://api.remix.bestbuy.com/v1/reviews(sku=1780275)?format=json&apiKey=5qxg5sxjbbxa9maxpfrqvqjw&show=comment"
        {:form-params "body"
         :content-type :json
         :as :json} :headers) :body) :reviews)] 
     (def sequence-of-reviews (map string/lower-case (for [i (range(count reviews-body))] ((reviews-body i) :comment)))) )

(def punctuation-marks ["." "!" ":" ","]) ;maybe this doesn't need to be kept

(def forbidden-words [":)" "the" "a" "an" "to" "that" "was" "is" "will" "on" "u" "you" "this"  
                            "can" "could" "my" "his" "has" "from" "each" "of" "one" "our" "we"
                            "he" "and" "for" "us" "her" "\"FaceTime\"" "it" "but" "when" "its" "cery" "don't"
                             "with" "It" "She" "I'm" "in" "if" "I" "no"]) ;define more words later

(defn remove-words-from-sentence
  [sentence words]
  (let [pattern (->> (for [w words] (str "\\b\\Q" w "\\E\\b"))
                     (string/join "|")
                     (format "(%s)\\s*"))]
    (.trim (.replaceAll sentence pattern "")))) ;function for removing forbidden words

(def pre-pre-tokens (for [j (range (count sequence-of-reviews))] 
                      (remove-words-from-sentence (nth sequence-of-reviews j) forbidden-words))) ;removing forbidden words

(def pre-tokens (for [s pre-pre-tokens] 
        (-> s ((apply comp 
                 (for [s punctuation-marks] #(.replace %1 s ""))))))) ;removing punctuation marks

(def tokens (distinct (flatten (for [ i (range (count pre-tokens))] 
                                 (string/split (nth pre-tokens i) #" "))))) ;getting sequence of tokens

(def tokenized-reviews (distinct (for [ i (range (count pre-tokens))] 
                                    (string/split (nth pre-tokens i) #" ")))) 

(def w-matrix (for [i (range (count pre-tokens) )] 
                  (for [j (range (count tokens))] (if (.contains (nth pre-tokens i) (nth tokens j)) 1 0)))) ;defining the W matrix

(def w-matrix-alternative (for [i (range (count tokenized-reviews) )] 
                  (for [j (range (count tokens))] (if (= (nth tokenized-reviews i) (nth tokens j)) 1 0)))) 

(def sequence-of-frequencies (apply  map + w-matrix)) ;defining matrix of frequencies

(def proba (for [i (range (count tokenized-reviews) )] 
                     (for [j (range (count tokens))] 
      					 (if (.contains (nth tokenized-reviews i) (nth tokens j)) 1 0)
                                                                                 ))) ;--> DOBROOO








