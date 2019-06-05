package org.ufl.hypogator.jackb.disambiguation.dimension.concept;

/*
 * Vocabulary.java
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
import org.ufl.hypogator.jackb.fuzzymatching.FuzzyMatcher;
import org.ufl.hypogator.jackb.fuzzymatching.Similarity;
import org.ufl.hypogator.jackb.fuzzymatching.SimilarityFactory;
import org.ufl.hypogator.jackb.fuzzymatching.TwoGramIndexer;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Predicate;

/**
 * A vocabulary is a set of terms. Over this set of terms, we want to perform some fuzzy matching
 * for the terms. This can be useful whenever words contain typos.
 */
public class AbstractVocabulary<Value> implements FuzzyMatcher<Value> {

    //protected final HashMultimap<String, Value> vocabulary;
    protected FuzzyMatcher<Value> vocabulary;
    protected IsStopwordPredicate predicate;

    @Override
    public Collection<Value> containsExactTerm2(String term) {
        return vocabulary.containsExactTerm2(term);
    }

    @Override
    public Map<Double, Collection<Value>> fuzzyMatch(Double threshold, Integer topK, Similarity sim, String objectStrings) {
        return this.fuzzyMatch(sim, objectStrings, topK, threshold);
    }

    public void forcePut(String toLowerCase, Value value) {
        if ((!predicate.test(toLowerCase)) && (vocabulary instanceof TwoGramIndexer))
            ((TwoGramIndexer<Value>)vocabulary).addGramsToMap(toLowerCase, value);
    }

    public void forcePut(Value value) {
        if (vocabulary instanceof TwoGramIndexer)
            ((TwoGramIndexer<Value>)vocabulary).addGramsToMap(value, predicate);
    }

    public long getSize() {
        return vocabulary instanceof TwoGramIndexer ? ((TwoGramIndexer<Value>) vocabulary).getExtensionSize() : 0;
    }

    public static class IsStopwordPredicate implements Predicate<String> {
        protected final HashSet<String> stopwords;
        public IsStopwordPredicate(File file) {
            System.err.println("[AbstractVocabulary.IsStopwordPredicate::new] Loading stopwords...");
            stopwords = new HashSet<>();
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    stopwords.add(line.trim().toLowerCase());
                }
                System.err.println("[AbstractVocabulary.IsStopwordPredicate::new] ...done");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public boolean test(String s) {
            return stopwords.contains(s);
        }
    }

    private static IsStopwordPredicate defaultPredicate = null;
    public static IsStopwordPredicate getIsStopwordPredicate(ClassLoader classLoader) {
        if (defaultPredicate == null) {
            File file = new File(classLoader.getResource("en_stopwords.txt").getFile());
            defaultPredicate = new IsStopwordPredicate(file);
        }
        return defaultPredicate;
    }

    public static IsStopwordPredicate getIsStopwordPredicate() {
        return getIsStopwordPredicate(AbstractVocabulary.class.getClassLoader());
    }

    public AbstractVocabulary(FuzzyMatcher<Value> vocabulary) {
        this.vocabulary = vocabulary;
        ClassLoader classLoader = getClass().getClassLoader();
        this.predicate = getIsStopwordPredicate(classLoader);
    }

    public AbstractVocabulary(FuzzyMatcher<Value> vocabulary, IsStopwordPredicate predicate) {
        this.vocabulary = vocabulary;
        this.predicate = predicate;
    }

    public boolean isStopWord(String obj) {
        return predicate.test(obj);
    }

    Map<Double, Collection<Value>> fuzzyMatch(Similarity sim, String toMatch, int topK, double threshold) {
        return vocabulary.fuzzyMatch(threshold, topK, sim, toMatch);
    }

    private Map<Double, Collection<Value>> fuzzyMatch(String simFunction, String toMatch, int topK, double threshold) {
        return fuzzyMatch(SimilarityFactory.getSimilarityFunction(simFunction), toMatch, topK, threshold);
    }

    public Map<Double, Collection<Value>> fuzzyMatch(String toMatch, int topK, double threshold) {
        return fuzzyMatch(((String)null), toMatch, topK, threshold);
    }

    public boolean containsExactTerm(String s) {
        return (!isStopWord(s)) && (!vocabulary/*.keySet().contains(s);*/.containsExactTerm2(s).isEmpty());
    }
}
