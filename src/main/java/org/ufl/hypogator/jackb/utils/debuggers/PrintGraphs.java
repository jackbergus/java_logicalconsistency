package org.ufl.hypogator.jackb.utils.debuggers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.ufl.hypogator.jackb.scraper.ScraperSources;
import org.ufl.hypogator.jackb.scraper.adt.DiGraph;
import org.ufl.hypogator.jackb.streamutils.collectors.Sink;
import org.ufl.hypogator.jackb.streamutils.data.DataIterator;
import org.ufl.hypogator.jackb.streamutils.functions.FilePrinter;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class PrintGraphs {

    public static void main(String args[]) throws IOException {
        ObjectMapper om = new ObjectMapper();
        File folder = new File("/media/giacomo/Biggus/project_dir/data/hierarchies");
        for (File file : folder.listFiles()) {
            if (file.isFile() && file.getName().endsWith(".json")) {
                FileWriter fw = new FileWriter(file.getAbsoluteFile().getPath().replace(".json", ".txt"));
                DiGraph<EdgeVertex> ev = new DiGraph<EdgeVertex>().loadFromFile(file,null, x -> {
                    try {
                        return om.readValue(x, EdgeVertex.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                });
                String dim = file.getName().replace("_map.json","");
                FilePrinter<String> fps = new FilePrinter<>(dim+".csv");
                new DataIterator<>(ev.getEdges().iterator())
                        .map(x -> ev.graph.getEdgeSource(x).label+","+dim+",true,"+ev.graph.getEdgeTarget(x).label+","+ev.graph.getEdgeWeight(x))
                        .yield(fps)
                        .collect(new Sink<>(false));
                fps.close();
            }
        }
    }
}
