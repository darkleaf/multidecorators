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

(defn- method [registry tag initial]
  (let [tags (reversed-me-and-ancestors tag)]
    (->> tags
         (map registry)
         (remove nil?)
         (reduce (fn [acc decorator]
                   (fn [obj & args]
                     (apply decorator acc obj args)))
                 initial))))

(defn multi [dispatch initial]
  (let [iregistry (atom {})]
    (fn
      ([] {:type      :dynamic
           :iregistry iregistry
           :dispatch  dispatch
           :initial   initial})
      ([obj & args]
       (let [tag  (apply dispatch obj args)
             f    (method @iregistry tag initial)]
         (apply f obj args))))))

(defn memoize-multi [multi]
  (case (:type (multi))
    :memoized multi
    :dynamic  (let [{:keys [iregistry
                            dispatch
                            initial]} (multi)
                    registry          @iregistry
                    mem-method        (memoize method)]
                (fn
                  ([] {:type     :memoized
                       :registry registry
                       :initial  initial
                       :dispatch dispatch})
                  ([obj & args]
                   (let [tag (apply dispatch obj args)
                         f   (mem-method registry tag initial)]
                     (apply f obj args)))))))

(defn ^{:style/indent :defn} decorate [multi tag decorator]
  (case (:type (multi))
    :dynamic (let [state     (multi)
                   iregistry (:iregistry state)]
               (swap! iregistry assoc tag decorator)
               multi)))
