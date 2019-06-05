/*
 * QueryCollection.java
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

 
package queries.sql.v1;

import algos.CopyConstructor;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class QueryCollection implements CopyConstructor<QueryCollection> {
    public ArrayList<SelectFromWhere> distinctQueries;
    public SetOperations op;
    public boolean isItAll;

    public QueryCollection(SetOperations op, boolean isItAll) {
       this(op, isItAll,0);
    }

    public QueryCollection(SetOperations op, boolean isItAll, int i) {
        this.op = op;
        distinctQueries = new ArrayList<>(i);
        this.isItAll = isItAll;
    }

    public QueryCollection(ArrayList<SelectFromWhere> distinctQueries, SetOperations op, boolean isItAll) {
        this.distinctQueries = distinctQueries;
        this.op = op;
        this.isItAll = isItAll;
    }

    public void add(SelectFromWhere sfw) {
        distinctQueries.add(sfw);
    }

    @Override
    public String toString() {
        return distinctQueries.stream().map(SelectFromWhere::toString).collect(Collectors.joining(op+" "+(isItAll ? "ALL " : "")+"\n\n\t"));
    }

    public boolean isEmpty() {
        return distinctQueries.isEmpty();
    }

    @Override
    public QueryCollection copy() {
        return new QueryCollection(CopyConstructor.listCopy(distinctQueries), op, isItAll);
    }
}
