/*
 * HammingDistance.java
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

package org.ufl.hypogator.jackb.fuzzymatching;

public class HammingDistance extends Similarity {
    @Override
    public double sim(String One, String Two)
    {
        if (One.length() != Two.length())
            return -1;

        int counter = 0;

        for (int i = 0; i < One.length(); i++)
        {
            if (One.charAt(i) != Two.charAt(i)) counter++;
        }

        return counter;
    }
}
