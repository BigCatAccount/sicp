(ns ch1-test
  (:use ch1
        midje.sweet)
  (:refer-clojure :exclude [next]))

(facts "1.7. sqrt-1-7"
  (sqrt-1-7 1e50) => (roughly 1e25)
  (sqrt-1-7 100) => (roughly 10)
  (sqrt-1-7 25) => (roughly 5)
  (sqrt-1-7 1) => (roughly 1)
  (sqrt-1-7 1e-20) => (roughly 1e-10)
  (sqrt-1-7 0) => (roughly 0))

(facts "1.11. fn-1-11-rec"
  (fn-1-11-rec 0) => 0
  (fn-1-11-rec 1) => 1
  (fn-1-11-rec 3) => 4
  (fn-1-11-rec 7) => 142)

(facts "1.11. fn-1-11-iter"
  (fn-1-11-iter 0) => 0
  (fn-1-11-iter 1) => 1
  (fn-1-11-iter 3) => 4
  (fn-1-11-iter 7) => 142)

(facts "1.16. fast-expt-1-16"
  (fast-expt-1-16 7 0) => 1
  (fast-expt-1-16 7 1) => 7
  (fast-expt-1-16 7 2) => 49
  (fast-expt-1-16 7 3) => 343

  (fast-expt-1-16 0 0) => 1 ; http://mathforum.org/dr.math/faq/faq.0.to.0.power.html
  (fast-expt-1-16 0 3) => 0

  (fast-expt-1-16 1 0) => 1
  (fast-expt-1-16 1 3) => 1)

(facts "1.18. fast-mult-rec"
  (fast-mult-rec 0 0) => 0
  (fast-mult-rec 0 3) => 0

  (fast-mult-rec 1 0) => 0
  (fast-mult-rec 1 1) => 1

  (fast-mult-rec 2 0) => 0
  (fast-mult-rec 2 2) => 4
  (fast-mult-rec 2 3) => 6
  (fast-mult-rec 2 4) => 8
  (fast-mult-rec 2 6) => 12

  (fast-mult-rec 6 2) => 12
  (fast-mult-rec 6 4) => 24

  (fast-mult-rec 28 3) => 84
  (fast-mult-rec 28 4) => 112

  (fast-mult-rec 29 3) => 87
  (fast-mult-rec 29 4) => 116
  (fast-mult-rec 29 29) => 841

  (fast-mult-rec 1567 986) => 1545062
  (fast-mult-rec 1566 987) => 1545642
  (fast-mult-rec 1566 986) => 1544076)

; TODO any way to parametrize facts in midje to refactor duplication?

(facts "1.18. fast-mult-iter"
  (fast-mult-iter 0 0) => 0
  (fast-mult-iter 0 3) => 0

  (fast-mult-iter 1 0) => 0
  (fast-mult-iter 1 1) => 1

  (fast-mult-iter 2 0) => 0
  (fast-mult-iter 2 2) => 4
  (fast-mult-iter 2 3) => 6
  (fast-mult-iter 2 4) => 8
  (fast-mult-iter 2 6) => 12

  (fast-mult-iter 6 2) => 12
  (fast-mult-iter 6 4) => 24

  (fast-mult-iter 28 3) => 84
  (fast-mult-iter 28 4) => 112

  (fast-mult-iter 29 3) => 87
  (fast-mult-iter 29 4) => 116
  (fast-mult-iter 29 29) => 841

  (fast-mult-iter 1567 986) => 1545062
  (fast-mult-iter 1566 987) => 1545642
  (fast-mult-iter 1566 986) => 1544076)

(facts "1.19. fib"
  (fib 0) => 0
  (fib 1) => 1
  (fib 2) => 1
  (fib 3) => 2
  (fib 4) => 3
  (fib 5) => 5
  (fib 6) => 8
  (fib 7) => 13)

(facts "1.23. next"
  (next 0) => 2
  (next 1) => 3
  (next 2) => 3
  (next 3) => 5
  (next 4) => 6)

(facts "1.23. smallest-divisor-1-23"
  (next 0) => 2
  (next 1) => 3
  (next 2) => 3
  (next 3) => 5
  (next 4) => 6)

(facts "1.27. Carmichael numbers do fool Fermat test"
  (every? passes-fermat-test?
    [561, 1105, 1729, 2465, 2821, 6601]) => true)

(facts "1.29. Simpson integral"
  (simpson-integral cube 0.0 1.0 100) => (roughly (/ 1.0 4) 1e-4))

; #(%) gets translated to (fn [x] (x)), so have to use chaining
; or 'identity' Clojure fn
(facts "1.30. sum-1-30"
  (sum-1-30 #(-> %) 0 inc 10) => (reduce + (range (inc 10))))

(facts "1.31. product-iter"
  (product-iter identity 1 inc 10) => (reduce * (range 1 (inc 10)))

  (product-rec identity 1 inc 10) => (reduce * (range 1 (inc 10))))

(facts "1.31. product-iter factorial"
  (factorial-1-31 0) => 1
  (factorial-1-31 1) => 1
  (factorial-1-31 2) => 2
  (factorial-1-31 10) => (reduce * (range 1 (inc 10))))

(facts "1.31. pi approximation"
  (pi-approx) => (roughly Math/PI))

(facts "1.32. sum via reduce"
  (sum-1-32 identity 1 inc 10) => (reduce + (range 1 (inc 10))))

(facts "1.32. product via reduce"
  (product-1-32 identity 1 inc 10) => (reduce * (range 1 (inc 10))))

(facts "1.32. accumulate via recursion"
  (accumulate-iter + 0 identity 1 inc 10) => (reduce + (range 1 (inc 10))))

(facts "1.33. filter-accumulate"
  (filtered-accumulate odd? + 0 identity 1 inc 10) => (+ 1 3 5 7 9))

(facts "1.33. sum of primes squares"
  (prime-sq-sum 0 20) => (reduce + (map square [1 2 3 5 7 11 13 17 19])))

(facts "1.33. product of relative primes"
  (product-of-relative-primes 10) => 189)

(def golden-r 1.618034)

(facts "1.35. golden ratio"
  (golden-ratio) => (roughly golden-r))

(facts "1.36. x^x = 1000"
  (x-to-x 2) => (roughly 4.55554)
  (x-to-x-dumped 2) => (roughly 4.55554))

(facts "1.37. golden ratio"
  (/ 1 (cont-frac-iter (fn [_] 1.0) (fn [_] 1.0) 100)) => (roughly golden-r)
  (/ 1 (cont-frac-rec (fn [_] 1.0) (fn [_] 1.0) 100)) => (roughly golden-r))

(facts "1.38. E"
  (+ 2 (e-approx)) => (roughly Math/E))

(facts "1.39. tan"
  (tan-cf 0.5 100) => (roughly (Math/tan 0.5)))

(facts "1.40. cubic equation solver"
  (newtons-method (cubic 2 2 2) 1) => (roughly (- 1.543689)))

(facts "1.41. double application"
  ((double-1-41 inc) 5) => 7
  ((double-1-41 #(* 2 %)) 5) => 20)

(facts "1.42. fn composition"
  ((compose-1-42 square inc) 6) => 49)

(facts "1.43. repeated fn composition"
  ((repeated square 2) 5) => 625)

(facts "1.45. average dumping"
  (sqrt-1-45 25) => (roughly 5)
  (cube-root-1-45 343) => (roughly 7))

(facts "1.45. nth-root via variable dumping"
  (n-th-root 16 4) => (roughly 2)
  (n-th-root 32 5) => (roughly 2)
  (n-th-root 8192 13) => (roughly 2)
  (n-th-root 343 3) => (roughly 7)
  (n-th-root 2401 4) => (roughly 7)
  (n-th-root 823543 7) => (roughly 7))

(facts "1.46. sqrt via iterative improvement"
  (sqrt-1-46-1 49) => (roughly 7)
  (sqrt-1-46-2 49) => (roughly 7))