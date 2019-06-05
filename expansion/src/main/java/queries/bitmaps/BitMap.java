/*
 * BitMap.java
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

 
package queries.bitmaps;

import java.util.Arrays;
import java.util.stream.Collectors;

public class BitMap {

    public int[] bitmap;
    private int size;

    public BitMap(int size) {
        bitmap = new int[size];
        this.size = size;
    }

    public void onAll() {
        for (int i = 0; i<size; i++)
            bitmap[i] = 1;
    }

    public void offAll() {
        for (int i = 0; i<size; i++)
            bitmap[i] = 0;
    }

    public void invert() {
        for (int i = 0; i<size; i++)
            bitmap[i] = 1-bitmap[i];
    }

    public void on(int pos) {
        if (pos >= 0 && pos < size) {
            bitmap[pos] = 1;
        }
    }

    public void off(int pos) {
        if (pos >= 0 && pos < size) {
            bitmap[pos] = 0;
        }
    }

    @Override
    public String toString() {
        return "B'"+ Arrays.stream(bitmap).mapToObj(Integer::toString).collect(Collectors.joining())+"'";
    }

    public String toString2() {
        return  Arrays.stream(bitmap).mapToObj(Integer::toString).collect(Collectors.joining());
    }

}
