/*
 * QuadrupleScraper.java
 * This file is part of scraper
 *
 * Copyright (C) 2018 giacomo
 *
 * scraper is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * scraper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with scraper. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.ufl.hypogator.jackb.scraper.adt;

import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;

public class QuadrupleScraper {
    public final SemanticNetworkEntryPoint root;
    public final SemanticNetworkEntryPoint current;
    public final int count;
    public final double probability;

    public QuadrupleScraper(SemanticNetworkEntryPoint root, SemanticNetworkEntryPoint current, int count, double probability) {
        this.root = root;
        this.current = current;
        this.count = count;
        this.probability = probability;
    }

    @Override
    public String toString() {
        return "QuadrupleScraper{" +
                "root='" + root + '\'' +
                ", current='" + current + '\'' +
                ", count=" + count +
                ", probability=" + probability +
                '}';
    }
}
