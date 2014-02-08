(ns dst.core
  (:require [instaparse.core :as insta]
            [clojure.set]))

(def grammar "<full-template> = ( escaped-blob | textblob | template-object ) + 
              escaped-blob = <'$'> '${' !'${' #'[^$}]*' '}'
              <template-object> = <'${'> inner-template-var <'}'>
              inner-template-var = #'[^}]*'
              textblob = !'${' #'[^$]*'
              fallback = #'.*'")

(def parser (insta/parser grammar))

(defn rewrite [tree] 
  (insta/transform {:escaped-blob (fn [_ txt _] txt)
                    :textblob (fn [data-val] data-val)
                    :inner-template-var (fn [data-val] (keyword data-val))} tree))

(defmacro generate-template 
  [template]
  (let [parsed-template (parser template)
        to-string-vector (rewrite parsed-template)
        vec-keywords (filter keyword? to-string-vector)
        rep-map (into {} (for [kw vec-keywords] [kw (gensym)]))
        as-map-name (gensym)
        destruct-bindings [(assoc (clojure.set/map-invert rep-map) :as as-map-name)]]
    `(fn ~destruct-bindings
         (let [as-map-things# ((comp set keys) ~as-map-name)
               provided-things# ~((comp set keys) rep-map)]
         (when (not= as-map-things# provided-things#)
           (throw (IllegalArgumentException. (str "Missing required keys for template " as-map-things# " " provided-things#)))))
         (str ~@(clojure.walk/postwalk-replace rep-map to-string-vector)))))

