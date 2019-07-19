/*
 * EdgeVertex.java
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

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetDimensionDisambiguationOperations;
import org.ufl.hypogator.jackb.fuzzymatching.LowConfidenceRank;
import org.ufl.hypogator.jackb.fuzzymatching.Similarity;
import org.ufl.hypogator.jackb.html.Table;
import org.ufl.hypogator.jackb.traversers.babelnet.BabelNetTraverser;
import org.ufl.hypogator.jackb.scraper.ScraperSources;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;
import javafx.util.Pair;
import org.jooq.Record2;
import org.ufl.hypogator.jackb.traversers.conceptnet.RecordResultForSingleNode;

import java.io.Serializable;
import java.util.*;

import static org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetDimensionDisambiguationOperations.unrectify;

public class EdgeVertex implements SemanticNetworkEntryPoint, Serializable {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static final long serialVersionUID = 44L;
    transient public BabelNetTraverser parent = null;
    transient private static Similarity sim = LowConfidenceRank.getInstance();

    @JsonProperty("@id")
    @JsonAlias({"@id", "semanticId"})
    public String id;

    @JsonProperty("label")
    @JsonAlias({"label", "value"})
    public String label;

    @JsonProperty("language")
    public String language;

    @JsonProperty("term")
    public String term;

    @JsonProperty("sense_label")
    @JsonAlias({"pos", "sense_label"})
    public String sense_label;

    @JsonProperty("site")
    private String site;

    @JsonProperty("site_available")
    private boolean site_available;

    @JsonProperty("networksource")
    @JsonAlias({"networksource", "generatingSource"})
    private ScraperSources source;



    public void addToHTMLTable(Table t) {
        t.addRow(id, label, language, term, sense_label, source.name());
    }

    public EdgeVertex() {
        eq = new HashSet<>();
    }

    @Override
    public boolean hasPOS() {
        return sense_label != null;
    }

    @Override
    public String getPOS() {
        return sense_label;
    }


    /*public BatchAnswerIterator asAnswer() {
        return new BatchAnswerIterator(new JsonQuery(id, true, false).conceptNetWebApi());
    }*/

    @Override
    public String getLanguage() {
        return language;
    }

    public final Set<SemanticNetworkEntryPoint> eq;
    @Override
    public Collection<SemanticNetworkEntryPoint> equivalenceClassBySemanticId() {
        return eq;
    }

    @Override
    public void addToEquivalenceSet(SemanticNetworkEntryPoint semanticNetworkEntryPoint) {
        eq.add(semanticNetworkEntryPoint);
    }

    @Override
    public String toString() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }

    @Override
    public String getSemanticId() {
        return id;
    }

    @Override
    public String getValue() {
        return label;
    }

    @Override
    public ScraperSources getGeneratingSource() {
        return source;
    }

    @Override
    public void setGeneratingSource(ScraperSources source) {
        this.source = source;
    }


    public static EdgeVertex generateSemanticRoot(String term) {
        EdgeVertex root = new EdgeVertex();

        //getSemanticId()
        root.id = "/aida/root/"+term;

        //getValue
        root.label = term;

        // getGeneratingSource
        root.source = ScraperSources.AIDA;

        root.sense_label = "n";

        root.language = "en";

        return root;
    }

    public RecordResultForSingleNode asRecordResultForSingleNode() {
        return new RecordResultForSingleNode(this.id, new String[]{this.label, this.term}, this);
    }

    public void fromConceptNet(Record2<String, Object[]> stringBigDecimalRecord3) {
        /*String[] stringArray = (String[])stringBigDecimalRecord3.value2();
        if (stringArray == null || stringArray.length == 0)
            stringArray = new String[]{unrectify(stringBigDecimalRecord3.value1())};
        this.id = stringBigDecimalRecord3.component1();
        String fromId = ConceptNetDimensionDisambiguationOperations.unrectify(this.id);
        this.term = Arrays.stream(stringArray).filter(x -> x != null && x.length()>0).map(x -> new Pair<>(x, sim.sim(fromId, x))).max(Comparator.comparing(Pair::getValue)).orElse(new Pair<>(this.term, 1.0)).getKey();
        if (term == null)
            term = unrectify(stringBigDecimalRecord3.value1());
        this.label = this.term;
        String[] split = this.id.split("/");
        this.language = split[2];
        this.sense_label = split.length == 5 ? split[4] : null;*/fromConceptNet(stringBigDecimalRecord3.value1(), stringBigDecimalRecord3.value2());
    }

    public void fromConceptNet(String id, Object[] elements) {
        String[] stringArray = (String[])elements;
        if (stringArray == null || stringArray.length == 0)
            stringArray = new String[]{unrectify(id)};
        this.id = id;
        String fromId = ConceptNetDimensionDisambiguationOperations.unrectify(this.id);
        this.term = Arrays.stream(stringArray).filter(x -> x != null && x.length()>0).map(x -> new Pair<>(x, sim.sim(fromId, x))).max(Comparator.comparing(Pair::getValue)).orElse(new Pair<>(this.term, 1.0)).getKey();
        if (term == null)
            term = unrectify(id);
        this.label = this.term;
        String[] split = this.id.split("/");
        this.language = split[2];
        this.sense_label = split.length == 5 ? split[4] : null;
    }

    public void fromConceptNet(String id, Collection<String> elements) {
        if (elements == null || elements.isEmpty()) {
            elements = new HashSet<>();
            elements.add(unrectify(id));
        }
        this.id = id;
        String fromId = ConceptNetDimensionDisambiguationOperations.unrectify(this.id);
        this.term = elements.stream().filter(x -> x != null && x.length()>0).map(x -> new Pair<>(x, sim.sim(fromId, x))).max(Comparator.comparing(Pair::getValue)).orElse(new Pair<>(this.term, 1.0)).getKey();
        if (term == null)
            term = unrectify(id);
        this.label = this.term;
        String[] split = this.id.split("/");
        this.language = split[2];
        this.sense_label = split.length == 5 ? split[4] : null;
    }

    public static SemanticNetworkEntryPoint fromSpace(String id, String term) {
        EdgeVertex self = new EdgeVertex();
        self.id = "/geonames/"+id;
        self.term = self.label = term;
        self.language = null;
        self.sense_label = null;
        self.source = ScraperSources.GEONAMES;
        return self;
    }

    public static SemanticNetworkEntryPoint fromSpace(long id, String term) {
        EdgeVertex self = new EdgeVertex();
        self.id = "/geonames/"+id;
        self.term = self.label = term;
        self.language = null;
        self.sense_label = null;
        self.source = ScraperSources.GEONAMES;
        return self;
    }

    public static SemanticNetworkEntryPoint fromTime(String x) {
        EdgeVertex self = new EdgeVertex();
        self.id = "/time/"+x;
        self.term = self.label = x;
        self.language = null;
        self.sense_label = null;
        self.source = ScraperSources.STANFORD;
        return self;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EdgeVertex that = (EdgeVertex) o;
        return site_available == that.site_available &&
                Objects.equals(id, that.id) &&
                Objects.equals(label, that.label) &&
                Objects.equals(language, that.language) &&
                Objects.equals(term, that.term) &&
                Objects.equals(sense_label, that.sense_label) &&
                Objects.equals(site, that.site) &&
                source == that.source;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, label, language, term, sense_label, site, site_available, source);
    }
}
