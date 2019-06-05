/*
 * TupleAttribute.java
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

package org.ufl.hypogator.jackb.inconsistency.legacy;

public enum TupleAttribute {
    ADMISSIBLE,
    UNADMISSIBLE,
    UNKNOWN;

    public static TupleAttribute compatibility(TupleAttribute left, TupleAttribute right) {
        if (left.equals(right))
            return ADMISSIBLE;
        else if (left.equals(UNKNOWN) || right.equals(UNKNOWN))
            return UNKNOWN;
        else
            return UNADMISSIBLE;
    }

    public static TupleAttribute fromChar(char input) {
        if (input == '+')
            return ADMISSIBLE;
        else if (input == 'h')
            return UNKNOWN;
        else if (input == '-')
            return UNADMISSIBLE;
        else
            return ADMISSIBLE;
    }

    public TupleAttribute invert() {
        switch (this) {
            case UNADMISSIBLE:
                return ADMISSIBLE;
            case UNKNOWN:
                return UNKNOWN;
            case ADMISSIBLE:
                return UNADMISSIBLE;
        }
        return UNKNOWN;
    }

    public String printable() {
        switch (this) {
            case UNADMISSIBLE:
                return "-";
            case UNKNOWN:
                return "h";
            case ADMISSIBLE:
                return "+";
        }
        return "h";
    }
}
