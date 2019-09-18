(defproject darkleaf/multidecorators "1.0.4"
  :description "Like multimethods but multidecorators."
  :url "https://github.com/darkleaf/multidecorators/"
  :license {:name "Unlicense"
            :url  "http://unlicense.org/"}
  :dependencies [[org.clojure/clojure "1.10.1" :scope "provided"]
                 [org.clojure/clojurescript "1.10.520" :scope "provided"]]
  :plugins [[lein-doo "0.1.11"]]

  :cljsbuild
  {:builds [{:id           "node-none"
             :source-paths ["src" "test"]
             :compiler     {:output-to     "out/node-none.js"
                            :target        :nodejs
                            :optimizations :none
                            :main          darkleaf.multidecorators-runner}}
            {:id           "node-advanced"
             :source-paths ["src" "test"]
             :compiler     {:output-to     "out/node-advanced.js"
                            :target        :nodejs
                            :optimizations :advanced
                            :main          darkleaf.multidecorators-runner}}]}

  :repl-options {:init-ns darkleaf.multidecorators})
