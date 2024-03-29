(ns clojure-caves.world)

; Constants ------------------------
(def world-size [160 50])

; Data structures ------------------
(defrecord World [tiles])
(defrecord Tile [kind glyph color])

(def tiles
  {:floor (->Tile :floor "." :white)
   :wall  (->Tile :wall  "#" :white)
   :bound (->Tile :bound "X" :black)})

; Convenience functions -------------
(defn get-tile-from-tiles [tiles [x y]]
    (get-in tiles [y x] (:bound tiles)))

(defn random-coordinate []
    (let [[cols rows] world-size]
          [(rand-int cols) (rand-int rows)]))

(defn print-row [row]
  (println (apply str (map :glyph row))))

(defn print-world [world]
  (dorun (map print-row (:tiles world))))


; World generation ----------------------
(defn random-tiles []
  (let [[cols rows] world-size]
    (letfn [(random-tile []
              (tiles (rand-nth [:floor :wall])))
            (random-row []
              (vec (repeatedly cols random-tile)))]
      (vec (repeatedly rows random-row)))))

(defn get-smoothed-tile [block]
  (let [tile-counts (frequencies (map :kind block))
        floor-threshold 5
        floor-count (get tile-counts :floor 0)
        result (if (>= floor-count floor-threshold)
                 :floor
                 :wall)]
    (tiles result)))

(defn block-coords [x y]
  (for [dx [-1 0 1]
        dy [-1 0 1]]
    [(+ x dx) (+ y dy)]))

(defn get-block [tiles x y]
    (map (partial get-tile-from-tiles tiles)
                (block-coords x y)))

(defn get-smoothed-row [tiles y]
  (mapv (fn [x]
          (get-smoothed-tile (get-block tiles x y)))
        (range (count (first tiles)))))

(defn get-smoothed-tiles [tiles]
  (mapv (fn [y]
          (get-smoothed-row tiles y))
        (range (count tiles))))

(defn smooth-world [{:keys [tiles] :as world}]
  (assoc world :tiles (get-smoothed-tiles tiles)))

(defn random-world []
  (let [world (->World (random-tiles))
        world (nth (iterate smooth-world world) 0)]
    world))

(defn get-tile [world coord]
    (get-tile-from-tiles (:tiles world) coord))

(defn get-tile-kind [world coord]
    (:kind (get-tile world coord)))

(defn find-empty-tile [world]
  (loop [coord (random-coordinate)]
    (if (#{:floor} (get-tile-kind world coord))
      coord
      (recur (random-coordinate)))))



