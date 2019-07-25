/*
 * HypoGatorRawTuple.java
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

package org.ufl.hypogator.jackb.ontology.data.tuples;

import org.ufl.hypogator.jackb.disambiguation.dimension.concept.AbstractVocabulary;
import org.ufl.hypogator.jackb.disambiguation.dimension.time.DimTime;
import org.ufl.hypogator.jackb.ontology.data.*;
import org.ufl.hypogator.jackb.ontology.data.tuples.projections.AIDATuple;
import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.streamutils.data.Tuple;
import org.ufl.hypogator.jackb.streamutils.data.Value;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

public class HypoGatorRawTuple {
    private final String id;
    public final String mId;
    public String treeId;
    public HypoGatorKBTypes refersTo;
    public HypoGatorSimpleTextInformation textInformation;
    public HypoGatorType type;
    public ArrayList<String> attribute;
    public HypoGatorDate start;
    public HypoGatorDate end;
    public String kbId;
    public HashMultimap<String, Pair<String, String>> argumentsByLabel_idType;
    public boolean isSolved;
    public String longType;
    public ArrayList<HypoGatorResolvedMentionAssociation> resolvedArguments;
    public boolean hasApproximatedInput;
    HashMultimap<String, String> elements = HashMultimap.create();

    public HypoGatorRawTuple(Tuple t) {
        this(t, null, null);
    }

    public HypoGatorRawTuple(Tuple t, HashMultimap<String, ArrayList<String>> laterResolve, AbstractVocabulary<HypoGatorTrimmedMentions> av) {
        this(t, new HypoGatorDate("start_date_type", "start_date", t), new HypoGatorDate("end_date_type", "end_date", t), laterResolve, av);
    }

    public HypoGatorRawTuple(Tuple t, HypoGatorDate start, HypoGatorDate end, HashMultimap<String, ArrayList<String>> laterResolve, AbstractVocabulary<HypoGatorTrimmedMentions> av) {
        attribute = new ArrayList<>();
        String baseNameType = "";

        String typeString = t.get("type").getAtomAsString();
        if (typeString.equals("Event")) {
            baseNameType = "event";
            refersTo = HypoGatorKBTypes.Event;
        } else if (typeString.equals("Relation")) {
            baseNameType = "relation";
            refersTo = HypoGatorKBTypes.Relationship;
        } else {
            baseNameType = "filler";
            refersTo = HypoGatorKBTypes.Filler;
        }
        treeId = t.get("tree_id").getAtomAsString();
        textInformation = new HypoGatorSimpleTextInformation(t);
        type = new HypoGatorType(t);
        this.start = start;
        this.end = end;
        kbId = t.get("kb_id").getAtomAsString();
        id = t.get("id").getAtomAsString();
        mId = t.get("mid2").getAtomAsString();

        if (laterResolve == null) {
            hasApproximatedInput = false;
            isSolved = false;
            argumentsByLabel_idType = HashMultimap.create();
            Value args = t.get("arguments");
            {
                int n = args.getArraySize();
                for (int i = 0; i < n; i++) {
                    Tuple ti = args.getSubValue(i).getAtom().asTuple();
                    elements.put(ti.get("slot_type").getAtomAsString(), ti.get("description").getAtomAsString());
                    argumentsByLabel_idType.put(ti.get("fieldAttribute").getAtomAsString(), new Pair<>(ti.get("arg_id").getAtomAsString(), ti.get("slot_type").getAtomAsString()));
                }
            }
        } else {
            hasApproximatedInput = true;
            Set<ArrayList<String>> size = laterResolve.get(type.subType);
            if (size == null || size.isEmpty()) {
                throw new RuntimeException("not type found: "+type.subType);
            }
            if (size.size() > 1) {
                throw new RuntimeException("One size per type expected");
            }
            ArrayList<String> types = size.iterator().next();
            int ssize = types.size();
            resolvedArguments = new ArrayList<>(ssize);
            Value args = t.get("arguments");
            int argsN = args.getArraySize();
            int currentExpected = 1;
            for (int i = 0; i<argsN; i++) {
                Tuple arg = args.getSubValue(i).getAtom().asTuple();
                int currentPos = Integer.valueOf(arg.get("arg_slot_pos").getAtomAsString());
                while (currentPos > currentExpected) {
                    resolvedArguments.add(new HypoGatorResolvedMentionAssociation(types.get(currentExpected), currentExpected++));
                }
                Value values = arg.get("values");
                int valN = values.getArraySize();
                // TODO: when having more than oneR argument, extract relationships between the differen data
                Tuple slot = values.getSubValue(0).getAtom().asTuple();
                resolvedArguments.add(new HypoGatorResolvedMentionAssociation(
                        slot.get("slot_type").getAtomAsString(),
                        new HypoGatorTrimmedMentions(slot, true, av),
                        currentExpected++,
                        new Pair<>(slot.get("entitymention_id").getAtomAsString(), slot.get("slotAttribute").getAtomAsString())));
            }
            while (currentExpected < argsN)
                resolvedArguments.add(new HypoGatorResolvedMentionAssociation(types.get(currentExpected), currentExpected++));
            resolveDate();
            isSolved = true;
        }


        if (!t.isKeyEmpty("attribute")) {
            Value tattr = t.get("attribute");
            int n = tattr.getArraySize();
            for (int i = 0; i < n; i++) {
                String s = tattr.getSubValue(i).getAtomAsString().trim();
                if (!s.isEmpty())
                    attribute.add(s);
            }
        }
        if (!t.isKeyEmpty("attribute2")) {
            Value tattr = t.get("attribute2");
            int n = tattr.getArraySize();
            for (int i = 0; i < n; i++) {
                String s = tattr.getSubValue(i).getAtomAsString().trim();
                if (!s.isEmpty())
                    attribute.add(s);
            }
        }
    }

    public Pair<String, String> removeArgument(String label) {
        Set<Pair<String, String>> value = argumentsByLabel_idType.get(label);
        if (value == null || value.isEmpty()) return null;
        Pair<String, String> first = value.iterator().next();
        argumentsByLabel_idType.remove(label, first);
        return first;
    }

    @Override
    public String toString() {
        return "HypoGatorRawTuple{" +
                "type=" + type +
                ", attribute=" + attribute +
                ", start=" + start +
                ", end=" + end +
                ", longType='" + longType + '\'' +
                ", resolvedArguments=" + resolvedArguments +
                '}';
    }

    /**
     * Analyses the data annotation from LDC. Whenether this is possible, it infers both the start and the ending
     *
     *
     * @return
     */
    private final static DimTime time = new DimTime();
    private static String resolveTime(String t) {
        try {
            return time.disambiguate(t).disambiguation.get(0).second.toHierarchy().get(0);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean resolveDate() {
        boolean resolved = false;
        HypoGatorResolvedMentionAssociation item = (resolvedArguments.get(resolvedArguments.size() - 1));
        int index = resolvedArguments.size() - 1;
        // LEGACY: start and end date
        if (start != null) {
            if (!start.date.isEmpty()) {
                resolvedArguments.add(new HypoGatorResolvedMentionAssociation("date." + start.type, new StringMention(start.type.contains("start") ? "start" : "Time", resolveTime(start.date)), index++, item.getMentionId()));
                resolved = true;
            } else {
                resolvedArguments.add(new HypoGatorResolvedMentionAssociation("date." + "starton", new StringMention("start", "?"), index++, item.getMentionId()));
            }
        }
        if (end != null) {
            if (!end.date.isEmpty()) {
                resolved = true;
                resolvedArguments.add(new HypoGatorResolvedMentionAssociation("date." + end.type, new StringMention(end.type.contains("end") ? "end" : "Time", resolveTime(end.date)), index++, item.getMentionId()));
            } else {
                resolvedArguments.add(new HypoGatorResolvedMentionAssociation("date." + "endon", new StringMention("end", "?"), index++, item.getMentionId()));
            }
        }
        long n = (resolvedArguments.stream().filter(x -> {
            String w = x.getLabel();
            return w != null && w.toLowerCase().startsWith("date");
        }).count());
        if (n == 3) {
            resolvedArguments.remove(resolvedArguments.size() - 3);
        } else if (n == 1) {
            // TODO: error only in legacy System.err.println("ERR");
        }
        return resolved;
    }

   /* public HypoGatorRawTuple resolve() {
        return resolveWith(null, null, stringToMention);
    }*/

    public AIDATuple resolveWith(HashMultimap<String, HypoGatorTrimmedMentions> entityResolver, Collection<RawEventRelationship> x3, AbstractVocabulary<HypoGatorTrimmedMentions> stringToMention) {
        if (entityResolver == null || x3 == null) {
            System.err.println("Parallel break");
        }
        if (!this.isSolved) {
            if (x3 == null || entityResolver == null || x3.isEmpty()) {
                this.isSolved = false;
                return asAIDATuple(stringToMention);      // Element cannot be resolved
            } else {
                RawEventRelationship ev = x3.iterator().next();
                this.longType = ev.getLongType();
                resolvedArguments = new ArrayList<>();
                boolean resolvedDate = true;
                // Returns the arguments by order
                ArrayList<String> argumentLabels = ev.getArgumentLabels();
                for (int i = 0, argumentTypesSize = argumentLabels.size(); i < argumentTypesSize; i++) {
                    // Legacy: I had to resolve the arguments
                    String label = argumentLabels.get(i);
                    Pair<String, String> idType = removeArgument(label);
                    Set<HypoGatorTrimmedMentions> mention = entityResolver.get(idType == null ? null : idType.getKey());
                    if (mention.size() != 1) {
                        if (idType == null) {
                            if (Objects.equals(label, "date") || Objects.equals(label, "Time")) {
                                resolvedDate = true;
                                resolveDate();
                            } else {
                                resolvedDate = false;
                                resolvedArguments.add(new HypoGatorResolvedMentionAssociation(label, i));
                            }
                        } else
                            throw new RuntimeException("Unexpected set size for mention id = " + idType + ", size = " + mention.size());
                    } else
                        // TA2 assuming that the data is never negated
                        resolvedArguments.add(new HypoGatorResolvedMentionAssociation(label, mention.iterator().next(), i, idType.getKey()));
                }
                if (!resolvedDate) {
                    resolveDate();
                }
                argumentsByLabel_idType.clear();
                this.isSolved = true;
            }
        }
        int n = resolvedArguments.size();
        HypoGatorResolvedMentionAssociation last = resolvedArguments.get(n - 1);
        if (last.getLabel().equals("date")) {
            last.setLabel("date.starton");
            resolvedArguments.add(new HypoGatorResolvedMentionAssociation("date.endon", last.getValue(), last.getPosition()+1, last.getMentionId(), last.isNegated(), last.isHedged()));
        }
        n = resolvedArguments.size();
        last = resolvedArguments.get(n - 1);
        if (!last.getLabel().startsWith("date.")){
            // If has no temporal information whatsover, then i put a null temporal information
            resolvedArguments.add(new HypoGatorResolvedMentionAssociation("date.starton", last.getPosition()+1));
            resolvedArguments.add(new HypoGatorResolvedMentionAssociation("date.endon", last.getPosition()+2));
        }
        return asAIDATuple(stringToMention);
    }

    public AIDATuple asAIDATuple(AbstractVocabulary<HypoGatorTrimmedMentions> abstractVocabulary) {
        String base;
        String type = "?";
        String kind = this.type.type;
        if (longType != null) {
            String[] args1 = longType.split("\\.");
            base = args1[1]; // Legacy: 2
            type = args1[0]; // Legacy: 1
        } else {
            base = this.type.subType;
        }
        AIDATuple at = new AIDATuple(treeId, id, mId, base, type, kind, resolvedArguments, this.type.subType, hasApproximatedInput, abstractVocabulary);
        if (attribute.contains("not")) {
            at.setNegation();
        }
        if (attribute.contains("hedged")) {
            at.setUncertain();
        }
        return at;
    }
}
