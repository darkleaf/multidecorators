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

(defn initial [obj]
  [])

(defonce func (md/multi identity #'initial))

(defn a-decorator [super obj]
  (conj (super obj) :a))
(md/decorate func ::a #'a-decorator)

(defn b-decorator [super obj]
  (conj (super obj) :b))
(md/decorate func ::b #'b-decorator)

(defn c-decorator [super obj]
  (conj (super obj) :c))
(md/decorate func ::c #'c-decorator)

(defn d-decorator [super obj]
  (conj (super obj) :d))
(md/decorate func ::d #'d-decorator)

(defn e-decorator [super obj]
  (conj (super obj) :e))
(md/decorate func ::e #'e-decorator)

(assert (= [:a] (func ::a)))
(assert (= [:a :b] (func ::b)))
(assert (= [:a :c] (func ::c)))
(assert (= [:a :b :c :d] (func ::d)))
(assert (= [:e] (func ::e)))
(assert (= [] (func ::f)))
```

## Development

```
lein test
lein doo node node-none once
lein doo node node-advanced once
```
