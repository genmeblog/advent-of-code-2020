(ns advent-of-code-2021.day19
  (:require [common :refer [read-data-as-blocks parse]]
            [fastmath.vector :as v]
            [fastmath.matrix :as mat]
            [clojure.set :as set]
            [fastmath.core :as m]))

(defn parse-block
  [block]
  (->> (rest block)
       (map #(str "[" % "]"))
       (parse)))

(defn parser
  [data]
  (mapv parse-block data))

(def data (parser (read-data-as-blocks 2021 19 "example")))

(defn distances
  [block]
  (update-vals (->> (for [a block
                          b block
                          :when (not= a b)]
                      [a (v/dist-abs a b)])
                    (group-by first))
               (comp set #(map second %))))

(def pairs (for [[p1 d1] (distances (nth data 1))
               [p2 d2] (distances (nth data 4))
               :when (>= (count (set/intersection d1 d2)) 10)]
           [p1 p2]))

(count pairs)
#_(map (partial apply v/sub) pairs)

(def a (map first pairs))
(def b (map second pairs))
(def ca (v/average-vectors a))
(def cb (v/average-vectors b))
(def a' (map #(v/sub % ca) a))
(def b' (map #(v/sub % cb) b))

(defmacro permutation-finder
  [sym]
  (let [syms '[x' (- x') y' (- y') z' (- z')]
        vecs [[0 1.0] [0 -1.0] [1 1.0] [1 -1.0] [2 1.0] [2 -1.0]]]
    `(cond ~@(mapcat(fn [id] `[(m/delta= ~sym ~(syms id)) ~(nth vecs id)]) (range 6)))))

(defn find-permutation
  [[x y z] [x' y' z']]
  [(permutation-finder x)
   (permutation-finder y)
   (permutation-finder z)])

(find-permutation (first a') (first b'))

[a' b']
;; => [([438.83333333333337 798.5 189.75]
;;      [-478.16666666666663 -853.5 -313.25]
;;      [298.83333333333337 844.5 155.75]
;;      [-580.1666666666666 -756.5 -231.25]
;;      [692.8333333333334 -589.5 -131.25]
;;      [400.83333333333337 826.5 218.75]
;;      [588.8333333333334 -581.5 50.75]
;;      [-574.1666666666666 512.5 127.75]
;;      [-505.16666666666663 448.5 135.75]
;;      [640.8333333333334 -444.5 -39.25]
;;      [-454.16666666666663 -659.5 -266.25]
;;      [-469.16666666666663 454.5 102.75])
;;     ([-189.75 438.8333333333333 -798.5]
;;      [313.25 -478.1666666666667 853.5]
;;      [-155.75 298.8333333333333 -844.5]
;;      [231.25 -580.1666666666666 756.5]
;;      [131.25 692.8333333333334 589.5]
;;      [-218.75 400.8333333333333 -826.5]
;;      [-50.75 588.8333333333334 581.5]
;;      [-127.75 -574.1666666666666 -512.5]
;;      [-135.75 -505.1666666666667 -448.5]
;;      [39.25 640.8333333333334 444.5]
;;      [266.25 -454.1666666666667 659.5]
;;      [-102.75 -469.1666666666667 -454.5])]

a'
;; => ([-589.25 -207.25 -458.6666666666667]
;;     [-632.25 -199.25 -412.6666666666667]
;;     [418.75 -58.25 -630.6666666666667]
;;     [-316.25 305.75 543.3333333333333]
;;     [432.75 28.75 -738.6666666666667]
;;     [556.75 -26.25 571.3333333333333]
;;     [-508.25 -206.25 -295.6666666666667]
;;     [-456.25 259.75 509.3333333333333]
;;     [451.75 -84.25 596.3333333333333]
;;     [487.75 -90.25 563.3333333333333]
;;     [572.75 -10.25 -727.6666666666667]
;;     [-418.25 287.75 480.3333333333333])

b'
;; => ([589.25 -207.25 458.6666666666667]
;;     [632.25 -199.25 412.6666666666667]
;;     [-418.75 -58.25 630.6666666666666]
;;     [316.25 305.75 -543.3333333333334]
;;     [-432.75 28.75 738.6666666666666]
;;     [-556.75 -26.25 -571.3333333333334]
;;     [508.25 -206.25 295.6666666666667]
;;     [456.25 259.75 -509.3333333333333]
;;     [-451.75 -84.25 -596.3333333333334]
;;     [-487.75 -90.25 -563.3333333333334]
;;     [-572.75 -10.25 727.6666666666666]
;;     [418.25 287.75 -480.3333333333333])
