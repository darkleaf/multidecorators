(ns darkleaf.multidecorators-test
  (:require
   [clojure.test :as t]
   [darkleaf.multidecorators :as md]))

;; diamond inheritance
(derive ::d ::b)
(derive ::d ::c)
(derive ::b ::a)
(derive ::c ::a)

(derive `s ::d)
#?(:clj (derive Object ::d))

(t/deftest usage
  (let [multi (doto (md/multi identity (constantly []))
                (md/decorate ::a (fn [super obj] (conj (super obj) :a)))
                ^:repeated
                (md/decorate ::a (fn [super obj] (conj (super obj) :a)))
                (md/decorate ::b (fn [super obj] (conj (super obj) :b)))
                (md/decorate ::c (fn [super obj] (conj (super obj) :c)))
                (md/decorate ::d (fn [super obj] (conj (super obj) :d)))
                ^:additional
                (md/decorate ::e (fn [super obj] (conj (super obj) :e)))
                (md/decorate `s  (fn [super obj] (conj (super obj) 's)))
                #?(:clj (md/decorate Object (fn [super obj] (conj (super obj) :obj)))))]

    (t/is (= [:a] (multi ::a)))
    (t/is (= [:a :b] (multi ::b)))
    (t/is (= [:a :c] (multi ::c)))
    (t/is (= [:a :b :c :d] (multi ::d)))
    (t/is (= [:e] (multi ::e)))
    (t/is (= [] (multi ::f)))
    (t/is (= [:a :b :c :d 's] (multi `s)))
    #?(:clj (t/is (= [:a :b :c :d :obj] (multi String))))))

(t/deftest memoization
  (let [multi (doto (md/multi identity (constantly []))
                (md/decorate ::a (fn [super obj] (conj (super obj) :a)))
                (md/decorate ::b (fn [super obj] (conj (super obj) :b)))
                (md/decorate ::c (fn [super obj] (conj (super obj) :c)))
                (md/decorate ::d (fn [super obj] (conj (super obj) :d)))
                (md/decorate `s  (fn [super obj] (conj (super obj) 's)))
                #?(:clj (md/decorate Object (fn [super obj] (conj (super obj) :obj)))))
        mem-multi (md/memoize-multi multi)]
    (doseq [_ (range 2)]
      (t/is (= [:a] (mem-multi ::a)))
      (t/is (= [:a :b] (mem-multi ::b)))
      (t/is (= [:a :c] (mem-multi ::c)))
      (t/is (= [:a :b :c :d] (mem-multi ::d)))
      (t/is (= [] (mem-multi ::f)))
      (t/is (= [:a :b :c :d 's] (mem-multi `s)))
      #?(:clj (t/is (= [:a :b :c :d :obj] (mem-multi String)))))))
