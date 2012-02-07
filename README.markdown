# This is... clojeopardy!

Clojeopardy is an api-like screen scraper for the thoroughly awesome [J! Archives](http://j-archive.com/) written in Clojure.

## Usage

```clj
(require '[clojeopardy.core :as jeopardy])

; lists all clues for game 3446
(jeopardy/clues 3446)

; lists all categories for game 3446
(jeopardy/categories 3446)
```

A few notes on usage:

* This library is best used to pull down whatever information you need from the games in question once and store locally. Building a live API off the J! Archive would be rude and would probably explode your app over time as HTML responses get memoized.
* A few features are currently lacking but are planned: Clues aren't linked to categories, listing games is impossible, categories come without any particular id associated to them.
* A few features are unplanned but may happen if someone asks nicely: Game metadata (date/time, etc), contestant info, response info (although you'd have to ask *very* nicely for that)

## Thanks

This wouldn't happen without the hard, manual labor of the dedicated fans at the [J! Archive](http://j-archive.com/). They are nice, and so we should be nice to them.

Thanks also to Jeopardy Productions, Inc, for making the show.

## License

Licenses for basically any kind of non-commercial use. COMMERCIAL USE STRICTLY PROHIBITED. Don't ruin this for everyone, please.

There is no connection real or implied between this project and either the J! Archive or JEOPARDY!, Jeopardy Productions, Inc., or their parent company. "JEOPARDY!" and "America's Favorite Quiz Show" are registered trademarks of Jeopardy Productions, Inc. All Rights Reserved.
