/*
 * DependencyGraph.java
 * This file is part of KnowledgeBaseExpansion
 *
 * Copyright (C) 2019 - Giacomo Bergami
 *
 * KnowledgeBaseExpansion is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * KnowledgeBaseExpansion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KnowledgeBaseExpansion. If not, see <http://www.gnu.org/licenses/>.
 */


package queries;

import algos.graphs.AllCycles;
import algos.graphs.AllDirectedPaths2;
import javafx.util.Pair;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import queries.graph.GraphDissectPaths;
import queries.sql.v1.QueryGenerationConf;
import ref.RuleListener;
import types.Rule;

import java.io.IOException;
import java.util.*;

// Strong assumption: there are predicates that have

public class DependencyGraph {

    public static String staring_nodes = "";
    public Collection<String> getPredicates;
    public Set<List<String>> cycles = new HashSet<>();

    // Reflexive or transitive rules that reduce to hooks over one vertex
    public Set<String> reflOrTtrans = new HashSet<>();

    // Reflexive or transitive rules that reduce to hooks over one vertex
    public Set<String> reflOrTtransFromTheBeginning = new HashSet<>();

    // By using the former hooks, I know which are the rules that must applied at the end
    public Set<String> endingNodes = new HashSet<>();

    // I want to start the join computation from the nodes that have no incoming rules, whenever that's possible
    public Set<String> startingNodes = new HashSet<>();

    // This set contains all the paths where the starting node has inDegree as zero and where the nodes belong to the starting nodes
    public Set<List<String>> pathsBeforeFixPointsWithNoInput = new HashSet<>();


    public Set<List<String>> pathFromStartingToCycles;
    public Set<List<String>> pathFromCyclesToEnding ;
    public Set<List<String>> pathDirectlyTerminal ;

    // Paths not providing cycles that are neither starting nor ending cycles
    public Set<List<String>> pathsCausingMoreComplexConditions = new HashSet<>();

    // Paths arriving into zero outgoing degree nodes
    public Set<List<String>> terminalPaths = new HashSet<>();

    // If there are some hooks that are in starting nodes, then these are all the rules that must be applied first using the fixed point
    public Set<Integer> firstRuleId = new HashSet<>();

    DefaultDirectedWeightedGraph<String, Edge> graph;

    //remove from the inference graph the nodes that have degree 0
    Set<String> toBeRemovedVertices = new HashSet<>();

    // All the cycles that we want to break somehow
    Set<List<String>> cycleSet = new HashSet<>();

    // Terminal node of a cycle
    Set<String> cycleMaxVertices = new HashSet<>();


    Set<String> remainingNodes = new HashSet<>();
    private Set<List<String>> fegatelli;

    public DependencyGraph(RuleListener l, QueryGenerationConf qgc) throws IOException {
        System.err.println("INFO -  Creating the graph");
        graph = new DefaultDirectedWeightedGraph<>(Edge.class);
        // All the predicates are the elements declared in the schema of the ontology
        //getPredicates = l.schema.keySet();
        /*// Each predicate will represent a node in the graph
        for (String x : getPredicates) {
            graph.addVertex(x);
        }*/
        // Inconsistency will be another node.
        graph.addVertex("_bot_");

        // Creating the map for all the high-level rule connected to the element
        Map<Integer, Pair<Set<String>, Set<String>>> map = new HashMap<>();

        l.ruleTabClassification4DB.entrySet().forEach((cp) -> {
            if (qgc.compileQuery(l.idToRuleTab.get(cp.getKey())) != null) {
                createHyperedge(cp.getKey(), cp.getValue(), map);
                remainingNodes.add(cp.getKey().toString());
                graph.addVertex(cp.getKey().toString());
            }
        });

        for (Map.Entry<Integer, Pair<Set<String>, Set<String>>> cp1 : map.entrySet()) {
            for (Map.Entry<Integer, Pair<Set<String>, Set<String>>> cp2 : map.entrySet()) {
                if (!cp1.getKey().equals(cp2.getKey())) { //self hooks are not required, as each node is per se a self hook
                    TreeSet<String> intersection = new TreeSet<>(cp1.getValue().getValue());
                    intersection.retainAll(cp2.getValue().getKey());
                    if (!intersection.isEmpty()) {
                        Edge e = graph.addEdge(cp1.getKey().toString(), cp2.getKey().toString());
                        e.src = cp1.getKey().toString();
                        e.dst = cp2.getKey().toString();
                        //System.out.println(e.src + " --> " + e.dst);
                        //e.putRuleId(cp1.getKey().toString()+"--"+cp2.getKey().toString());
                        e.putRuleId(intersection.size());
                    }
                }
            }

            /*int ruleId = cp.getKey();
            HashSet<String> origDependencies = new HashSet<>();
            HashSet<String> dstDependencies = new HashSet<>();
            cp.getValue().body.stream().map(x -> x.prop.relName).forEach(origDependencies::add);
            cp.getValue().head.stream().filter(x -> x.exists.isEmpty()).map(x -> x.prop.relName).forEach(dstDependencies::add);
            cp.getValue().head.stream().filter(x -> !x.exists.isEmpty()).map(x -> x.prop.relName).forEach(origDependencies::add);
            if (cp.getValue().isFinalBottom)
                dstDependencies.add("_bot_");
            for (String x : origDependencies) {
                for (String y : dstDependencies) {
                    Edge e = graph.getEdge(x, y);
                    if (e == null) {
                        e = graph.addEdge(x, y);
                        e.src = x;
                        e.dst = y;
                    }
                    e.putRuleId(ruleId);
                }
            }*/
        }

        graph.edgeSet().forEach(x -> {
            graph.setEdgeWeight(x, 1.0 / x.getWeight());
        });

        System.err.println("INFO - removing all the tuples that do not participate in the inference process.");
        graph.vertexSet().forEach(v -> {
            if (graph.outDegreeOf(v) + graph.inDegreeOf(v) == 0)
                toBeRemovedVertices.add(v);
        });
        graph.removeAllVertices(toBeRemovedVertices);

        System.err.println("INFO - possible starting points are the nodes with maximum outDegree");
        Set<String> startingPoints = new HashSet<>();
        {
            Integer maxOutDegree = Integer.MIN_VALUE;
            for (String x : graph.vertexSet()) {
                int deg = graph.outDegreeOf(x);
                if (Integer.max(deg, maxOutDegree) != maxOutDegree) {
                    startingPoints.clear();
                    maxOutDegree = deg;
                }
                startingPoints.add(x);
            }
        }

        // Sorting the vertices by ingoing degree
        Set<String> cycleNodes = new HashSet<>();
        System.err.println("INFO - Sorting the vertices by ingoing degree");
        Map<String, Integer> degreeMap = new HashMap<>();
        for (String x : graph.vertexSet()) {
            int inDeg = graph.inDegreeOf(x);

            // I will start to produce the rules from the nodes that I know that have no inputs
            if (inDeg == 0) {
                startingNodes.add(x);
            } else if (graph.outDegreeOf(x) == 0) {
                endingNodes.add(x);
            }
            degreeMap.put(x, graph.inDegreeOf(x));
        }

        //String maxIndegree = degreeMap.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();

        // Getting all the cycles (strongly connected components) in a graph.
        // As a consequence, the algorithm visits all the possible connected components
        //Tarjan t = new Tarjan(graph);
        System.out.println("Detecting Cycles:");
        //new AllCycles(graph).run()
        new AllCycles(graph).run()
        //System.exit(1);
        //new SCC(graph).runDFSStack()
        //t.run()
                .forEach(cycle -> {
                    //List<String> cycle = new ArrayList<>(cycleColl);
                    //Set<String> x = graph.vertexSet();
                    if (cycle.size() == 1) {
                        /*reflOrTtrans.addAll(cycle);
                        String y = cycle.get(0);
                        if (graph.getEdge(y, y) != null) {
                            (startingNodes.contains(y) ? firstRuleId : lastRuleId).add(Integer.valueOf(y));
                            remainingNodes.remove(y);
                        }*/
                    } else {
                        // Getting the node in the cycle with the maximum ingoing degree
                        System.out.println(cycle);
                        String cycleMaxInDegree = Collections.max(cycle, Comparator.comparingInt(degreeMap::get));
                        // This vertex should be one towards which we find some paths
                        cycleMaxVertices.add(cycleMaxInDegree);
                        // Getting the position of the maximum element within the list
                        int maxIndex = cycle.indexOf(cycleMaxInDegree);
                        if (maxIndex != cycle.size() - 1) {
                            // Now, the current cycle is rewritten in a way that it will end in the maximum degree node.
                            List<String> head = new ArrayList<>();
                            ListIterator<String> itt = cycle.listIterator(maxIndex + 1);
                            while (itt.hasNext()) {
                                head.add(itt.next());
                                itt.remove();
                            }
                            head.addAll(cycle);
                            cycle = head;
                        }
                        //System.out.println("\t * " + cycle);
                        cycle.forEach(y -> remainingNodes.remove(y));
                        cycleSet.add(cycle);
                        cycleNodes.addAll(cycle);
                        startingNodes.removeAll(cycle);
                    }
                });


        firstRuleId.forEach(x -> startingNodes.add(x.toString()));
        //startingNodes.addAll();
        System.out.println("Starting nodes: " + startingNodes);
        System.out.println("Ending nodes: " + endingNodes);

        remainingNodes.removeAll(startingNodes);
        endingNodes.forEach(x -> remainingNodes.remove(x.toString()));
        System.out.println("\n\nRemaining nodes: " + remainingNodes); // Supposedly, these nodes should be the ones connecting the starting nodes to the ones in the cycles, or the ones connecting the starting nodes to the ending nodes.
        //System.exit(1);

        // TODO: getting all the paths from the beginning node and the ending nodes
        System.out.println("INFO: Generating all the paths going directly from the beginning to the terminal nodes, and not passing through the cycleNodes");

        //generateShortestPaths(cycleNodes, graph, startingNodes, endingNodes, pathFromCyclesToEnding);
        GraphDissectPaths algorithm = new GraphDissectPaths(startingNodes, cycleNodes, endingNodes, remainingNodes, graph);
        algorithm.invoke();
        this.pathFromCyclesToEnding = algorithm.pathFromCyclesToEnding;
        this.pathFromStartingToCycles = algorithm.pathFromStartingToCycles;
        this.fegatelli = algorithm.fegatelli;
        this.pathDirectlyTerminal = algorithm.pathDirecltyTerminal;

        /*pathFromStartingToCycles = generateAllPaths(graph, startingNodes, cycleNodes);
        pathFromCyclesToEnding = generateAllPaths(graph, cycleNodes, endingNodes);*/

        /*// If there are no starting nodes, incrementally choose the starting nodes until I reach some kind of fixpoint.
        // This case needs to be considered when there are no nodes having zero degree from which we know for sure where to start from.
        Set<String> remainingVertices = null;
        Set<List<String>> startingPaths = null;
        if (startingNodes.isEmpty()) {
            System.err.println("INFO - Incrementally choose the starting nodes until I reach some kind of fixpoint");

            {
                DijkstraShortestPath<String, Edge> sp = new DijkstraShortestPath<>(graph);
                do {
                    HashSet<String> maxes = new HashSet<>();
                    if (startingPaths == null)
                        startingPaths = new HashSet<>();
                    else {
                        startingPaths.clear();
                        remainingVertices.clear();
                    }
                    for (String src : this.startingNodes) {
                        for (String dst : this.cycleMaxVertices) {
                            GraphPath<String, Edge> path = sp.getPath(src, dst);
                            if (path != null) {
                                List<String> vertexList = path.getVertexList();
                                // Then, interrupt the path on the vertex of maximum degree
                                String maxDegVTX = Collections.max(vertexList, Comparator.comparingInt(degreeMap::get));
                                vertexList = vertexList.subList(0, vertexList.indexOf(maxDegVTX) + 1);
                                startingPaths.add(vertexList);
                                maxes.add(maxDegVTX);
                            }
                        }
                    }
                    if (remainingVertices == null)
                        remainingVertices = new HashSet<>(graph.vertexSet());
                    else {
                        remainingVertices.addAll(graph.vertexSet());
                    }
                    cycleSet.forEach(remainingVertices::removeAll);
                    startingPaths.forEach(remainingVertices::removeAll);
                    maxes.forEach(max -> graph.outgoingEdgesOf(max).forEach(e -> startingNodes.add(e.dst)));

                } while (startingNodes.addAll(remainingVertices) || cycleMaxVertices.addAll(remainingVertices));
            }
        } else {
            startingPaths = new HashSet<>();
            remainingVertices = new HashSet<>();
        }

        // Now I need to detect which are the paths connecting the starting nodes to the paths.

        // The vertices that I should reach from the
        cycleMaxVertices.clear();
        cycleSet.stream().flatMap(x -> x.stream()).distinct().forEach(cycleMaxVertices::add);

        // Clearing the starting nodes: if a starting node is reached from another, this one shall not be a starting node
        AllDirectedPaths2 sp = new AllDirectedPaths2(graph);
        if (true) {
            System.err.println("INFO - Getting all the paths from the starting nodes towards the nodes having a sort of cycle");
            //AllDirectedPaths<String, Edge> sp = new AllDirectedPaths<>(graph);
            do {
                startingPaths.clear();
                remainingVertices.clear();
                for (List<String> vertexList : sp.getAllPaths(startingNodes, cycleMaxVertices, false)) {
                    // List<String> vertexList = path.getVertexList();
                    // Then, interrupt the path on the vertex of maximum degree
                    String maxDegVTX = Collections.max(vertexList, Comparator.comparingInt(degreeMap::get));
                    vertexList = vertexList.subList(0, vertexList.indexOf(maxDegVTX) + 1);
                    startingPaths.add(vertexList);
                }
                remainingVertices.addAll(graph.vertexSet());
                cycleSet.forEach(remainingVertices::removeAll);
                startingPaths.forEach(remainingVertices::removeAll);
            } while (startingNodes.addAll(remainingVertices) || cycleMaxVertices.addAll(remainingVertices));

            if (startingNodes.size() == graph.vertexSet().size())
                throw new UnexpectedException("This algorithm is extimated to be efficient iif. the starting nodes are not all the vertices of the graph");

            //Set<List<String>> sp2 = startingPaths;
            //startingPaths.stream().filter(x -> sp2.stream().anyMatch(ls -> Collections.indexOfSubList(ls, x) != -1)).collect(Collectors.toList()).forEach(sp2::remove);

            // Getting all the cycles that will lead to a fixpoint
            System.out.println("Cycles:");
            System.out.println("===============");
            HashSet<String> cycleNodes = new HashSet<>();
            HashMultimap<String, List<String>> cyclesWithTerminals = HashMultimap.create();
            cycleSet.forEach(x -> {
                cyclesWithTerminals.put(x.get(x.size() - 1), x);
            });
            cyclesWithTerminals.asMap().forEach((x, y) -> {
                System.out.println(x);
                y.forEach(z -> {
                    cycleNodes.addAll(z);
                    System.out.println("\t" + z);
                });
                graph.outgoingEdgesOf(x).forEach(z -> {
                    if (!x.equals(z.dst)) System.out.println("\t -->" + z.dst);
                });
            });

            //
            System.out.println("Starting paths:");
            System.out.println("```````````````");
            startingPaths.forEach(x -> {
                ArrayList<List<String>> splittedPath = new ArrayList<>();
                getLesserPosition(x, cycleNodes, splittedPath, false);
                for (List<String> subpath : splittedPath) {
                    if (startingNodes.contains(subpath.get(0))) {
                        if (graph.inDegreeOf(subpath.get(0)) == 0)
                            this.pathsBeforeFixPointsWithNoInput.add(subpath);
                        else if (graph.outDegreeOf(subpath.get(subpath.size() - 1)) != 0) {
                            if (graph.inDegreeOf(subpath.get(0)) == 1 && graph.getEdge(subpath.get(0), subpath.get(0)) != null) {
                                this.reflOrTtransFromTheBeginning.add(subpath.get(0));
                                this.reflOrTtrans.remove(subpath.get(0));
                            } else
                                this.pathsBeforeFixPointsWithInput.add(subpath);
                        } else
                            this.terminalPaths.add(subpath);
                    } else if (cycleNodes.contains(subpath.get(0))) {
                        if (graph.outDegreeOf(subpath.get(subpath.size() - 1)) != 0)
                            this.pathsCausingMoreComplexConditions.add(subpath);
                        else
                            this.terminalPaths.add(subpath);
                    } else
                        this.pathsCausingMoreComplexConditions.add(subpath);
                }
            });
            System.out.println("Starting paths [Paths before fixpoint]: no input");
            System.out.println("================================================");
            pathsBeforeFixPointsWithNoInput.forEach(y -> System.out.println("\t" + y));

            System.out.println("Paths before fixpoint: input which requires to solve some self-`closure`");
            System.out.println("=================================");
            reflOrTtransFromTheBeginning.forEach(y -> System.out.println("\t" + y));

            System.out.println("Paths before fixpoint: input [??]");
            System.out.println("=================================");
            pathsBeforeFixPointsWithInput.forEach(y -> System.out.println("\t" + y));

            System.out.println("Terminal Paths [Paths after fixpoint]");
            System.out.println("=====================================");
            terminalPaths.forEach(y -> System.out.println("\t" + y));

            System.out.println("Paths causing problems for more complex conditions:");
            System.out.println("===================================================");
            pathsCausingMoreComplexConditions.forEach(y -> System.out.println("\t" + y));
        }*/
    }

    private static List<List<String>> generateAllPaths(DefaultDirectedWeightedGraph<String, Edge> graph, Collection<String> sources, Collection<String> sinks) {
        AllDirectedPaths2 sp = new AllDirectedPaths2(graph);
        return AllCycles.removeSuppaths(sp.getAllPaths(sources, sinks, false));
    }

    private static void createHyperedge(Integer r, Map<Integer, Rule> cps, Map<Integer, Pair<Set<String>, Set<String>>> map) {
        Pair<Set<String>, Set<String>> cp = new Pair<>(new HashSet<>(), new HashSet<>());
        cps.forEach((k, v) -> {
            v.body.stream().map(x -> x.prop.relName).forEach(cp.getKey()::add);
            v.head.stream().filter(x -> x.exists.isEmpty()).map(x -> x.prop.relName).forEach(cp.getValue()::add);
            v.head.stream().filter(x -> !x.exists.isEmpty()).map(x -> x.prop.relName).forEach(cp.getKey()::add);
            if (v.isFinalBottom)
                cp.getValue().add("_bot_");
        });
        map.put(r, cp);
    }

    public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Map.Entry.comparingByValue());
        Map<K, V> result = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    private static void getLesserPosition(List<String> path, Set<String> terminators, List<List<String>> splittedPath, boolean firstMiss) {
        int minPos = Integer.MAX_VALUE;
        for (String candidate : terminators) {
            int candidatePos = path.indexOf(candidate);
            if (candidatePos != -1 && (!firstMiss || minPos != 0)) {
                minPos = Integer.min(minPos, candidatePos);
            }
        }
        if (minPos == 0) {
            // if the element is located at the beginning of the string:
            if (path.size() > 1) {
                // if the path still contains one element from a cycle, then skip it
                try {
                    if (terminators.contains(path.get(1)))
                        getLesserPosition(path.subList(1, path.size()), terminators, splittedPath, false);
                        // otherwise, if the next element is out from the cycle, then ignore the fact that the first element is from a cycle, and continue the search
                    else if (path.size() > 2 && !firstMiss)
                        getLesserPosition(path, terminators, splittedPath, true);
                    else
                        splittedPath.add(path);
                } catch (StackOverflowError e) {
                    System.err.println(path);
                }
            }
        } else if (minPos == Integer.MAX_VALUE) {
            // If I cannot find a terminator
            splittedPath.add(path);
        } else {
            splittedPath.add(path.subList(0, minPos + 1));
            getLesserPosition(path.subList(minPos, path.size()), terminators, splittedPath, false);
        }
    }

    public void plot() {
        MultiGraph graph = new MultiGraph("Tutorial 1");
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.stylesheet", "edge {\n" +
                "\tshape: line;\n" +
                "\tfill-mode: dyn-plain;\n" +
                "\tfill-color: #222, #555, green, yellow;\n" +
                "\tarrow-size: 3px, 2px;\n" +
                "}\n" +
                "\nnode.startingnodes { fill-color: green; }\n" +
                "\nnode.endingnodes { fill-color: red; }\n" +
                "edge.startingpaths { shape:line; fill-color: green; }" +
                "edge.beforepaths { shape:line; fill-color: blue; }" +
                "edge.terminalpaths { shape:line; fill-color: red;  size: 2px; }" +
                "node.singlefixpoint { fill-color: #FF7F50; }" +
                "node.loopnodes { fill-color: blue; }" +
                "edge.bohpaths { fill-color: red; }" +
                "edge.cycles {  size: 2px; fill-color: yellow; stroke-width: 1px; stroke-mode: plain;  }");

        for (String v : this.graph.vertexSet()) {
            Node node = graph.addNode(v);
            node.setAttribute("ui.label", v);
            if (remainingNodes.contains(v))
                node.addAttribute("ui.class", "loopnodes");
            else if (startingNodes.contains(v))
                node.addAttribute("ui.class", "startingnodes");
            else if (endingNodes.contains(Integer.valueOf(v)))
                node.addAttribute("ui.class", "endingnodes");
        }
        for (Edge e : this.graph.edgeSet()) {
            org.graphstream.graph.Edge ep = graph.addEdge(e.src + "_" + e.dst , e.src, e.dst, true);

            //ep.setAttribute("ui.label", e.ruleToNoInstances.values()+"");
        }
        for (List<String> cycle : this.cycleSet) {
            for (int i = 0, n = cycle.size(); i<n; i++ ) {
                int next = (i+1) % (n);
                org.graphstream.graph.Edge ep;
                ep =  graph.getEdge(cycle.get(i)  + "_" +  cycle.get(next));
                // OK! System.out.println("\t\t"+ep.getSourceNode() + " --> " + ep.getTargetNode() );
                if (ep != null) {
                    graph.getNode(cycle.get(i)).addAttribute("ui.class", "singlefixpoint");
                    graph.getNode(cycle.get(next)).addAttribute("ui.class", "singlefixpoint");
                    ep.setAttribute("ui.class", "cycles");
                    graph.removeEdge(ep);
                }
            }
        }

        for (List<String> x : pathFromStartingToCycles) {
            System.out.println("PathsBefore");
            System.out.println(x);
            for (int i = 0, N = x.size(); i < N - 1; i++) {
                Edge e = this.graph.getEdge(x.get(i), x.get(i + 1));
                org.graphstream.graph.Edge ep = graph.getEdge(e.src + "_" + e.dst );
                //ep.setAttribute("ui.class", "beforepaths");
                graph.removeEdge(ep);
            }
        }

        for (List<String> x : pathDirectlyTerminal) {
            System.out.println("PathsBefore");
            System.out.println(x);
            for (int i = 0, N = x.size(); i < N - 1; i++) {
                Edge e = this.graph.getEdge(x.get(i), x.get(i + 1));
                org.graphstream.graph.Edge ep = graph.getEdge(e.src + "_" + e.dst );
                //ep.setAttribute("ui.class", "beforepaths");
                graph.removeEdge(ep);
            }
        }

        /*for (List<String> x : pathsBeforeFixPointsWithNoInput) {
            for (int i = 0, N = x.size(); i < N - 1; i++) {
                Edge e = this.graph.getEdge(x.get(i), x.get(i + 1));
                org.graphstream.graph.Edge ep = graph.getEdge(e.src + "_" + e.dst );
                ep.setAttribute("ui.class", "startingpaths");
            }
        }


        for (List<String> x : pathsBeforeFixPointsWithInput) {
            for (int i = 0, N = x.size(); i < N - 1; i++) {
                Edge e = this.graph.getEdge(x.get(i), x.get(i + 1));
                org.graphstream.graph.Edge ep = graph.getEdge(e.src + "_" + e.dst);
                ep.setAttribute("ui.class", "bohpaths");
            }
        }
        for (List<String> x : pathsCausingMoreComplexConditions) {
            for (int i = 0, N = x.size(); i < N - 1; i++) {
                Edge e = this.graph.getEdge(x.get(i), x.get(i + 1));
                org.graphstream.graph.Edge ep = graph.getEdge(e.src + "_" + e.dst);
                ep.setAttribute("ui.class", "complicated");
            }
        }*/

        /*for (List<String> x : terminalPaths) {
            for (int i = 0, N = x.size(); i < N - 1; i++) {
                Edge e = this.graph.getEdge(x.get(i), x.get(i + 1));
                //org.graphstream.graph.Edge ep = graph.getEdge(e.src + "_" + e.dst);
                //ep.setAttribute("ui.class", "terminalpaths");
                //graph.removeEdge(ep);
            }
        }*/

        for (List<String> x : pathFromCyclesToEnding) {
            for (int i = 0, N = x.size(); i < N - 1; i++) {
                Edge e = this.graph.getEdge(x.get(i), x.get(i + 1));
                org.graphstream.graph.Edge ep = graph.getEdge(e.src + "_" + e.dst);
                //ep.setAttribute("ui.class", "terminalpaths");
                graph.removeEdge(ep);
            }
        }

        for (List<String> x : fegatelli) {
            for (int i = 0, N = x.size(); i < N - 1; i++) {
                Edge e = this.graph.getEdge(x.get(i), x.get(i + 1));
                org.graphstream.graph.Edge ep = graph.getEdge(e.src + "_" + e.dst);
                //ep.setAttribute("ui.class", "terminalpaths");
                graph.removeEdge(ep);
            }
        }

        Set<String> toBeremovedNodes = new HashSet<>();
        for (Node vertex : graph.getNodeSet()) {
            if (vertex.getDegree() == 0) {
                toBeremovedNodes.add(vertex.getId());
            }
        }
        toBeremovedNodes.forEach(graph::removeNode);

        graph.display();
    }

    public static class Edge {
        //public HashMap<Integer, AtomicInteger> ruleToNoInstances = new HashMap<>();
        public String dst, src;
        double weight;

        public double getWeight() {
            return /*ruleToNoInstances.values().stream().mapToInt(AtomicInteger::get).sum()*/weight;
        }

        public void putRuleId(double id) {
            /*AtomicInteger exp = null;
            if ((exp = ruleToNoInstances.get(id)) == null) {
                ruleToNoInstances.put(id, new AtomicInteger(1));
            } else {
                exp.incrementAndGet();
            }*/
            weight = id;
        }
    }

}
