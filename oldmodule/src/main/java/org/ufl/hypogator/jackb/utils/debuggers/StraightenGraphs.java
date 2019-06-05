package org.ufl.hypogator.jackb.utils.debuggers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.ufl.hypogator.jackb.scraper.ScraperSources;
import org.ufl.hypogator.jackb.scraper.adt.DiGraph;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;

import java.io.*;
import java.util.Set;

public class StraightenGraphs {

    public static void main(String args[]) throws IOException {
        File dir = new File("/media/giacomo/Data/Progetti/hypogator/LogicalCoherence/java_logicalconsistency/data/hierarchies");
        File[] files = dir.listFiles();
        if (files != null) for (File file : files) {
            if (!file.getName().endsWith("_map.json")) continue;
            System.out.println(file.getName()+"-->"+file.getAbsolutePath().replace("/hierarchies/", "/hierarchies2/"));
            ObjectMapper om = new ObjectMapper();
            DiGraph<EdgeVertex> ev = new DiGraph<EdgeVertex>().loadFromFile(file,null, x -> {
                try {
                    return om.readValue(x, EdgeVertex.class);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            });

            DiGraph<EdgeVertex> dstG = new DiGraph<>();
            Set<DefaultWeightedEdge> edges = ev.getEdges();

            for (DefaultWeightedEdge x : edges) {
                EdgeVertex src = ev.graph.getEdgeSource(x);
                EdgeVertex dst = ev.graph.getEdgeTarget(x);
                if (!dst.getGeneratingSource().equals(ScraperSources.AIDA)) {
                    dstG.add(dst, src, ev.graph.getEdgeWeight(x));
                } else {
                    dstG.add(src, dst, ev.graph.getEdgeWeight(x));
                }
            }

            dstG.writeToFile(new File(file.getAbsolutePath().replace("/hierarchies/", "/hierarchies2/")), EdgeVertex::toString);
        }

    }

}
