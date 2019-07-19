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

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.util.Pair;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetVocabulary;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;
import org.ufl.hypogator.jackb.utils.FileChannelLinesSpliterator;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;
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
public class DiGraphEquivalenceClass extends DiGraph<SemanticNetworkEntryPoint> {

    private HashMap<String, SemanticNetworkEntryPoint> map;
    private static final ObjectMapper om = new ObjectMapper();

    public DiGraphEquivalenceClass() {
        super();
        map = new HashMap<>();
    }

    private static String rectifyId(String s) {
        String array[] = s.split("/");
        return "/"+array[1]+"/"+array[2]+"/"+array[3];
    }

    @Override
    public DiGraphEquivalenceClass loadFromFile(File file, ConceptNetVocabulary voc, Function<String, SemanticNetworkEntryPoint> deserialize) throws IOException {
        FileChannelLinesSpliterator.lines(file.toPath(), Charset.defaultCharset()).forEach(x -> {
            DiGraphEquivalenceClass.this.addEdge(x, voc, deserialize);
        });
        return this;
    }

    public void loadFromFile2(File file, ConceptNetVocabulary voc, Function<String, SemanticNetworkEntryPoint> deserialize) throws IOException {
        FileChannelLinesSpliterator.lines(file.toPath(), Charset.defaultCharset()).forEach(y -> {
            DiGraphEquivalenceClass.this.addEdge(y,voc,  x -> {
                try {
                    return om.readValue(x, EdgeVertex.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            });
        });
    }

    @Override
    public boolean vertexExists(SemanticNetworkEntryPoint s) {
        return map.containsKey(rectifyId(s.getSemanticId())) || super.vertexExists(s);
    }

    @Override
    public Vertex findVertex(SemanticNetworkEntryPoint v) {
        String id = rectifyId(v.getSemanticId());
        SemanticNetworkEntryPoint rawVertex = map.get(id);
        return rawVertex == null ? super.findVertex(v) : new Vertex(rawVertex);
    }

    @Override
    public List<Vertex> getPath(SemanticNetworkEntryPoint from, SemanticNetworkEntryPoint to) {
        return super.getPath(getVertex(from), getVertex(to));
    }

    @Override @Deprecated
    public Pair<Double, List<Vertex>> getPathWithWeights(SemanticNetworkEntryPoint from, SemanticNetworkEntryPoint to) {
        return super.getPathWithWeights(getVertex(from), getVertex(to));
    }


    public Pair<Double, List<SemanticNetworkEntryPoint>> getPathWithWeightsOk(SemanticNetworkEntryPoint from, SemanticNetworkEntryPoint to) {
        Pair<Double, List<Vertex>> cp = super.getPathWithWeights(getVertex(from), getVertex(to));
        return cp == null ? new Pair<>(1.0, new ArrayList<>()) : new Pair<>(cp.getKey(), cp.getValue() == null ? new ArrayList<>() : cp.getValue().stream().map(x -> x.value).collect(Collectors.toList()));
    }

    @Override
    public boolean keyExists(SemanticNetworkEntryPoint label) {
        return this.vertexExists(label);
    }

    public SemanticNetworkEntryPoint getVertex(SemanticNetworkEntryPoint v) {
        return map.get(rectifyId(v.getSemanticId()));
    }

    @Override
    protected SemanticNetworkEntryPoint addVertex(SemanticNetworkEntryPoint v) {
        String id = rectifyId(v.getSemanticId());
        SemanticNetworkEntryPoint rawVertex = map.get(id);
        if (rawVertex == null) {
            super.addVertex(v);
            map.put(id, v);
            rawVertex = v;
        }
        return rawVertex;
    }

    public SemanticNetworkEntryPoint resolveId(String id) {
        return map.get(id);
    }
}