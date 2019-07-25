/*
 * InconsistencyDetection.java
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

package org.ufl.hypogator.jackb.server.handlers.concrete;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.HashMultimap;
import javafx.util.Pair;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.AbstractVocabulary;
import org.ufl.hypogator.jackb.fuzzymatching.TwoGramIndexer;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;
import org.ufl.hypogator.jackb.inconsistency.legacy.TupleComparator;
import org.ufl.hypogator.jackb.logger.LoggerFactory;
import org.ufl.hypogator.jackb.ontology.JsonOntologyLoader;
import org.ufl.hypogator.jackb.ontology.data.HypoGatorTrimmedMentions;
import org.ufl.hypogator.jackb.ontology.data.RawEventRelationship;
import org.ufl.hypogator.jackb.ontology.data.tuples.HypoGatorRawTuple;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.ufl.hypogator.jackb.ontology.data.tuples.projections.AIDATuple;
import org.ufl.hypogator.jackb.ontology.data.tuples.projections.EquivalenceObject;
import org.ufl.hypogator.jackb.server.handlers.abstracts.SimplePostRequest;
import org.ufl.hypogator.jackb.streamutils.collectors.*;
import org.ufl.hypogator.jackb.streamutils.data.AlgebraSupport;
import org.ufl.hypogator.mr.Fact;

import java.util.*;
import java.util.stream.Collectors;

public class InconsistencyDetection extends SimplePostRequest {
    private static ObjectReader reader = new ObjectMapper().readerFor(new TypeReference<Fact>() {});
    private static ObjectMapper writer = new ObjectMapper();
    private final static org.ufl.hypogator.jackb.logger.Logger LOGGER = LoggerFactory.getLogger(InconsistencyDetection.class);
    private final static boolean parallelize = false;

    public static class Inconsistencies {
        public List<List<String>> inconsistencies;

        @JsonCreator
        public Inconsistencies(@JsonProperty("inconsistencies") List<List<String>> incos) {
            this.inconsistencies = incos;
        }

        public Inconsistencies() {
            inconsistencies = new ArrayList<>();
        }
    }

    public InconsistencyDetection() {
    }

    @Override
    public String handleContent(String content, HashMultimap<String,String> arguments) {
        setContentType("application/json");
        try {
            // Mentions that have been collected
            final AbstractVocabulary<HypoGatorTrimmedMentions> stringToMention =
                    new AbstractVocabulary<>(new TwoGramIndexer<>(HypoGatorTrimmedMentions::getPrecomputedElements));
            Inconsistencies toReturn = new Inconsistencies();

            // Obtaining the unique stirng representation from the KB
            // This operation performs a transformation into a leagy data representation.

            // ---> Translation into SQL: select all the elements having the same kbId, and associate to them all the possible string representations

            // TODO: use the same data representation
            LOGGER.out("Creating the entity resolver (legacy)");
            HashMap<String, HypoGatorTrimmedMentions> entityResolver = new AlgebraSupport<Fact>(reader.readValues(content))
                    .map(t -> t.refactor().asTrimmedMention()) // Getting the trimmed mentions only for entities and fillers. The other ones here have no kbId
                    .map(t -> new HypoGatorTrimmedMentions(t, stringToMention))
                    .collectAndRestream(new CollectToHashMultimap<>(false, (HypoGatorTrimmedMentions x) -> x.kbIds.iterator().next()))
                    .map(x -> {
                        HypoGatorTrimmedMentions elem = x.getValue().iterator().next();
                        for (HypoGatorTrimmedMentions z : x.getValue()) {
                            elem.apply(z);
                        }
                        return new Pair<>(x.getKey(), elem);
                    })
                    .collect(new ObjectGroupByWithValue<Pair<String, HypoGatorTrimmedMentions>, String, HypoGatorTrimmedMentions>(parallelize, Pair::getKey, Pair::getValue));

            // TODO: all
            LOGGER.out("Creating the eventfiller_map and the term resolver");
            HashMultimap<String, HypoGatorTrimmedMentions> eventfiller_map = HashMultimap.create();
            for (Map.Entry<String, HypoGatorTrimmedMentions> kbIdToAggregatedMention : entityResolver.entrySet()) {
                for (String id : kbIdToAggregatedMention.getValue().mentionedInId) {
                    eventfiller_map.put(id, kbIdToAggregatedMention.getValue());
                    for (Iterator<String> iterator = kbIdToAggregatedMention.getValue().arguments(); iterator.hasNext(); ) {
                        String k = iterator.next();
                        if (kbIdToAggregatedMention.getValue().getType().contains("Entity") || kbIdToAggregatedMention.getValue().getType().contains("Filler"))
                            stringToMention.forcePut(k.toLowerCase(), kbIdToAggregatedMention.getValue());
                    }
                }
            }
            entityResolver.clear();
            
            // The method "resolved" names something that has been disambiguated
            LOGGER.out("Collecting all the mentions");
            HashMultimap<EquivalenceObject, AIDATuple> map2 = new AlgebraSupport<Fact>(reader.readValues(content))
                    .yield(Fact::refactor)
                    .map(t -> t.refactor().asTuple())
                    .collectAndRestream(TupleGroupBy.perform(parallelize, "arguments","mid", "attribute", "type", "subtype", "id", "mid2", "tree_id"))
                    .map(t -> new HypoGatorRawTuple(t, null, null, null, stringToMention))


                    // Now I'm co-grouping the resolved mentions to the events, so that I can attach the event/relationships to the resolved facts
                    //
                    // The subtype is descriptive enough
                    // Providing the event/relations resolution. Trying to fill in the Entities and fillers
                    .<String, RawEventRelationship, Collection<RawEventRelationship>, HashMultimap<String, RawEventRelationship>, AIDATuple>coGroupWithLeftSelector(aidaResolvedArgsMention -> aidaResolvedArgsMention.type.subType)
                    .<HypoGatorRawTuple, String, Collection<RawEventRelationship>, HashMultimap<String, RawEventRelationship>, AIDATuple>withGropuedRight(JsonOntologyLoader.getInstance().asBackwardRepresentation(), new MatchEventRelWithAIDAResolvedargs(false))
                    .where((m, s) -> m.get(s), (x1, x2, x3) -> x1.resolveWith(eventfiller_map, x3, stringToMention))
                    // Expliciting the attributes for negation and hedged
                    //.map(t -> t.asAIDATuple(stringToMention))

                    // Grouping by key
                    .collect(new ObjectGroupBy<>(parallelize, t -> t.equivalenceObject));

            // Removing duplicated tuples: this allows to reduce the number of comparisons
            LOGGER.out("Removing duplicated tuples");
            //List<AIDATuple> ls = new ArrayList<>(map2.size());
            HashMultimap<AIDATuple.Key, AgileRecord> ls = HashMultimap.create();
            for (Collection<AIDATuple> tColl : map2.asMap().values()) {
                List<String> ids = tColl.stream().map(x -> x.mentionId).collect(Collectors.toList());
                AIDATuple candidate = tColl.iterator().next();
                candidate.setSimilarMentionIds(ids);
                AgileRecord r = candidate.asAgileRecord();
                if (r.schema.size() > 1)
                ls.put(candidate.equivalenceObject.key, r);
            }

            map2.clear();

            //ls.values().stream().filter(x -> x.mentionsId.contains("VM779963.000383") || x.mentionsId.contains("VM779963.000365") || x.mentionsId.contains("VM779963.000032") || x.mentionsId.contains("VM779963.000347")).forEach(System.out::println);
            LOGGER.out("Obtaining the tuples over which evaluate the inconsistency");
            TupleComparator comparator = TupleComparator.getDefaultTupleComparator();
            List<List<String>> inconsistentPair = new ArrayList<>();

            ls.asMap().forEach((k, lsT) -> {
                LOGGER.out(k.toString());
                HashMultimap<List<String>, AgileRecord> hmm = HashMultimap.create();
                for (AgileRecord r : lsT) {
                    hmm.put(r.schema, r);
                }

                // Effectively comparing the tuples within the same schema
                hmm.asMap().forEach((s, lsR) -> {
                    List<AgileRecord> arl = new ArrayList<>(lsR);
                    memoryWriteInconsistencies(comparator, inconsistentPair, s, arl);
                });

                // Getting the inconsistencies between all the elements with subset schemas, but having not the same schema
                for (List<String> schemas1 : hmm.keySet()) {
                    for (List<String> schemas2 : hmm.keySet()) {
                        if (schemas1.containsAll(schemas2) && (!schemas2.containsAll(schemas1))) { // the first is a superset of the second, and they should not be equivalent sets
                            ArrayList<AgileRecord> lsL = new ArrayList<>(hmm.get(schemas1));
                            ArrayList<AgileRecord> lsR = new ArrayList<>(hmm.get(schemas2));

                            LOGGER.out("\t ~~ "+schemas1+" ^^ "+schemas2);
                            int N = lsL.size();
                            int M = lsR.size();
                            for (int i = 0; i < N; i++) {
                                AgileRecord ari = lsL.get(i);
                                if (!schemas2.containsAll(schemas1)) {
                                    ari = ari.projectWith(schemas2);
                                }
                                for (int j=0; j<M; j++) {
                                    AgileRecord arj = lsR.get(j);

                                    // If the two records are the same after the projection, then skip
                                    if (ari.equals(arj)) continue;

                                    PartialOrderComparison cmp = comparator.compare(ari, arj);
                                    if (cmp.t.equals(POCType.Uncomparable)) {
                                        for (String idLeft : ari.mentionsId) {
                                            for (String idRight : arj.mentionsId) {
                                                inconsistentPair.add(Arrays.asList(idLeft, idRight));
                                            }
                                        }
                                    }
                                }
                            }
                            System.out.println("#incons tot. = "+ inconsistentPair.size());
                        }
                    }
                }
            });

            return writer.writeValueAsString(new Inconsistencies(inconsistentPair));
        } catch (Exception e) {
            e.printStackTrace();
            return "[]";
        }

    }

    private void memoryWriteInconsistencies(TupleComparator comparator, List<List<String>> inconsistentPair, List<String> s, List<AgileRecord> arl) {
        LOGGER.out("\t"+s.toString()+" [equivalence]");
        int N = arl.size();
        for (int i = 0; i < N; i++) {
            AgileRecord ari = arl.get(i);
            for (int j=0; j<i; j++) {
                AgileRecord arj = arl.get(j);
                PartialOrderComparison cmp = comparator.compare(ari, arj);
                if (cmp.t.equals(POCType.Uncomparable)) {
                    for (String idLeft : ari.mentionsId) {
                        for (String idRight : arj.mentionsId) {
                            inconsistentPair.add(Arrays.asList(idLeft, idRight));
                        }
                    }
                }
            }
        }
        System.out.println("#incons tot. = "+ inconsistentPair.size());
    }


}
