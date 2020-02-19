[![Clojars Project](https://img.shields.io/clojars/v/darkleaf/multidecorators.svg)](https://clojars.org/darkleaf/multidecorators)

# Multidecorators

Like multimethods but multidecorators.

+ clojure(script)
+ dead simple
+ no deps
+ no macros


## Usage

```clojure
(ns example
  (:require
   [darkleaf.multidecorators :as md]))

;; diamond inheritance
(derive ::d ::b)
(derive ::d ::c)
(derive ::b ::a)
(derive ::c ::a)

(defn dispatch [obj]
  obj)

(defn initial [obj]
  [])

(defonce func (md/multi #'dispatch #'initial))

(md/decorate func ::a
  (fn a-decorator [super obj]
    (conj (super obj) :a)))

(md/decorate func ::b
  (fn b-decorator [super obj]
    (conj (super obj) :b)))

(md/decorate func ::c
  (fn c-decorator [super obj]
    (conj (super obj) :c)))

(md/decorate func ::d
  (fn d-decorator [super obj]
    (conj (super obj) :d)))

(md/decorate func ::e
  (fn e-decorator [super obj]
    (conj (super obj) :e)))

(assert (= [:a] (func ::a)))
(assert (= [:a :b] (func ::b)))
(assert (= [:a :c] (func ::c)))
(assert (= [:a :b :c :d] (func ::d)))
(assert (= [:e] (func ::e)))
(assert (= [] (func ::f)))
```

## Memoization

```clojure
(defn -main []
  (alter-var-root #'func md/memoize-multi))
```

## Development

```
lein test
lein doo node node-none once
lein doo node node-advanced once
```
