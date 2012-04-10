(ns clojeopardy.core
  (:require [net.cgrand.enlive-html :as html]))

(def base-url "http://www.j-archive.com/showgame.php?game_id=")

(defn build-url [id]
  (java.net.URL. (str base-url id)))

(defn fetch-game-live [id]
  (html/html-resource (build-url id)))

(def fetch-game (memoize fetch-game-live))

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
  (flatten
    (for [round [:single :double :final]]
      (categories-for-round round id))))

(defn question-for [element]
  (re-find #"(?<=correct_response\">).*(?=</em>)"
           ((element :attrs) :onmouseover)))

(defn answer-for [element]
  (re-find #"(?<=_stuck', ').*(?='\))"
           ((element :attrs) :onmouseout)))

(def round-for-shorthand {"J" :single "DJ" :double "FJ" :final})

(defn category-for [element game-id]
  (let [round-str (re-find #"(?<= 'clue_).*(?=_stuck)"
                         ((element :attrs) :onmouseout))]
    (if (= "FJ" round-str)
      (first (categories-for-round :final game-id))
      (let [round-shorthand (re-find #"^[^_]*(?=_.)" round-str)
            round (round-for-shorthand round-shorthand)
            order (Integer. (re-find #"(?<=J_)[0-9]*" round-str))]
        (nth (categories-for-round round game-id) (dec order))))))

(defn pos-for [element]
  (if-let [pos-str (re-find #"(?<=J_[0-9]_).*(?=_stuck)" 
                     ((element :attrs) :onclick))]
    (Integer. pos-str)
    0))

(defn id-for [element]
  (when-let [link (first (html/select element [:.clue_order_number :a]))]
       (re-find #"(?<=clue_id=).*" ((link :attrs) :href))))

(defn daily-double? [element]
  (boolean (seq (html/select element [:.clue_value_daily_double]))))

(defn parse-clue [element game-id]
  { :question (question-for element)
    :answer (answer-for element)
    :category (category-for element game-id)
    :pos (pos-for element)
    :id (id-for element)
    :daily-double? (daily-double? element)})

(defn clues [id]
  (map #(parse-clue %1 id) (concat
                             (html/select (fetch-game id) [:.clue :div])
                             (html/select (fetch-game id) [:.final_round :div]))))
