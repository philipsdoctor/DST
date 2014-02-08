(ns dst.core-test
  (:require [clojure.test :refer :all]
            [dst.core :refer [generate-template parser]]
            [instaparse.core :as insta]))

(deftest parser-grammar-works
  (testing "only text blob"
    (is (= '([:textblob "Test text!"]) (parser "Test text!"))))
  (testing "var-blob"
    (is (= '([:inner-template-var "greeting"] [:textblob " guy."]) (parser "${greeting} guy."))))
  (testing "blob-var"
    (is (= '([:textblob "Hello "] [:inner-template-var "name"]) (parser "Hello ${name}"))))
  (testing "just var"
    (is (= '([:inner-template-var "x"]) (parser "${x}"))))
  (testing "nested var is a strange degenerate case"
    (is (= '([:inner-template-var "${test"] [:textblob "}"]) (parser "${${test}}"))))
  (testing "escape char"
    ;TODO
    ;(is (= '([:textblob "${test}"]) (parser "$${test}")))
    )
  (testing "malformed inputs"
    (is (insta/failure? (parser "Test text! ${"))) ))

(deftest generates-template
  (testing "generates a simple template with one map value"
    (let [my-template (generate-template "Hello ${name}")]
      (is (= (my-template {:name "Phil"}) "Hello Phil")))))
