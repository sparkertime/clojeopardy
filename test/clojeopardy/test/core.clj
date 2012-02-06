;  (slurp "test/fixtures/jarchive.html"))

(ns clojeopardy.test.core
  (:use clojure.test)
  (:require [clojeopardy.core :as jeopardy]))

(deftest test-categories
  (with-redefs [jeopardy/build-url (fn [id] "http://www.j-archive.com/showgame.php?game_id=3446")]
  (is (= (jeopardy/categories 3445) [{:name "COLLEGE TOWNS", :comments "", :round :single, :pos 1} {:name "SPORTS QUOTES", :comments "", :round :single, :pos 2} {:name "DOUBLE TALK", :comments "", :round :single, :pos 3} {:name "SUPERMARKET SWEEP", :comments "", :round :single, :pos 4} {:name "FERROUS, BUELLER", :comments "", :round :single, :pos 5} {:name "BEFORE & AFTER", :comments "", :round :single, :pos 6} {:name "WORLD WAR II", :comments "", :round :double, :pos 1} {:name "PLEAS & CARATS", :comments "", :round :double, :pos 2} {:name "FAMOUS LAST WORDS", :comments "", :round :double, :pos 3} {:name "I LOVE A MYSTERY", :comments "", :round :double, :pos 4} {:name "HERE'S 2 \"U\"", :comments "(Alex: Each correct response will contain two \"U\"s.)", :round :double, :pos 5} {:name "MR. ROBINSON", :comments "", :round :double, :pos 6} {:name "MONARCHIES", :comments "", :round :final, :pos 1}])))
         )
