/*
 * Substitutable.java
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

 
package algos;

import java.util.Set;

public interface Substitutable<T> {
    public int inductiveCasesSize();
    public int boundedCasesSize();
    public Substitutable<T> getInductiveCase(int i);
    public T getBoundedCases(int i);
    public void updateCaseWith(int i, T newCase);
    void updateBoundedCaseWith(int i, T newCase);
    public Set<T> getFreeVariables();
    public default void updateWith(T oldCase, T newCase) {
        Substitute<T, ? extends Substitutable<T>> l = new Substitute<>(oldCase);
        l.accept(new Substitute.SubPair<>(this, newCase));
    }
}
