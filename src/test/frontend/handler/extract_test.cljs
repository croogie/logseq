(ns frontend.handler.extract-test
  (:require [cljs.test :refer [deftest is are testing use-fixtures run-tests]]
            [cljs-run-test :refer [run-test]]
            [frontend.util :as util]
            [frontend.handler.extract :as extract]))

(defn- extract
  [text]
  (let [result (last (extract/extract-blocks-pages "repo" "a.md" text))
        lefts (map (juxt :block/parent :block/left) result)]
    (if (not= (count lefts) (count (distinct lefts)))
      (do
        (util/pprint (map (fn [x] (select-keys x [:block/uuid :block/level :block/content :block/left])) result))
        (throw (js/Error. ":block/parent && :block/left conflicts")))
      (mapv (juxt :block/level :block/content) result))))

(deftest test-extract-blocks-pages
  []
  (are [x y] (= (extract x) y)
    "- a
  - b
    - c"
    [[1 "a"] [2 "b"] [3 "c"]]

    "## hello
    - world
      - nice
        - nice
      - bingo
      - world"
    [[1 "## hello"] [2 "world"] [3 "nice"] [4 "nice"] [3 "bingo"] [3 "world"]]

    "# a
## b
### c
#### d
### e
- f
  - g
    - h
  - i
- j"
    [[1 "# a"]
     [1 "## b"]
     [1 "### c"]
     [1 "#### d"]
     [1 "### e"]
     [1 "f"]
     [2 "g"]
     [3 "h"]
     [2 "i"]
     [1 "j"]]))

(deftest test-regression-1902
  []
  (are [x y] (= (extract x) y)
    "- line1
    - line2
      - line3
     - line4"
    [[1 "line1"] [2 "line2"] [3 "line3"] [3 "line4"]]))

#_(run-tests)
