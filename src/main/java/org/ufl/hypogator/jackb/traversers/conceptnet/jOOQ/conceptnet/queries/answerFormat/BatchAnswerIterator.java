/*
 * BatchAnswerIterator.java
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

import java.util.Iterator;

public class BatchAnswerIterator implements Iterator<FirstBatchAnswer> {

    private Iterator<Edge> current;

    public BatchAnswerIterator(FirstBatchAnswer current) {
        this.current = current.iterator();
    }

    @Override
    public boolean hasNext() {
        return current != null;
    }

    @Override
    public FirstBatchAnswer next() {
        throw new UnsupportedOperationException("Error: Web api is no longer supported.");
        /*FirstBatchAnswer toReturn = current;
        if (toReturn != null) {
            current = toReturn.nextBatchAnswer();
            return toReturn.iterator();
        } else
            return null;*/
    }
}
