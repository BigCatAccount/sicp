(ns ch1
  (:refer-clojure :exclude [next]))

(defn square [x] (* x x))

(defn abs [x]
  (if (< x 0)
    (- x)
    x))

(defn good-enough? [guess x]
  (< (abs (- (square guess) x)) 0.001))

(defn average [x y]
  (/ (+ x y) 2))

(defn improve [guess x]
  (average guess (/ x guess)))

(defn sqrt-iter [guess x]
  (if (good-enough? guess x)
    guess
    (sqrt-iter (improve guess x) x)))

(defn sqrt [x]
  (sqrt-iter 1.0 x))

; -----------------------------------------
; 1.6

; StackOverflow due to eager eval of args. Recursive case is evaluated eagerly.
; Call-by-name needed, or delay or smth lazy like that...
(defn new-if [predicate then-clause else-clause]
  (cond
    predicate then-clause
    :else else-clause))

(defn sqrt-iter-1-6 [guess x]
  (new-if
    (good-enough? guess x)
    guess
    (sqrt-iter-1-6 (improve guess x) x)))

(defn sqrt-1-6 [x]
  (sqrt-iter-1-6 1.0 x))

; -----------------------------------------
; 1.7

; (good-enough 1e+50) is giving me StackOverflow: condition never converges

; I'll be using loop-recur since Clojure (contrary to Scheme)
; does not give us free tail-call optimizations...

; if we check for fixed tolerance for small numbers sqrt the ratio soon becomes 1
; (thus becoming an inefficient check). Need to check tolerance interval where ratio approaches 1.
(defn sqrt-1-7 [x]
  (cond
    (zero? x) 0 ; if not preset - have problems converging in Clojure
    :else (let [good-enough? (fn [old-guess new-guess]
                               (let [ratio (/ old-guess new-guess)]
                                 (and
                                   (> ratio 0.999)
                                   (< ratio 1.001))))]
            (loop [old-guess 0 new-guess 1.0]
              (if (good-enough? old-guess new-guess)
                new-guess
                (recur new-guess (improve new-guess x)))))))

; -----------------------------------------
; 1.11

(defn fn-1-11-rec [n]
  (if (< n 3)
    n
    (+ (fn-1-11-rec (- n 1))
      (* 2 (fn-1-11-rec (- n 2)))
      (* 3 (fn-1-11-rec (- n 3))))))

; fn = fc + 2fb + 3fa
; n |  c  b  a
; 0 |  0  0  0
; 1 |  1  0  0
; 2 |  2  0  0
; 3 | f2 f1 f0 = 2  + 2*1 + 3*0
; 4 | f3 f2 f1 = f3 + 2*2 + 3*1
(defn fn-1-11-iter [n]
  (loop [c 2
         b 1
         a 0
         counter 0]
    (if (> counter n)
      c
      (let [current (if (< counter 3)
                      counter
                      (+ c (* 2 b) (* 3 a)))]
        (recur current c b (inc counter))))))

; -----------------------------------------
; 1.16

; for even n we do (b*b) - so twice less iterations
(defn fast-expt-1-16 [b n]
  (let [square-b (square b)]
    (loop [a 1
           x n]
      (cond
        (zero? x) a
        (even? x) (recur (* a square-b) (- x 2))
        :else (recur (* a b) (dec x))))))

; -----------------------------------------
; 1.17

; My names               | SICP names (ch 1.2)
; linear tail-recursion  | linear iterative process
; usual linear recursion | linear recursive process

; don't want to shadow Clojure "double" - I'll use it for casting in avoidance of rationals
(defn dbl [n] (* n 2))

(defn halve [n] (/ n 2))

; 5*6
; 2*(5*3)
; 2*(5+(5*2)) ...
; decrement is (+ (-1) x)

(defn fast-mult-rec [a b]
  (cond
    (zero? b) 0
    (= 1 b) a
    (even? b) (dbl (fast-mult-rec a (halve b)))
    :else (+ a (fast-mult-rec a (dec b)))))

; -----------------------------------------
; 1.18

; 5*6
; 5       6 0
; 2*5=10  3 0
; 10      2 10
; 20      1 10
; 20+10=30

; 29*3
; 29      3 0
; 29      2 29
; 2*29=58 1 29
; 58+29=87

(defn fast-mult-iter [a b]
  (loop [a' a
         b' b
         delta 0]
    (cond
      (zero? b') 0
      (= 1 b') (+ a' delta)
      (even? b') (recur (dbl a') (halve b') delta)
      :else (recur a' (dec b') (+ delta a')))))

; -----------------------------------------
; 1.19

; this took a bit longer :)

; Tpq(a0, b0) =
; a1 = b0q + a0q + a0p
; b1 = b0p + a0q
;
; one more application of transformation, substitution & simplification on my paper:
; Tpq(a1, b1) =
; a2 = b1q + a1q + a1p = ... = b0(2pq + q^2) + a0(2pq + q^2) + a0(p^2 + q^2)   #1
; b2 = b1p + a1q       = ... = b0(p^2 + q^2) + a0                              #2
;
; by definition
; Tp'q'(a0, b0) = Tpq(a1, b1) => (a2, b2)
;
; So Tp'q'(a0, b0) yields
; a2 = b0q' + a0q' + a0p'
; b2 = b0p' + a0q'
;
; matching it with #1 and #2 we get
; p' = p^2 + q^2
; q' = 2pq + q^2

(defn fib [n]
  (loop [a 1
         b 0
         p 0
         q 1
         count n]
    (cond
      (zero? count) b
      (even? count) (recur
                      a
                      b
                      (+ (square p) (square q))
                      (+ (dbl (* p q)) (square q))
                      (/ count 2))
      :else (recur
              (+ (* b q) (* a q) (* a p))
              (+ (* b p) (* a q))
              p
              q
              (dec count)))))

; PS. even? is optimization - so fn works without it

; -----------------------------------------
; 1.23

(declare find-divisor)

(defn smallest-divisor [n]
  (find-divisor n 2))

(defn divides? [a b]
  (= (rem b a) 0))

(defn prime? [n]
  (= n (smallest-divisor n)))

(defn find-divisor [n test-divisor]
  (cond
    (> (square test-divisor) n) n
    (divides? test-divisor n) test-divisor
    :else (find-divisor n (+ test-divisor 1))))

(defn next [n]
  (if (= 2 n)
    3
    (+ 2 n)))

(defn smallest-divisor-1-23 [n]
  (let [find-divisor (fn [test-divisor]
                       (cond
                         (> (square test-divisor) n) n
                         (divides? test-divisor n) test-divisor
                         :else (recur (next test-divisor))))]
    (find-divisor 2)))

(defn prime-1-23? [n]
  (= n (smallest-divisor-1-23 n)))

; helpers

(defn- first-n [pedicate n]
  (take n (filter pedicate (map inc (range)))))

(defn take-primes [n]
  (first-n prime? n))

(defn take-primes-1-23 [n]
  (first-n prime-1-23? n))

; -----------------------------------------
; 1.27

; mod(b^e, m)
; *' for autopromotion
(defn expmod [base exp m]
  (cond
    (zero? exp) 1
    (even? exp) (rem
                  (square (expmod base (/ exp 2) m))
                  m)
    :else (rem
            (*' base (expmod base (dec exp) m))
            m)))

(defn congruent? [a n]
  (= a (expmod a n n)))

(defn passes-fermat-test? [n]
  (every? true? (map #(congruent? % n)
                  (map inc (range (dec n)))))) ; 1...n-1

(defn charmichel-numbers [limit]
  (filter
    #(and (passes-fermat-test? %)
       (not (prime? %)))
    (range limit)))

; -----------------------------------------
; 1.28

; I was a bit mislead by the fact that this excercise
; fails clearly explaining that Miller's primality test
; is still a probabilistic and must still be evaluated multiple times to reveal a "non-trivial" root.
; test does not guarantee final answer in a single invocation.

(defn expmod-1-28 [base exp m]
  (cond
    (zero? exp) 1
    (even? exp) (let [temp-result (expmod-1-28 base (/ exp 2) m)
                      reminder (rem (square temp-result) m)
                      non-trivial-result? (and
                                            (= 1 reminder)
                                            (not= temp-result 1)
                                            (not= temp-result (dec m)))]
                  (if non-trivial-result?
                    0
                    reminder))
    :else (rem (*' base (expmod-1-28 base (dec exp) m)) m)))

(defn miller-rabin-test [n]
  (let [random (inc (rand-int (dec n)))] ; 1...n-1
    (= 1 (expmod-1-28 random (dec n) n))))

(defn take-non-primes [n]
  (first-n (complement prime?) n))

(defn take-odd-non-primes [n]
  (first-n #(and
              ((complement prime?) %)
              (odd? %))
    n))

; -----------------------------------------
; 1.29

(defn sum [term a next b]
  (if (> a b)
    0
    (+ (term a)
      (sum term (next a) next b))))

(defn cube [x] (* x x x))

(defn simpson-integral [f a b n]
  (let [h (/ (- b a) n)
        term (fn [k] (let [c (cond
                               (or (zero? k) (= n k)) 1
                               (odd? k) 4
                               :else 2)]
                       (* c (f (+ a (* k h))))))
        simpson-sum (sum term 0 inc n)]
    (/ (* h simpson-sum) 3)))

; -----------------------------------------
; 1.30

(defn sum-1-30 [term a' next b]
  (loop [a a'
         accum 0]
    (if (> a b)
      accum
      (recur (next a) (+ accum (term a))))))

; -----------------------------------------
; 1.31

(defn product-iter [term a' next b]
  (loop [a a'
         accum 1]
    (if (> a b)
      accum
      (recur (next a) (*' accum (term a))))))

(defn product-rec [term a next b]
  (if (> a b)
    1
    (*' (term a)
      (product-rec term (next a) next b))))

(defn factorial-1-31 [n]
  (product-iter identity 1 inc n))

; observed a form of given equation - it can be "shifted":
; pi = 4*2 * (4/3)^2 * (6/5)^2 * ... and finally divided by "hanging"
; denom from "next" iteration

(defn pi-approx []
  (let [limit 1000
        nth-even #(+ 2 (* 2 %))
        product (product-iter
                  #(let [k (nth-even %)
                         term (/ (dbl k) (dbl (dec k)))] ; casting to avoid Clojure rationals
                     (* term term))
                  1
                  inc
                  limit)]
    (/ (* 8 product)
      (dec (nth-even (inc limit)))))) ; denom due to "shift"

; -----------------------------------------
; 1.32

(defn accumulate-iter [combiner null-value term a' next b]
  (loop [a a'
         accum null-value]
    (if (> a b)
      accum
      (recur (next a) (combiner accum (term a))))))

(defn accumulate-rec [combiner null-value term a next b]
  (if (> a b)
    null-value
    (combiner (term a)
      (accumulate-rec term (next a) next b))))

(defn sum-1-32 [term a next b]
  (accumulate-iter + 0 term a next b))

(defn product-1-32 [term a next b]
  (accumulate-iter * 1 term a next b))

; -----------------------------------------
; 1.33

(defn filtered-accumulate [predicate combiner null-value term a' next b]
  (loop [a a'
         accum null-value]
    (if (> a b)
      accum
      (let [t (term a)
            new-accum (if (predicate t)
                        (combiner accum t) ; #1
                        accum)]
        (recur (next a) new-accum)))))

(defn prime-sq-sum [a b]
  (filtered-accumulate
    prime?
    (fn [accum elem] ; order of args as in #1
      (+ accum (square elem)))
    0
    identity
    a
    inc
    b))

(defn gcd [a b]
  (if (zero? b)
    a
    (recur b (rem a b))))

(defn product-of-relative-primes [n]
  (filtered-accumulate
    #(= 1 (gcd % n))
    *
    1
    identity
    1
    inc
    n))

; -----------------------------------------
; 1.35

(def tolerance 0.00001)

(defn fixed-point-traced [f first-guess
                          & {:keys [trace] :or {trace false}}] ; optional
  (let [close-enough? (fn [v1 v2]
                        (< (abs (- v1 v2)) tolerance))
        try-it (fn [guess]
                 (let [next (f guess)]
                   (if trace
                     (println next))
                   (if (close-enough? guess next)
                     next
                     (recur next))))]
    (try-it first-guess)))

(defn fixed-point [f first-guess]
  (fixed-point-traced f first-guess))

(defn golden-ratio []
  (fixed-point
    #(+ 1 (/ 1.0 %))
    1))

; -----------------------------------------
; 1.36

(defn x-to-x [first-guess]
  (let [log1000 (Math/log 1000)]
    (fixed-point-traced
      #(/ log1000 (Math/log %))
      first-guess
      :trace true)))

; add x to both parts and devide by 2
(defn x-to-x-dumped [first-guess]
  (let [log1000 (Math/log 1000)]
    (fixed-point-traced
      #(/ (+ % (/ log1000 (Math/log %))) 2)
      first-guess
      :trace true)))

; -----------------------------------------
; 1.37

(defn cont-frac-iter [n-fn d-fn k]
  (loop [accum (d-fn k)
         i k]
    (if (zero? i)
      accum
      (let [d-n-1 (if (= 1 i)
                    0.0 ; avoiding Clojure rationals
                    (d-fn (dec i)))
            n (n-fn i)]
        (recur (+ d-n-1 (/ n accum)) (dec i))))))

(defn cont-frac-rec [n-fn d-fn k]
  (let [cont-frac (fn f [i]
                    (if (= k i)
                      (d-fn k)
                      (let [d-n-1 (if (= 1 i)
                                    0.0
                                    (d-fn (dec i)))
                            n (n-fn i)]
                        (+ d-n-1 (/ n (f (inc i)))))))]
    (cont-frac 1)))

; -----------------------------------------
; 1.38

; repl plays ;)
; (take n (map #(-> [% (dgen %)]) (range)))

(defn- dgen [n]
  (if (or
        (zero? (rem n 3))
        (zero? (rem (dec n) 3)))
    1
    (* 2 (inc (/ (- n 2) 3)))))

(defn e-approx []
  (cont-frac-iter
    (fn [_] 1.0)
    dgen
    100))

; -----------------------------------------
; 1.39

(defn tan-cf [x k]
  (let [d-fn (fn [n] (dec (* 2 n)))
        neg-sq (- (square x))
        n-fn (fn [n] (if (= 1 n)
                       x
                       neg-sq))]
    (cont-frac-iter n-fn d-fn k)))

; -----------------------------------------
; 1.40

(def dx 0.00001)

(defn deriv [g]
  (fn [x]
    (/ (- (g (+ x dx)) (g x))
      dx)))

(defn newton-transform [g]
  (fn [x]
    (- x (/ (g x) ((deriv g) x)))))

(defn newtons-method [g guess]
  (fixed-point (newton-transform g) guess))

(defn cubic [a b c]
  (fn [x] (+ (* x x x) (* x x a) (* x b) c)))

; -----------------------------------------
; 1.41

; see Clojure impl of "comp": reversing list of fns, and then - recursive "apply" on them

(defn double-1-41 [f]
  (fn [x] ((comp f f) x)))

; -----------------------------------------
; 1.42

(defn compose-1-42 [f g]
  (fn [x] (f (g x))))

; -----------------------------------------
; 1.43

(defn repeated [f n]
  (if (zero? n)
    identity
    (compose-1-42 f (repeated f (dec n)))))

; -----------------------------------------
; 1.44

; dx defined above

(defn smooth [f]
  (fn [x]
    (let [f0 (f (- x dx))
          f1 (f x)
          f2 (f (+ x dx))]
      (/ (+ f0 f1 f2) 3.0))))

; so we need to repeat "smooth" n times
; and the input to the chain will be original f...
(defn smooth-n-times [f n]
  ((repeated smooth n) f))

; -----------------------------------------
; 1.45

(defn average-damp [f]
  (fn [x] (average x (f x))))

(defn sqrt-1-45 [x]
  (fixed-point
    (average-damp (fn [y] (/ x y)))
    1.0))

(defn cube-root-1-45 [x]
  (fixed-point
    (average-damp (fn [y] (/ x (square y))))
    1.0))

; tested for 2^n = x, haven't found any super-pattern when it doesn't
; converge for me by applying single dumping (n=4,5,13,...) ...
(defn nth-root-repl-play [x n]
  (fixed-point
    (average-damp (fn [y] (/ x
                            (apply * (repeat (dec n) y))))) ; repeat in core.clj
    1.0))

(defn n-th-root [x n]
  (let [convering-fn (fn [y] (/ x
                               (apply * (repeat (dec n) y))))
        times-to-dump (Math/floor (/ (Math/log n) (Math/log 2))) ; from numeric methods...
        repeated-dumping (repeated average-damp times-to-dump)]
    (fixed-point
      (repeated-dumping convering-fn)
      1.0)))

; -----------------------------------------
; 1.46

(defn iterative-improve [good-guess? improver]
  (fn [guess]
    (loop [result guess]
      (let [next-result (improver result)]
        (if (good-guess? result next-result)
          next-result
          (recur next-result))))))

(defn sqrt-1-46-1 [x]
  ((iterative-improve
     (fn [_ new-guess] ; old-guess is not used
       (< (abs (- (square new-guess) x)) 0.001))
     (fn [guess]
       (average guess (/ (double x) guess))))
    x))

(defn fixed-point-1-46 [f first-guess]
  ((iterative-improve
     (fn [old-guess new-guess]
       (< (abs (- old-guess new-guess)) tolerance))
     (fn [guess]
       (f guess)))
    first-guess))

(defn sqrt-1-46-2 [x]
  (fixed-point-1-46 (fn [y] (average y (/ x y))) 1.0))