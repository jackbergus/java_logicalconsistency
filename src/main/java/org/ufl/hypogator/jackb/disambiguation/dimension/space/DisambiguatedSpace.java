/*
 * SpaceInformation.java
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

import org.ufl.hypogator.jackb.disambiguation.DisambiguatedValue;
import org.ufl.hypogator.jackb.utils.adt.Triple;

import java.util.Collection;
import java.util.List;

public class DisambiguatedSpace extends DisambiguatedValue<ResolvedSpace> {


    @Override
    public List<String> pathFromDisambiguation(ResolvedSpace disambiguation) {
        return disambiguation.generateDisambiguationPath();
    }

    @Override
    public String matchedString(ResolvedSpace disambiguation) {
        return disambiguation.getMatchedName();
    }

    /**
     * String representation of the given element
     *
     * @param str
     */
    public DisambiguatedSpace(String str) {
        super(str);
    }

    @Override
    public String getType() {
        return "loc";
    }

    @Override
    public void setDisambiguation(String matched, ResolvedSpace disambiguation, double score) {
        this.disambiguation.add(new Triple<>(disambiguation.getMatchedName(), disambiguation, disambiguation.getConfidence()));
    }

    public void setDisambiguation(Triple<String, ResolvedSpace, Double> elem) {
        this.disambiguation.add(elem);
    }

    @Override
    public void setDisambiguations(Collection<ResolvedSpace> disambiguation) {
        for (ResolvedSpace ex : disambiguation) {
            this.disambiguation.add(new Triple<>(ex.getMatchedName(), ex, ex.getConfidence()));
        }
    }

    private boolean isRefactored = false;

    @Override
    public Collection<Triple<String, ResolvedSpace, Double>> getDisambiguation() {
        /*if (!isRefactored) {
            List<Triple<String, ResolvedSpace, Double>> replacement = new ArrayList<>();
            HashMultimap<List<String>, Pair<ResolvedSpace, Double>> map = HashMultimap.create();
            for (Triple<String, ResolvedSpace, Double> x : this.disambiguation) {
                map.put(x.second.generateDisambiguationPath(), new Pair<>(x.second, x.third));
            }
            for (Map.Entry<List<String>, Collection<Pair<ResolvedSpace, Double>>> k : map.asMap().entrySet()) {
                replacement.add(new Triple<>(k.getKey(),
                                             k.getValue().iterator().next().getKey(),
                                             k.getValue().stream().mapToDouble(Pair::getValue).average().orElse(0)
                ));
            }
            disambiguation = replacement;
            isRefactored = true;
        }*/
        return this.disambiguation;
    }
}
