(ns darkleaf.multidecorators)

(def ^:private empty-queue
  #?(:clj  clojure.lang.PersistentQueue/EMPTY
     :cljs cljs.core/PersistentQueue.EMPTY))

(defn- tag-name [tag]
  (cond
    #?@(:clj [(class? tag) (.getSimpleName tag)])
    :else (name tag)))

(defn- reversed-me-and-ancestors [tag]
  (loop [acc   (list tag)
         queue (conj empty-queue tag)]
    (if-some [tag (peek queue)]
      (let [tag-parents          (->> tag parents (sort-by tag-name))
            reversed-tag-parents (reverse tag-parents)]
        (recur (into acc reversed-tag-parents)
               (into (pop queue) tag-parents)))
      (distinct acc))))

(defn multi [dispatch initial]
  (let [iregistry (atom {})]
    (fn
      ([] {:type      :dynamic
           :iregistry iregistry
           :dispatch  dispatch
           :initial   initial})
      ([obj & args]
       (let [tag  (apply dispatch obj args)
             tags (reversed-me-and-ancestors tag)
             reg  @iregistry
             f    (reduce (fn [acc tag]
                            (if-some [decorator (reg tag)]
                              (fn [obj & args]
                                (apply decorator acc obj args))
                              acc))
                          initial
                          tags)]
         (apply f obj args))))))

(defn ^{:style/indent :defn} decorate [multi tag decorator]
  (let [state     (multi)
        iregistry (:iregistry state)]
    (swap! iregistry assoc tag decorator)
    multi))
