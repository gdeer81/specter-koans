(ns koans.01-selection
  (:require [koan-engine.core :refer :all]
            [com.rpl.specter :refer :all]))

(meditations
  "We shall contemplate selection by exploring navigation, first ALL"
  (= __ (= [1 2 3] (select [ALL] [1 2 3])))

  "To understand selection you must understand navigation"
  (= __ (count (select [ALL odd?] [1 2 3])))

  "You can navigate collections with many nesting levels"
  (let [multi-nested-collection
          {:first-level
           {:second-level
             {:third-level
              {:fourth-level
               {:fifth-level [1 2 3 4 5 6 7 8 9 10]}}}}}]
       (= __ (count (select
                     [MAP-VALS MAP-VALS MAP-VALS MAP-VALS MAP-VALS ALL odd?]
                     multi-nested-collection))))

  "Some things may look familiar. getting a value in a nested map is in core."
  (let [nested-collection {:address {:state "TX"}}]
    (= __ (= "TX" (get-in nested-collection [:address :state])
                  (select-first [:address :state] nested-collection)
                  (select-first [MAP-VALS MAP-VALS] nested-collection))))

 "Remember that select returns a vector even if there is only one value"
 (let [test-collection [[[1 2]] 2 3]]
    (= __ (= (first (first (first test-collection)))
             (select [FIRST FIRST FIRST] test-collection))))

 "Unparameterized Navigators are in all caps and can be composed. some are used in selections, but not all. try not to get them confused"
 (= __ (not= (select [BEGINNING] [1 [2 [3 [4]]] 5 6])
             (select [FIRST]     [1 [2 [3 [4]]] 5 6])))

 "FIRST is obvious on collections except for on a map"
 (= __ (= [[0 :a]] (select FIRST (sorted-map 0 :a 1 :b))))

 "You will also run into the same thing with LAST on a map"
 (= __ (= [1 :b] (select LAST (sorted-map 0 :a 1 :b))))

 "To understand selection we must also know what selection functions we have available"
 (= __ (select-one [MAP-VALS] {:greeting "hello"})
       (select-first [MAP-VALS] {:greeting "hello"}))

 "select-one is dangerous because it will error out if given a collection of multiple values.
 select-any on the other hand is the most chill and the fastest of them all"
 (= __ (select-any [MAP-VALS MAP-VALS ALL :first-name] {:people {:the-bobs [{:first-name "BOB" :last-name "ROSS"} {:first-name "BOB" :last-name "HOPE"}]}}))

 "select-one will also returns nil if it is given an empty collection"
 (= __ (select-one [ALL] []))

 "Speaking of nil, what do you do if you navigate to a nil value? there are a handful of NIL-><collection type> so you'll just get an empty collection back
 otherwise it will just return the value it landed on"
 (= __ (select-any [MAP-VALS MAP-VALS LAST NIL->VECTOR] {:people {:ids [1 2 3]}})))
