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

import com.google.common.collect.HashMultimap;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.cycle.TarjanSimpleCycles;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import queries.sql.v1.QueryGenerationConf;
import ref.RuleListener;
import types.Rule;

import java.io.IOException;
import java.rmi.UnexpectedException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

// Strong assumption: there are predicates that have

public class DependencyGraph {

    public Collection<String> getPredicates;
    DefaultDirectedWeightedGraph<String, Edge> graph;
    public Set<List<String>> cycles = new HashSet<>();

    // Reflexive or transitive rules that reduce to hooks over one vertex
    public Set<String> reflOrTtrans = new HashSet<>();

    // Reflexive or transitive rules that reduce to hooks over one vertex
    public Set<String> reflOrTtransFromTheBeginning = new HashSet<>();

    // By using the former hooks, I know which are the rules that must applied at the end
    public Set<Integer> lastRuleId = new HashSet<>();

    // I want to start the join computation from the nodes that have no incoming rules, whenever that's possible
    public Set<String> startingNodes = new HashSet<>();

    // This set contains all the paths where the starting node has inDegree as zero and where the nodes belong to the starting nodes
    public Set<List<String>> pathsBeforeFixPointsWithNoInput = new HashSet<>();

    // Paths eventually connecting some cycles, starting from the starting nodes but not arriving into zero outgoing degree nodes
    public Set<List<String>> pathsBeforeFixPointsWithInput = new HashSet<>();

    // Paths not providing cycles that are neither starting nor ending cycles
    public Set<List<String>> pathsCausingMoreComplexConditions = new HashSet<>();

    // Paths arriving into zero outgoing degree nodes
    public Set<List<String>> terminalPaths = new HashSet<>();

    // If there are some hooks that are in starting nodes, then these are all the rules that must be applied first using the fixed point
    public Set<Integer> firstRuleId = new HashSet<>();

    //remove from the inference graph the nodes that have degree 0
    Set<String> toBeRemovedVertices = new HashSet<>();

    // All the cycles that we want to break somehow
    Set<List<String>> cycleSet = new HashSet<>();

    // Terminal node of a cycle
    Set<String> cycleMaxVertices = new HashSet<>();

    public static class Edge {
        public HashMap<Integer, AtomicInteger> ruleToNoInstances = new HashMap<>();
        public String dst, src;

        public Edge() {
        }

        public double getWeight() {
            return ruleToNoInstances.values().stream().mapToInt(AtomicInteger::get).sum();
        }

        public void putRuleId(int id) {
            AtomicInteger exp = null;
            if ((exp = ruleToNoInstances.get(id)) == null) {
                ruleToNoInstances.put(id, new AtomicInteger(1));
            } else {
                exp.incrementAndGet();
            }
        }
    }

    public DependencyGraph(RuleListener l) throws IOException {
        graph = new DefaultDirectedWeightedGraph<>(Edge.class);
        getPredicates = l.schema.keySet();
        for (String x : getPredicates) {
            graph.addVertex(x);
        }
        graph.addVertex("_bot_");
        Iterator<Map.Entry<Integer, Rule>> it = l.ruleTabClassification4DB.values().stream().flatMap(x -> x.entrySet().stream()).iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Rule> cp = it.next();
            int ruleId = cp.getKey();

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
            }
        }

        graph.edgeSet().forEach(x -> {
            graph.setEdgeWeight(x, 1.0 / x.getWeight());
        });

        // removing all the tuples that do not participate in the inference process.
        graph.vertexSet().forEach(v -> {
            if (graph.outDegreeOf(v) + graph.inDegreeOf(v) == 0)
                toBeRemovedVertices.add(v);
        });
        graph.removeAllVertices(toBeRemovedVertices);

        // One of the possible starting points are the nodes with maximum outDegree
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
        Map<String, Integer> degreeMap = new HashMap<>();
        for (String x : graph.vertexSet()) {
            int inDeg = graph.inDegreeOf(x);

            // I will start to produce the rules from the nodes that I know that have no inputs
            if (inDeg == 0) {
                startingNodes.add(x);
            }
            degreeMap.put(x, graph.inDegreeOf(x));
        }

        String maxIndegree = degreeMap.entrySet().stream().max(Comparator.comparingInt(Map.Entry::getValue)).get().getKey();

        if (startingNodes.isEmpty())
            throw new UnexpectedException("This algorithm works by supposing that there are some predicates with indegree of zero");

        // Cycles that will require many possible fixpoints
        TarjanSimpleCycles<String, Edge> cd = new TarjanSimpleCycles<>(graph);
        cd.findSimpleCycles().forEach(x -> {
            if (x.size() == 1) {
                reflOrTtrans.addAll(x);
                String y = x.get(0);
                (startingNodes.contains(y) ? firstRuleId : lastRuleId).addAll(graph.getEdge(y, y).ruleToNoInstances.keySet());
            } else {
                List<String> cycle = new ArrayList<>(x);
                String cycleMaxInDegree = Collections.max(cycle, Comparator.comparingInt(degreeMap::get));
                cycleMaxVertices.add(cycleMaxInDegree);
                int maxIndex = cycle.indexOf(cycleMaxInDegree);
                if (maxIndex != cycle.size() - 1) {
                    List<String> head = new ArrayList<>();
                    ListIterator<String> itt = cycle.listIterator(maxIndex + 1);
                    while (itt.hasNext()) {
                        head.add(itt.next());
                        itt.remove();
                    }
                    head.addAll(cycle);
                    cycle = head;
                }
                cycleSet.add(cycle);
            }
        });

        startingNodes.addAll(startingPoints);

        // Incrementally choose the starting nodes until I reach some kind of fixpoint
        Set<String> remainingVertices = null;
        Set<List<String>> startingPaths = null;
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

        AllDirectedPaths<String, Edge> sp = new AllDirectedPaths<>(graph);
        do {
            startingPaths.clear();
            remainingVertices.clear();
            for (GraphPath<String, Edge> path : sp.getAllPaths(startingNodes, cycleMaxVertices, true, null)) {
                List<String> vertexList = path.getVertexList();
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

        Set<List<String>> sp2 = startingPaths;
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
                    else if (graph.outDegreeOf(subpath.get(subpath.size()-1)) != 0) {
                        if (graph.inDegreeOf(subpath.get(0)) == 1 && graph.getEdge(subpath.get(0), subpath.get(0)) != null) {
                            this.reflOrTtransFromTheBeginning.add(subpath.get(0));
                            this.reflOrTtrans.remove(subpath.get(0));
                        } else
                            this.pathsBeforeFixPointsWithInput.add(subpath);
                    } else
                        this.terminalPaths.add(subpath);
                } else if (cycleNodes.contains(subpath.get(0))) {
                    if (graph.outDegreeOf(subpath.get(subpath.size()-1)) != 0)
                        this.pathsCausingMoreComplexConditions.add(subpath);
                    else
                        this.terminalPaths.add(subpath);
                } else
                    this.pathsCausingMoreComplexConditions.add(subpath);
            }
        });
        System.out.println("Starting paths [Paths before fixpoint]: no input");
        System.out.println("================================================");
        pathsBeforeFixPointsWithNoInput.forEach(y -> System.out.println("\t"+y));

        System.out.println("Paths before fixpoint: input which requires to solve some self-`closure`");
        System.out.println("=================================");
        reflOrTtransFromTheBeginning.forEach(y -> System.out.println("\t"+y));

        System.out.println("Paths before fixpoint: input [??]");
        System.out.println("=================================");
        pathsBeforeFixPointsWithInput.forEach(y -> System.out.println("\t"+y));

        System.out.println("Terminal Paths [Paths after fixpoint]");
        System.out.println("=====================================");
        terminalPaths.forEach(y -> System.out.println("\t"+y));

        System.out.println("Paths causing problems for more complex conditions:");
        System.out.println("===================================================");
        pathsCausingMoreComplexConditions.forEach(y -> System.out.println("\t"+y));
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

    public static String staring_nodes = "";

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
                "edge.startingpaths { shape:line; fill-color: green; }"+
                "edge.beforepaths { shape:line; fill-color: blue; }"+
                "edge.terminalpaths { shape:line; fill-color: #6A5ACD; }"+
                "node.singlefixpoint { fill-color: #FF7F50; }"+
                "edge.bohpaths { fill-color: red; }"+
                "edge.complicated {  size: 2px; stroke-color: orange; stroke-width: 1px; stroke-mode: plain;  }");

        for (String v : this.graph.vertexSet()) {
            Node node = graph.addNode(v);
            node.setAttribute("ui.label", v);
            if (reflOrTtransFromTheBeginning.contains(v))
                node.addAttribute("ui.class", "singlefixpoint");
            else if (startingNodes.contains(v))
                node.addAttribute("ui.class", "startingnodes");
        }
        for (Edge e : this.graph.edgeSet()) {
            org.graphstream.graph.Edge ep = graph.addEdge(e.src + "_" + e.dst + "_" + e.ruleToNoInstances.keySet(), e.src, e.dst, true);
            //ep.setAttribute("ui.label", e.ruleToNoInstances.values()+"");
        }

        for (List<String> x :pathsBeforeFixPointsWithNoInput) {
            for (int i = 0, N = x.size(); i<N-1; i++) {
                Edge e = this.graph.getEdge(x.get(i), x.get(i + 1));
                org.graphstream.graph.Edge ep = graph.getEdge(e.src + "_" + e.dst + "_" + e.ruleToNoInstances.keySet());
                ep.setAttribute("ui.class", "startingpaths");
            }
        }
        for (List<String> x : pathsBeforeFixPointsWithInput) {
            for (int i = 0, N = x.size(); i<N-1; i++) {
                Edge e = this.graph.getEdge(x.get(i), x.get(i + 1));
                org.graphstream.graph.Edge ep = graph.getEdge(e.src + "_" + e.dst + "_" + e.ruleToNoInstances.keySet());
                ep.setAttribute("ui.class", "beforepaths");
            }
        }
        for (List<String> x : terminalPaths) {
            for (int i = 0, N = x.size(); i<N-1; i++) {
                Edge e = this.graph.getEdge(x.get(i), x.get(i + 1));
                org.graphstream.graph.Edge ep = graph.getEdge(e.src + "_" + e.dst + "_" + e.ruleToNoInstances.keySet());
                ep.setAttribute("ui.class", "terminalpaths");
            }
        }
        for (List<String> x : pathsBeforeFixPointsWithInput) {
            for (int i = 0, N = x.size(); i<N-1; i++) {
                Edge e = this.graph.getEdge(x.get(i), x.get(i + 1));
                org.graphstream.graph.Edge ep = graph.getEdge(e.src + "_" + e.dst + "_" + e.ruleToNoInstances.keySet());
                ep.setAttribute("ui.class", "bohpaths");
            }
        }
        for (List<String> x :pathsCausingMoreComplexConditions) {
            for (int i = 0, N = x.size(); i<N-1; i++) {
                Edge e = this.graph.getEdge(x.get(i), x.get(i + 1));
                org.graphstream.graph.Edge ep = graph.getEdge(e.src + "_" + e.dst + "_" + e.ruleToNoInstances.keySet());
                ep.setAttribute("ui.class", "complicated");
            }
        }

        graph.display();
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
            splittedPath.add(path.subList(0, minPos+1));
            getLesserPosition(path.subList(minPos,path.size()),  terminators, splittedPath, false);
        }
    }

}
