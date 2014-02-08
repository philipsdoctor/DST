(ns dst.core
  (:require [instaparse.core :as insta]
            [clojure.set]))

(def grammar "<full-template> = ( textblob | template-object ) + 
              <template-object> = <'${'> inner-template-var <'}'>
              inner-template-var = #'[^}]*'
              textblob = !'${' #'[^$]*' 
              fallback = #'.*'")

(def parser (insta/parser grammar))

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
           (throw (Exception. (str "Missing required keys for template " as-map-things# " " provided-things#)))))
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

