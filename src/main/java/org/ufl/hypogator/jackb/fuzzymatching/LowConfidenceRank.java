/*
 * LowConfidenceRank.java
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
package org.ufl.hypogator.jackb.fuzzymatching;

import javafx.util.Pair;

import java.util.*;

/**
 * @author vasistas
 */
public class LowConfidenceRank extends Similarity {

    private static LowConfidenceRank self;

    LowConfidenceRank() {
    }

    public static LowConfidenceRank getInstance() {
        if (self == null)
            self = new LowConfidenceRank();
        return self;
    }

    //Credits to http://www.catalysoft.com/articles/StrikeAMatch.html
    private static String[] compareString_letterPairs(String str) {
        int numPairs = str.length() - 1;
        if (numPairs == 0)
            return new String[]{str};
        if (numPairs < 0) numPairs = 0;
        String[] pairs = new String[numPairs];
        for (int i = 0; i < numPairs; i++)
            pairs[i] = str.substring(i, i + 2);
        return pairs;
    }

    private static Pair<HashMap<String, Integer>, List<Integer>> compareStringHashmap(String str, Pair<HashMap<String, Integer>, List<Integer>> cp) {
        if (cp == null) {
            cp = new Pair<>(new HashMap<>(), new ArrayList<>());
        }
        int numPairs = str.length() - 1;
        if (numPairs == 0) {
            cp.getKey().put(str, 0);
            cp.getValue().add(1);
            return cp;
        }
        if (numPairs < 0) numPairs = 0;
        int singleGrams = 0;
        for (int i = 0; i < numPairs; i++) {
            String s = str.substring(i, i + 2);
            Integer pos = cp.getKey().get(s);
            if (pos == null) {
                cp.getKey().put(s, singleGrams++);
                cp.getValue().add(1);
            } else {
                int val = cp.getValue().get(pos);
                cp.getValue().set(pos, val+1);
            }
        }


        return cp;
    }

    //Credits to http://www.catalysoft.com/articles/StrikeAMatch.html
    public static ArrayList<String> compareString_wordLetterPairs(String str) {
        ArrayList<String> allPairs = new ArrayList<>();
        String[] words = str.split("\\s");
        for (String w : words) {
            String[] pairsInWord = compareString_letterPairs(w);
            allPairs.addAll(Arrays.asList(pairsInWord));
        }
        return allPairs;
    }

    public static Pair<HashMap<String, Integer>, List<Integer>> compareStringHashMap(String str) {
        String[] words = str.split("\\s");
        Pair<HashMap<String, Integer>, List<Integer>> cp = null;
        for (String w : words) {
            cp = compareStringHashmap(w, cp);
        }
        for (Map.Entry<String, Integer> kp : cp.getKey().entrySet()) {
            try {
                kp.setValue(cp.getValue().get(kp.getValue()));
            } catch (IndexOutOfBoundsException iobe) {
                iobe.printStackTrace();
            }
        }
        return cp;
    }

    /**
     * Scores the
     *
     * @param str1
     * @param str2
     * @return
     * @author http://www.catalysoft.com/articles/StrikeAMatch.html
     */
    @Override
    public double sim(String str1, String str2) {
        if (str1.length() == 0 && str2.length() == 0)
            return 1;
        else if ((str1.length() == 0 || str2.length() == 0))
            return 0;
        ArrayList<String> pairs1 = compareString_wordLetterPairs(str1.toLowerCase());
        ArrayList<String> pairs2 = compareString_wordLetterPairs(str2.toLowerCase());
        int intersection = 0;
        int union = pairs1.size() + pairs2.size();
        for (String pair1 : pairs1) {
            String toremove = null;
            for (String pair2 : pairs2) {
                if (pair1.equals(pair2)) {
                    intersection++;
                    toremove = pair2;
                    break;
                }
            }
            if (toremove != null)
                pairs2.remove(toremove);
        }
        return (2.0 * intersection) / union;
    }

    public static void main(String args[]) {
        System.out.println(
                new LowConfidenceRank().sim("airplane", "Yanukovitch's airplane"));
    }

}
