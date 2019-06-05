/*
 * BabelNetTraverser.java
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

package org.ufl.hypogator.jackb.traversers.babelnet;

import org.ufl.hypogator.jackb.traversers.conceptnet.Concept5ClientConfigurations;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.Edge;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.Relationship;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.aggregation_1.SemanticEdge;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships.raw_type.RelationshipTypes;
import org.ufl.hypogator.jackb.scraper.adt.SemanticNetworkTraversers;
import org.ufl.hypogator.jackb.scraper.ScraperSources;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;
import it.uniroma1.lcl.babelnet.*;
import it.uniroma1.lcl.babelnet.data.BabelPointer;
import it.uniroma1.lcl.jlt.util.Language;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint.BabelNetListSeparator;

public class BabelNetTraverser implements SemanticNetworkTraversers<BabelPointer> {

    private final static BabelNet bn = BabelNet.getInstance();
    private final Language[] languages;

    public BabelNetTraverser() {
        String langs[] = Concept5ClientConfigurations.instantiate().getConceptNetEntityLanguages();
        ArrayList<Language> langList = new ArrayList<>();
        for (int i = 0, langsLength = langs.length; i < langsLength; i++) {
            String x = langs[i];
            Language l = Language.fromISO(x);
            if (l != null)
                langList.add(l);
        }
        if (langList.isEmpty()) {
            langList.add(Language.EN);
        }
        languages = langList.toArray(new Language[0]);
    }

    private boolean isIdPointer(String str) {
        return str.startsWith("bn:");
    }

    private BabelSynset fromPointer(String id) {
        return bn.getSynset(new BabelSynsetID(id));
    }

    public Language getPreferredLanguage(Set<Language> languages) {
        int n = this.languages.length;
        for (int i = 0; i < n; i++) {
            if (languages.contains(this.languages[i]))
                return this.languages[i];
        }
        return null;
    }

    /*public List<BabelSynset> disambiguateString(SemanticNetworkEntryPoint str) {
        if (isIdPointer(str)) {
            return Collections.singletonList(fromPointer(str));
        } else {
            BabelNetQuery query = new BabelNetQuery.Builder((str))
                    .from(languages)
                    .toSameLanguages()
                    .build();
            return bn.getSynsets(query);
        }
    }*/

    public EdgeVertex toVertex(BabelSynset k) {
        EdgeVertex src = new EdgeVertex();
        src.parent = this;
        src.id = k.getID().getID();
        Language preferred =  getPreferredLanguage(k.getLanguages());
        if (preferred == null) {
            return null;
        }
        src.language = preferred.name().toLowerCase();
        src.sense_label = k.getPOS().toString();
        src.setGeneratingSource(ScraperSources.BABELNET);
        //src_source = g.getSource().getSourceName();
        src.term = k.getMainSensePreferrablyIn(preferred).get().getSimpleLemma(); // TODO
        src.label = src.term; // TODO
        return src;
    }

    public EdgeVertex toVertex(List<BabelSynset> k, String term) {
        EdgeVertex src = new EdgeVertex();
        src.parent = this;
        src.id = k.stream().map(x -> x.getID().getID()).collect(Collectors.joining(BabelNetListSeparator));
        Language preferred =  getPreferredLanguage(k);
        if (preferred == null) {
            return null;
        }
        src.language = preferred.name().toLowerCase();
        src.sense_label = null;
        src.setGeneratingSource(ScraperSources.BABELNET);
        //src_source = g.getSource().getSourceName();
        src.term = term; // TODO
        src.label = src.term; // TODO
        return src;
    }

    private Language getPreferredLanguage(List<BabelSynset> k) {
            for (BabelSynset s : k) {
                Language ret = getPreferredLanguage(s.getLanguages());
                if (ret != null)
                    return ret;
            }
            return null;
    }

    public Edge toEdge(EdgeVertex src, EdgeVertex dst, BabelSynsetRelation r) {
        Edge e = new Edge();
        e.start = src;
        e.end = dst;
        BabelPointer ptr = r.getPointer();
        e.id = ptr.toString();
        e.weight = 1.0;

        if (ptr.isMeronym()) {
            e.rel = new Relationship(null, RelationshipTypes.PartOf.name());
        } else if (ptr.isHyponym()) {
            e.rel = new Relationship(null, RelationshipTypes.IsA.name());
        } else if (ptr.isHypernym()) {
            e.rel = new Relationship(null, RelationshipTypes.HasA.name());
        } else if (ptr.isHolonym()) {
            e.rel = new Relationship(null, RelationshipTypes.HasA.name());
        } else if (ptr.getName().equals(BabelPointer.GLOSS_DISAMBIGUATED.getName())){
            e.rel = new Relationship(null, RelationshipTypes.Synonym.name());
        } else if (Arrays.asList(rel).stream().anyMatch(x -> x.getName().equals(ptr.getName()))) {
            e.rel = new Relationship(null, RelationshipTypes.RelatedTo.name());
        } else {
            throw new RuntimeException("Unexpected label:" + ptr.getRelationGroup());
        }

        return e;
    }

    public <K> List<K> getOutgoingEdges(BabelSynset k, Function<Edge, K> fun, BabelPointer... types) {
        List<BabelSynsetRelation> ls = k.getOutgoingEdges(types);
        List<K> toReturn = new ArrayList<>();
        EdgeVertex src = toVertex(k);
        if (src != null) {
            for (int i = 0, lsSize = ls.size(); i < lsSize; i++) {
                BabelSynsetRelation edge = ls.get(i);
                BabelSynset dstSynset = edge.getBabelSynsetIDTarget().toSynset();
                EdgeVertex dst = toVertex(dstSynset);

                if (dst != null) {
                    Edge e = toEdge(src, dst, edge);
                    if (e != null)
                        toReturn.add(fun.apply(e));
                }
            }
        }
        return toReturn;
    }

    /*private String cleanString(String str) {
        return str;
    }*/

    private <K> Iterable<K> commonEdgeFinding(SemanticNetworkEntryPoint term, Function<Edge, K> fun, BabelPointer... types) {
        List<BabelSynset> ls = term.asSynset();
        HashSet<K> edges = new HashSet<>(ls.size());
        for (int i = 0, lsSize = ls.size(); i < lsSize; i++) {
            BabelSynset s = ls.get(i);
            edges.addAll(getOutgoingEdges(s, fun, types));
        }
        return edges;
    }

    private final static Function<Edge, Edge> idEdge = x -> x;

    @Override
    public Iterable<Edge> synonymsOutgoing(SemanticNetworkEntryPoint currentHierarchyElement) {
        return commonEdgeFinding(currentHierarchyElement, idEdge, synonymType());
    }

    @Override
    public Iterable<SemanticEdge> similarIngoing(SemanticNetworkEntryPoint currentHierarchyElement) {
        return commonEdgeFinding(currentHierarchyElement, SemanticEdge::fromEdge, synonymType());
    }

    @Override
    public Iterable<SemanticEdge> descendHierarchy(SemanticNetworkEntryPoint currentHierarchyElement) {
        return commonEdgeFinding(currentHierarchyElement, x -> {
                SemanticEdge u = SemanticEdge.fromEdge(x);
        return u.symmetry();
        }, superDescendingTypes());
    }

    @Override
    public Iterable<SemanticEdge> getRelatedEdges(SemanticNetworkEntryPoint currentHierarchyElement, boolean isOutgoing) {
        return commonEdgeFinding(currentHierarchyElement, SemanticEdge::fromEdge, relatedTypes());
    }

    @Override
    public Iterable<SemanticEdge> getSemanticUpwardEdges(SemanticNetworkEntryPoint currentHierarchyElement) {
        return commonEdgeFinding(currentHierarchyElement, x -> {
            SemanticEdge u = SemanticEdge.fromEdge(x);
            return u.symmetry();
        }, superAscendingTypes());
    }

    @Override
    public SemanticNetworkEntryPoint resolveTerm(final String term) {
        List<BabelSynset> ls;
        if (term.startsWith("bn:")) {
            ls = new ArrayList<>(1);
            ls.add(bn.getSynset(new BabelSynsetID(term)));
        } else {
            BabelNetQuery query = new BabelNetQuery.Builder(term)
                    .from(languages)
                    .toSameLanguages()
                    .filterSenses(x -> x.getFullLemma().toLowerCase().equals(term))
                    .build();
            ls = bn.getSynsets(query);
        }

        for (BabelSynset x : ls) {
            BabelSense s = x.getMainSense().orElse(null);
            if (s != null && s.getFullLemma().toLowerCase().equals(term.toLowerCase())) {
                ls.add(x);
            }
        }
        return toVertex(ls, term);
    }

    public static final BabelPointer[] rel = new BabelPointer[]
            {BabelPointer.DERIVATIONALLY_RELATED, BabelPointer.SEMANTICALLY_RELATED, BabelPointer.ALSO_SEE, BabelPointer.SIMILAR_TO };
    @Override
    public BabelPointer[] relatedTypes() {
        return rel;
    }

    public static final BabelPointer[] asc = new BabelPointer[]
            {BabelPointer.ANY_HYPERNYM, BabelPointer.ANY_HOLONYM };
    @Override
    public BabelPointer[] superAscendingTypes() {
        return asc;
    }


    public static final BabelPointer[] desc = new BabelPointer[]
            {BabelPointer.ANY_HYPONYM, BabelPointer.ANY_MERONYM, BabelPointer.TOPIC_MEMBER, BabelPointer.USAGE_MEMBER };
    @Override
    public BabelPointer[] superDescendingTypes() {
        return desc;
    }

    @Override
    public BabelPointer synonymType() {
        return BabelPointer.GLOSS_DISAMBIGUATED;
    }


    private static final BabelNetTraverser t = new BabelNetTraverser();
    private final Set<String> visitedId = new HashSet<>();
    private final Set<String> visitedString = new HashSet<>();
    public void printer(SemanticNetworkEntryPoint rt, int pos) {
        if ((visitedId.add(rt.getSemanticId())) && (visitedString.add(rt.getValue()))) {
            System.out.println(new String(new char[pos + 1]).replace("\0", "-") + rt.getValue());
            if (pos == 10)
                return;
            for (SemanticEdge j : t.descendHierarchy(rt)) {
                printer(j.src, pos + 1);
            }
        }
    }

    public void test() {
        visitedId.clear();
        visitedString.clear();
        SemanticNetworkEntryPoint l = t.resolveTerm("weapon");
        if (l != null) {
            List<BabelSynset> rt = l.asSynset();
            for (BabelSynset j : rt) {
                printer(t.toVertex(j), 0);
            }
        }
    }

    public static void main(String args[]) {
        new BabelNetTraverser().test();
    }


}
