/*
 * MultiWordSimilarity.java
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
package org.ufl.hypogator.jackb.fuzzymatching;


/**
 * This class provides a non-simmetric similarity, that could be used both
 * for text ranking and term corrections
 *
 * @author vasistas
 */
public class MultiWordSimilarity extends Similarity {

    private Similarity base_similarity;

    public MultiWordSimilarity(Similarity single_word_similarity) {
        //this.default_cleanser = language_based_cleanser;
        this.base_similarity = single_word_similarity;
    }

    /**
     * Calculates a MultiWordSimilarity by using Levenshtein for targeting
     * precisely long words and LowConfidenceRank for strengthen the low results
     */
    public MultiWordSimilarity() {
        this(new Similarity() {

            //private Similarity l = newDimensions MyMongeElkan();
            private Similarity lcr = new LowConfidenceRank();

            @Override
            public double sim(String word1, String word2) {
                return ((lcr.sim(word1, word2)));
            }
        });
    }

    private static MultiWordSimilarity self = null;

    public static MultiWordSimilarity getInstance() {
        if (self == null)
            self = new MultiWordSimilarity();
        return self;
    }

    /**
     * Given the data element and a query, returns how the data is similar to the query
     * <p>
     * score[i] = max_j{sim(q[i],d[1]),...,sim(q[i],d[n])}
     *
     * @param query
     * @param data
     * @return
     */
    @Override
    public double sim(String query, String data) {
        return Double.min(bixSim(query, data), bixSim(data, query));
    }

    private double bixSim(String query, String data) {
        String query_vec[] = query.split("\\s+");
        String data_vec[] = (data).split("\\s+");
        double score = 0;
        //int discarded = 0;
        for (String q : query_vec) {
            double d = 0;
            for (String t : data_vec) {
                double s = this.base_similarity.sim(q, t);
                if (s > d)
                    d = s;
            }
            score += d;
        }
        return (score / (((double) (query_vec.length))));
    }

}
