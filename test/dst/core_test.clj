(ns dst.core-test
  (:require [clojure.test :refer :all]
            [dst.core :refer [generate-template]]))

(deftest generates-template
  (testing "generates a simple template with one map value"
    (let [my-template (generate-template "Hello ${name}")]
      (is (= (my-template {:name "Phil"}) "Hello Phil")))))
