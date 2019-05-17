package org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.db;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetDimensionDisambiguationOperations;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.Edge;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.Relationship;
import org.ufl.hypogator.jackb.scraper.ScraperSources;

import java.util.List;

public class PgObjectString {
    @JsonProperty("start")
    public String start;

    @JsonProperty("end")
    public String end;

    @JsonProperty("rel")
    public String rel;

    @JsonProperty("uri")
    public String uri;

    @JsonProperty("weight")
    public double weight;

    @JsonProperty("license")
    public String license;

    @JsonProperty("surfaceText")
    public String surfaceText;

    @JsonProperty("surfaceStart")
    public String surfaceStart;

    @JsonProperty("surfaceEnd")
    public String surfaceEnd;

    @JsonProperty("sources")
    public List<ObjectNode> sources;

    @JsonProperty("dataset")
    public String dataset;

    @JsonProperty("features")
    public List<String> features;

    public PgObjectString() {}

    private static boolean isAbsoluteUrl(String s){
        return s.startsWith("http:") || s.startsWith("cc:");
    }

    public Edge asBackwardCompatibilityEdge() {
        Edge e = new Edge();
        e.id = uri;
        e.weight = weight;
        e.surfaceText = surfaceText;

        Relationship rel = new Relationship(this.rel, uri);
        e.rel = rel;

        EdgeVertex src = new EdgeVertex();
        src.id = start;
        String[] srcSplit = src.id.split("/"); // TODO
        if (surfaceStart == null) {                   // TODO
            surfaceStart = ConceptNetDimensionDisambiguationOperations.unrectify(start);
        }
        src.label = surfaceStart.trim();
        src.language = srcSplit[2];
        if (srcSplit.length == 5) {
            src.sense_label = srcSplit[4];
        }
        src.setGeneratingSource(ScraperSources.CONCEPTNET);
        e.start = src;


        EdgeVertex dst = new EdgeVertex();
        dst.id = end;
        String[] dstSplit = dst.id.split("/");
        if (surfaceEnd == null) {
            surfaceEnd = ConceptNetDimensionDisambiguationOperations.unrectify(end);
        }
        dst.label = surfaceEnd;
        dst.language = dstSplit[2];
        if (dstSplit.length == 5) {
            src.sense_label = dstSplit[4];
        }
        dst.setGeneratingSource(ScraperSources.CONCEPTNET);
        e.end = dst;

        e.setGeneratingSource(ScraperSources.CONCEPTNET);
        return e;
    }
}
