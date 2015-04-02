(ns temp
  (:use temp
        midje.sweet))

(facts "my-sum"
  (my-sum []) => -1
  (my-sum [1 2 3]) => 6)