/*
 * ComparingTimeResolutions.java
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

import javafx.util.Pair;
import org.ufl.hypogator.jackb.comparators.partialOrders.InformationPreservingComparator;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.comparators.partialOrders.SubListComparator;
import org.ufl.hypogator.jackb.disambiguation.dimension.memoization.MemoizationGeneralizer;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;

import java.io.File;
import java.util.HashMap;

public class ComparingTimeResolutions extends InformationPreservingComparator<ResolvedTime> {

    private static final SubListComparator<String> slc = SubListComparator.getInstance();

    @Override
    protected PartialOrderComparison nonNullCompare(ResolvedTime left, ResolvedTime right) {
        /*
         * We have no benefits from memoizing the time information:
         * therefore, we might just re-compute the already-disambiguated time information
         */
        return slc.compare(left.generateDisambiguationPath(), right.generateDisambiguationPath());
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public void serializeToDisk(File file) {
        System.err.println("Time comparison is not memoized, since it is the fastes");
    }

    @Override
    public void loadFromDisk(File file) {
        System.err.println("Time comparison is not memoized, since it is the fastes");
    }

    @Override
    public void close() {

    }
}
