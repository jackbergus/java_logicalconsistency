/*
 * Scorer.java
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

package org.ufl.hypogator.jackb.scraper;

import javafx.util.Pair;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetVocabulary;
import org.ufl.hypogator.jackb.fuzzymatching.FuzzyMatcher;
import org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNet5Postgres;

import java.util.List;

public interface TermScorer<T> {
    Pair<Double, List<T>> scoreWithPath(T root, T elem);
    FuzzyMatcher<ConceptNet5Postgres.RecordResultForSingleNode> getEnrichedVocabulary();
    String getDimension();
}
