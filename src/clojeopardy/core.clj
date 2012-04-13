(ns clojeopardy.core
  (:require [net.cgrand.enlive-html :as html]))

(defn build-url
  "Given a game id, returns the proper URL to retrieve it"
  [id]
  (java.net.URL. (str "http://www.j-archive.com/showgame.php?game_id=" id)))

(defn fetch-game-live
  "Given a game id, returns all HTML in that game's page"
  [id]
  (html/html-resource (build-url id)))

(def fetch-game (memoize fetch-game-live))

(defn- name-for
  "Returns the category name of a particular category element"
  [element]
  (html/text (first (html/select element [:.category_name]))))

(defn- comments-for
  "Returns any of Alex's comments for a particular category element"
  [element]
  (html/text (first (html/select element [:.category_comments]))))

(defn- parse-category
  "Transforms a category element in position idx in a round into a map containing :name, :comments, :round, and :pos"
  [element idx round]
  { :name (name-for element)
    :comments (comments-for element)
    :round round
    :pos (inc idx) })

(def css-class-for-round
  {:single :#jeopardy_round
   :double :#double_jeopardy_round
   :final :#final_jeopardy_round})

(defn- categories-for-round
  "For a particular round and game id, returns all categories within the round"
  [round id]
  (map-indexed #(parse-category %2 %1 round)  (html/select (fetch-game id) [(css-class-for-round round) :.category])))

(defn categories
  "Given a particular game, returns all categories in that game as a collection of maps"
  [id]
  (flatten
    (for [round [:single :double :final]]
      (categories-for-round round id))))

(defn- question-for
  "Returns the correct question for a particular clue element"
  [element]
  (re-find #"(?<=correct_response\">).*(?=</em>)"
           ((element :attrs) :onmouseover)))

(defn- answer-for
  "Returns the answer for a particular clue element"
  [element]
  (re-find #"(?<=_stuck', ').*(?='\))"
           ((element :attrs) :onmouseout)))

(def round-for-shorthand {"J" :single "DJ" :double "FJ" :final})

(defn- category-for
  "Returns the category map for the clue element in a particular game."
  [element game-id]
  (let [round-str (re-find #"(?<= 'clue_).*(?=_stuck)"
                         ((element :attrs) :onmouseout))]
    (if (= "FJ" round-str)
      (first (categories-for-round :final game-id))
      (let [round-shorthand (re-find #"^[^_]*(?=_.)" round-str)
            round (round-for-shorthand round-shorthand)
            order (Integer. (re-find #"(?<=J_)[0-9]*" round-str))]
        (nth (categories-for-round round game-id) (dec order))))))

(defn- pos-for
  "Returns a clue element's position in the sequence of a category's clues. Final Jeopardy clues will have no position."
  [element]
  (if-let [pos-str (re-find #"(?<=J_[0-9]_).*(?=_stuck)"
                     ((element :attrs) :onclick))]
    (Integer. pos-str)
    0))

(defn- id-for
  "Returns the id of a clue element. Final Jeopardy clues will have no id"
  [element]
  (when-let [link (first (html/select element [:.clue_order_number :a]))]
       (re-find #"(?<=clue_id=).*" ((link :attrs) :href))))

(defn- daily-double?
  "Returns true or false to indicate whether a clue element is a daily double"
  [element]
  (boolean (seq (html/select element [:.clue_value_daily_double]))))

(defn- parse-clue
  "Given a div element containing a clue and a game-id, transforms that element into a map containing :question, :answer, :pos, :id, :daily-double?, and :category (this last element itself a map - see parse-category). Note that :id will not be available for Final Jeopardy clues"
  [element game-id]
  { :question (question-for element)
    :answer (answer-for element)
    :category (category-for element game-id)
    :pos (pos-for element)
    :id (id-for element)
    :daily-double? (daily-double? element)})

(defn clues
  "Returns all answer/question pairs in a particular game as a collection of maps"
  [id]
  (map #(parse-clue %1 id) (concat
                             (html/select (fetch-game id) [:.clue :div])
                             (html/select (fetch-game id) [:.final_round :div]))))
