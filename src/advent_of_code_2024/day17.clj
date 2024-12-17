(ns advent-of-code-2024.day17
  (:require [common :refer [read-data-as-blocks get-numbers]]
            [clojure.string :as str]))

(set! *unchecked-math* :warn-on-boxed)

(defn parse [[registers program]]
  [(conj (vec (mapcat get-numbers registers)) 0 [])
   (get-numbers (first program))])

(def data (parse (read-data-as-blocks 2024 17)))

(defn combo ^long [^long op ^long A ^long B ^long C] (case (int op) 4 A 5 B 6 C op))

(defn step [program [^long A ^long B ^long C ^long ip out]]
  (if (>= ip (count program))
    [A B C ip out :end]
    (let [op (long (program (inc ip)))
          ip2 (+ ip 2)]
      (case (int (program ip))
        0 [(bit-shift-right A (combo op A B C)) B C ip2 out]
        1 [A (bit-xor B op) C ip2 out]
        2 [A (bit-and (combo op A B C) 7) C ip2 out]
        3 (if (zero? A)
            [A B C ip2 out]
            [A B C op out])
        4 [A (bit-xor B C) C ip2 out]
        5 [A B C ip2 (conj out (bit-and (combo op A B C) 7))]
        6 [A (bit-shift-right A (combo op A B C)) C ip2 out]
        7 [A B (bit-shift-right A (combo op A B C)) ip2 out]))))

(defn run [[registers program]]
  (->> registers
       (iterate (partial step program))
       (drop-while (comp vector? last))
       (first)))

(defn output [data] ((run data) 4))
(defn output->str [data] (str/join "," (output data)))

(def part-1 (output->str data))
;; => "7,3,0,5,7,1,4,0,5"

(defn run-with-A [[_ program] A] (output [[A 0 0 0 []] program]))

;; 3 xors, up to 10 LSB bits generates a number
(defn find-possible [data target ^long off in]
  (let [bitoff (long (* 3 (+ 2 off)))
        cnt (count target)]
    (-> (for [^long prefix (range 16)
              :let [l (bit-shift-left prefix bitoff)]
              ^long in in
              :let [n (bit-or l (bit-and in (dec (bit-shift-left 1 bitoff))))]
              :when (= target (take cnt (run-with-A data n)))]
          n)
        (distinct))))

(defn find-A [data]
  (let [target (last data)]
    (->> (reduce (fn [buff ^long id]
                   (find-possible data (take id target) (dec id) buff)) (range 64) (range 1 17))
         (apply min))))

(def part-2 (find-A data))
;; => 202972175280682
