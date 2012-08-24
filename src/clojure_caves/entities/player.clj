(ns clojure-caves.entities.player
    (:use [clojure-caves.entities.core :only [Entity]]
          [clojure-caves.world :only [find-empty-tile get-tile-kind]]))

(defrecord Player [id glyph location])

(extend-type Player Entity
    (tick [this world]
          world))

(defn make-player [world]
  (->Player :player "@" (find-empty-tile world)))


