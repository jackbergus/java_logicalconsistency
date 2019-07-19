/*
 * ConceptNetDisambiguator.java
 * This file is part of aida_scraper
 *
 * Copyright (C) 2018 giacomo
 *
 * aida_scraper is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * aida_scraper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aida_scraper. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.ufl.hypogator.jackb.disambiguation.dimension.concept;


import javafx.util.Pair;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.disambiguation.dimension.Direction;
import org.ufl.hypogator.jackb.disambiguation.dimension.memoization.MemoizationLessData;
import org.ufl.hypogator.jackb.fuzzymatching.FuzzyMatcher;
import org.ufl.hypogator.jackb.scraper.MultiConceptScraper;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;
import org.ufl.hypogator.jackb.scraper.adt.DiGraph;
import org.ufl.hypogator.jackb.scraper.adt.DiGraphEquivalenceClass;
import org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNetJNITraverser;
import org.ufl.hypogator.jackb.traversers.conceptnet.RecordResultForSingleNode;
import org.ufl.hypogator.jackb.utils.SetOperations;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides the disambiguation between the perfect concepts that have been presented.
 */
public class ConceptNetDimensionDisambiguationOperations {

    /**
     * Memoization that is dependant to the current dimension of interest
     */
    //private final MemoizationLessData<SemanticNetworkEntryPoint> memoizerTester;

    public void serializeToDisk(File file) {
      //  memoizerTester.serializeToDisk(file);
    }

    public void loadFromDisk(File file) {
        // noop
    }

    /**
     * Disambiguator using the pg_dump. In order to perform the fuzzy match, we also need a dictionary, that is going to
     * be provided by the scraper for the dimension
     */
    private final ConceptNetJNITraverser resolutor;

    /**
     * ConceptScraper for the current dimension. It is also associated to a dictionary
     */
    private final MultiConceptScraper.PrivateTermScorer dimensionScraper;
    private final String dimension;
    private final Integer recursiveGraphExpander = ConfigurationEntrypoint.getInstance().recursiveGraphExpander;
    private final double threshold = ConfigurationEntrypoint.getInstance().threshold;
    double maxint = (double) Integer.MAX_VALUE;

    public ConceptNetDimensionDisambiguationOperations(String dim) {
        // ConceptNetTraverser and MultiConcpetScraper have a mutual dependency. Setting the vocabulary as null at first
        this.resolutor = ConceptNetJNITraverser.getInstance();
        // Then, generating and loading the vocabulary
        this.dimensionScraper = new MultiConceptScraper<>(resolutor).dimension(dim, ConfigurationEntrypoint.getInstance().useJNI); // TODO: true
        // Then, setting the vocabulary
        this.resolutor.setVocabulary(dimensionScraper.getEnrichedVocabulary());
        this.dimension = dim;
        //memoizerTester = new MemoizationLessData<>();
    }

    public void close() {
        this.dimensionScraper.close();
    }

    /**
     * Provides the expected representation for the string in Concepts
     *
     * @param x
     * @return
     */
    public static String rectify(String x) {
        return x.replaceAll("\\s", "_").replaceAll("-", "_").toLowerCase();
    }

    public static String unrectify(String x) {
        if (x.startsWith("/c/"))
            return unrectify(x.split("/")[3]);
        else {
            return x.replaceAll("_", " ");
        }
    }

    /**
     * Provides a boolean interpretation of the pair.
     *
     * @param cp Query outcome as a pair
     * @return If the double value is zero or the list is empty, it means that there is not enough information to satisfy that
     */
    public static boolean isOK(Pair<Double, List<SemanticNetworkEntryPoint>> cp) {
        return (cp != null && (!cp.getKey().equals(0.0)) && (!cp.getValue().isEmpty()));
    }

    /**
     * This method chains two query result using transitivity
     *
     * @param left  Left path (e.g., from subtype to type)
     * @param right Right path (e.g., from type to dimension)
     * @return Chained path with updated score. The score is the conjunction of the two paths
     */
    public static Pair<Double, List<SemanticNetworkEntryPoint>> transitivity
            (Pair<Double, List<SemanticNetworkEntryPoint>> left,
             Pair<Double, List<SemanticNetworkEntryPoint>> right) {
        List<SemanticNetworkEntryPoint> leftList = left.getValue();
        List<SemanticNetworkEntryPoint> rightList = right.getValue();
        if (leftList.isEmpty() || rightList.isEmpty()) {
            return new Pair<>(0.0, new ArrayList<>());
        } else {
            if (leftList.get(leftList.size() - 1).belongToSameEquivalenceClass(right.getValue().get(0))) {
                ArrayList<SemanticNetworkEntryPoint> chained = new ArrayList<>(leftList);
                chained.addAll(rightList.subList(1, rightList.size()));
                return new Pair<>(1.0 - ((1 - left.getKey()) * (1 - right.getKey())), SetOperations.removeDuplicatesFromList(chained));
            } else {
                return new Pair<>(0.0, new ArrayList<>());
            }
        }
    }

    /**
     * This class is associated to a vocabulary depending on its associated dimension. Therefore, this vocabulary may
     * be enriched with the adjuncted data from the hierarchies. This function provides such enriched vocabulary.
     *
     * @return
     */
    public FuzzyMatcher<RecordResultForSingleNode> getEnrichedVocabulary() {
        return dimensionScraper.getEnrichedVocabulary();
    }

    /**
     * Returns the dimension associated to the current evaluator/extractor
     * @return
     */
    public String getDimension() {
        return dimension;
    }

    /**
     * First Check whether the term appears in the graph+conceptNet
     *
     * @param term
     * @return
     */
    public SemanticNetworkEntryPoint resolveExactTerm(String term) {
        return resolutor.resolveTerm(term);
    }

    /**
     * Checks whether there is a is-a (part-of) path from subtype to type
     *
     * @param type    Supertype
     * @param subtype Type that has to be checked
     * @return Returns the score and the path as a witness
     */
    public Pair<Double, List<SemanticNetworkEntryPoint>> scoreTyping(String type, String subtype) {
        type = (type.equals(getDimension())) ? type : rectify(type);
        subtype = (subtype.equals(getDimension())) ? subtype : rectify(subtype);
        return scoreTyping(resolveExactTerm(type), resolveExactTerm(subtype));
    }

    public Pair<Double, List<SemanticNetworkEntryPoint>> scoreTyping(SemanticNetworkEntryPoint type, SemanticNetworkEntryPoint subtype) {
        return dimensionScraper.scoreWithPath(type, subtype);
    }

    public Pair<Direction, Optional<Pair<Double, List<SemanticNetworkEntryPoint>>>> getDirection
            (SemanticNetworkEntryPoint left, SemanticNetworkEntryPoint right) {
        return getDirection(0, left, right);
    }


    public Pair<Direction, Optional<Pair<Double, List<SemanticNetworkEntryPoint>>>> getDirectionWithMemoization
            (int steps, SemanticNetworkEntryPoint left, SemanticNetworkEntryPoint right) {
        boolean unionLeft = false;
        boolean unionRight = false;
        Pair<Double, List<SemanticNetworkEntryPoint>> resultLeftRight = null;
        Pair<Double, List<SemanticNetworkEntryPoint>> resultRightLeft = null;

        // Extract the element for which we already know the top, if available
        if (left.getValue().equals(getDimension())) {
            unionLeft = true;
            resultLeftRight = scoreTyping(left, right);
        }
        if (right.getValue().equals(getDimension())) {
            unionRight = true;
            resultRightLeft = scoreTyping(right, left);
        }

        // If no Union is give, then I do not know the direction for sure, and I have to evaluate the remaining element
        if ((!unionLeft) && (!unionRight)) {
            if (resultLeftRight == null)
                resultLeftRight = scoreTyping(left, right);
            if (resultRightLeft == null)
                resultRightLeft = scoreTyping(right, left);
        }

        return inferDirectionForPairs(steps, new Pair<>(left, right), resultLeftRight, resultRightLeft);
    }

    /**
     * Infers the direction between the two resolved entrypoints
     *
     * @param steps     Expansion steps that may be further on applied
     * @param left      Left argument
     * @param right     Right argument
     * @return
     */
    @Deprecated
    public Pair<Direction, Optional<Pair<Double, List<SemanticNetworkEntryPoint>>>> getDirection
    (int steps, SemanticNetworkEntryPoint left, SemanticNetworkEntryPoint right) {
        return getDirectionWithMemoization(steps, left, right);
    }

    public Pair<Direction, Optional<Pair<Double, List<SemanticNetworkEntryPoint>>>> getDirection
            (String left, String right) {
        return getDirection(0, left, right);
    }

    /**
     * Infers the direction between two non-resolved elements
     * @param steps
     * @param left      Left unresolved argument
     * @param right     Right unresolved argument
     * @return
     */
    @Deprecated
    private Pair<Direction, Optional<Pair<Double, List<SemanticNetworkEntryPoint>>>> getDirection
        (int steps, String left, String right) {
        return getDirection(steps, resolveExactTerm(left), resolveExactTerm(right));
    }

    /**
     * Given two nodes provided in {@code cp}, this function allows to infer which is the direction of the both. In
     * particular, this function may eventually use some {@code expansion} steps to rectify the inference
     *
     * @param steps                 Expansion steps to be used to further analyse and disambiguate the function.
     *                              If steps > 0, then no memoization will occur
     * @param cp                    Pair of element via which we want to detect the direction
     * @param resultLeftRight       Result of the previous inference step which supposed that left implies right
     * @param resultRightLeft       Result of the previous inference step which supposed that right implies left
     * @return                      The actual direction of the inference, with the uncertainty score and the witness
     *                              of such inference
     */
    private Pair<Direction, Optional<Pair<Double, List<SemanticNetworkEntryPoint>>>>

            inferDirectionForPairs(int steps, Pair<SemanticNetworkEntryPoint, SemanticNetworkEntryPoint> cp,
                                   Pair<Double, List<SemanticNetworkEntryPoint>> resultLeftRight,
                                   Pair<Double, List<SemanticNetworkEntryPoint>> resultRightLeft) {

        Pair<Direction, Optional<Pair<Double, List<SemanticNetworkEntryPoint>>>> res;

        // Note that if both unionLeft and unionRight are true, then they are the same dimension,
        // and this case is already covered by the very beginning. Please also note that isOk also
        // covers the case that the result must not be null
        if ((isOK(resultLeftRight)) && (isOK(resultRightLeft))) {
            double score = resultLeftRight.getKey() * resultRightLeft.getKey();
            int cmp = Double.compare(resultLeftRight.getKey(), resultRightLeft.getKey());
            if (cmp < 0) {
                resultLeftRight.getValue().addAll(resultRightLeft.getValue());
                res = doExpandGraph(cp.getKey(), cp.getValue(), steps, new Pair<>(resultLeftRight.getValue().isEmpty() ? Direction.NONE : Direction.BOTH, Optional.of(new Pair<>(score, resultLeftRight.getValue()))));
                //if (steps == 0) memoizerTester.memoizeAs(res);
                return res;
            } else {
                resultRightLeft.getValue().addAll(resultLeftRight.getValue());
                res = doExpandGraph(cp.getKey(), cp.getValue(), steps, new Pair<>(resultRightLeft.getValue().isEmpty() ? Direction.NONE :  Direction.BOTH, Optional.of(new Pair<>(score, resultRightLeft.getValue()))));
                //if (steps == 0) memoizerTester.memoizeAs(res);
                return res;
            }
        } else if (isOK(resultLeftRight)) {
            res  = doExpandGraph(cp.getKey(), cp.getValue(), steps, new Pair<>(resultLeftRight.getValue().isEmpty() ? Direction.NONE : Direction.LEFT_TYPE_RIGHT_SUBTYPE, Optional.of(resultLeftRight)));
            //if (steps == 0) memoizerTester.memoizeAs(res);
            return res;
        } else if (isOK(resultRightLeft)) {
            res  = doExpandGraph(cp.getKey(), cp.getValue(), steps, new Pair<>(resultRightLeft.getValue().isEmpty() ? Direction.NONE : Direction.RIGHT_TYPE_LEFT_SUBTYPE, Optional.of(resultRightLeft)));
            //if (steps == 0) memoizerTester.memoizeAs(res);
            return res;
        } else {
            res = new Pair<>(Direction.NONE, Optional.empty());
            //if (steps == 0) memoizerTester.memoizeAs(res);
            return res;
        }
    }

    /**
     * This function expands the graph {@code tmpGraph} which represents the previous step of computation and replaces
     * each edge with a subgraph via {@code navigatePathPrecisely}. Then, after terminating the expansion, the graph
     * is either further expanded or the directions between {@code rootSrc} and {@code rootDst} are inferred.
     *
     * @param rootSrc   Left argument over which evaluate the direction
     * @param rootDst   Right argument over which evaluate the direction
     * @param steps     Number of expansion steps that were performed so far
     * @param dir       The direction that
     * @param score
     * @param oldGraph
     * @return
     */
    private Pair<Direction, Optional<Pair<Double, List<SemanticNetworkEntryPoint>>>> doExpandGraph

            (SemanticNetworkEntryPoint rootSrc, SemanticNetworkEntryPoint rootDst, int steps, Direction dir, double score, DiGraphEquivalenceClass oldGraph) {

        if (steps < 0 || this.recursiveGraphExpander <= (steps) || dir.equals(Direction.NONE))
            throw new RuntimeException("Unexpected call of the graph if I have nothing to traverse");

        DiGraphEquivalenceClass graph = new DiGraphEquivalenceClass();

        for (DefaultWeightedEdge x : oldGraph.getEdges()) {
            SemanticNetworkEntryPoint src = oldGraph.graph.getEdgeSource(x);
            SemanticNetworkEntryPoint dst = oldGraph.graph.getEdgeTarget(x);

            if (src.getSemanticId().equals(dst.getSemanticId()))
                continue;

            navigatePathPrecisely(steps, score, graph, src, dst);
        }

        //graph.getEdges().forEach(x -> System.out.println(graph.graph.getEdgeSource(x).getValue() + "-->" + graph.graph.getEdgeTarget(x).getValue()));

        // Now, getting whether there is a path between the two elements
        if (steps == this.recursiveGraphExpander - 1) {
            // no expansion is allowed, therefore I extract the paths
            return traverseGraphForFinalDecision(rootSrc, rootDst, graph);
        } else {
            return doExpandGraph(rootSrc, rootDst, steps + 1, dir, score, graph);
        }
    }

    /**
     * Performs the "paraconsistent" reasoning, a.k.a. graph expansion (in this case), where wrong assumptions are
     * refined into checking in detail whether the assumptions were correct or not.
     *
     * @param rootSrc
     * @param rootDst
     * @param steps
     * @param cp
     * @return
     */
    private Pair<Direction, Optional<Pair<Double, List<SemanticNetworkEntryPoint>>>>

            doExpandGraph(SemanticNetworkEntryPoint rootSrc, SemanticNetworkEntryPoint rootDst, int steps,
                          Pair<Direction, Optional<Pair<Double, List<SemanticNetworkEntryPoint>>>> cp) {

        if (steps < 0 || this.recursiveGraphExpander <= (steps) || cp.getKey().equals(Direction.NONE) || !cp.getValue().isPresent())
            return cp;

        double score = cp.getValue().isPresent() ? cp.getValue().get().getKey() : 0.0;
        List<SemanticNetworkEntryPoint> sharedPath = cp.getValue().isPresent() ? cp.getValue().get().getValue() : Collections.emptyList();
        Direction globalDir = cp.getKey();

        DiGraphEquivalenceClass graph = new DiGraphEquivalenceClass();

        for (int i = 0; i <= sharedPath.size() - 2; i++) {
            // Edge to expaond
            SemanticNetworkEntryPoint src = sharedPath.get(i);
            SemanticNetworkEntryPoint dst = sharedPath.get(i + 1);

            if (src.getSemanticId().equals(dst.getSemanticId()))
                continue;
            navigatePathPrecisely(steps, score, graph, src, dst);
        }

        //graph.getEdges().forEach(x -> System.out.println(graph.graph.getEdgeSource(x).getValue() + "-->" + graph.graph.getEdgeTarget(x).getValue()));

        // Now, getting whether there is a path between the two elements
        if (steps == this.recursiveGraphExpander - 1) {
            // no expansion is allowed, therefore I extract the paths
            return traverseGraphForFinalDecision(rootSrc, rootDst, graph);
        } else {
            return doExpandGraph(rootSrc, rootDst, steps + 1, globalDir, score, graph);
        }
    }

    /**
     * This function checks which is the path between two distinct entrypoints, and recreates such path within {@code graph}
     * @param steps Number of disambiguation steps
     * @param score score to be associated to the path
     * @param graph Graph where to store the traversal/result of the
     * @param src
     * @param dst
     */
    private void navigatePathPrecisely(int steps, double score, DiGraphEquivalenceClass graph, SemanticNetworkEntryPoint src, SemanticNetworkEntryPoint dst) {
        // Recursively expanding the path. Plus, blocking the recursivity of the expansion
        // Having steps+1 tells which is the refinement step and implicitely states that the intermediate steps won't be memoized at all
        Pair<Direction, Optional<Pair<Double, List<SemanticNetworkEntryPoint>>>> dir = getDirectionWithMemoization(steps + 1, src, dst);
        boolean valuePresent = dir.getValue().isPresent();
        double internalScore = valuePresent ? dir.getValue().get().getKey() : 0.0;
        List<SemanticNetworkEntryPoint> internalSharedPath = valuePresent ? dir.getValue().get().getValue() : Collections.emptyList();

        // Cases when I cannot create an edge.
        if (dir.getKey().equals(Direction.NONE) || !dir.getValue().isPresent())
            return;

        //System.err.println(src.getSemanticId() + " --> " + dst.getSemanticId() + " -- step " + steps + " -- " + dir.getKey());

        //System.err.println(dir.getValue().get().getValue().stream().map(SemanticNetworkEntryPoint::getValue).collect(Collectors.joining(", ")));

        // Associating the highest path integer score to the number nearer to zero.
        // The element that has value 1, is kept as 1, so that provides the smallest distance
        double xScore = internalScore * threshold + score * (1-threshold);
        //double assScore = maxint + xScore - maxint * xScore;

        // If I have a zero score, I have no reliabilty, and therefore I won't add the score
        if (internalScore > 0.05) {
            for (int j = 0; j <= internalSharedPath.size() - 2; j++) {
                SemanticNetworkEntryPoint internalSrc = internalSharedPath.get(j);
                SemanticNetworkEntryPoint internalDst = internalSharedPath.get(j + 1);
                //System.err.println(internalSrc.getValue() + " [e] " + internalDst.getValue() + " s=" + assScore);
                graph.add(internalSrc, internalDst, xScore);
            }
        }
    }


    /**
     * Given two vertices which connections were expanded in the given {@code graph}, it returns the actual direction
     * connecting the two elements, independenty from the direction that was previously infer. This allows us to
     * even change the direction that was previously predicted.
     *
     * @param rootSrc       See {@code globalDir}
     * @param rootDst       See {@code globalDir}
     * @param graph         Graph from which we want to extract the path
     * @return              Inferred direction, with also the provided direction
     */
    private Pair<Direction, Optional<Pair<Double, List<SemanticNetworkEntryPoint>>>> traverseGraphForFinalDecision(SemanticNetworkEntryPoint rootSrc, SemanticNetworkEntryPoint rootDst, DiGraphEquivalenceClass graph) {
        //if (globalDir == null)
        //    return new Pair<>(Direction.NONE, Optional.empty());

        boolean unionLeft = false;
        boolean unionRight = false;
        Pair<Double, List<SemanticNetworkEntryPoint>> resultLeftRight = null;
        Pair<Double, List<SemanticNetworkEntryPoint>> resultRightLeft = null;

        // Before
        //graph.invertPathDistance();
        if (rootSrc.getValue().equals(getDimension())) {
            unionLeft = true;
            resultLeftRight = graph.getPathWithWeightsOk(rootDst, rootSrc);
        }
        if (rootDst.getValue().equals(getDimension())) {
            unionRight = true;
            resultRightLeft = graph.getPathWithWeightsOk(rootSrc, rootDst);
        }

        // If no Union is give, then I do not know the direction for sure, and I have to evaluate the remaining element
        if ((!unionLeft) && (!unionRight)) {
            if (resultLeftRight == null)
                resultLeftRight = graph.getPathWithWeightsOk(rootDst, rootSrc);
            if (resultRightLeft == null)
                resultRightLeft = graph.getPathWithWeightsOk(rootSrc, rootDst);
        }

        // Force to perform the last round, therefore I won't expand anymore
        return inferDirectionForPairs(this.recursiveGraphExpander, new Pair<>(rootSrc, rootDst), resultLeftRight, resultRightLeft);
    }


    /**
     * This function always checks whether the terms appear in the memoization first. If not, then the disambiguation
     * process starts.
     *
     * @param disambiguated
     * @param disambiguated1
     * @return
     */
    private long size = 0;
    public PartialOrderComparison getDirectionWithMemoization(String disambiguated, String disambiguated1) {
        return getDirectionWithMemoization2(resolveExactTerm(disambiguated), resolveExactTerm(disambiguated1));
    }

    public PartialOrderComparison getDirectionWithMemoization2(SemanticNetworkEntryPoint left, SemanticNetworkEntryPoint right) {
        /*MemoizationLessData<SemanticNetworkEntryPoint> memoizationGeneralizer = memoizerTester.invoke(left, right);
        if (memoizerTester.getMemoizationSize() != size) {
            size = memoizerTester.getMemoizationSize();
            //System.err.println(dimension + " [memo] " + size);
        }
        if (memoizationGeneralizer.hasResult())
            return memoizationGeneralizer.getResult();
        Pair<SemanticNetworkEntryPoint, SemanticNetworkEntryPoint> cp = memoizationGeneralizer.getCp();*/

        return MemoizationLessData.fromVerbose(getDirectionWithMemoization(0, left, right));
    }

    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    //////// TODO: UNTESTED CODE NOT IN PRODUCTION ///
    //////////////////////////////////////////////////
    //////////////////////////////////////////////////
    //////////////////////////////////////////////////

    /**
     * Checks whether there is a is-a (part-of) path from the element to the dimension
     *
     * @param dimension Supertype
     * @param element   Type that has to be checked
     * @return Returns the score and the path as a witness
     */
    public Pair<Double, List<SemanticNetworkEntryPoint>> fitsDimension(SemanticNetworkEntryPoint dimension, SemanticNetworkEntryPoint element) {
        return scoreTyping(dimension, element);
    }

    /**
     * This method checks whether a subtype has a given type within a dimension
     *
     * @param dimension Coarser dimension type to be checked
     * @param type      Type to be checked
     * @param subtype   SubType/Element to be checked
     * @return Transitivity Path
     */
    public Pair<Double, List<SemanticNetworkEntryPoint>> isSubtypeInDimension(SemanticNetworkEntryPoint dimension, SemanticNetworkEntryPoint type, SemanticNetworkEntryPoint subtype) {
        Pair<Double, List<SemanticNetworkEntryPoint>> right = fitsDimension(dimension, subtype);
        if (!isOK(right)) {
            // If I cannot directly check whether the subtype directly refers to the dimension,
            // then I have to use transitivity

            right = fitsDimension(dimension, type);
            if (!isOK(right)) {
                // If the type does not belong to the dimension, then there could not be a path from subtype to dimension via type
                // Therefore, I have no problems
                return right;
            } else {
                // Otherwise, I try to check whether there is a path from the subtype to the type
                Pair<Double, List<SemanticNetworkEntryPoint>> left = scoreTyping(type, subtype);
                return transitivity(left, right);
            }
        } else {
            // If there is already a path between type and subtype, then provide it
            Pair<Double, List<SemanticNetworkEntryPoint>> left = scoreTyping(type, subtype);
            Pair<Double, List<SemanticNetworkEntryPoint>> right2 = scoreTyping(dimension, type);
            Pair<Double, List<SemanticNetworkEntryPoint>> updateScore = transitivity(left, right2);
            return new Pair<>(right.getKey() + updateScore.getKey() - (right.getKey() * updateScore.getKey()), updateScore.getValue());
        }
    }

    public Pair<Double, List<SemanticNetworkEntryPoint>> isSubtypeInDimensions(SemanticNetworkEntryPoint root, Collection<SemanticNetworkEntryPoint> dimensions, SemanticNetworkEntryPoint type, SemanticNetworkEntryPoint subtype) {
        List<Pair<Double, List<SemanticNetworkEntryPoint>>> al = new ArrayList<>(dimensions.size());
        for (SemanticNetworkEntryPoint alternative : dimensions) {
            Pair<Double, List<SemanticNetworkEntryPoint>> oks = isSubtypeInDimension(alternative, type, subtype);
            if (isOK(oks))
                al.add(oks);
        }
        return getDoubleListPair(root, dimensions, type, subtype, al);
    }

    public Pair<Double, List<SemanticNetworkEntryPoint>> isElementInDimensions(SemanticNetworkEntryPoint root, Collection<SemanticNetworkEntryPoint> dimensions, SemanticNetworkEntryPoint subtype) {
        List<Pair<Double, List<SemanticNetworkEntryPoint>>> al = new ArrayList<>(dimensions.size());
        for (SemanticNetworkEntryPoint alternative : dimensions) {
            Pair<Double, List<SemanticNetworkEntryPoint>> oks = scoreTyping(alternative, subtype);
            if (isOK(oks))
                al.add(oks);
        }
        return getDoubleListPair(root, dimensions, null, subtype, al);
    }

    private Pair<Double, List<SemanticNetworkEntryPoint>> getDoubleListPair(SemanticNetworkEntryPoint root, Collection<SemanticNetworkEntryPoint> dimensions, @Nullable SemanticNetworkEntryPoint nullableType, SemanticNetworkEntryPoint subtype, List<Pair<Double, List<SemanticNetworkEntryPoint>>> al) {
        if (!al.isEmpty()) {
            DiGraphEquivalenceClass graph = new DiGraphEquivalenceClass();
            // Common paths to all the alternatives.
            List<SemanticNetworkEntryPoint> sharedPath = null;

            // If I have a type information, I can easily detect a first shared path from type to subtype, which is common
            if (nullableType != null) {
                Pair<Double, List<SemanticNetworkEntryPoint>> substring = this.scoreTyping(nullableType, subtype);
                sharedPath = substring.getValue();
                // Adding the common path only once
                for (int i = 0; i <= sharedPath.size() - 2; i++) {
                    SemanticNetworkEntryPoint dst = sharedPath.get(i);
                    SemanticNetworkEntryPoint src = sharedPath.get(i + 1);
                    graph.add(src, dst, 1.0);
                }
            } else {
                // Otherwise, I will just use dijkstra
            }

            // From the remaining subpaths, remove the common shared path and connect it to the root
            List<Pair<Double, List<SemanticNetworkEntryPoint>>> toRemove = new ArrayList<>();
            for (Pair<Double, List<SemanticNetworkEntryPoint>> cps : al) {
                if (sharedPath != null) cps.getValue().removeAll(sharedPath);
                cps.getValue().removeAll(dimensions); // e.g. weapon and weaponry are both wea. Therefore, the second last has to be linked directly to the root
                if (cps.getValue().isEmpty()) {
                    toRemove.add(cps);
                }
            }
            al.removeAll(toRemove);

            // Establish the relationships between all the elements within the non-shared paths. This is done
            // as a way to connect multiple different path information into one single final path considering all
            // the possible features. The "inconsistent" features that do not collect the subtype to the dimension
            // are discarded
            for (int i = 0; i < al.size(); i++) {
                Pair<Double, List<SemanticNetworkEntryPoint>> l_i = al.get(i);
                for (int j = 0; j < al.size(); j++) {
                    if (j != i) {
                        Pair<Double, List<SemanticNetworkEntryPoint>> l_j = al.get(j);
                        double scorePairList = l_i.getKey() * l_j.getKey();
                        for (SemanticNetworkEntryPoint x : l_i.getValue()) {
                            for (SemanticNetworkEntryPoint y : l_j.getValue()) {
                                // This step allows not to memoize all the intermediate and possible wrongful computations
                                Pair<Direction, Optional<Pair<Double, List<SemanticNetworkEntryPoint>>>> res = getDirectionWithMemoization(0, x, y);
                                switch (res.getKey()) {
                                    case LEFT_TYPE_RIGHT_SUBTYPE: {
                                        graph.add(x, y, scorePairList + res.getValue().get().getKey() - (scorePairList * res.getValue().get().getKey()));
                                    }
                                    break;
                                    case RIGHT_TYPE_LEFT_SUBTYPE: {
                                        graph.add(y, x, scorePairList + res.getValue().get().getKey() - (scorePairList * res.getValue().get().getKey()));
                                    }
                                    break;
                                    case NONE: // create no edge
                                        break;
                                }
                            }
                        }
                    }
                }
            }

            // For each remaining list in al, link the last type to the root.
            // Do that if the element is not linked to the main type
            for (int i = 0; i < al.size(); i++) {
                Pair<Double, List<SemanticNetworkEntryPoint>> l_i = al.get(i);
                List<SemanticNetworkEntryPoint> list = l_i.getValue();
                DiGraph<SemanticNetworkEntryPoint>.Vertex v = graph.findVertex(list.get(list.size() - 1));
                if (v.outSize() == 0)
                    graph.add(root, list.get(list.size() - 1), 1.0);

                DiGraph<SemanticNetworkEntryPoint>.Vertex w = graph.findVertex(list.get(0));
                if (w.outSize() == 0) {
                    graph.add(list.get(0), sharedPath.get(sharedPath.size() - 1), 1);
                }
            }

            // A path with highest score is associated to 1.
            // A path with 0 score is associated to zero
            graph.invertPathDistance();
            // Return the path from the dimension to the element
            List<SemanticNetworkEntryPoint> ls = graph.getPath(root, subtype).stream().map(x -> x.value).collect(Collectors.toList());

            double returnedScore = 1;
            for (Pair<Double, List<SemanticNetworkEntryPoint>> cp : al) {
                returnedScore = returnedScore * (1.0 - cp.getKey());
            }
            returnedScore = 1.0 - returnedScore;
            return new Pair<>(returnedScore, ls);

        } else
            return new Pair<>(0.0, new ArrayList<>());
    }

    public Pair<Double, List<SemanticNetworkEntryPoint>> isSubtypeInDimensions(SemanticNetworkEntryPoint root, SemanticNetworkEntryPoint type, SemanticNetworkEntryPoint subtype, SemanticNetworkEntryPoint... dimensions) {
        return isSubtypeInDimensions(root, Arrays.asList(dimensions), type, subtype);
    }

    public Pair<Double, List<SemanticNetworkEntryPoint>> isElementInDimensions(SemanticNetworkEntryPoint root, SemanticNetworkEntryPoint subtype, SemanticNetworkEntryPoint... dimensions) {
        return isElementInDimensions(root, Arrays.asList(dimensions), subtype);
    }

}
