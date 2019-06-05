/*
 * PartialTimeComparator.java
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

package org.ufl.hypogator.jackb.disambiguation.dimension.time;

import org.ufl.hypogator.jackb.utils.legacy.PartialOrderComparator;

import java.util.Optional;

/**
 * The partial time dimension is used not to determine whether one date precedes another one, but to determine
 * whether the validity of one date implies the validity of the other one. For this reason, if we assume that a
 * less precise date information is valid, then a more precise one is as valid as well.
 */
@Deprecated
public class PartialTimeComparator implements PartialOrderComparator<ResolvedTime> {

    /**
     * The Par
     *
     * @param left
     * @param right
     * @param <T>
     * @return
     */
    private static <T extends Comparable<T>> Optional<Integer> compareForEquality(T left, T right) {
        if (left == null && right == null)
            return Optional.of(0);
        else if (left == null)
            return Optional.of(-1);
        else if (right == null)
            return Optional.of(1);
        else {
            if (!left.equals(right))
                return Optional.empty();
            else
                return Optional.of(0);
        }
    }

    @Override
    public boolean isComparable(ResolvedTime left, ResolvedTime right) {
        return compareForEquality(left, right).isPresent();
    }

    @Override
    public Optional<Integer> semiCompare(ResolvedTime left, ResolvedTime right) {
        return compareForEquality(left, right);
    }
}
