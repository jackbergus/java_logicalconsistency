/*
 * DimensionFactory.java
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

package org.ufl.hypogator.jackb.disambiguation.dimension;


import org.ufl.hypogator.jackb.comparators.partialOrders.InformationPreservingComparator;
import org.ufl.hypogator.jackb.disambiguation.dimension.space.DimLocation;
import org.ufl.hypogator.jackb.disambiguation.dimension.time.DimTime;

@Deprecated
public class DimensionFactory {

    public final static String NULL_TYPE = "⊥";
    public final static String TIME = "tme";
    public final static String LOCATION = "loc";
    public final static String START_TIME = "start";
    public final static String END_TIME = "end";

    private final static DimLocation l = new DimLocation();
    private final static DimTime t = new DimTime();
    private final static DimTime s = new DimTime(START_TIME);
    private final static DimTime e = new DimTime(END_TIME);
    //private final static HashMap<String, Concepts> conceptMap = new HashMap<>();

    // TODO: union data
    //private final static Concepts gpeSub = new Concepts("gpe");
    //private final static UnionDimension gpe = new UnionDimension("gpe", l, gpeSub);
    //private final static UnionDimensionAsDimension u = new UnionDimensionAsDimension(gpe);

    public static InformationPreservingComparator<String> generate(String dimension) {
        if (dimension.equals(NULL_TYPE)) {
            return null;
        } else if (dimension.equals(TIME)) {
            return t;
        } else if (dimension.equals(LOCATION)) {
            return l;
        } else if (dimension.equals(START_TIME)) {
            return s;
        } else if (dimension.equals(END_TIME)) {
            return e;
        }
        return null;
        /* TODO:
            if (!conceptMap.containsKey(dimension)) {
            System.err.println("Generating NO dimension for Concepts: " + dimension);
            return null;
            // TODO: onceptMap.put(dimension, new Concepts(dimension));
        }*/
        // TODO:
        // return conceptMap.get(dimension); //Concepts cannot be loaded once for all the different possible dimensions
    }

}
