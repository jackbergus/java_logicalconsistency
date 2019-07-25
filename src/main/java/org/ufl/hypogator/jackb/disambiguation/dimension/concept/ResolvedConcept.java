/*
 * ConceptNetDisambiguation.java
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

package org.ufl.hypogator.jackb.disambiguation.dimension.concept;

import javafx.util.Pair;
import org.ufl.hypogator.jackb.disambiguation.Resolved;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ResolvedConcept extends InformativeConcept implements Resolved {
    public static final long serialVersionUID = 42L;

    /*public ResolvedConcept(String matched, String dimension, Pair<Double, List<String>> value) {
        super(matched, dimension, value);
        setDisambiguation(this.getValue(), this, this.getScore().floatValue());
    }*/
    public final Double score;
    public final List<SemanticNetworkEntryPoint> list;

    public ResolvedConcept(String matched, String dimension, Double score, List<String> concpetResolved) {
        this(matched, dimension, score, concpetResolved, concpetResolved.isEmpty() ? Collections.emptyList() : null);
    }

    public ResolvedConcept(String matched, String dimension, Double score, List<String> concpetResolved, List<SemanticNetworkEntryPoint> lst) {
        super(matched, dimension, new Pair<>(score, concpetResolved));
        this.score = score;
        this.list = lst;
        setDisambiguation(this.getValue(), this, this.getScore().floatValue());
    }

    public ResolvedConcept(String t, String dim, Pair<Double, List<SemanticNetworkEntryPoint>> doubleListPair) {
        this(t, dim, doubleListPair.getKey(), doubleListPair.getValue().stream().map(SemanticNetworkEntryPoint::ensureValue).collect(Collectors.toList()), doubleListPair.getValue());
    }

    @Override
    public List<String> generateDisambiguationPath() {
        return getDisambiguationPath();
    }
}
