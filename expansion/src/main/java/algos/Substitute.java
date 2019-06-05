/*
 * Substitute.java
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

import java.util.Objects;
import java.util.function.Consumer;

public class Substitute<K, T extends Substitutable<K>> implements Consumer<Substitute.SubPair<Substitutable<K>, K>> {

    private K toReplace;

    public Substitute(K toReplace) {
        this.toReplace = toReplace;
    }

    public static class SubPair<K, V> {
        public K first;
        public V second;

        public SubPair(K first, V second) {
            this.first = first;
            this.second = second;
        }
    }

    @Override
    public void accept(SubPair<Substitutable<K>, K> cp) {
        Substitutable<K> kSubstitutable = cp.first;
        if (cp.first == null)
            return;
        K newValue = cp.second;
        int inductiveN = kSubstitutable.inductiveCasesSize();
        if (inductiveN == 0) {
            for (int i = 0, M = kSubstitutable.boundedCasesSize(); i<M; i++) {
                K element = kSubstitutable.getBoundedCases(i);
                if (Objects.equals(toReplace, element))
                    kSubstitutable.updateBoundedCaseWith(i, newValue);
            }
        } else {
            int bindedM = kSubstitutable.boundedCasesSize();
            if (bindedM == 0) {
                for (int i = 0; i<inductiveN; i++) {
                    // cp.second is still always the same
                    accept(new SubPair<>(kSubstitutable.getInductiveCase(i), cp.second));
                }
            } else {
                for (int j = 0; j<bindedM; j++) {
                    if (Objects.equals(kSubstitutable.getBoundedCases(j), toReplace))
                        return;
                }
                if (!kSubstitutable.getFreeVariables().contains(toReplace)) {
                    for (int i = 0; i<inductiveN; i++) {
                        accept(new SubPair<>(kSubstitutable.getInductiveCase(i), cp.second));
                    }
                }
            }
        }
    }

    public void setSubstitute(K newCase) {
        this.toReplace = newCase;
    }
}
