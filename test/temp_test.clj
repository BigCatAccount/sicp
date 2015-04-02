(ns temp-test
  (:use temp
        midje.sweet))

(facts "my-sum"
  (my-sum []) => 0
  (my-sum [1 2 3]) => 6)