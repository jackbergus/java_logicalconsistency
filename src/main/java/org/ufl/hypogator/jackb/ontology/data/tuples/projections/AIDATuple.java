/*
 * AIDATuple.java
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

package org.ufl.hypogator.jackb.ontology.data.tuples.projections;

import com.ibm.icu.text.Transliterator;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.AbstractVocabulary;
import org.ufl.hypogator.jackb.fuzzymatching.ldc.LDCMatching;
import org.ufl.hypogator.jackb.fuzzymatching.ldc.LDCResult;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;
import org.ufl.hypogator.jackb.ontology.data.HypoGatorResolvedMentionAssociation;
import org.ufl.hypogator.jackb.ontology.data.HypoGatorTrimmedMentions;
import org.ufl.hypogator.jackb.utils.MemoizationComparator;
import org.ufl.hypogator.jackb.ontology.data.TypedValue;
import org.ufl.hypogator.jackb.utils.TerminalColors;
import org.ufl.hypogator.jackb.streamutils.data.Value;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AIDATuple {

    /**
     * Internal representation of the projection providing the tuple's equivalence class
     */
    public GroupedByConceptsKey equivalenceObject;

    /**
     * Id associated to the element
     */
    public final String id;
    private final String subType;
    public final String mentionId;
    private final boolean hasApproximatedInput;
    public final AbstractVocabulary<HypoGatorTrimmedMentions> abstractVocabulary;
    public final Set<String> subelementsMentionId;
    public Set<String> similarMentionIds;
    public static final LDCMatching extremaRatio = LDCMatching.getInstance();
    public static final Transliterator t = Transliterator.getInstance("Cyrillic-Latin; Latin-Ascii");

    public AIDATuple(String treeId, String id, String mentionId, String base, String type, String kind, ArrayList<HypoGatorResolvedMentionAssociation> resolvedArguments, String subType, boolean hasApproximatedInput, AbstractVocabulary<HypoGatorTrimmedMentions> abstractVocabulary) {
        this.subType = subType;
        this.id = id;
        this.mentionId = mentionId;
        this.hasApproximatedInput = hasApproximatedInput;
        this.abstractVocabulary = abstractVocabulary;
        this.equivalenceObject = new GroupedByConceptsKey(treeId, new Key(base, type, kind, subType, abstractVocabulary), resolvedArguments, abstractVocabulary);
        similarMentionIds = new HashSet<>();
        subelementsMentionId = new HashSet<>();
    }

    public HypoGatorResolvedMentionAssociation get(int i) {
        return equivalenceObject.resolvedArguments.get(i);
    }

    public void addConsequence(String mentionId) {
        subelementsMentionId.add(mentionId);
    }

    public boolean isUncertain() {
        return equivalenceObject.uncertain;
    }

    public Collection<String> getSimilarMentionIds() {
        return similarMentionIds;
    }


    public Collection<String> getExpectedArguments() {
        return equivalenceObject.resolvedArguments.stream()
                .map(HypoGatorResolvedMentionAssociation::getMentionId)
                .collect(Collectors.toList());
    }

    /**
     * Returns the satisfiability provided by the LDC
     *
     * @return
     */
    public Boolean getSat() {
        return this.equivalenceObject.isSat;
    }

    public String attributes() {
        return this.equivalenceObject.attributes();
    }

    public AgileRecord asAgileRecord() {
        AgileRecord record = new AgileRecord(this.equivalenceObject.key.toString());
        for (HypoGatorResolvedMentionAssociation cp : this.equivalenceObject.resolvedArguments) {
            TypedValue valueAndType = cp.getValue();
            String label = cp.getLabel();
            if (valueAndType != null) {
                record.addField(label, valueAndType);
            }
        }
        record.setSimilarMentionsId(similarMentionIds);
        return record;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(mentionId).append("= ");
        if (this.equivalenceObject.isSat != null) {
            sb.append(this.equivalenceObject.isSat ? "VAL " : "INV ");
        }
        if (this.equivalenceObject.negation) sb.append(TerminalColors.ANSI_RED + "¬" + TerminalColors.ANSI_RESET);
        else sb.append(' ');
        if (this.equivalenceObject.uncertain) sb.append(TerminalColors.ANSI_BLUE + "~" + TerminalColors.ANSI_RESET);
        else sb.append(' ');


        sb.append(TerminalColors.RED_BOLD + this.equivalenceObject.key + TerminalColors.ANSI_RESET);
        sb
                //.append(key.base == null ? "?" : key.base)
                .append("(");
        Iterator<HypoGatorResolvedMentionAssociation> it = this.equivalenceObject.resolvedArguments.iterator();
        while (it.hasNext()) {
            HypoGatorResolvedMentionAssociation cp = it.next();
            TypedValue value = cp.getValue();
            String re = null;
            if (value != null) {
                re = TerminalColors.GREEN_UNDERLINED + value.value() + TerminalColors.ANSI_RESET;
            }
            if (re == null) {
                re = " ?:" + cp.getLabel() + " ";
            }
            sb.append(re);
            if (it.hasNext()) sb.append(", ");
        }
        sb.append(") ");
        if (!similarMentionIds.isEmpty())
            sb.append(similarMentionIds.stream().collect(Collectors.joining(",", " Equivalence{", "}")));

        if (!subelementsMentionId.isEmpty())
            sb.append(subelementsMentionId.stream().collect(Collectors.joining(",", " SubelementCandidates{", "}")));
                    //.append(" : ").append(key.type == null ? "?" : key.type).append(" :: ").append(key.kind == null ? "*" : key.kind)
                    ;
        return (sb.toString());
    }

    /**
     * If the only text avaliable for some text is the description, I want to use this description.
     * In some cases we have that text descriptions cannot be trimmed with regexes, and they must
     * be truncated. In order to do so, we can set a maximum number of words that can be used to
     * identify the target. This is mainly done to reduce the evaluation time for
     */
    public static final int MAXIMUM_REPRESENTATION = 5;

    public static LDCResult parseForDisplay(String str) {
        return parseForDisplay(str, false, null, 4);
    }

    public static LDCResult parseForDisplay(String str, AbstractVocabulary<HypoGatorTrimmedMentions> abstractVocabulary) {
        return parseForDisplay(str, false, abstractVocabulary, 4);
    }

    public static LDCResult parseForDisplay(String str, boolean hasApproximatedInput, AbstractVocabulary<HypoGatorTrimmedMentions> abstractVocabulary, int rec) {
        if (rec > 1) {
            if (str == null)
                return null;
            if (str.contains("a.k.a")) {
                int pos = str.indexOf("a.k.a");
                return parseForDisplay(str.substring(0, pos), hasApproximatedInput, abstractVocabulary, rec);
            } else if (str.contains("- ")) {
                int pos = str.indexOf("- ");
                return parseForDisplay(str.substring(0, pos), hasApproximatedInput, abstractVocabulary, rec);
            } else if (str.contains("(")) {
                int pos = str.indexOf('(');
                return parseForDisplay(str.substring(0, pos), hasApproximatedInput, abstractVocabulary, rec);
            } else if (str.contains("[")) {
                int pos = str.indexOf('[');
                return parseForDisplay(str.substring(0, pos), hasApproximatedInput, abstractVocabulary, rec);
            }
        }

        // In some cases there are too much words
        // The best solution would have been to use NLP to detect the subject of the sentence.
        // TODO
        String[] wc = str.split("\\s+");
        if (wc.length > MAXIMUM_REPRESENTATION) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < MAXIMUM_REPRESENTATION; i++) {
                sb.append(wc[i]);
                if (i != MAXIMUM_REPRESENTATION - 1) sb.append(" ");
            }
            str = sb.toString();
        }

        // TODO: Latinization or disambiguation
            if ((hasApproximatedInput && abstractVocabulary != null) || Pattern.matches(".*\\p{InCyrillic}.*", str) || (rec > 0)) {
                if (abstractVocabulary == null) {
                    System.err.println("ERR");
                }
                Map<Double, Collection<HypoGatorTrimmedMentions>> mentions = abstractVocabulary.fuzzyMatch(str, 1, 0.0);
                if (!mentions.isEmpty() && rec > 0) {
                    // false = keep the disambiguation, without any further discussions
                    String resolved = mentions.entrySet().iterator().next().getValue().iterator().next().resolved(rec-1);
                    LDCResult strPremise = parseForDisplay(resolved, false, abstractVocabulary, rec-1);
                    if (strPremise != null && strPremise.resolved != null)
                        str = strPremise.resolved;
                    // false = keep the disambiguation, without any further discussions
                    else if (str != null && resolved != null)
                        str = resolved;
                } else {
                    // If I cannot process it anymore, then just use transliteration, and then try to do more disambiguation
                    String resolved = mentions.entrySet().iterator().next().getValue().iterator().next().resolved(rec -1);
                    str = t.transliterate(resolved);
                }
            }

        LDCResult matched = extremaRatio.bestFuzzyMatch(str);
        return matched == null ? new LDCResult(str) : matched;
    }

    public void setNegation() {
        this.equivalenceObject.negation = true;
    }

    public void setUncertain() {
        this.equivalenceObject.uncertain = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AIDATuple aidaTuple = (AIDATuple) o;
        return Objects.equals(equivalenceObject, aidaTuple.equivalenceObject) &&
                Objects.equals(id, aidaTuple.id) &&
                Objects.equals(subType, aidaTuple.subType) &&
                Objects.equals(mentionId, aidaTuple.mentionId) &&
                Objects.equals(similarMentionIds, aidaTuple.similarMentionIds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(equivalenceObject, id, subType, mentionId, similarMentionIds);
    }

    public boolean contentEquals(AIDATuple o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(equivalenceObject, o.equivalenceObject);
    }


    public void setSimilarMentionIds(Collection<String> similarMentionIds) {
        if (similarMentionIds != null) this.similarMentionIds.addAll(similarMentionIds);
    }

    public void satAssignment(Value value) {
        String v = value.getAtomAsString();
        equivalenceObject.isSat = v.equals("fully-relevant");
    }

    @Deprecated
    private final static MemoizationComparator memoizeCompare = new MemoizationComparator();

    public static class Key {
        public final String base;
        public final String type;
        public final String kind;
        public final String raw;
        private final AbstractVocabulary<HypoGatorTrimmedMentions> abstractVocabulary;

        public Key(String base, String type, String kind, String raw, AbstractVocabulary<HypoGatorTrimmedMentions> abstractVocabulary) {
            this.base = base;
            this.type = type;
            this.kind = kind;
            this.raw = raw;
            this.abstractVocabulary = abstractVocabulary;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Key key = (Key) o;
            return Objects.equals(base, key.base) &&
                    Objects.equals(type, key.type) &&
                    Objects.equals(kind, key.kind);
        }

        @Override
        public int hashCode() {
            return Objects.hash(base, type, kind);
        }

        @Override
        public String toString() {
            return base + ":" + type + "::" + kind;
        }

        public String toSQLTableName() { return (base+"_"+type+"_"+kind).replaceAll("\\-","_"); }

        public GroupedByConceptsKey asGroupedByConceptsKey() {
            return new GroupedByConceptsKey(null, this, Collections.emptyList(), abstractVocabulary);
        }
    }

}
