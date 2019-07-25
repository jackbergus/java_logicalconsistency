/*
 * PartialOrderComparison.java
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

import java.io.Serializable;
import java.util.Objects;

/**
 * The PartialOrderComparison provides the result of the comparison
 */
public class PartialOrderComparison implements Serializable {
    public static final long serialVersionUID = 41L;
    public static final PartialOrderComparison PERFECT_EQUAL = new PartialOrderComparison(POCType.Equal, 1.0);
    public static final PartialOrderComparison PERFECT_LESSER = new PartialOrderComparison(POCType.Lesser, 1.0);
    public static final PartialOrderComparison PERFECT_GREATER = new PartialOrderComparison(POCType.Greater, 1.0);
    public static final PartialOrderComparison PERFECT_UNCOMPARABLE = new PartialOrderComparison(POCType.Uncomparable, 1.0);

    /**
     * Direction of the comparison
     */
    public POCType t;

    /**
     * Score associated to the comparison
     */
    public double uncertainty;

    public static final PartialOrderComparison IMPOSSIBLE = new PartialOrderComparison(POCType.Uncomparable, 0.0);

    public PartialOrderComparison(POCType t, Double uncertainty) {
        this.t = t;
        this.uncertainty = uncertainty;
    }

    /**
     * Resolves the comparison with a given threshold. If any relation is below such threshold, then the relation
     * is automatically converted into an uncomparable element.
     *
     * @param threshold Threshold under which any relation is unacceptable.
     * @return
     */
    public PartialOrderComparison withThreshold(double threshold) {
        if (uncertainty > threshold)
            return this;
        else
            return new PartialOrderComparison(POCType.Uncomparable, 1 - uncertainty);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartialOrderComparison that = (PartialOrderComparison) o;
        return Double.compare(that.uncertainty, uncertainty) == 0 &&
                t == that.t;
    }

    @Override
    public int hashCode() {
        return Objects.hash(t, uncertainty);
    }

    @Override
    public String toString() {
        return "POC{" +
                "t=" + t +
                ", u=" + uncertainty +
                '}';
    }
}
