/*
 * DiGraph.java
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

package org.ufl.hypogator.jackb.scraper.adt;

import javafx.util.Pair;
import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.connectivity.ConnectivityInspector;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleDirectedWeightedGraph;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetVocabulary;
import org.ufl.hypogator.jackb.utils.FileChannelLinesSpliterator;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
/**
 * Creates a directed, weighted <tt>Graph</tt> for any Comparable type
 * <p> add Edge date with <code>add(T valueforVertexFrom, T valueForVertexTo, int cost)</code>
 * <p> use <code>getPath(T valueFrom, T valueTo)</code> to getPairwiseArgument the shortest path between
 * the two using dijkstra's Algorithm
 * <p> If returned List has a size of 1 and a cost of Integer.Max_Value then no conected path
 * was found
 *
 * @author /u/Philboyd_Studge
 */
public class DiGraph<T /*extends Comparable<T>*/> {
    private ConceptNetVocabulary vocabulary;

    public boolean vertexExists(T s) {
        return findVertex(s) != null;
    }

    public boolean keyExists(T label) {
        return vertexExists(label);
    }

    @Deprecated
    public void serialize(File out) throws Exception {
        FileOutputStream bout = new FileOutputStream(out);
        ObjectOutputStream oos = new ObjectOutputStream(bout);
        oos.writeObject(graph);
        oos.flush();
        oos.close();
        bout.close();
    }

    @Deprecated
    public DiGraph<T> deserialize(File in) throws IOException, ClassNotFoundException {
        FileInputStream bin = new FileInputStream(in);
        ObjectInputStream ins = new ObjectInputStream(bin);
        graph = (Graph<T, DefaultWeightedEdge>) ins.readObject();
        return this;
    }

    public void writeToFile(File file, Function<T, String> serialize) throws IOException {
        if (serialize == null)
            serialize = T::toString;
        FileWriter fw = new FileWriter(file);
        for (DefaultWeightedEdge we : graph.edgeSet()) {
            T from = graph.getEdgeSource(we);
            T to = graph.getEdgeTarget(we);
            double weight = graph.getEdgeWeight(we);
            fw.write(serialize.apply(from) + "\t" + serialize.apply(to) + "\t" + weight);
            fw.write("\n");
        }
        fw.close();
    }

    public Graph<T, DefaultWeightedEdge> graph;

    /**
     * Default Constructor
     */
    public DiGraph() {
        graph = new SimpleDirectedWeightedGraph<>(DefaultWeightedEdge.class);
    }

    public Set<DefaultWeightedEdge> getEdges() {
        return graph.edgeSet();
    }

    protected void addEdge(String line, ConceptNetVocabulary voc, Function<String, T> deserialize) {
        if (this.vocabulary == null) {
            this.vocabulary = voc;
        }
        String[] tabs = line.split("\t");
        if (tabs.length >= 2) {
            double d = 1;
            try {
                d = Double.max(0.0, Double.min(Double.valueOf(tabs[2]), 1.0));
            } catch (Exception e) {
            }
            if (voc != null) {
                voc.addTermsFromVertex(deserialize.apply(tabs[0]));
                voc.addTermsFromVertex(deserialize.apply(tabs[1]));
            }
            add(deserialize.apply(tabs[0]), deserialize.apply(tabs[1]), d);
        }
    }

    protected void addInvertedEdge(String line, ConceptNetVocabulary voc, Function<String, T> deserialize) {
        String[] tabs = line.split("\t");
        if (tabs.length >= 2) {
            double d = 1;
            try {
                d = Double.valueOf(tabs[2]);
            } catch (Exception e) {
            }
            if (voc != null) {
                voc.addTermsFromVertex(deserialize.apply(tabs[0]));
                voc.addTermsFromVertex(deserialize.apply(tabs[1]));
            }
            add(deserialize.apply(tabs[1]), deserialize.apply(tabs[0]), d);
        }
    }

    public DiGraph<T> loadFromFile(File file, ConceptNetVocabulary voc, Function<String, T> deserialize) throws IOException {
        System.err.println("[DiGraph::loadFromFile] Reading file="+file.toString());
        FileChannelLinesSpliterator.lines(file.toPath(), Charset.defaultCharset()).forEach(x -> {
            DiGraph.this.addEdge(x, voc, deserialize);
        });
        System.err.println("[DiGraph::loadFromFile] Reading done="+file.toString());
        //Files.readAllLines(file.toPath()).forEach(x -> addEdge(x, deserialize));
        return this;
    }

    public DiGraph<T> loadFromInvertedFile(File file, ConceptNetVocabulary voc, Function<String, T> deserialize) throws IOException {
        FileChannelLinesSpliterator.lines(file.toPath(), Charset.defaultCharset()).forEach(x -> addInvertedEdge(x, voc, deserialize));
        return this;
    }

    protected T addVertex(T v) {
        graph.addVertex(v);
        return v;
    }

    /**
     * Creates Edge from two values T directed from -- to
     *
     * @param from LegacyValue for Vertex 1
     * @param to   LegacyValue for Vertex 2
     * @param cost Cost or weight of edge
     */
    public boolean add(T from, T to, double cost) {
        if (cost <0) cost = 0;
        if (cost > 1.0) isDistanceInverted = true;
        from = addVertex(from);
        to = addVertex(to);
        if (from.equals(to)) {
            //System.err.println("Warning, src == dst (" + from + ")");
            return false;
        }
        DefaultWeightedEdge e = new DefaultWeightedEdge();
        boolean toRet = true;
        if (!graph.addEdge(from, to, e)) {
            // From and to here are already solved
            e = findEdge(from, to);
            cost = Double.max(graph.getEdgeWeight(e), cost);
            toRet = false;
        }
        graph.setEdgeWeight(e, cost);
        return toRet;
    }

    /**
     * find Vertex in Graph from value
     *
     * @param v value of type T
     * @return Vertex, or <code>null</code> if not found.
     */
    public Vertex findVertex(T v) {
        if (graph.containsVertex(v)) {
            return new Vertex(v);
        } else {
            return null;
        }
    }

    /**
     * Find edge from two values
     *
     * @param from from value of type T (already solved)
     * @param to   to value of type T (already solved)
     * @return Edge, or <code>null</code> if not found.
     */
    private DefaultWeightedEdge findEdge(T from, T to) {
        return graph.getEdge(from, to);
    }

    public boolean isConnected() {
        return true;
    }


    // This method is used to find the shortest path. Therefore, I
    boolean isDistanceInverted = false;
    public void invertPathDistance() {
        if (!isDistanceInverted) {
            Iterator<DefaultWeightedEdge> it = graph.edgeSet().iterator();
            while (it.hasNext()) {
                DefaultWeightedEdge e = it.next();
                double e_cost = graph.getEdgeWeight(e);
                e_cost = (1.0 - Integer.MAX_VALUE) * (e_cost - 1.0) + 1.0;
                graph.setEdgeWeight(e, e_cost);
            }
            isDistanceInverted = true;
        }
    }

    public void revertPathDistance() {
        if (isDistanceInverted) {
            Iterator<DefaultWeightedEdge> it = graph.edgeSet().iterator();
            while (it.hasNext()) {
                DefaultWeightedEdge e = it.next();
                double e_cost = graph.getEdgeWeight(e);
                e_cost = (Integer.MAX_VALUE - e_cost) / (Integer.MAX_VALUE - 1.0);
                graph.setEdgeWeight(e, e_cost);
            }
            isDistanceInverted = false;
        }
    }


    /**
     * PUBLIC WRAPPER FOR PRIVATE FUNCTIONS
     * Calls the dijkstra method to build the path tree for the given
     * starting vertex, then calls getGreedyPath method to return
     * a list containg all the steps in the shortest path to
     * the destination vertex.
     *
     * @param from value of type T for Vertex 'from'
     * @param to   value of type T for vertex 'to'
     * @return ArrayList of type String of the steps in the shortest path.
     */

    DijkstraShortestPath<T, DefaultWeightedEdge> algorithm = null;

    public List<Vertex> getPath(T from, T to) {
        // On an absent path, return null (1)
        if (from == null || to == null)
            return null;

        if (algorithm == null) {
            algorithm = new DijkstraShortestPath<>(graph);
        }
        GraphPath<T, DefaultWeightedEdge> path = null;
        if (graph.containsVertex(from) && graph.containsVertex(to)) {
            path = algorithm.getPath(from, to);
        }

        // On an absent path, return null (2)
        if (path == null || path.getEdgeList().isEmpty())
            return null;

        return path.getVertexList().stream().map(Vertex::new).collect(Collectors.toList());
    }

    public long getGraphSize() {
        long size = 0;
        if (graph != null)
            size += graph.vertexSet().size() + graph.edgeSet().size();
        return size;
    }

    public static int checkedAdd(int a, int b) {
        long result = ((long) a) + ((long) b);
        return result == (int) result ? (int) result : Integer.MAX_VALUE;
    }

    public Pair<Double, List<Vertex>> getPathWithWeights(T from, T to) {
        // On an absent path, return null (1)
        if (from == null || to == null)
            return null;

        if (algorithm == null) {
            algorithm = new DijkstraShortestPath<>(graph);
        }
        GraphPath<T, DefaultWeightedEdge> path = null;

        invertPathDistance();
        if (graph.containsVertex(from) && graph.containsVertex(to)) {
            path = algorithm.getPath(from, to);
        }

        // On an absent path, return null (2)
        if (path == null || path.getEdgeList().isEmpty())
            return null;

        int add = 0;
        List<DefaultWeightedEdge> edgeList = path.getEdgeList();
        List<Vertex> toReturn = new ArrayList<>(edgeList.size()+1);
        toReturn.add(new Vertex(graph.getEdgeSource(edgeList.get(0))));
        toReturn.add(new Vertex(graph.getEdgeTarget(edgeList.get(0))));
        for (int i = 1, edgeListSize = edgeList.size(); i < edgeListSize; i++) {
            DefaultWeightedEdge edge = edgeList.get(i);
            add = checkedAdd(add, (int)graph.getEdgeWeight(edge));
            toReturn.add(new Vertex(graph.getEdgeTarget(edgeList.get(i))));
        }

        return new Pair<>((Integer.MAX_VALUE - add) / (Integer.MAX_VALUE - 1.0), toReturn);
    }

    public DiGraph<T> transitiveClosure() {
        GraphOperations.graphClosure(this.graph);
        return this;
    }

    public List<Set<T>> connectedComponents() {
        return new ConnectivityInspector<T, DefaultWeightedEdge>(this.graph).connectedSets();
    }

    public Set<T> vertexSet() {
        return this.graph.vertexSet();
    }


    public class Vertex {
        public final T value;

        public Vertex(T value) {
            this.value = value;
        }

        public int outSize() {
            return graph.outDegreeOf(value);
        }

        public int inSize() {
            return graph.inDegreeOf(value);
        }
    }
}