(defproject startingproject "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
               ;  [ring "1.0.1"] ;definsemo zavisnost, prvo za server
                 [compojure "1.0.1"] ;za rute
                 [enlive "1.0.0"] ;za formatiranje html-a
                 [org.clojure/data.json "0.2.6"] ;za parsiranje json-a
                 [clj-http "1.1.0"] ;za skidanje jsona
                 [local/jml "2.8"]
                 [local/LIBLINEAR "1.0"]
                 [local/commons-math "2.2"]
                 [local/jmathplot "1.0"]
                 [http-kit "2.1.16"]]
   :repositories {"startingproject" "file:repo"}
  )