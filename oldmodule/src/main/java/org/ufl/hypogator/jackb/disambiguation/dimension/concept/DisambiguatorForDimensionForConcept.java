/*
 * ConceptDisambiguator.java
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

package org.ufl.hypogator.jackb.disambiguation.dimension.concept;

import javafx.util.Pair;
import org.apache.log4j.Logger;
import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.disambiguation.DisambiguationAlgorithm;
import org.ufl.hypogator.jackb.disambiguation.DisambiguatorForDimension;
import org.ufl.hypogator.jackb.disambiguation.dimension.Direction;
import org.ufl.hypogator.jackb.fuzzymatching.MultiWordSimilarity;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;
import org.ufl.hypogator.jackb.m9.configuration.Concept5ClientConfigurations;
import org.ufl.hypogator.jackb.traversers.conceptnet.RecordResultForSingleNode;
import org.ufl.hypogator.jackb.utils.adt.HashMultimapSerializer;

import java.io.File;
import java.util.*;

/**
 * When the information is perfect, this code provides the most compliant representation.
 * When this is not the case, this code provides some fuzzy string matching, through which we try to reconstruct
 */
public class DisambiguatorForDimensionForConcept extends ConceptNetDimensionDisambiguationOperations
        implements DisambiguatorForDimension<ResolvedConcept, InformativeConcept> {

    /**
     * The Vocabulary lists all the Concepts's entities from csv file, and loads the concepts.
     */
    //public static final ConceptNetVocabulary voc = ConceptNetVocabulary.readDefaultVocabulary();
    private Map<String, ResolvedConcept> memoization = new HashMap<>();
    static Logger logger = Logger.getLogger(DisambiguationAlgorithm.class);
    static boolean debugging = ConfigurationEntrypoint.getInstance().logging;

    /**
     * Thereshold for the vocabulary's matching
     */

    private static final double threshold = Concept5ClientConfigurations.instantiate().threshold;
    private String[] argumentsForPartof;

    /**
     * The disambiguator has always to be associated to a dimension
     *
     * @param dim LegacyDimension associated
     * @param argumentsForPartof
     */
    public DisambiguatorForDimensionForConcept(String dim, String[] argumentsForPartof) {
        super(dim);
        this.argumentsForPartof = argumentsForPartof;
    }

    private static final MultiWordSimilarity mws = new MultiWordSimilarity();

    @Override
    public InformativeConcept disambiguate(String t) {
        if (t == null) return null;
        if (memoization.containsKey(t)) {
            if (debugging)
                logger.debug("returning the memoization for t="+memoization.get(t));
            return memoization.get(t);
        }
        String dim = getDimension();

        // rectify the string representation. This is required to perform the query
        String tmpUnrectified = t.toLowerCase();
        Direction dir;
        ResolvedConcept resolved = null;

        // TODO: The hierarchy may contain trained element, which did not fit in ConceptNet's Vocabulary.
        // TODO: Therefore, the vocabulary should also contain every element within the hierarchy
        // In order to do so, while loading the graph, I update hte
        if ((!getEnrichedVocabulary().isStopWord(tmpUnrectified)) && getEnrichedVocabulary().containsExactTerm(tmpUnrectified)) {
            t = rectify(t.toLowerCase());
            logger.debug("the term is exactely matched by the vocabulary. This means that it may exist as a nodeg");
            Pair<Direction, Optional<Pair<Double, List<SemanticNetworkEntryPoint>>>> cp = getDirection(dim, t);

            dir = cp.getKey();

            switch (dir) {
                case LEFT_TYPE_RIGHT_SUBTYPE:
                case BOTH:
                    resolved = new ResolvedConcept(t, dim, cp.getValue().get());
                    memoization.put(t, resolved);
                    return resolved;

                case RIGHT_TYPE_LEFT_SUBTYPE:
                case NONE:
                    break;
            }
        } else {
            if (debugging)
                logger.debug("term "+t+" is not in the vocabulary");
        }

        // If either the concept is not within the hierarchy or no right relationship has been found (e.g., the term
        // is a superconcept outside the hierarchy), then I try to perform some fuzzy matching, by also extracting the
        // concept that shares the subword terms as in the term element.
        Map<Double, Collection<RecordResultForSingleNode>> m = getEnrichedVocabulary().fuzzyMatch(threshold, 3, null, t.replace('_', ' ')/*, 3, threshold*/);
        Iterator<Map.Entry<Double, Collection<RecordResultForSingleNode>>> it = m.entrySet().iterator();
        double score = -1;

        // Return only the concept which maximizes the dimensional score
        while (it.hasNext()) {
            Map.Entry<Double, Collection<RecordResultForSingleNode>> cp2 = it.next();
            double d = cp2.getKey();
            for (RecordResultForSingleNode node : cp2.getValue()) {
                for (String x : node.getStrings()) {
                    /*Pair<Double, List<SemanticNetworkEntryPoint>>*/
                    Pair<Direction, Optional<Pair<Double, List<SemanticNetworkEntryPoint>>>> gen = getDirection(dim, x);
                    double key = gen.getValue().isPresent() ? gen.getValue().get().getKey() : 1.0;
                    List<SemanticNetworkEntryPoint> list = gen.getValue().isPresent() ? gen.getValue().get().getValue() : Collections.emptyList();
                    double dGen = (/*gen.getKey()*/ key * .8 + d * 0.2) * mws.sim(t, x);
                    if (resolved == null) {
                        score = dGen;
                        resolved = new ResolvedConcept(unrectify(x), dim, new Pair<>(dGen, list));
                    } else if (Double.max(score, dGen) == dGen) {
                        score = dGen;
                        resolved = new ResolvedConcept(unrectify(x), dim, new Pair<>(dGen, list));
                    }
                }
            }
        }

        if (resolved == null) {
            resolved = new ResolvedConcept(tmpUnrectified, dim, 0.0, Collections.emptyList());
        }
        memoization.put(t, resolved);
        return resolved;
    }

    @Override
    public DisambiguationAlgorithm<ResolvedConcept, InformativeConcept> getAlgorithm(double threshold) {
        return new DisambiguationAlgorithm<>(this, threshold, argumentsForPartof, allowReflexiveExpansion());
    }

    @Override
    public String[] allowedKBTypesForTypingExpansion() {
        return argumentsForPartof;
    }

    @Override
    public boolean allowReflexiveExpansion() {
        return false;
    }

    @Override
    public void serializeToDisk(File f) {
        //super.serializeToDisk(f);
        HashMultimapSerializer.serializeMap(this.memoization, new File(f.getAbsolutePath()+"_fordisambiguation"));
    }

    public Map<String, ResolvedConcept> loadForDisambiguatiuon(File f) {
        return HashMultimapSerializer.unserializeMap(f);
    }

    @Override
    public void loadFromDisk(File file) {
        //super.loadFromDisk(file);
        this.memoization = HashMultimapSerializer.unserializeMap(new File(file.getAbsolutePath()+"_fordisambiguation"));
        if (this.memoization == null)
            this.memoization = new HashMap<>();
    }

    public void appendFromDisk(File file) {
        Map<String, ResolvedConcept> map = HashMultimapSerializer.unserializeMap(new File(file.getAbsolutePath()+"_fordisambiguation"));
        if (map != null)
            this.memoization.putAll(map);
    }
}
