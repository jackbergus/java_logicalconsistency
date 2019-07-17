/*
 * Disambiguation.java
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

package org.ufl.hypogator.jackb.disambiguation;



import org.ufl.hypogator.jackb.utils.adt.Triple;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * A disambiguation is an association between a string-representation of a value and
 *
 * @param <T> Class describing the disambiguation
 */
public abstract class DisambiguatedValue<T extends Resolved> implements Iterable<Triple<String, T, Double>> {
    public final String str;
    public List<Triple<String, T, Double>> disambiguation;

    /**
     * String representation of the given element
     *
     * @param str Original string
     */
    public DisambiguatedValue(String str) {
        this.str = str;
        this.disambiguation = new ArrayList<>();
    }

    /**
     * Associates a disambiguated element to a path of increasing precision information
     *
     * @param disambiguation Parameter to represent as a path of strings
     * @return
     */
    public List<String> pathFromDisambiguation(T disambiguation) {
        return disambiguation.generateDisambiguationPath();
    }

    @Deprecated
    public List<String> pathFromDisambiguation(int pos) {
        return pathFromDisambiguation(disambiguation.get(pos).second);
    }

    /**
     * Original string that allowed to provide this disambiguation
     *
     * @param disambiguation Element from which extract the matchied term
     * @return
     */
    public abstract String matchedString(T disambiguation);

    /**
     * LegacyDimension (or type) associated to the given entity
     *
     * @return
     */
    public abstract String getType();

    public void setDisambiguation(String matchedPart, T disambiguation, double score) {
        this.disambiguation.add(new Triple<>(matchedPart, disambiguation, score));
    }

    public void expandWith(DisambiguatedValue<T> outer) {
        this.disambiguation.addAll(outer.disambiguation);
    }

    public abstract void setDisambiguations(Collection<T> disambiguation);

    /**
     * Original string
     *
     * @return
     */
    public String getValue() {
        return str;
    }

    /**
     * If the text represents the correct getConceptNet, then it contains some information about it
     *
     * @return
     */
    public boolean isCorrectDimension() {
        return !this.disambiguation.isEmpty();
    }

    @Override
    public Iterator<Triple<String, T, Double>> iterator() {
        return disambiguation.iterator();
    }

    public abstract Collection<Triple<String, T, Double>> getDisambiguation();

    public void clearDisambiguations() {
        disambiguation.clear();
    }
}
