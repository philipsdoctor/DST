(ns dst.core
  (:require [instaparse.core :as insta]
            [clojure.set]))

(def grammar "<full-template> = ( escaped-template-object | textblob | template-object ) + 
              <escaped-template-object> = <'$'> '${' escaped-blob '}'
              <template-object> = <'${'> inner-template-var <'}'>
              inner-template-var = #'[^}]*'
              textblob = !'${' #'[^$]*'
              escaped-blob = !'${' #'[^$}]*' 
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
        to-string-vector (map rewrite parsed-template)
        vec-keywords (filter keyword? to-string-vector)
        rep-map (into {} (for [kw vec-keywords] [kw (gensym)]))
        bindings (vec (vals rep-map))
        as-map-name (gensym)
        destruct-bindings [(assoc (clojure.set/map-invert rep-map) :as as-map-name)]]
    `(fn ~destruct-bindings
         (let [as-map-things# ((comp set keys) ~as-map-name)
               provided-things# ~((comp set keys) rep-map)]
         (when (not= as-map-things# provided-things#)
           (throw (Exception. (str "Missing required keys for template " as-map-things# " " provided-things#)))))
         (str ~@(clojure.walk/postwalk-replace rep-map to-string-vector)))))

