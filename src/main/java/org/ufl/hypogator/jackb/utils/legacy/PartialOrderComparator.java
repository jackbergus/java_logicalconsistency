/*
 * PartialOrderComparator.java
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

package org.ufl.hypogator.jackb.utils.legacy;

import java.util.Comparator;
import java.util.Optional;

/**
 * A partial order dimension cannot possibly determine a subtyping relationship for all the data.
 * Therefore, two elements are either comparable or not.
 */
@Deprecated
public interface PartialOrderComparator<T> extends Comparator<T> {

    /**
     * Given two elements, it returns true if the two elements are comparable, and false otherwise
     *
     * @param left  left operand
     * @param right right operand
     * @return Comparability statement
     */
    boolean isComparable(T left, T right);

    /**
     * A partial comparison between two elements
     *
     * @param left  left operand
     * @param right right operand
     * @return If the elements are comparable
     */
    Optional<Integer> semiCompare(T left, T right);

    /**
     * Trying to implement a total ordering over partial order elements. Given that I cannot give a
     * number for non-comparable elements, an exception is rased.
     *
     * @param left  left operand
     * @param right right operand
     * @return Returns an integer only when the two elements are comparable
     * @throws RuntimeException when the objects are not comparable
     */
    default int compare(T left, T right) {
        Optional<Integer> demiCompare = semiCompare(left, right);
        if (demiCompare == null)
            System.err.println(demiCompare);
        if (demiCompare.isPresent()) return demiCompare.get();
        else throw new UncomparableException();
    }

    /**
     * Two elements are the same only when they are comparable and they have the same value, indeed
     *
     * @param left  left operand
     * @param right right operand
     * @return Equaility result
     */
    default boolean equality(T left, T right) {
        Optional<Integer> result = semiCompare(left, right);
        return result.isPresent() && result.get().equals(0);
    }

}
