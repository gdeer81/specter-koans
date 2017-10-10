(ns koans.02-unparameterized-navigators
  (:require [koan-engine.core :refer :all]
            [com.rpl.specter :refer :all]))

(meditations
  "The naming convention for Unparameterized navigators is that they are in all caps..."
   (= __  (do " you understand?" "YES"))

  "AFTER-ELEM navigates to the void element after the sequence. For transformations if the result is not NONE then append the value"
   (= __ (setval AFTER-ELEM "Mary" ["Peter" "Paul"])
         (setval AFTER-ELEM "Mary" '("Peter" "Paul")))

 "Nathan Says: AFTER-ELEM is only intended for transformations
  it doesn't make sense in the context of a set
  to add a single element to a set most efficiently use NONE-ELEM
  likewise, it doesn't make sense in the context of a map"
 (= __ (= #{"Peter" "Paul" "Mary"} (setval AFTER-ELEM "Mary" #{"Peter" "Paul" "Mary"}))
       (= {:name "Peter" :friend "Mary"} (setval AFTER-ELEM {:friend "Mary"} {:name "Peter"})))

 "ALL navigates to every element in a collection."
 (= __ (select ALL [0 1 2 3]))

 "ALL composes with predicate functions"
 (= __ (select [ALL even? #(> % 0)] [0 1 2 3 4]))

 "If the collection is a map, ALL will navigate to each key-value pair [key value]"
 (= __ (select ALL {:a :b, :c :d}))

 "This makes sense when doing a transformation"
 (= __ (transform ALL reverse {"Rich" :name "Clojure" :language-created}))

 "ALL can transform to NONE to remove elements"
 (= __ (setval [ALL nil?] NONE [1 nil 2 nil 3 nil])
       (setval [ALL #(> % 100)] NONE [1 101 2 200 3 300])
       (setval [ALL #(= "marker-node" (:type %))] NONE [1 {:type "marker-node"} 2 {:type "marker-node"} 3 {:type "marker-node"}]))

 "ATOM navigates to the value of an atom which is nice for transformations"
 (= __ (let [a (atom 41)]
        (deref (transform ATOM inc a))))

 "BEFORE-ELEM navigates to the 'void' element before the sequence. For transformations – if result is not NONE, then prepend that value"
 (= __ (setval BEFORE-ELEM :i [:before :e]))

 "BEGINNING navigates to the empty subsequence before the beginning of a collection. Useful with setval to add values onto the beginning of a sequence."
 (= __ (setval BEGINNING (range 3) (range 3 11)))

 "As of Specter 1.0.0, BEGINNING can now work with strings. It navigates to or transforms substrings"
 (= __ (setval BEGINNING "un" "believable"))

 "END navigates to the empty subsequence after the end of a collection. Useful with setval to add values onto the end of a sequence."
 (= __ (setval END (range 3 11) (range 3)))

 "As of Specter 1.0.0, END can now work with strings. It navigates to or transforms substrings"
 (= __ (setval END "believable" "un"))

 "DISPENSE Drops all collected values for subsequent navigation, so first lets see a transform without it..."
 (= __ (transform [ALL VAL] str (range 4)))

 "...now lets see that same transform with DISPENSE thrown in..."
 (= __ (transform [ALL VAL DISPENSE] str (range 4)))

 "FIRST navigates to the first element of a collection. If the collection is a map, returns a key-value pair [key value]. If the collection is empty, navigation stops."
 (= __ (select-one FIRST [:peanuts :the-butter :the-jelly])
       (select-one FIRST (select-one FIRST (sorted-map :peanuts true
                                                       :the-butter true
                                                       :the-jelly true
                                                       :some-banana false
                                                       :some-cheese "wtf really?"))))

 "FIRST returns nil on an empty collection"
 (= __ (select-one FIRST []))

 "FIRST can transform to NONE to remove elements"
 (= __ (setval FIRST NONE ["junk data" 1 2 3]))

 "As of Specter 1.0.0, FIRST can now work with strings. It navigates to characters..."
 (= __ (select-any FIRST "Clojure"))

 "...or transform characters"
 (= __ (setval FIRST \C "Klojure"))

 "INDEXED-VALS navigates to [index elem] pairs for each element in a sequence.
  Transforms of index move element at that index to the new index,
  shifting other elements in the sequence.
  Indices seen during transform take into account any shifting from prior sequence elements changing indices"
 (= __
    (select [INDEXED-VALS]
            [1 2 3 4 5]))

 "Here is an example of using INDEXED-VALS with a transform"
 (= __ (setval [INDEXED-VALS FIRST] 0 [1 2 3 4 5]))

 "Another example of using INDEXED-VALS with a transform"
 (= __ (setval [INDEXED-VALS FIRST] 1 [1 2 3 4 5]))

 "LAST navigates to the last element of a collection. "
 (= __ (select-one LAST (range 5)))

 "If the collection is a map, LAST returns a key-value pair [key value]. "
 (= __ (select-one LAST (sorted-map 0 :a 1 :b)))

 "With LAST, if the collection is empty, navigation stops."
 (= __ (select-one LAST []))

 "LAST can transform to NONE to remove elements."
 (= __ (setval LAST NONE [:a :b :c :d :e]))

 "As of Specter 1.0.0, LAST can now work with strings.
  It navigates to..."
 (= __ (select-any LAST "abc"))

 "...or transforms characters."
 (= __ (setval LAST "see" "abc"))

 "MAP-KEYS navigates to every key in a map.
  MAP-KEYS is more efficient than [ALL FIRST]."
 (= __ (select [MAP-KEYS] {:a 3 :b 4}))

 "MAP-VALS navigates to every value in a map.
  MAP-VALS is more efficient than [ALL LAST]."
 (= __ (select MAP-VALS {:a 3, :c 4}))

 "MAP-VALS can transform to NONE to remove elements"
 (= __ (setval [MAP-VALS even?] NONE {:a 1 :b 2 :c 3 :d 4}))

 "Double MAP-VALS for nested maps"
 (= __ (select [MAP-VALS MAP-VALS] {:a {:b :c}, :d {:e :f}}))

 "META navigates to the metadata of the structure,
  or nil if the structure has no metadata or may not contain metadata."
 (= __ (select-one META (with-meta {:a 0} {:meta :data})))

 "You can even transform meta data with META if that's what you're into"
 (= __ (meta (transform META #(assoc % :meta :datum)
                           (with-meta {:a 0} {:meta :data}))))

 "NAME navigates to the name of a keyword or symbol."
 (= __ (select [NAME] :key))

 "NAME can be used with MAP-KEYS to get the names of keys in a map"
 (= __ (select [MAP-KEYS NAME] {:a 3 :b 4 :c 5}))

 "using NAME with setval will let you set the name to whatever you want"
 (= __ (setval [MAP-KEYS NAME] "q" {'a/b 3 'bbb/c 4 'd 5}))

 "NAMESPACE navigates to the namespace of keywords or symbols."
 (= __ (select [ALL NAMESPACE] [::test ::fun]))

 "NAMESPACE will return nil on unqualified keywords "
 (= __ (select [ALL NAMESPACE] [::test :fun]))

 "NAMESPACE will let you set the namespace of a keyword as well"
 (= __ (setval [ALL NAMESPACE] "a" [::test :fun]))

 "NIL->LIST navigates to the empty list '() if the value is nil. Otherwise it stays at the current value."
 (= __ (select-one NIL->LIST nil))

 "NIL->SET navigates to the empty set #{} if the value is nil. Otherwise it stays at the current value."
 (= __ (select-one NIL->SET nil))

 "NIL->VECTOR navigates to the empty vector [] if the value is nil. Otherwise it stays at the current value."

 (= __ (select-one NIL->VECTOR nil))

 "NIL->LIST, NIL->SET, and NIL->VECTOR all stay on the current value if it isn't nil"
 (= __ (select-one NIL->LIST :some-val-other-than-nil)
       (select-one NIL->SET :some-val-other-than-nil)
       (select-one NIL->VECTOR :some-val-other-than-nil))

 "NONE-ELEM navigates to the 'void' elem in a set. For transformations - if the result is not NONE, then add that value to the set."
 (= __ (setval NONE-ELEM 3 #{1 2}))

 "for transformations NON-ELEM also gives back a set with that value added if you pass in nil set"
 (= __ (setval NONE-ELEM 1 nil))

 "STAY stays in place. It is the no-op navigator."
 (= __ (select-one STAY :dest-key))

 "STOP stops navigation. "
 (= __ (select-one STOP :foo))

 "STOP returns the expected colletion type but it might just be empty"
 (= __ (select [ALL STOP] (range 5)))

 "with STOP for transformation, it returns the structure unchanged."
 (= __ (transform [ALL STOP] inc (range 5)))

 "VAL collects the current structure. See also collect, collect-one, and putval"
 (= __ (select [VAL ALL] (range 3)))

 "Collected values are passed as initial arguments to the update fn."
 (= __ (transform [VAL ALL] (fn [coll x] (+ x (count coll))) (range 5))))
