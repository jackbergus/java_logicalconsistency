/*
 * DisambiguatorForDimension.java
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

/**
 * A disambiguator is a class that associates a String to a disambiguated element
 *
 * @param <T>
 * @param <K>
 */
public interface DisambiguatorForDimension<T extends Resolved, K extends DisambiguatedValue<T>> {
    /**
     * Associates a string to a given entity within a specific getConceptNet
     *
     * @param str String to be disambiguated to the given getConceptNet
     * @return Object containing both the string and the disambiguation's information
     */
    K disambiguate(String str);

    /**
     * @param threshold Maximum value after which we can consider the match as good enough
     * @return
     */
    default DisambiguationAlgorithm<T, K> getAlgorithm(double threshold) {
        return new DisambiguationAlgorithm<>(this, threshold, allowedKBTypesForTypingExpansion(), allowReflexiveExpansion());
    }

    public String[] allowedKBTypesForTypingExpansion();
    public boolean allowReflexiveExpansion();
}
