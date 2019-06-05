/*
 * EventAssociation.java
 * This file is part of KnowledgeBaseExpansion
 *
 * Copyright (C) 2019 - Giacomo Bergami
 *
 * KnowledgeBaseExpansion is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * KnowledgeBaseExpansion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KnowledgeBaseExpansion. If not, see <http://www.gnu.org/licenses/>.
 */

 
package ref;

import algos.Substitute;
import types.Schema;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EventAssociation {
    /**
     * Element that is associated to a begin and end event
     */
    public String schemaName;
    /**
     * Specification of the begin event
     */
    public BeginEndSpecification begin;
    /**
     * Specification of the end event
     */
    public BeginEndSpecification end;

    /**
     * A transfer of ownership: relaxation of the inconsistency contstraint when two element can violate the MVD only for a given amount of time
     */
    public boolean isTransfer;


    public EventAssociation(String schemaName) {
        this.schemaName = schemaName;
        begin = end = null;
    }

    public static class BeginEndSpecification {
        /**
         * If the variable is set to true, once it starts and it ends, it is for ever.
         * Otherwise, if no instance is repeated in the future, then it won't never happen again
         */
        public boolean isUnique;

        /**
         * Event name associated to the begin/end
         */
        public String associatedEventName;

        /**
         * Target on how to rewrite the current element into the given specification
         */
        public Schema schema;

        /**
         * Time arguments associated to the event description
         */
        public ArrayList<String> timeArgs;
        public Map<Integer, String> schemaArgumentWithNoTime() {
            int n = schema.arguments.size();
            return IntStream.range(0, n)
                    .mapToObj(x -> new Substitute.SubPair<>(x, schema.arguments.get(x)))
                    .filter(x -> !timeArgs.contains(x.second))
                    .collect(Collectors.toMap(x -> x.first, x -> x.second));
        }
    }
}
