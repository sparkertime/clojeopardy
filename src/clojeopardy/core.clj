(ns clojeopardy.core
  (:require [net.cgrand.enlive-html :as html]))

(def base-url "http://www.j-archive.com/showgame.php?game_id=")

(defn build-url [id]
  (java.net.URL. (str base-url id)))

(defn fetch-game-live [id]
  (html/html-resource (build-url id)))

(def fetch-game (memoize fetch-game-live))

(defn question-for [element]
  (re-find #"(?<=correct_response\">).*(?=</em>)" ((element :attrs) :onmouseover)))

(defn answer-for [element]
  (re-find #"(?<=_stuck', ').*(?='\))" ((element :attrs) :onmouseout)))

(defn category-for [element] "none")

(defn pos-for [element]
  (Integer. (re-find #"(?<=J_[0-9]_).*(?=_stuck)" ((element :attrs) :onclick))))

(defn id-for [element]
  (let [link (first (html/select element [:.clue_order_number :a]))]
       (Integer. (re-find #"(?<=clue_id=).*" ((link :attrs) :href)))))

(defn daily-double? [element]
  (boolean (seq (html/select element [:.clue_value_daily_double]))))


(def css-class-for-round
  {:single :#jeopardy_round
   :double :#double_jeopardy_round
   :final :#final_jeopardy_round})

(defn name-for [element]
  (html/text (first (html/select element [:.category_name]))))

(defn comments-for[element]
  (html/text (first (html/select element [:.category_comments]))))

(defn parse-category [idx element round]
  { :name (name-for element)
    :comments (comments-for element)
    :round round
    :pos (inc idx) })

(defn categories-for-round [round id]
  (map-indexed #(parse-category %1 %2 round)  (html/select (fetch-game id) [(css-class-for-round round) :.category])))

(defn categories [id]
  (concat (categories-for-round :single id)
          (categories-for-round :double id)
          (categories-for-round :final id)))

(defn parse-clue [element]
  { :question (question-for element)
    :answer (answer-for element)
    :category (category-for element)
    :pos (pos-for element)
    :id (id-for element)
    :daily-double? (daily-double? element)})

(defn clues [id]
  (map parse-clue (html/select (fetch-game id) [:.clue :div])))
