package queries.graph;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import queries.DependencyGraph;

import java.util.*;

import static com.ibm.icu.util.LocalePriorityList.add;

public class GraphDissectPaths {
    private Set<String> startingPoints;
    private Set<String> endingNodes;
    private Set<String> cycleNodes;
    private Set<String> remainingNodes;
    private DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> graph;

    // Paths eventually connecting some cycles, starting from the starting nodes but not arriving into zero outgoing degree nodes
    // These are the paths to be executed for first, up until the terminal node.
    public Set<List<String>> pathFromStartingToCycles = new HashSet<>();

    // Paths that can be evaluated when all the loops are converged, and all the fegatelli covered.
    public Set<List<String>> pathFromCyclesToEnding = new HashSet<>();

    // These paths are the ones that, starting from the beginning nodes, directly terminate on the terminal nodes.
    // Please note that the final terminal node must be executed only at the end
    public Set<List<String>> pathDirecltyTerminal = new HashSet<>();

    // Fegatelli are the "bitter smaller bites", that are neither loops, not starting point. These are all of these
    // situations that make the whole execution redundant and unpredictable. These paths must be run whenever one
    // of the first node of the paths is met, so that the fegatello is immediately run.
    //
    // Then, the paths containing the intermediate and terminal nodes must be scheduled immediately for second.
    public Set<List<String>> fegatelli = new HashSet<>();

    // TODO:when a path can be for sure discarded from computation, or a set of paths? when the path (or all the paths together) produce updates no more.

    public GraphDissectPaths(Set<String> startingPoints, Set<String> cycleNodes, Set<String> endingNodes, Set<String> remainingNodes,
                             DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> graph) {
        this.startingPoints = new HashSet<>(startingPoints);
        this.cycleNodes = new HashSet<>(cycleNodes);
        this.endingNodes = new HashSet<>(endingNodes);
        this.remainingNodes = new HashSet<>(remainingNodes);
        this.graph = new DefaultDirectedWeightedGraph<>(DependencyGraph.Edge.class);
        for (String v : graph.vertexSet()) {
            this.graph.addVertex(v);
        }
        for (DependencyGraph.Edge e : graph.edgeSet()) {
            this.graph.addEdge(e.src, e.dst, e);
        }
    }

    public static void removeEdges(Collection<List<String>> pathFromStartingToCycles, DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> graph) {
        for (List<String> x : pathFromStartingToCycles) {
            for (int i = 0, N = x.size(); i < N - 1; i++) {
                DependencyGraph.Edge e = graph.getEdge(x.get(i), x.get(i + 1));
                graph.removeEdge(e);
            }
        }
    }

    public static void generateShortestPaths(Set<String> toBeExcludedNodes, DefaultDirectedWeightedGraph<String, DependencyGraph.Edge> graph, Collection<String> sources, Collection<String> sinks, Set<List<String>> resultingPaths) {
        DijkstraShortestPath<String, DependencyGraph.Edge> sp = new DijkstraShortestPath<>(graph);
        for (String src: sources) {
            for (String dstI : sinks) {
                GraphPath<String, DependencyGraph.Edge> path = sp.getPath(src, dstI);
                if (path != null) {
                    List<String> vertexList = path.getVertexList();
                    if ((vertexList.size()-1 >= 1) && Collections.disjoint(vertexList.subList(1, vertexList.size()-1), toBeExcludedNodes)) {
                        resultingPaths.add(vertexList);
                    }
                    // Then, interrupt the path on the vertex of maximum degree
                    /*String maxDegVTX = Collections.max(vertexList, Comparator.comparingInt(degreeMap::get));
                    vertexList = vertexList.subList(0, vertexList.indexOf(maxDegVTX) + 1);
                    startingPaths.add(vertexList);
                    maxes.add(maxDegVTX);*/
                }
            }
        }
    }

    public void invoke() {
        int graphSizeVPrev;
        int graphSizeEPrev;
        do {
            graphSizeVPrev = graph.vertexSet().size();
            graphSizeEPrev = graph.edgeSet().size();

            // 1) Getting all the paths from the starting nodes towards the cycle nodes, but excluding other intermediate cycle nodes and terminal nodes
            {
                Set<String> notBeforeCycle = new HashSet<>(endingNodes);
                notBeforeCycle.addAll(cycleNodes);
                generateShortestPaths(notBeforeCycle, graph, startingPoints, cycleNodes, pathFromStartingToCycles);
            }
            removeEdges(pathFromStartingToCycles, graph);

            // 2) Getting all the paths from the cycle nodes towards the ending nodes, but avoiding other intermediate cycle nodes.
            generateShortestPaths(cycleNodes, graph, cycleNodes, endingNodes, pathFromCyclesToEnding);
            removeEdges(pathFromCyclesToEnding, graph);

            // 3) Getting all the paths from the starting nodes towards the ending nodes, but avoiding other intermediate cycle nodes
            generateShortestPaths(cycleNodes, graph, startingPoints, endingNodes, pathDirecltyTerminal);
            removeEdges(pathFromCyclesToEnding, graph);

            // 4) Temporairly removing all the nodes that have no more connected edges
            Set<String> toBeremovedNodes = new HashSet<>();
            for (String vertex : graph.vertexSet()) {
                if (vertex.equals("126"))
                    System.err.println("DEBUG");
                if (graph.degreeOf(vertex) == 0) {
                    toBeremovedNodes.add(vertex);
                }
            }
            toBeremovedNodes.forEach(graph::removeVertex);
            endingNodes.removeAll(toBeremovedNodes);
            startingPoints.removeAll(toBeremovedNodes);
            remainingNodes.removeAll(toBeremovedNodes);
            cycleNodes.removeAll(toBeremovedNodes);
            endingNodes.retainAll(graph.vertexSet());
            startingPoints.retainAll(graph.vertexSet());
            remainingNodes.retainAll(graph.vertexSet());
            cycleNodes.retainAll(graph.vertexSet());

            // 5) Now, getting all the fegatelli
            // a. edges among remaining loop nodes
            for (String loopnode : cycleNodes) {
                for (DependencyGraph.Edge e : graph.outgoingEdgesOf(loopnode)) {
                    if (cycleNodes.contains(e.dst)) {
                        fegatelli.add(new ArrayList<String>(){{add(e.src); add(e.dst);}});
                    }
                }
            }
            // b. paths from the loop nodes towards the remaining nodes
            generateShortestPaths(cycleNodes, graph, cycleNodes, remainingNodes, fegatelli);
            // ...and viceversa
            generateShortestPaths(cycleNodes, graph, remainingNodes, cycleNodes, fegatelli);
            // c. paths from the remaining nodes towards the terminal nodes
            generateShortestPaths(cycleNodes, graph, remainingNodes, endingNodes, pathFromCyclesToEnding);
            // d. paths from the beginning nodes towards the remaining nodes
            generateShortestPaths(cycleNodes, graph, startingPoints, remainingNodes, fegatelli);
            // e. remaining paths among remaining nodes
            generateShortestPaths(cycleNodes, graph, remainingNodes, remainingNodes, fegatelli);
            removeEdges(fegatelli, graph);
            removeEdges(pathFromCyclesToEnding, graph);

        } while (graphSizeVPrev != graph.vertexSet().size() && graphSizeEPrev != graph.edgeSet().size());


    }
}
