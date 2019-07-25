/*
 * InformationPreservingComparator.java
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


import javafx.util.Pair;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetDimensionDisambiguationOperations;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ResolvedConcept;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;

import java.io.File;
import java.util.List;
import java.util.Optional;

/**
 * We say that information in X implies the information in Y if the content in Y implies the content in Y.
 * This is not a simple number comparison: for examples, two dates like 2018-10-02 and 2017-11-24 are not
 * comparable, since if we assume the left (right) to be correct, then we cannot say if the right (left) is
 * correct, too.
 * <p>
 * Therefore, this partial order says if the valid information X also implies that Y is valid, too. Within this
 * context, null values are not "Void", because from a null value we cannot infer that any other hypothesis is
 * correct while, on the other hand, given an hypothesis, a null element is admissible.
 */
public abstract class InformationPreservingComparator<T> implements PartialOrder<T> {



    /**
     * Comparing two terms which are not null. The comparison of the terms where at least one element is null is
     * directly provided by the compare method.
     *
     * @param left
     * @param right
     * @return
     */
    protected abstract PartialOrderComparison nonNullCompare( T left,  T right);

    /**
     * The default comparison provides an uniform interface for comparing null values with any kind of value. On the
     * other hand, comparison of valid values must be implemented by the user.
     *
     * @param left
     * @param right
     * @return
     */
    public PartialOrderComparison compare(T left, T right) {
        if (left == null && right == null)
            return PartialOrderComparison.PERFECT_EQUAL;
        else if (right == null)
            return PartialOrderComparison.PERFECT_LESSER;
        else if (left == null)
            return PartialOrderComparison.PERFECT_GREATER;
        else return nonNullCompare(left, right);
    }

    /**
     * String identifying the sub-dimension
     *
     * @return
     */
    public abstract String getName();

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof InformationPreservingComparator))
            return false;
        return getName().equals(((InformationPreservingComparator) o).getName());
    }

    /**
     * Persist the intermediate comparisons from disk
     * @param file
     */
    public abstract void serializeToDisk(File file);

    /**
     * Allows to load the previously-persited computation
     * @param file
     */
    public abstract void loadFromDisk(File file);

    public abstract void close();
}
