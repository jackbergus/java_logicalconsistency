/*
 * Time2.java
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


import org.ufl.hypogator.jackb.disambiguation.dimension.Dimension;
import org.ufl.hypogator.jackb.inconsistency.legacy.TupleComparator;

import java.io.File;

/**
 * This class provides the time dimension for temporal comparisons. This class also performs time disambiguations
 * by using Stanford NLP's techniques, thus allowing to compare dates and (eventually timestamps) in different
 * temporal representations.
 */
public class DimTime extends Dimension<ResolvedTime, InformativeTime> {
    private static final DisambiguatorForDimensionForTime dft = new DisambiguatorForDimensionForTime();
    private static final ComparingTimeResolutions ctr = new ComparingTimeResolutions();
    private final String dim;

    public DimTime(String dim) {
        super(ctr, dft);
        this.dim = dim;
        System.err.println("Loaded: DimTime for " + dim);
    }

    public DimTime() {
        this(TupleComparator.TIME);
    }


    @Override
    public String getName() {
        return dim;
    }

    @Override
    public void serializeToDisk(File file) {
        ctr.serializeToDisk(file);
    }

    @Override
    public void loadFromDisk(File file) {
        ctr.loadFromDisk(file);
    }

    @Override
    public void close() {

    }
}
