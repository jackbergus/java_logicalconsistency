/*
 * FirstBatchAnswer.java
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

package org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Iterator;
import java.util.List;

public class FirstBatchAnswer implements Iterable<Edge> {
    @JsonProperty("@context")
    private List<String> context;

    @JsonProperty("@id")
    private String id;

    @JsonProperty("edges")
    private List<Edge> edges;

    @JsonProperty("view")
    private View view;

    public Iterator<Edge> nextBatchAnswer() {
        return view == null ? null : view.firstBatchAnswer();
    }

    @Override
    public Iterator<Edge> iterator() {
        return edges.iterator();
    }
}
