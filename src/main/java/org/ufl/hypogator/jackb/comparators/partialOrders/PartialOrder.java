/*
 * PartialOrder.java
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

package org.ufl.hypogator.jackb.comparators.partialOrders;

public interface PartialOrder<T> {
    /**
     * Within a partial order, two elements can be comparable or not.
     *
     * @param left
     * @param right
     * @return It returns null if the values are not comparable.
     * <p>
     * <p>
     * It returns a negative value if the left is inferior to the right one.
     * It returns a positive value if the left is superior to the right one.
     * It returns 0 if the two elements are the same.
     */
    PartialOrderComparison compare(T left, T right);

    default boolean isComparable(T left, T right) {
        return compare(left, right) != null;
    }
}
