## Memory Occupation

   1. Disambiguations are memoized. The disambiguation is a java object, which structure depends on the actual
      dimension.
      
## Time

   1. `org.ufl.hypogator.jackb.comparators.partialOrders.DisambiguationsComparator.nonNullCompare`:
      By allowing a multiple possible number of comparations, we gain better precision, but the computation time
      is drastically deteriorated.
      
      **a.** A possible reason is that the top-K does return more than it is expected to return, and furthermore it may
      contain many duplicates. Please double check.
      
      **b.** Instead of performing the disambiguation **n*m**, check which is the performance and the outcome of 
      performing the approach of trying to graph-disambiguate all the elements together, within the same graph.
      The best solution will be the minimum path between one of the elements in **n** and one in **m**. (That can be easily
      solved by providing a dummy source node and a dummy target node, while the former has only outgoing edges towards 
      **n**, and the latter only ingoing edges from **m**.)
      
      **c.** A possibile reason for this is the graph library, that is too slow to compute. Change even that if necessary.
      

## Code Analysis

   1. `org.ufl.hypogator.jackb.comparators.partialOrders.DisambiguatorsWithApproximations.nonTerms`:
      This field is only used to store which string representations are mapped into no disambiguation 
      Still, we might use that in order to check which elements are pronouns/etc, and then discard them.
      
   2. TODO: if the great part of memory occupation is provided by the term memoization, then refactor all the 
      disambiguated classes into one single class, that can be stored into a key-value store. 