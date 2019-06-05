/*
 * CopyConstructor.java
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface CopyConstructor<T> extends Cloneable {
    <K extends CopyConstructor<T>> K copy();

    public static ArrayList emptyArrayList = new ArrayList<>();
    static <T, K extends CopyConstructor<T>> ArrayList<K> listCopy(List<K> original) {
        if (original != null) {
            ArrayList<K> toreturn = new ArrayList<>(original.size());
            for (int i = 0, originalSize = original.size(); i < originalSize; i++) {
                CopyConstructor<T> x = original.get(i);
                toreturn.add(x.copy());
            }
            return toreturn;
        } else {
            return (ArrayList<K>)emptyArrayList;
        }
    }
}
