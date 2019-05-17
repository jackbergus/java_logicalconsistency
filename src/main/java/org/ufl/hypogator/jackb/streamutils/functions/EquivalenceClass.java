/*
 * EquivalenceClass.java
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

package org.ufl.hypogator.jackb.streamutils.functions;

import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * We can express an equivalence class through a labelling function.
 * The labelling function associates each element to one and only one possible case.
 * Therefore, two objects are equal if they share the same label.
 *
 * @param <Type>
 * @param <EqWitness>
 */
public abstract class EquivalenceClass<Type, EqWitness> implements Function<Type, EqWitness>,
        BiPredicate<Type, Type> {

    /**
     * Function mapping each element to its label/witness
     * @param t     Element to be classified
     * @return      Result of the classification
     */
    @Override
    public abstract EqWitness apply(Type t);

    /**
     * Testing the objects' equivalence through their
     * @param type      First element
     * @param type2     Second element
     * @return  Equivalence w.r.t. their label
     */
    @Override
    public boolean test(Type type, Type type2) {
        return Objects.equals(apply(type), apply(type2));
    }

}
