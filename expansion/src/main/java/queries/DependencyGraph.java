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
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import queries.graph.GraphDissectPaths;
import queries.sql.v1.QueryGenerationConf;
import queries.sql.v1.SelectFromWhere;
import ref.RuleListener;
import types.Rule;

import java.io.IOException;
import java.util.*;

// Strong assumption: there are predicates that have

public class DependencyGraph {

    public static String staring_nodes = "";
    private final RuleListener l;
    private final QueryGenerationConf qgc;
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
    //public Set<List<String>> pathsBeforeFixPointsWithNoInput = new HashSet<>();


    public Set<List<String>> pathFromStartingToCycles;
    public Set<List<String>> pathFromCyclesToEnding;
    public Set<List<String>> pathDirectlyTerminal;

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
        this.l = l;
        this.qgc = qgc;
        generatePathsForExpansionModule();
    }

    public GraphDissectPaths generatePathsForExpansionModule() {
        System.err.println("INFO -  Creating the graph");
        graph = new DefaultDirectedWeightedGraph<>(Edge.class);
        // Inconsistency will be another node.
        graph.addVertex("_bot_");

        // Creating the map for all the high-level rule connected to the element
        Map<Integer, Pair<Set<String>, Set<String>>> map = new HashMap<>();

        l.ruleTabClassification4DB.forEach((key, value) -> {
            SelectFromWhere compiledQuery = qgc.compileQuery(l.idToRuleTab.get(key));
            if (compiledQuery != null) {
                createHyperedge(key, value, map);
                remainingNodes.add(key.toString());
                graph.addVertex(key.toString());
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

        // Sorting the vertices by ingoing degree
        System.err.println("INFO - Sorting the vertices by ingoing degree");
        Set<String> cycleNodes = new HashSet<>();
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
                .forEach(cycle -> {
                    if (cycle.size() == 1) {
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
                        cycle.forEach(y -> remainingNodes.remove(y));
                        cycleSet.add(cycle);
                        cycleNodes.addAll(cycle);
                        startingNodes.removeAll(cycle);
                    }
                });


        firstRuleId.forEach(x -> startingNodes.add(x.toString()));
        System.out.println("Starting nodes: " + startingNodes);
        System.out.println("Ending nodes: " + endingNodes);

        remainingNodes.removeAll(startingNodes);
        endingNodes.forEach(x -> remainingNodes.remove(x.toString()));
        System.out.println("\n\nRemaining nodes: " + remainingNodes);
        // Supposedly, these nodes should be the ones connecting the starting nodes to the ones in the cycles, or the ones connecting the starting nodes to the ending nodes.

        System.out.println("INFO: Generating all the paths going directly from the beginning to the terminal nodes, and not passing through the cycleNodes");

        GraphDissectPaths algorithm = new GraphDissectPaths(startingNodes, cycleNodes, endingNodes, remainingNodes, graph);
        algorithm.invoke();
        this.pathFromCyclesToEnding = algorithm.pathFromCyclesToEnding;
        this.pathFromStartingToCycles = algorithm.pathFromStartingToCycles;
        this.fegatelli = algorithm.fegatelli;
        this.pathDirectlyTerminal = algorithm.pathDirecltyTerminal;
        return algorithm;
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
            org.graphstream.graph.Edge ep = graph.addEdge(e.src + "_" + e.dst, e.src, e.dst, true);

            //ep.setAttribute("ui.label", e.ruleToNoInstances.values()+"");
        }
        for (List<String> cycle : this.cycleSet) {
            for (int i = 0, n = cycle.size(); i < n; i++) {
                int next = (i + 1) % (n);
                org.graphstream.graph.Edge ep;
                ep = graph.getEdge(cycle.get(i) + "_" + cycle.get(next));
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
                org.graphstream.graph.Edge ep = graph.getEdge(e.src + "_" + e.dst);
                graph.removeEdge(ep);
            }
        }

        for (List<String> x : pathDirectlyTerminal) {
            System.out.println("PathsBefore");
            System.out.println(x);
            for (int i = 0, N = x.size(); i < N - 1; i++) {
                Edge e = this.graph.getEdge(x.get(i), x.get(i + 1));
                org.graphstream.graph.Edge ep = graph.getEdge(e.src + "_" + e.dst);
                graph.removeEdge(ep);
            }
        }

        for (List<String> x : pathFromCyclesToEnding) {
            for (int i = 0, N = x.size(); i < N - 1; i++) {
                Edge e = this.graph.getEdge(x.get(i), x.get(i + 1));
                org.graphstream.graph.Edge ep = graph.getEdge(e.src + "_" + e.dst);
                graph.removeEdge(ep);
            }
        }

        for (List<String> x : fegatelli) {
            for (int i = 0, N = x.size(); i < N - 1; i++) {
                Edge e = this.graph.getEdge(x.get(i), x.get(i + 1));
                org.graphstream.graph.Edge ep = graph.getEdge(e.src + "_" + e.dst);
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
        public String dst, src;
        double weight;

        public double getWeight() {
            return weight;
        }

        public void putRuleId(double id) {
            weight = id;
        }
    }

}
