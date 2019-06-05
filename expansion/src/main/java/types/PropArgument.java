/*
 * PropArgument.java
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

 
package types;

import algos.CopyConstructor;
import algos.Substitutable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

public class PropArgument implements CopyConstructor<PropArgument>, Substitutable<String> {
    public boolean isVariable;
    public String value;

    private PropArgument(boolean isVariable, String value) {
        this.isVariable = isVariable;
        this.value = value;
    }

    public PropArgument(PropArgument propArgument) {
        isVariable = propArgument.isVariable;
        value = new String(propArgument.value);
    }

    public static PropArgument var(String var) {
        return new PropArgument(true, var);
    }

    public static PropArgument val(String var) {
        return new PropArgument(false, var);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropArgument that = (PropArgument) o;
        return isVariable == that.isVariable &&
                Objects.equals(value, that.value);
    }

    public void asVariable() {
        if (!isVariable) {
            isVariable = true;
            value = "_"+value;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(isVariable, value);
    }

    @Override
    public PropArgument copy() {
        return new PropArgument(this);
    }

    @Override
    public int inductiveCasesSize() {
        return 0;
    }

    @Override
    public int boundedCasesSize() {
        return 1;
    }

    @Override
    public Substitutable<String> getInductiveCase(int i) {
        return null;
    }

    @Override
    public String getBoundedCases(int i) {
        return i == 0 ? value : null;
    }

    @Override
    public void updateCaseWith(int i, String newCase) {
        //noop
    }

    @Override
    public void updateBoundedCaseWith(int i, String newCase) {
        if (i == 0) {
            value = newCase;
        }
    }

    @Override @JsonIgnore
    public Set<String> getFreeVariables() {
        return Collections.singleton(value);
    }
}
