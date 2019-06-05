/*
 * MemoizationComparator.java
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

package org.ufl.hypogator.jackb.utils;

import org.ufl.hypogator.jackb.disambiguation.dimension.DimensionFactory;

import java.util.HashMap;

@Deprecated
public class MemoizationComparator {
    private MapMultiKey mkey;
    private HashMap<MapMultiKey, Boolean> memoizer;
    public int memoizedAccess = 0;
    public int retrievedAccess = 0;

    public MemoizationComparator() {
        memoizer = new HashMap<>();
    }

    public boolean invoke(String v1, String v2, String t1, String t2) {
        mkey = new MapMultiKey(v1, v2, t1, t2);
        Boolean val = memoizer.get(mkey);
        if (val == null) {
            retrievedAccess++;
            if (t1.equals(t2)) {
                switch (DimensionFactory.generate(t1).compare(v1, v2).t) {
                    case Greater:
                    case Uncomparable: {
                        memoizer.put(mkey, true);
                        return true;
                    }
                }
            } else {
                memoizer.put(mkey, true);
                return true;
            }
            memoizer.put(mkey, false);
            return false;
        } else {
            memoizedAccess++;
            return val;
        }
    }

    public double getHowMuchMemoizationInfluencesPerformances() {
        return ((double)memoizedAccess)/((double)(memoizedAccess+retrievedAccess));
    }

    private class MapMultiKey {
        private String v1;
        private String v2;
        private String t1;
        private String t2;

        public MapMultiKey(String v1, String v2, String t1, String t2) {
            this.v1 = v1;
            this.v2 = v2;
            this.t1 = t1;
            this.t2 = t2;
        }
    }
}
