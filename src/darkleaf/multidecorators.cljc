(ns darkleaf.multidecorators)

(def ^:private empty-queue
  #?(:clj  clojure.lang.PersistentQueue/EMPTY
     :cljs cljs.core/PersistentQueue.EMPTY))

(defn- inversed-compare [a b]
  (compare b a))

(defn- reversed-me-and-ancestors [tag]
  (loop [acc   (list tag)
         queue (conj empty-queue tag)]
    (if-some [tag (peek queue)]
      (let [prnts (->> tag parents (sort-by str inversed-compare))]
        (recur (into acc         prnts)
               (into (pop queue) prnts)))
      (distinct acc))))

(defn multi [dispatch initial]
  (let [registry (atom {})]
    (fn
      ([] registry)
      ([obj & args]
       (let [tag  (apply dispatch obj args)
             tags (reversed-me-and-ancestors tag)
             reg  @registry
             f    (reduce (fn [acc tag]
                            (if-some [decorator (reg tag)]
                              (fn [obj & args]
                                (apply decorator acc obj args))
                              acc))
                          initial
                          tags)]
         (apply f obj args))))))

(defn decorate [multi tag decorator]
  (swap! (multi) assoc tag decorator)
  multi)
