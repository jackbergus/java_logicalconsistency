/*
 * PartialComparable.java
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

/**
 * A PartialComparable is a type which has an associated run-time type (dimension)
 * The associated dimension is used to perform a comparison between different comparators
 *
 * @param <T>
 */
public interface PartialComparable<T> {

    public T value();

    default PartialOrderComparison compare(PartialComparable<T> right) {
        InformationPreservingComparator<T> comparator = dimension();
        if (!comparator.getName().equals(right.dimension().getName()))
            return PartialOrderComparison.PERFECT_UNCOMPARABLE;
        return dimension().compare(this.value(), right.value());
    }

    InformationPreservingComparator<T> dimension();

}
