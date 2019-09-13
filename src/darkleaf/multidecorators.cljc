(ns darkleaf.multidecorators)

(defn- ancestor? [parent child]
  (contains? (ancestors child) parent))

(defn- me-and-my-ancestors [tag]
  (into #{tag} (ancestors tag)))

(defn- tag-comparator [[a _] [b _]]
  (cond
    (= a b)         0
    (ancestor? a b) -1
    (ancestor? b a) 1
    :else           (compare (str a) (str b))))

(defn multi [dispatch initial]
  (let [registry (atom (sorted-set-by tag-comparator))]
    (fn
      ([] registry)
      ([obj & args]
       (let [tag  (apply dispatch obj args)
             pred (me-and-my-ancestors tag)
             f    (->> @registry
                       (filter (fn [[t _]] (pred t)))
                       (map second)
                       (reduce (fn [acc f] (fn [obj & args]
                                             (apply f acc obj args)))
                               initial))]
         (apply f obj args))))))

(defn decorate [multi tag decorator]
  (swap! (multi) conj [tag decorator])
  multi)
