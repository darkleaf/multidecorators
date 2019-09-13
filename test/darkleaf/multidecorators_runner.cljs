(ns darkleaf.multidecorators-runner
  (:require
   [doo.runner :refer-macros [doo-tests]]
   [darkleaf.multidecorators-test]))

(doo-tests 'darkleaf.multidecorators-test)
