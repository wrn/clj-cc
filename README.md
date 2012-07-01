# clj-cc

This is "Clojure core candies, or Clojure core candidates". Here in this library I collect
functions that I wish to be included in clojure.core, or be implemented in a cleaner way
that keeps a clear separation between abstraction and concrete types.

In a broader context, this arises from my disagreement with certain "design choices" some
people claim to be the rationale behind certain things in Clojure. One of the issues is, some people
think we whould have different functions for different concrete types ("last" for lists, and
"peek" for vectors, for example) so we can avoid using the wrong functions on a type that results
in poor performance. On the contrary, I think that approach defeats the purpose of abstraction and
makes writing generic code difficult, which is one of major advantages of using a dynamically typed
language. Instead, performance should be achieved via clear documentation, programmer's
awareness and profiling without any compromise to the abstration itself. Also, what makes the 
above "design choice" even worse is, Clojure itself is not even consistent with it.

If you are interested, you can read our debate on Google Clojure group:
<ol>
<li><a href="https://groups.google.com/forum/?fromgroups#!topic/clojure/apkNXk08Xes">Why cannot "last" be fast on vector?</a>
</li>
<li><a href="https://groups.google.com/forum/?fromgroups#!topic/clojure/q4iN7OLfjkU">General subsequence function</a>
</li>
</ol>

Let me know if you have suggestions for more functions to be included in this lib. Thanks.

Note 1: the name "core candidates" does not imply any official support from the Clojure
team. One of the purposes of this library is for me to get such support from the
community.

Note 2: Some people may wonder why I am not happy just to use these functions as they are
without being included in the core. Here are the reasons:

<ol>
<li>
   These are very basic functions. If people agree with me that those functions are better
   than what is already in the core, or filled a gap in the core, then that is the logical
   place for them.
</li>

<li>
   If they are in the core, they benefit every one using the language and hence also make
   the language more successful. I want the language to be successful so it can benefit
   me.
</li>

<li>
   If they are in the core, they may be optimized to achieve efficiency not possible
   otherwise because the implementation may have access to some internal parts. And the
   better efficiency will benefit every one too.
</li>

<li>
   Last but not the least, I hope by pushing these changes into the core, a broad
   consensus will form on the rejection of the design principle mentioned above, which
   sacrifices abstraction coherence for (doubtfully any) speed gain. That may bring about
   changes to fix other parts with similar problems.
</li>
</ol>

Note 3: For reasons in Note 2, for now please focus more on the function interface rather
than the efficiency. Of course you are welcome to help me improve the efficiency even with
the limitation that they are outside of the core.

## Usage

This is a very simple library. You can just directly copy the single file
src/clj_cc/core.clj into the "src" directory of your own project.
(a Clojar lib may be available later).

Right now there are only three functions, here are their doc strings:

-------------------------
clj-cc.core/last
([coll])

  Like the original 'clojure.core/last'. However, it will be much more efficient on
  vectors, so we don't need to choose between 'last' and 'peek'.

-------------------------
clj-cc.core/append
([coll & more])

  Append collections in 'more' into 'coll', and returns the new collection in the same concreate type as 'coll'.

Examples:

(append [1 2] '(3 4)) => [1 2 3 4]

(append '(1 2) [3 4] '(5 6)) => (1 2 3 4 5 6)

-------------------------
clj-cc.core/slice
([seq start] [seq start end])

  Like the original 'clojure.core/subvec'. However, it works on any 'clojure.lang.Seqable'
  types, and returns the sliced sequence in the same concrete type as the input 'seq'.

Examples:

(slice [1 2 3 5 6] 1 3) => [2 3]

(slice '(1 2 3 5 6) 1 3) => (2 3)

(slice '(1 2 3 5 6) 1) => (2 3 5 6)

## License

Copyright © 2012
Distributed under the Eclipse Public License, the same as Clojure.
