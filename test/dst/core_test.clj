(ns dst.core-test
  (:require [clojure.test :refer :all]
            [dst.core :refer [generate-template parser log-error]]
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
    ; this transformation will be improved in a later step
    (is (= '([:escaped-blob "${" "test" "}"]) (parser "$${test}"))))
  (testing "malformed inputs"
    (is (= '([:textblob "Test text! "] [:fallback "${"]) (parser "Test text! ${")))
    (is (= '([:textblob "Test text! "] [:fallback "$"]) (parser "Test text! $")))
    ; (is (thrown? IllegalArgumentException (parser "Test text! ${}"))) ; validated in template macro
    ))

(deftest generates-template
  (testing "generates a simple template with one map value"
    (let [my-template (generate-template {:template "Hello ${name}"})]
      (is (= (my-template {:name "Phil"}) "Hello Phil"))))
  (testing "generates a template with no values"
    (let [my-template (generate-template {:template "Hello!"})]
      (is (= (my-template {}) "Hello!"))))
  (testing "generates a template with an escaped symbol"
    (let [my-template (generate-template {:template "Hello $${name} ${name2}"})]
      (is (= (my-template {:name2 "Phil"}) "Hello ${name} Phil"))))
  ; throws at compile, TODO figure out how to test that
  ;(testing "Validates missing symbols"
  ;  (is (thrown? IllegalArgumentException (generate-template "Hello ${}"))))
  )

(deftest validates-inputs
  (testing "If the template specifies a key that is missing from the map then an error is thrown"
    (let [my-template (generate-template {:template "Hello ${name}"})]
      (is (thrown? IllegalArgumentException (my-template {}))))))

(deftest custom-error-handling
  (testing "Can specify a custom handler"
    (let [my-template (generate-template {:template "Hello ${name}" :error-handler log-error})]
      (is (= (with-out-str (my-template {})) "\"Missing required keys for template #{} #{:name}\"\n"))))
  (testing "Defaults to throw"
    (let [my-template (generate-template {:template "Hello ${name}"})]
      (is (thrown? IllegalArgumentException (my-template {}))))))
