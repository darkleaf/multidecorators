(ns darkleaf.multidecorators)

(defn- tag-comparator [a b]
  (cond
    (= a b)    0
    (isa? a b) 1
    (isa? b a) -1
    :else      (compare (str a) (str b))))

(defn- ->isa?
  "performance optimization"
  [child]
  (let [an-ancestors (ancestors child)]
    (fn [parent]
      (or (= child parent)
          (contains? an-ancestors parent)))))

(defn multi [dispatch initial]
  (let [registry (atom (sorted-map-by tag-comparator))]
    (fn
      ([] registry)
      ([obj & args]
       (let [tag  (apply dispatch obj args)
             pred (->isa? tag)
             f    (reduce-kv (fn [acc tag decorator]
                               (if (pred tag)
                                 (fn [obj & args]
                                   (apply decorator acc obj args))
                                 acc))
                             initial
                             @registry)]
         (apply f obj args))))))

(defn decorate [multi tag decorator]
  (swap! (multi) assoc tag decorator)
  multi)
