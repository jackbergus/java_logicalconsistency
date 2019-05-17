/*
 * TimeInformation.java
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

import org.ufl.hypogator.jackb.disambiguation.DisambiguatedValue;
import org.ufl.hypogator.jackb.utils.adt.Triple;

import java.util.Collection;
import java.util.List;

public class InformativeTime extends DisambiguatedValue<ResolvedTime> {

    public InformativeTime(String str) {
        super(str);
    }

    /**
     * This method converts the information to the year-month-day information.
     *
     * @param now Calendar disambiguation
     * @return List of associated string values
     */
    @Override
    public List<String> pathFromDisambiguation(ResolvedTime now) {
        return now.toHierarchy();
    }

    @Override
    public String matchedString(ResolvedTime disambiguation) {
        return disambiguation.matched;
    }

    @Override
    public String getType() {
        return "tme";
    }

    @Override
    public void setDisambiguations(Collection<ResolvedTime> disambiguation) {
        throw new RuntimeException("Time information should never execute this method");
    }

    @Override
    public Collection<Triple<String, ResolvedTime, Double>> getDisambiguation() {
        return this.disambiguation;
    }
}
