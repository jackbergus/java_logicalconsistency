/*
 * PartialOrderCandidate.java
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

import org.ufl.hypogator.jackb.inconsistency.legacy.TupleAttribute;

/**
 * This class associates judgements to comparable elements.
 * Comparisons are used to draw the correct judgements for all the data hypotheses
 *
 * @param <T>
 */
public class PartialOrderCandidate<T> {

    private final InformationPreservingComparator<T> comparator;

    /**
     * Left value
     */
    private final T left;

    /**
     * Attribute associated to the left value
     */
    private final TupleAttribute leftAttribute;

    public PartialOrderCandidate(InformationPreservingComparator<T> comparator, T left, TupleAttribute leftAttribute) {
        this.comparator = comparator;
        this.left = left;
        this.leftAttribute = leftAttribute;
    }

    /**
     * TOUSE for M9
     * @param comparator
     * @param left
     */
    public PartialOrderCandidate(InformationPreservingComparator<T> comparator, T left) {
        this(comparator, left, TupleAttribute.ADMISSIBLE);
    }

    /**
     * TOUSE for M9
     * @param right
     * @return
     */
    public TupleAttribute compare(T right) {
        return compare(right, TupleAttribute.ADMISSIBLE);
    }

    public TupleAttribute compare(T right, TupleAttribute rightAttribute) {
        if (left == null || right == null) {
            return TupleAttribute.UNKNOWN; // If I have tuple values, I do not know their values, so I cannot infer a lot.
        }
        PartialOrderComparison cmpPair = comparator.compare(left, right);
        switch (cmpPair.t) {
            case Equal: {
                switch (leftAttribute) {
                    case ADMISSIBLE:
                        return rightAttribute;
                    case UNADMISSIBLE:
                        return rightAttribute.invert();
                    default:
                        return TupleAttribute.ADMISSIBLE;
                }
            }

            case Lesser: {
                switch (leftAttribute) {
                    case ADMISSIBLE: {
                        switch (rightAttribute) {
                            case UNADMISSIBLE:
                                return TupleAttribute.UNADMISSIBLE;
                            default:
                                return TupleAttribute.ADMISSIBLE;
                        }
                    }
                    default:
                        return TupleAttribute.UNKNOWN;
                }
            }

            case Greater: {
                switch (leftAttribute) {
                    case UNADMISSIBLE: {
                        switch (rightAttribute) {
                            case ADMISSIBLE:
                                return TupleAttribute.UNADMISSIBLE;
                            default:
                                return TupleAttribute.ADMISSIBLE;
                        }
                    }
                    default:
                        return TupleAttribute.UNKNOWN;
                }
            }

            default: {
                switch (leftAttribute) {
                    case ADMISSIBLE: {
                        switch (rightAttribute) {
                            case ADMISSIBLE:
                                return TupleAttribute.UNADMISSIBLE;
                            default:
                                return TupleAttribute.UNKNOWN;
                        }
                    }

                    case UNADMISSIBLE: {
                        switch (rightAttribute) {
                            case ADMISSIBLE:
                                return TupleAttribute.ADMISSIBLE;
                            default:
                                return TupleAttribute.UNKNOWN;
                        }
                    }

                    default:
                        return TupleAttribute.UNKNOWN;
                }
            }
        }
    }

}
