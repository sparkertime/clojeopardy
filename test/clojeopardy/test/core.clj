(ns clojeopardy.test.core
  (:use clojure.test)
  (:require [clojeopardy.core :as jeopardy]))

(defmacro with-fixtures [body]
  `(with-redefs [jeopardy/build-url (fn [id#] "fixtures/jarchive.html")] ~body))

(deftest test-categories
  (with-fixtures
    (is (= (jeopardy/categories 3446) [{:name "COLLEGE TOWNS", :comments "", :round :single, :pos 1} {:name "SPORTS QUOTES", :comments "", :round :single, :pos 2} {:name "DOUBLE TALK", :comments "", :round :single, :pos 3} {:name "SUPERMARKET SWEEP", :comments "", :round :single, :pos 4} {:name "FERROUS, BUELLER", :comments "", :round :single, :pos 5} {:name "BEFORE & AFTER", :comments "", :round :single, :pos 6} {:name "WORLD WAR II", :comments "", :round :double, :pos 1} {:name "PLEAS & CARATS", :comments "", :round :double, :pos 2} {:name "FAMOUS LAST WORDS", :comments "", :round :double, :pos 3} {:name "I LOVE A MYSTERY", :comments "", :round :double, :pos 4} {:name "HERE'S 2 \"U\"", :comments "(Alex: Each correct response will contain two \"U\"s.)", :round :double, :pos 5} {:name "MR. ROBINSON", :comments "", :round :double, :pos 6} {:name "MONARCHIES", :comments "", :round :final, :pos 1}]))))

(deftest test-clues
  (with-fixtures
    (is (= (filter :daily-double? (jeopardy/clues 3446)) 
           [{:question "rust", :answer "An olden theory said that when metallic iron loses its phlogiston, it becomes this remnant we know as an oxide", :category "none", :pos 5, :id 192760, :daily-double? true} {:question "Okinawa", :answer "The final land battle of WWII was fought on this island in the Ryukyus in June 1945", :category "none", :pos 5, :id 192770, :daily-double? true} {:question "calculus", :answer "The name of this branch of math comes from the Latin for \"small stone\"", :category "none", :pos 5, :id 192790, :daily-double? true}]))))
