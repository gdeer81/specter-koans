(ns koans.03-parameterized-navigators-and-functions
  (:require [koan-engine.core :refer :all]
            [com.rpl.specter :refer :all]))

(meditations
 "The naming convention for parameterized navigators is that they follow function naming conventions..."
 (= __  (do " you understand?" "yes"))

 "before-index
  (before-index index)
  Navigates to the empty space between the index and the prior index. "
 (= __ (setval (before-index 0) :a [1 2 3]))

 "before-index Selects navigate to :com.rpl.specter.impl/NONE."
 (= __ (select-any (before-index 0) [1 2 3])))

"before-index Transforms to NONE don't insert at the index position."
(= __ (setval (before-index 1) NONE [1 2 3]))

"before-index Transforms to non-NONE insert at the index position example 1."
(= __ (setval (before-index 1) :a [1 2 3]))

"before-index Transforms to non-NONE insert at the index position example 2."
(= __ (setval (before-index 3) :a [1 2 3]))

"codewalker
 (codewalker afn)
 Using clojure.walk, codewalker executes a depth-first search for nodes where afn returns a truthy value.
 When afn returns a truthy value, codewalker stops searching that branch of the tree
 and continues its search of the rest of the data structure.
 codewalker preserves the metadata of any forms traversed.
 See also walker."
(= __ (select (codewalker #(and (map? %) (even? (:a %))))
            (list (with-meta {:a 2} {:foo :bar}) (with-meta {:a 1} {:foo :baz}))))

"collect
 (collect & paths)
 collect adds the result of running select with the given path on the current
 value to the collected vals.
 Note that collect, like select, returns a vector containing its results.
 If transform is called,
 each collected value will be passed as an argument to the transforming function
 with the resulting value as the last argument.
 See also VAL, collect-one, and putval"

(= __ (select-one [(collect ALL) FIRST] (range 3)))

"select ALL of a (collect ALL)"
(= __ (select [(collect ALL) ALL] (range 3)))

"select ALL of collecting ALL of collecting ALL"
(= __
  (select [(collect ALL) (collect ALL) ALL] (range 3)))

"Add the sum of the evens to the first element of the seq using (collect ALL even?)"
(= __
  (transform [(collect ALL even?) FIRST]
             (fn [evens first] (reduce + first evens))
             (range 5)))

"Replace the first element of the seq with the entire seq"
(= __ (transform [(collect ALL) FIRST] (fn [all _] all) (range 3)))

"collect-one
 (collect-one & paths)
 collect-one adds the result of running select-one with the given path on the
 current value to the collected vals.
 Note that collect-one, like select-one, returns a single result.
 If there is more than one result, an exception will be thrown.
 If transform is called, each collected value will be passed as an argument to
 the transforming function with the resulting value as the last argument.
 See also VAL, collect, and putval"
(= __ (select-one [(collect-one FIRST) LAST] (range 5)))

"collect-one of the first values from ALL the range values"
(= __ (select [(collect-one FIRST) ALL] (range 3)))

"transform the value of :a by collecting the value of :b and adding it to the value of :a"
(= __ (transform [(collect-one :b) :a] + {:a 2 :b 3}))

"transform the value of :a by collecting :b (5) and :c (7) and multiplying by the value of :a (3 x 5 x 7)"
(= __ (transform [(collect-one :b) (collect-one :c) :a] * {:a 3, :b 5, :c 7}))

"comp-paths
 (comp-paths & path)
 Returns a compiled version of the given path for use with compiled-{select/transform/setval/etc.} functions."
(= __ (let [my-path (comp-paths :a :b :c)]
       (compiled-select-one my-path {:a {:b {:c 0}}})))

"compiled-*
These functions operate in the same way as their uncompiled counterparts,
but they require their path to be precompiled with comp-paths. also more performant"
(= __ (let [my-path (comp-paths :outer :inner :destination)]
       (compiled-select-one my-path {:outer {:inner {:destination 42}}}))

 "cond-path
 (cond-path & conds)
 Takes as arguments alternating cond-path1 path1 cond-path2 path2...
 Tests if selecting with cond-path on the current structure returns anything.
 If so, it navigates to the corresponding path.
 Otherwise, it tries the next cond-path.
 If nothing matches, then the structure is not selected.
 The input paths may be parameterized, in which case the result of cond-path will
 be parameterized in the order of which the parameterized navigators were declared.
 See also if-path"
 (= __ (select [ALL (cond-path (must :a) :a (must :b) :c)] [{:a 0} {:b 1 :c 2}]))

 "cond-path empty list"
 (= __ (select [(cond-path (must :a) :b)] {:b 1}))

 "continue-then-stay
 (continue-then-stay & path)
 Navigates to the provided path and then to the current element.
 This can be used to implement post-order traversal.
 See also stay-then-continue."
 (= __ (select (continue-then-stay MAP-VALS) {:a 0 :b 1 :c 2}))

 "continuous-subseqs
 (continuous-subseqs pred)
 Navigates to every continuous subsequence of elements matching pred."
 (= __ (select (continuous-subseqs #(< % 10)) [5 6 11 11 3 12 2 5]))

 "if predicate doesn't match you get nothing"
 (= __  (select (continuous-subseqs #(< % 10)) [12 13]))

 "with setval continuous-subseqs sets those subseqs to whatever you pass in"
 (= __ (setval (continuous-subseqs #(< % 10)) [] [3 2 5 11 12 5 20]))

 "eachnav
  (eachnav navigator)
  Turns a navigator that takes one argument into a navigator that takes
  many arguments and uses the same navigator with each argument.
  There is no performance cost to using this.
  keypath, must, and nthpath are all implemented using eachnav,
  making multiple arguments possible.
  See their documentation or look at their implementation in Specter core."
 (= __ (do "Did you look at the implementation" "yes"))

 "filterer
  (filterer & path)
  Navigates to a view of the current sequence that only contains elements that match the given path.
  An element matches the selector path if calling select on that element with
  the path yields anything other than an empty sequence.
  Returns a vector when used in a select.
  The input path may be parameterized, in which case the result of filterer will
  be parameterized in the order of which the parameterized selectors were declared.
  Note that filterer is a function which returns a navigator.
  It is the arguments to filterer that can be late-bound parameterized, not filterer.
  See also subselect."
 (= __ (select-one (filterer identity) ['() [] #{} {} "" true false nil]))

 "for filterer note that clojure functions have been extended to implement the navigator protocol"
 (= __ (select-one (filterer even?) (range 10)))

 "if-path
   (if-path cond-path then-path) (if-path cond-path then-path else-path)
   Like cond-path, but with if semantics.
   If no else path is supplied and cond-path is not satisfied, stops navigation."
 (= __ (select (if-path (must :d) :a) {:a 0, :d 1}))

 "if-path example with else condition"
 (= __ (select (if-path (must :d) :a :b) {:a 0, :b 1}))

 "if-path example with no else condition"
 (= __  (select (if-path (must :d) :a) {:b 0, :d 1})
        (select (if-path (must :d) :a STOP) {:b 0, :d 1}))

 "index-nav
  Navigates to the index of the sequence if within 0 and size.
  Transforms move element at that index to the new index, shifting other elements in the sequence.
  (index-nav index)"
 (= __ (select [(index-nav 0)] [1 2 3 4 5]))

 "selecting index-nav out of bounds returns empty vector"
 (= __ (select [(index-nav 7)] [1 2 3 4 5]))

 "transforming with index-nav, set value at index 2 (3) to index 0 so i[0] shifts to i[1] and i[1] shifts to i[2]"
 (= __ (setval (index-nav 2) 0 [1 2 3 4 5]))

 "keypath
  (keypath & keys)
  Navigates to the specified key, navigating to nil if it does not exist.
  Note that this is different from stopping navigation if the key does not exist.
  If you want to stop navigation, use must."
 (= 0 (select-one (keypath :a) {:a 0}))

 "using keypath like get-in"
 (= 1 (select-one (keypath :a :b) {:a {:b 1}}))

 "using keypath when a map is missing the key"
 (= [0 nil] (select [ALL (keypath :a)] [{:a 0} {:b 1}]))

 "keypath does not stop navigation"
 (= [0 :boo] (select [ALL (keypath :a) (nil->val :boo)] [{:a 0} {:b 1}]))

 "keypath can now take multiple arguments, for concisely specifying multiple steps.
  It navigates to each key one after another. Here is a one-step..."
 (= {"in" 3} (select-one (keypath "out") {"out" {"in" 3}}))

 "and here is keypath with multiple arguments"
 (= 3 (select-one (keypath "out" "in") {"out" {"in" 3}}))

 "keypath can transform to NONE to remove elements"
 (= {:b 4} (setval [(keypath :a)] NONE {:a 3 :b 4}))

 "map-key
  (map-key key)
  Navigates to the given key in the map (not to the value)."
 (= [:a] (select [(map-key :a)] {:a 2 :b 3}))

 "using setval with map-key is confusing since the val is the key so setval with
  map-key should be thought of as setkey, example, the key :c will replace :a"
 (= {:b 3, :c 2} (setval [(map-key :a)] :c {:a 2 :b 3}))

 "map-key navigates only if the key currently exists in the map."
 (= []  (select [(map-key :z)] {:a 2 :b 3}))

 "Can transform to NONE to remove the key/value pair from the map."
 (= {:b 3} (setval [(map-key :a)] NONE {:a 2 :b 3}))

 "multi-path
  (multi-path & paths)
  A path that branches on multiple paths.
  For transforms, applies updates to the paths in order."
 (= '(0 1) (select (multi-path :a :b) {:a 0, :b 1, :c 2}))

 "select with multiple filterer paths"
 (= '([1 3 5 7 9] [0 2 4 6 8]) (select (multi-path (filterer odd?) (filterer even?)) (range 10)))

 "multi-path with transform example"
 (= {:a -1 :b 0 :c 2} (transform (multi-path :a :b) (fn [x] (dec x)) {:a 0, :b 1, :c 2}))

 "must
  (must & keys)
  Navigates to the key only if it exists in the map.
  Note that must stops navigation if the key does not exist.
  If you do not want to stop navigation, use keypath."
 (= 0 (select-one (must :a) {:a 0}))

 "must stops navigation when the key doesn't exist"
 (= nil (select-one (must :a) {:b 1}))

 "must can now take multiple arguments, for concisely specifying multiple steps. It navigates to each key, one after another."
 (= 2 (select-any (must :a :b) {:a {:b 2} :c 3}))

 "must can transform to NONE to remove elements."
 (= {:b 2} (setval (must :a) NONE {:a 1 :b 2}))

 "nil->val
   (nil->val v)
   Navigates to the provided val if the structure is nil. Otherwise it stays navigated at the structure."
 (= :a (select-one (nil->val :a) nil))

 "example where the structure is not nil"
 (= :b (select-one (nil->val :a) :b))

 "nthpath
  (nthpath & indices)
  Navigate to the specified indices (one after another). "
 (= [3] (select [(nthpath 2)] [1 2 3]))

 "Transform to NONE to remove the element from the sequence."
 (= [1 2] (setval [(nthpath 2)] NONE [1 2 3]))

 "nthpath can now take multiple arguments, for concisely specifying multiple steps.
  It navigates to each index, one after another."
 (= [0] (select [(nthpath 0 0)] [[0 1 2] 2 3]))

 "parser
   (parser parse-fn unparse-fn)
   Navigate to the result of running parse-fn on the value.
   For transforms, the transformed value then has unparse-fn
   run on it to get the final value at this point."
 (let [parse (fn [address] (clojure.string/split address #"@"))
       unparse (fn [address] (clojure.string/join "@" address))]
    (= [["test" "gmail.com"]]
       (select [ALL (parser parse unparse) #(= "gmail.com" (second %))]
               ["test@example.com" "test@gmail.com"])))

 "parser with a transform"
 (let [parse (fn [address] (clojure.string/split address #"@"))
       unparse (fn [address] (clojure.string/join "@" address))]
    (= ["test@example.com" "test+spam@gmail.com"]
       (transform [ALL (parser parse unparse) #(= "gmail.com" (second %))]
                  (fn [[name domain]] [(str name "+spam") domain])
                  ["test@example.com" "test@gmail.com"])))

 "pred
   (pred apred)
   Keeps the element only if it matches the supplied predicate.
   This is the late-bound parameterized version of using a function directly in a path.
   See also must."
 (= [0 2 4 6 8] (select [ALL (pred even?)] (range 10)))

 "pred=
   (pred= value)
   Keeps elements only if they equal the provided value."
 (= [2 2] (select [ALL (pred= 2)] [1 2 2 3 4 0]))

 "pred<
   (pred< value)
   Keeps elements only if they are less than the provided value."
 (= [1 2 2 0] (select [ALL (pred< 3)] [1 2 2 3 4 0]))

 "pred>
   (pred> value)
   Keeps elements only if they are greater than the provided value."
 (=  [4] (select [ALL (pred> 3)] [1 2 2 3 4 0]))

 "pred<=
   (pred<= value)
   Keeps elements only if they are less than or equal to the provided value."
 (= [1 2 2 3 0] (select [ALL (pred<= 3)] [1 2 2 3 4 0]))

 "pred>=
   (pred>= value)
   Keeps elements only if they are greater than or equal to the provided value."
 (= [3 4] (select [ALL (pred>= 3)] [1 2 2 3 4 0]))

 "putval
   (putval val)
   Adds an external value to the collected vals.
   Useful when additional arguments are required to the transform function that
   would otherwise require partial application or a wrapper function.
   example incrementing val at path [:a :b] by 3:"
 (= {:a {:b 3}} (transform [:a :b (putval 3)] + {:a {:b 0}}))

 "not-selected?
    (not-selected? & path)
    Stops navigation if the path navigator finds a result.
    Otherwise continues with the current structure.
    The input path may be parameterized, in which case the result of selected?
    will be parameterized in the order of which the parameterized navigators were declared.
    See also selected?."
 (= [1 3 5 7 9] (select [ALL (not-selected? even?)] (range 10)))

 "selection must be :a but must not have been selected with the even? predicate"
 (= [{:a 1} {:a 3}] (select [ALL (not-selected? [(must :a) even?])] [{:a 0} {:a 1} {:a 2} {:a 3}]))

 "Path returns [0 2], so navigation stops"
 (= nil (select-one (not-selected? [ALL (must :a) even?]) [{:a 0} {:a 1} {:a 2} {:a 3}]))

 "selected?
    (selected? & path)
    Stops navigation if the path navigator fails to find a result.
    Otherwise continues with the current structure.
    The input path may be parameterized, in which case the result of selected?
    will be parameterized in the order of which the parameterized navigators were declared.
    See also not-selected?."
 (= [0 2 4 6 8] (select [ALL (selected? even?)] (range 10)))

 "selection must be :a and have been selected with the even? predicate"
 (= [{:a 0} {:a 2}] (select [ALL (selected? [(must :a) even?])] [{:a 0} {:a 1} {:a 2} {:a 3}]))

 "Path returns [0 2], so selected? returns the entire structure"
 (= [{:a 0} {:a 1} {:a 2} {:a 3}] (select-one (selected? [ALL (must :a) even?]) [{:a 0} {:a 1} {:a 2} {:a 3}]))

 "set-elem
    (set-elem element)
    Navigates to the given element in the set only if it exists in the set.
    Can transform to NONE to remove the element from the set."
 (= [3] (select [(set-elem 3)] #{3 4 5}))

 "set-elem won't navigate to the element since it doesn't exist"
 (= [] (select [(set-elem 3)] #{4 5}))

 "use setval with NONE to remove an element"
 (= #{4 5} (setval [(set-elem 3)] NONE #{3 4 5}))

 "srange
     (srange start end)
     Navigates to the subsequence bound by the indexes
     start (inclusive) and end (exclusive)
     Will throw IndexOutOfBoundsException
     See also srange-dynamic."
 (= [2 3] (select-one (srange 2 4) (range 5)))

 "use setval with srange and empty collection to remove values"
 (= '(0 1 4) (setval (srange 2 4) [] (range 5)))

 "As of Specter 1.0.0, srange can now work with strings. It navigates to or transforms substrings."
 (= "bc" (select-any (srange 1 3) "abcd"))

 "srange with setval to remove from a string"
 (= "ad" (setval (srange 1 3) "" "abcd"))

 "use srange to add a letter to a string"
 (= "abcxd" (setval [(srange 1 3) END] "x" "abcd"))

 "srange-dynamic
     (srange-dynamic start-fn end-fn)
     Uses start-fn and end-fn to determine the bounds of the subsequence to select when navigating.
     Each function takes in the structure as input.
     See also srange."
 (= [2 3] (select-one (srange-dynamic #(.indexOf % 2) #(.indexOf % 4)) (range 5)))

 "using srange-dynamic with start-fn that always returns 0 and end-fn returns half the count"
 (= [0 1 2 3 4] (select-one (srange-dynamic (fn [_] 0) #(quot (count %) 2)) (range 10)))

 "stay-then-continue
      (stay-then-continue)
      Navigates to the current element and then navigates via the provided path. This can be used to implement pre-order traversal.
      See also continue-then-stay."
 (= '({:a 0, :b 1, :c 2} 0 1 2) (select (stay-then-continue MAP-VALS) {:a 0 :b 1 :c 2}))

 "submap
      (submap m-keys)
      Navigates to the specified submap (using select-keys)"
 (= {:a 0, :b 1} (select-one (submap [:a :b]) {:a 0, :b 1, :c 2}))

 "example submap when key doesn't exist"
 (= {} (select-one (submap [:c]) {:a 0}))

 "In a transform, that submap in the original map is changed to the new value of the submap.
      example: (submap [:a :c]) returns {:a 0} with no :c"
 (= {:b 1, :a 1} (transform [(submap [:a :c]) MAP-VALS]
                           inc
                           {:a 0, :b 1}))

 "We replace the empty submap with {:c 2} and merge with the original structure"
 (= {:a 0, :b 1, :c 2} (transform (submap []) #(assoc % :c 2) {:a 0, :b 1}))

 "subselect
       (subselect & path)
       Navigates to a sequence that contains the results of (select ...),
       but is a view to the original structure that can be transformed.
       Without subselect, we could only transform selected values individually.
       subselect lets us transform them together as a seq, much like filterer.
       Requires that the input navigators will walk the structure's children in the
       same order when executed on select and then transform
       See also filterer."
 (= [1 [[[10]] 3] 5 [8 [7 6]] 2]
    (transform (subselect (walker number?) even?) reverse [1 [[[2]] 3] 5 [6 [7 8]] 10]))

 "subset
       (subset aset)
       Navigates to the specified subset (by taking an intersection).
       In a transform, that subset in the original set is changed to the new value of the subset."
 (= #{:b} (select-one (subset #{:a :b}) #{:b :c}))

 "Replaces the #{:a} subset with #{:a :c} and unions back into the original structure"
 (= #{:c :b :a} (setval (subset #{:a}) #{:a :c} #{:a :b}))

 "terminal
        (terminal update-fn)
        Added in 0.12.0
        For usage with multi-transform, defines an endpoint in the navigation that will
        have the parameterized transform function run.
        The transform function works just like it does in transform,
        with collected values given as the first arguments.
        See also terminal-val and multi-transform."

 (= 4 (multi-transform [(putval 3) (terminal +)] 1))

 "inc :c and add 3 to :d"
 (= {:a {:b {:c 43, :d 4}}}
    (multi-transform [:a :b (multi-path [:c (terminal inc)]
                                        [:d (putval 3) (terminal +)])]
                     {:a {:b {:c 42 :d 1}}}))

 "terminal-val
        (terminal-val val)
        Added in 0.12.0
        Like terminal but specifies a val to set at the location
        regardless of the collected values or the value at the location."
 (= 2 (multi-transform (terminal-val 2) 3))

 "transformed
         (transformed path update-fn)
         Navigates to a view of the current value by transforming it with the specified path and update-fn.
         The input path may be parameterized, in which case the result of transformed
         will be parameterized in the order of which the parameterized navigators were declared.
         See also view"
 (= '(0 2 2 6 4 10 6 14 8 18) (select-one (transformed [ALL odd?] #(* % 2)) (range 10)))

 "transform the transformation of double all odds by dividing the whole thing by 2"
 (= '(0 1 1 3 2 5 3 7 4 9) (transform [(transformed [ALL odd?] #(* % 2)) ALL] #(/ % 2) (range 10)))

 "traversed
          (traversed path reduce-fn)
          Navigates to a view of the current value by transforming with a reduction over the specified traversal."
 (= 10 (select-any (traversed ALL +) [1 2 3 4]))

 "view
          (view afn)
          Navigates to result of running afn on the currently navigated value.
          See also transformed."
 (= 1 (select-one [FIRST (view inc)] (range 5)))

 "walker
          (walker afn)
          Using clojure.walk, walker executes a depth-first search for nodes where afn
          returns a truthy value. When afn returns a truthy value,
          walker stops searching that branch of the tree and continues
          its search of the rest of the data structure.
          See also codewalker"
 (= '(4 2 6) (select (walker #(and (number? %) (even? %))) '(1 (3 4) 2 (6))))

 "In this example note that (3 4) and (6 7) are not returned because the search halted at
          (2 (3 4) (5 (6 7)))."
 (= '((2 (3 4) 5 (6 7)) (8 9))
     (select (walker #(and (counted? %) (even? (count %))))
       '(1 (2 (3 4) 5 (6 7)) (8 9))))

 "In this example I don't even know whats going on, good luck..."
 (= '(1 :double :double)
     (setval (walker #(and (counted? %) (even? (count %))))
            :double
            '(1 (2 (3 4) 5 (6 7)) (8 9)))))
