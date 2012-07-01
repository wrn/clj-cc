
(ns clj-cc.core)

(defprotocol IJavaArray
  (jarray? [x]))

;; dynamically extend IJavaArray protocol. thank David Nolen for this cool idea.  see
;; http://dosync.posterous.com/51626638
(extend-type Object
  IJavaArray
  (jarray? [x]
            (let [c (.getClass x)]
             (if (.isArray c)
               (do
                 (extend-type (.getClass x)
                   IJavaArray
                   (jarray? [x] true))
                 true)
               false))))

;; Thank puzzler for the protocol based implementation.
(defprotocol ILast
  (last [s]
    "Like the original 'clojure.core/last'. However, it will be much more efficient on
  vectors and Java arrays, so we don't need to choose between 'last' and 'peek'."))

(extend-protocol ILast
  nil
  (last [s] (clojure.core/last s))

  Object
  (last [s]
    (if (jarray? s)
      (do
        ;; dynamically extend ILast to type
        (extend-type (.getClass s)
          ILast
          (last [s]
            (let [len (alength s)]
              ;; to conform to the original last behavior (last []) => nil
              (if (> len 0)
                (aget s (dec len))
                nil))))
        (last s))
      (clojure.core/last s)))

  clojure.lang.ISeq
  (last [s] (clojure.core/last s))

  clojure.lang.Reversible
  (last [s] (first (rseq s)))

  java.lang.String
  (last [s] (nth s (dec (count s))))

  clojure.lang.IPersistentVector
  (last [s] (peek s)))

(defn- create-method [c]
  (.getMethod (if (isa? c clojure.lang.ISeq)
                clojure.lang.PersistentList
                c)
              "create"
              (into-array Class [java.util.List])))

;; thank Tassilo Horn for providing 'append' (along with the "create-method" function)
(defn append
  "Append collections in 'more' into 'coll', and returns the new collection in the same concreate type as 'coll'.

Examples:
(append [1 2] '(3 4)) => [1 2 3 4]
(append '(1 2) [3 4] '(5 6)) => (1 2 3 4 5 6)

Compared to 'into':
(into [1 2] '(3 4)) => [1 2 3 4]
(into '(1 2) [3 4]) => (4 3 1 2)  ;; order messed up!
(into '(1 2) [3 4] '(5 6)) => clojure.lang.ArityException
"
  [coll & more]
  (.invoke (create-method (class coll)) nil
           (to-array [(apply concat coll more)])))

;; the name subseq is already taken in clojure.core, so have to use a different one, as my
;; intention is to have this included into the core.
(defn slice
  "Like the original 'clojure.core/subvec'. However, it works on any 'clojure.lang.Seqable'
  types, and returns the sliced sequence in the same concrete type as the input 'seq'.

Examples:
(slice [1 2 3 5 6] 1 3) => [2 3]
(slice '(1 2 3 5 6) 1 3) => (2 3)
(slice '(1 2 3 5 6) 1) => (2 3 5 6)
"
  ([seq start]
     (if (vector? seq)
       (subvec seq start)
       (append (empty seq) (drop start seq))
       ))
  ([seq start end]
     (if (vector? seq)
       (subvec seq start end)
       (append (empty seq) (drop-last (- (count seq) end) (drop start seq))))))
