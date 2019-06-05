/*
 * Triple.java
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

package org.ufl.hypogator.jackb.utils.adt;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Objects;

public class Triple<T1, T2, T3> implements Comparable<Triple<T1, T2, T3>>, Serializable {
    public T1 first;
    public T2 second;
    public T3 third;

    @JsonCreator
    public Triple(@JsonProperty("first") T1 first, @JsonProperty("second") T2 second, @JsonProperty("third") T3 third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public T1 first() {
        return this.first;
    }

    public T2 second() {
        return this.second;
    }

    public T3 third() {
        return this.third;
    }

    public void setFirst(T1 o) {
        this.first = o;
    }

    public void setSecond(T2 o) {
        this.second = o;
    }

    public void setThird(T3 o) {
        this.third = o;
    }

    @Override
    public int compareTo(Triple<T1, T2, T3> another) {
        int comp = ((Comparable) this.first()).compareTo(another.first());
        if (comp != 0) {
            return comp;
        } else {
            comp = ((Comparable) this.second()).compareTo(another.second());
            return comp != 0 ? comp : ((Comparable) this.third()).compareTo(another.third());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return Objects.equals(first, triple.first) &&
                Objects.equals(second, triple.second) &&
                Objects.equals(third, triple.third);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second, third);
    }
}