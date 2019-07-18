package org.ufl.hypogator.jackb.scraper;

import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetDimensionDisambiguationOperations;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetID;

import java.util.*;

/**
 * This provides the default entry point representation for a vertex
 */
public interface SemanticNetworkEntryPoint {

    public static String BabelNetListSeparator = "\t";

    /**
     * Returns the id associated to a specific semantic network
     * @return
     */
    String getSemanticId();

    /**
     * Returns the string interpretation
     * @return
     */
    String getValue();

    /**
     * Returns from which semantic network the edge was generated from
     * @return
     */
    ScraperSources getGeneratingSource();

    /**
     * Sets the generating source associated to the current element
     * @param source
     */
    void setGeneratingSource(ScraperSources source);

    /**
     * Checks whether the current term has a part of speech definition
     * @return
     */
    boolean hasPOS();

    /**
     * Returns a part of speech definition and, otherwise, it returns null
     * @return
     */
    String getPOS();

    /**
     * Returns the language associated to the element
     * @return
     */
    String getLanguage();

    static SemanticNetworkEntryPoint generateDefaultRoot(String term) {
        return EdgeVertex.generateSemanticRoot(term);
    }

    static String toConceptNetString(EdgeVertex dst) {
        String arr[] = dst.term.split("/");
        return (arr == null || arr.length == 0) ? dst.label : arr[arr.length - 1];
    }

    default List<BabelSynsetID> asBabelnetIds() {
        switch (getGeneratingSource()) {
            case BABELNET: {
                String[] array = getSemanticId().split(BabelNetListSeparator);
                List<BabelSynsetID> ls = new ArrayList<>(array.length);
                for (int i = 0, arrayLength = array.length; i < arrayLength; i++) {
                    String x = array[i];
                    ls.add(new BabelSynsetID(x));
                }
                return ls;
            }
           default:
                return null;
        }
    }

    /**
     * After the traversal process, each object may have an associated equivalence set
     * @return
     */
    Collection<SemanticNetworkEntryPoint> equivalenceClassBySemanticId();

    default List<BabelSynset> asSynset() {
        switch (getGeneratingSource()) {
            case BABELNET: {
                List<BabelSynsetID> ls = asBabelnetIds();
                ArrayList<BabelSynset> bs = new ArrayList<>(ls.size());
                for (int i = 0, lsSize = ls.size(); i < lsSize; i++) {
                    BabelSynsetID id = ls.get(i);
                    bs.add(id.toSynset());
                }
                return bs;
            }
            default:
                return null;
        }
    }

    default String asConceptNetId() {
        switch (getGeneratingSource()) {
            case CONCEPTNET: {
                String[] x = getSemanticId().split("/");
                return "/"+x[1]+"/"+x[2]+"/"+x[3];
            }
            default:
                return null;
        }
    }

    default String ensureValue() {
        switch (getGeneratingSource()) {
            case CONCEPTNET:
                return ConceptNetDimensionDisambiguationOperations.unrectify(getValue());
            default:
                return getValue();
        }
    }

    default boolean isStopPointFor(SemanticNetworkEntryPoint pt) {
        return pt != null && (ensureValue().equals(pt.ensureValue()) || belongToSameEquivalenceClass(pt));
    }

    default boolean belongToSameEquivalenceClass(SemanticNetworkEntryPoint pt) {
        return pt != null &&
                (getSemanticId().equals(pt.getSemanticId()) ||
                 equivalenceClassBySemanticId().contains(pt) ||
                 pt.equivalenceClassBySemanticId().contains(this)
                );
    }

    void addToEquivalenceSet(SemanticNetworkEntryPoint semanticNetworkEntryPoint);
}
