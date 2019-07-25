/*
 * Edge.java
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

package org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.raw_type.RelationshipTypes;
import org.ufl.hypogator.jackb.utils.Scored;
import org.ufl.hypogator.jackb.scraper.ScraperSources;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;

import java.util.List;
import java.util.Objects;

public class Edge implements Scored {
    @JsonProperty("@id")
    public String id;

    @JsonProperty("dataset")
    private String dataset;

    @JsonProperty("end")
    public EdgeVertex end;

    @JsonProperty("license")
    private String license;

    @JsonProperty("rel")
    public Relationship rel;

    @JsonProperty("sources")
    private List<Source> sources;

    @JsonProperty("surfaceText")
    public String surfaceText;

    @JsonProperty("weight")
    public double weight;

    @JsonProperty("start")
    public EdgeVertex start;

    @JsonProperty("context")
    private List<String> context;
    private ScraperSources source;

    @Override
    public double uncertainty() {
        return weight;
    }

    public String getEvidence() {
        return surfaceText;
    }

    public SemanticNetworkEntryPoint getTarget() {
        return end;
    }

    public String getSemanticId() {
        return id;
    }

    public SemanticNetworkEntryPoint getSource() {
        return start;
    }

    public void setGeneratingSource(ScraperSources source) {
        this.source = source;
    }

    public ScraperSources getGeneratingSource() {
        return source;
    }

    public boolean isAnglophone() {
        String langSrc = start.getLanguage();
        return langSrc.equals("en") && langSrc.equals(end.getLanguage());
    }

    public boolean notVerbOrAdjective() {
        return start.getPOS() == null || start.getPOS().equals("n");
    }

    public boolean anglophoneAndNotVerbOrAdjective() {
        return isAnglophone() && notVerbOrAdjective();
    }

    public RelationshipTypes getRelationship() {
        String id = rel.getId();
        String spl[] = null;
        if (id != null) {
            spl = rel.getId().trim().split("/");
        }
        if (spl != null && spl.length == 0) {
            spl = null;
        }
        return RelationshipTypes.valueOf(spl == null ?  rel.getLabel() : spl[spl.length - 1]);
    }

    @Override
    public String toString() {
        return "Edge{" +
                "end=" + end +
                ", rel=" + getRelationship().toString() +
                ", surfaceText='" + surfaceText + '\'' +
                ", weight=" + weight +
                ", start=" + start +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge = (Edge) o;
        return Double.compare(edge.weight, weight) == 0 &&
                Objects.equals(id, edge.id) &&
                Objects.equals(dataset, edge.dataset) &&
                Objects.equals(end, edge.end) &&
                Objects.equals(license, edge.license) &&
                Objects.equals(rel, edge.rel) &&
                Objects.equals(sources, edge.sources) &&
                Objects.equals(surfaceText, edge.surfaceText) &&
                Objects.equals(start, edge.start) &&
                Objects.equals(context, edge.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dataset, end, license, rel, sources, surfaceText, weight, start, context);
    }

    public String getSurfaceText() {
        return surfaceText;
    }

    public List<String> getContext() {
        return context;
    }
}
