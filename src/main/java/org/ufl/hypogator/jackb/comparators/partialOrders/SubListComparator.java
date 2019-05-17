/*
 * SubListComparator.java
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

package org.ufl.hypogator.jackb.comparators.partialOrders;

import java.io.File;
import java.util.Collections;
import java.util.List;

/**
 * Comparing two hierarchies represented as lists.
 *
 * @param <T>
 */
public class SubListComparator<T> extends InformationPreservingComparator<List<T>> {

    private SubListComparator() {
    }

    private static SubListComparator self = new SubListComparator();

    public static <K> SubListComparator<K> getInstance() {
        return self;
    }

    @Override
    protected PartialOrderComparison nonNullCompare(List<T> leftHierarchy, List<T> rightHierarchy) {
        int cmpLeft, cmpRight;
        boolean isLeft = false, isRight = false;

        cmpRight = Collections.indexOfSubList(rightHierarchy, leftHierarchy);
        if (cmpRight >= 0) isRight = true;

        cmpLeft = Collections.indexOfSubList(leftHierarchy, rightHierarchy);
        if (cmpLeft >= 0) isLeft = true;

        if (isLeft && isRight) {
            return PartialOrderComparison.PERFECT_EQUAL;
        } else if (isLeft) {
            return PartialOrderComparison.PERFECT_LESSER;
        } else if (isRight) {
            return PartialOrderComparison.PERFECT_GREATER;
        } else {
            return PartialOrderComparison.PERFECT_UNCOMPARABLE;
        }
    }

    public PartialOrderComparison compare(List<T> left, List<T> right) {
        return super.compare(left == null || left.isEmpty() ? null : left,
                right == null || right.isEmpty() ? null : right);
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public void serializeToDisk(File file) {
        System.err.println("serializeToDisk: NOOP FOR "+getName());
    }

    @Override
    public void loadFromDisk(File file) {
        System.err.println("loadFromDisk: NOOP FOR "+getName());
    }

    @Override
    public void close() {

    }
}
