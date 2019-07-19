package org.ufl.hypogator.jackb.fuzzymatching;

import org.ufl.hypogator.jackb.disambiguation.dimension.concept.AbstractVocabulary;

import java.util.Collection;
import java.util.Map;

public interface FuzzyMatcher<K> {
    Map<Double, Collection<K>> fuzzyMatch(Double threshold, Integer topK, Similarity sim, String objectStrings);
    Collection<K> containsExactTerm2(String term);
    default boolean containsExactTerm(String term) {
        Collection<K> ret = containsExactTerm2(term);
        return ret != null && ret.size() > 0;
    }
    default boolean isStopWord(String value) {
        return AbstractVocabulary.getIsStopwordPredicate().test(value);
    }
}
