(ns template-eng.core
  (:require [instaparse.core :as insta])
  (:import [java.util.regex Pattern]))

(def grammar "<full-template> = ( textblob | template-object ) + 
              <template-object> = <'${'> inner-template-var <'}'>
              inner-template-var = #'[^}]*'
              textblob = !'${' #'[^$]*' 
              fallback = #'.*'")

(def parser (insta/parser grammar))

(def x "Hello ${name}")

; from clojure contrib....
(defn safe-get 
    "Like get, but throws an exception if the key is not found."
    [map key] 
    (lazy-get map key 
      (throw  (IllegalArgumentException.  (format "Key %s not found in %s" key map)))))

(defn rewrite  
  [[data-type data-val]]  
  (cond  
    (= data-type :textblob) data-val  
    (= data-type :inner-template-var) (keyword data-val))) 
 
(defmacro generate-template 
  [template]
  (let [parsed-template (parser template)
        tt (map rewrite parsed-template)
        ta (filter keyword? tt)
        rep-map (into {} (for [kw ta] [kw (gensym)]))
        bindings (vec (vals rep-map))
        as-map-name (gensym)
        destruct-bindings [(assoc (clojure.set/map-invert rep-map) :as as-map-name)]]
    `(fn ~destruct-bindings
         (let [as-map-things# ((comp set keys) ~as-map-name)
               provided-things# ~((comp set keys) rep-map)]
         (when (not= as-map-things# provided-things#)
           (throw (Exception. (str "Yolo! " as-map-things# " " provided-things#)))))
         (str ~@(clojure.walk/postwalk-replace rep-map tt)))))

 
;(defmacro generate-template 
;  [template]
;  (let [parsed-template (parser template)
;        tt (map rewrite parsed-template)
;        ta (filter keyword? tt)
;        rep-map (into {} (for [kw ta] [kw (gensym)]))
;        bindings (vec (vals rep-map))
;        destruct-bindings [(clojure.set/map-invert rep-map)]]
;    `(fn ~destruct-bindings
 ;        (str ~@(clojure.walk/postwalk-replace rep-map tt)))))



;(defmacro generate-template 
;  [template]
;  (let [parsed-template (parser template)
;        tt (map rewrite parsed-template)
;        ta (filter keyword? tt)
;        rep-map (into {} (for [kw ta] [kw (gensym)]))
;        bindings (vec (vals rep-map))]]
;    `(fn ~bindings
;         (str ~@(clojure.walk/postwalk-replace rep-map tt)))))

 
 
;(defmacro generate-template 
;  [template]
;  (let [parsed-template (parser template)
;        tt (map rewrite parsed-template)]
;    `(fn [~'name] 
;       (str ~@tt))
;    )
;)


;(println (str (map rewrite (parser x))))

(defn sample-output [{name :name :as example}]
  (let [template ["Hello " name]]
  (clojure.string/join template)))


