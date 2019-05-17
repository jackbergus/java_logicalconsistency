/*
 * SimilarityFactory.java
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

import org.ufl.hypogator.jackb.ConfigurationEntrypoint;

public class SimilarityFactory {

    private static Levenshtein lev = new Levenshtein();
    private static LowConfidenceRank lcr = new LowConfidenceRank();
    private static MultiWordSimilarity mws = new MultiWordSimilarity();
    private static String similarity = null;

    public static Similarity getDefaultSimilarity() {
        if (similarity == null) {
            similarity = ConfigurationEntrypoint.getInstance().fuzzyAlgorithm;
        }
        return getSimilarityFunction(similarity);
    }

    /**
     * This function retrieves the preliminarly-loaded scoring function.
     * @param function
     * @return
     */
    public static Similarity getSimilarityFunction(String function) {
        if (function == null)
            return null;
        if (function.equals("Levenshtein"))
            return lev;
        else if (function.equals("LowConfidenceRank"))
            return lcr;
        else if (function.equals("MultiWordSimilarity"))
            return mws;
        else
            return lcr;
    }

}
