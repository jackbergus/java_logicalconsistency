/*
 * GenericEdge.java
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

package org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.relationships;

import org.ufl.hypogator.jackb.utils.Scored;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;

import java.util.List;

/**
 * Defines a general structure for Concepts's edges
 *
 * @param <EdgeType>
 */
public class GenericEdge<EdgeType> implements Scored {

    public final SemanticNetworkEntryPoint src;
    public final SemanticNetworkEntryPoint dst;
    public final String surfaceText;
    private double weight;
    public final List<String> context;
    public final EdgeType type;

    public GenericEdge(SemanticNetworkEntryPoint src, SemanticNetworkEntryPoint dst, String surfaceText, double weight, List<String> context, EdgeType type) {
        this.src = src;
        this.dst = dst;
        this.surfaceText = surfaceText;
        this.weight = weight;
        this.context = context;
        this.type = type;
    }

    @Override
    public double uncertainty() {
        return weight >= 1.0 ? 1.0 : weight;
    }

    public SemanticNetworkEntryPoint getSrc() {
        return src;
    }

    public SemanticNetworkEntryPoint getDst() {
        return dst;
    }

    public String getSurfaceText() {
        return surfaceText;
    }

    public List<String> getContext() {
        return context;
    }

    public EdgeType getType() {
        return type;
    }
}
