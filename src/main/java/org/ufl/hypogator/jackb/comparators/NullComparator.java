/*
 * NullComparator.java
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

package org.ufl.hypogator.jackb.comparators;

public class NullComparator {

    /**
     * This is the comparison function that is used for functional dependencies' detection. This implementation
     * shall ignore missing values, and focus more on the actual data. Therefore, the similarity between a null
     * element against any possible element (null included) returns zero.
     *
     * @param left
     * @param right
     * @param <T>
     * @return
     */
    public static <T extends Comparable<T>> int compareForFunctionalDependency(T left, T right) {
        return (left == null || right == null) ? 0 : left.compareTo(right);
    }


    /**
     * This is the type comparison that is used for subtying-implying similarity. Therefore, any missing
     * argument is inferior to any element of a given type.
     *
     * @param left
     * @param right
     * @param <T>
     * @return
     */
    /*public static <T extends Comparable<T>> int compareForSubtyping(T left, T right) {
        if (left == null && right == null)
            return 0;
        else if (left == null)
            return -1;
        else if (right == null)
            return 1;
        else
            return left.compareTo(right);
    }*/
}
