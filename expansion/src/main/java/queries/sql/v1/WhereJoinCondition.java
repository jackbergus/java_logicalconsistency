/*
 * WhereJoinCondition.java
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

import java.util.Objects;

public class WhereJoinCondition extends WhereEqValueCondition {

    public String tableLeft;
    public String argLeft;

    public String tableRight;
    public String argRight;

    public WhereJoinCondition(String tableLeft, String argLeft, String tableRight, String argRight) {
        super(tableLeft, argRight, tableRight+"."+argRight);
        this.tableLeft = tableLeft;
        this.argLeft = argLeft;
        this.tableRight = tableRight;
        this.argRight = argRight;
    }

    @Override
    public String toString() {
        return tableLeft + "." + argLeft +" = " + tableRight+"."+argRight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WhereJoinCondition that = (WhereJoinCondition) o;
        return Objects.equals(tableLeft, that.tableLeft) &&
                Objects.equals(argLeft, that.argLeft) &&
                Objects.equals(tableRight, that.tableRight) &&
                Objects.equals(argRight, that.argRight);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableLeft, argLeft, tableRight, argRight);
    }

    @Override
    public WhereJoinCondition copy() {
        return new WhereJoinCondition(tableLeft, argLeft, tableRight, argRight);
    }
}
