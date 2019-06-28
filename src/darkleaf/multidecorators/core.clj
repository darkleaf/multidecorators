(ns darkleaf.multidecorators.core
  (:refer-clojure :exclude [defmulti derive ancestors])
  (:require
   [com.stuartsierra.dependency :as dep]))

(defonce hierarchy (atom (dep/graph)))

(defn derive [child parent]
  (swap! hierarchy dep/depend child parent))

(defn- ancestors [graph tag]
  (let [ancestors (dep/transitive-dependencies graph tag)
        ancestors (conj ancestors tag) ;; fix name
        comp      (dep/topo-comparator graph)]
    (sort-by identity comp ancestors)))

(comment
  (let [g (-> (dep/graph)
              (dep/depend :b :a)
              (dep/depend :c :b)
              (dep/depend :c :a)
              (dep/depend :d :c))]
    (ancestors g :c)))



(defrecord MultiDecorator [dispatch-fn initial-fn table]
  clojure.lang.IFn
  #_(invoke [this])
  (invoke [this arg1]
    (let [tag        (dispatch-fn arg1)
          _          (prn tag)
          tags       (ancestors @hierarchy tag)
          _          (prn tags)
          decorators (map #(get @table % identity)
                          tags)
          ;; todo: cache me
          f          (reduce (fn [acc decorator] (decorator acc))
                             initial-fn
                             decorators)]
      (f arg1))))

(defmacro defmulti [md-name dispatch-fn initial-fn]
  `(defonce ~md-name (->MultiDecorator ~dispatch-fn ~initial-fn (atom {}))))


(defmacro defdecorator [md-name tag args & body]
  (let [[f & f-args] args]
    `(let [decorator# (fn [~f]
                        (fn [~@f-args]
                          (do ~@body)))]
       (swap! (:table ~md-name) assoc ~tag decorator#))))


(comment
  (derive :publication :god)
  (derive :article :publication)
  (derive :interview :publication)
  (derive :interview :god) ;; <--
  (defmulti validate
    (fn [x] (type x))
    (fn [x] x))
  (defdecorator validate :god [f x]
    (conj (f x) :god-errors))
  (defdecorator validate :publication [f x]
    (conj (f x) :pub-errors))
  (defdecorator validate :article [f x]
    (conj (f x) :article-errors))
  (defdecorator validate :interview [f x]
    (conj (f x) :interview-errors))

  ;; #=> [:god-errors :pub-errors :interview-errors]
  (validate (with-meta [] {:type :interview})))




  ;; (invoke [this arg1 arg2])
  ;; (invoke [this arg1 arg2 arg3])
  ;; (invoke [this arg1 arg2 arg3 arg4])
  ;; (invoke [this arg1 arg2 arg3 arg4 arg5])
  ;; (invoke [this arg1 arg2 arg3 arg4 arg5 arg6])
  ;; (invoke [this arg1 arg2 arg3 arg4 arg5 arg6 arg7])
  ;; (invoke [this arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8])
  ;; (invoke [this arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9])
  ;; (invoke [this arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10])
  ;; (invoke [this arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11])
  ;; (invoke [this arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12])
  ;; (invoke [this arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13])
  ;; (invoke [this arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13 arg14])
  ;; (invoke [this arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13 arg14 arg15])
  ;; (invoke [this arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13 arg14 arg15 arg16])
  ;; (invoke [this arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13 arg14 arg15 arg16 arg17])
  ;; (invoke [this arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13 arg14 arg15 arg16 arg17 arg18])
  ;; (invoke [this arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13 arg14 arg15 arg16 arg17 arg18 arg19])
  ;; (invoke [this arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13 arg14 arg15 arg16 arg17 arg18 arg19 arg20])
  ;; ;; maybe wrong
  ;; (invoke [this arg1 arg2 arg3 arg4 arg5 arg6 arg7 arg8 arg9 arg10 arg11 arg12 arg13 arg14 arg15 arg16 arg17 arg18 arg19 arg20 args]))
