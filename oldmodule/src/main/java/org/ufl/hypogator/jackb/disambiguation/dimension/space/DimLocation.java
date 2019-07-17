/*
 * DimLocation.java
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

package org.ufl.hypogator.jackb.disambiguation.dimension.space;

import org.ufl.hypogator.jackb.disambiguation.dimension.Dimension;
import org.ufl.hypogator.jackb.inconsistency.legacy.TupleComparator;

import java.io.File;

/**
 * This class provides the spatial dimension for spatial comparisons. This class also performs time disambiguations
 * by Clavin's tool integrating GeoNames with Lucene. The basic GeoNames representation is enriched by associating
 * a continent to each state, where all the continents belong to the Earth. Therefore,
 */
public class DimLocation extends Dimension<ResolvedSpace, DisambiguatedSpace> {
    public static String[] locationElements = new String[]{"Physical.LocatedNear"};
    public static final DisambiguatorForSpace dfs = DisambiguatorForSpace.getInstance(locationElements);
    public static final ComparingPlaceResolutions cpr = new ComparingPlaceResolutions();

    public DimLocation() {
        super(cpr, dfs);
        System.err.println("Loaded: DimLocation");
    }

    @Override
    public String getName() {
        return TupleComparator.LOCATION;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public void serializeToDisk(File file) {
        System.err.println("DimLocation: memoizing to secondary memory only the [ComparingPlaceResolutions]");
        cpr.serializeToDisk(file);
    }

    @Override
    public void loadFromDisk(File file) {
        System.err.println("DimLocation: memoizing to secondary memory only the [ComparingPlaceResolutions]");
        cpr.loadFromDisk(file);
    }

    @Override
    public void close() {

    }

    @Override
    public String[] allowedKBTypesForTypingExpansion() {
        return locationElements;
    }

    @Override
    public boolean allowReflexiveExpansion() {
        return true;
    }
}
