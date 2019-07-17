/*
 * DisambiguationAlgorithm.java
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

package org.ufl.hypogator.jackb.disambiguation;

import javafx.util.Pair;
import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.logger.Logger;
import org.ufl.hypogator.jackb.logger.LoggerFactory;
import org.ufl.hypogator.jackb.utils.SetOperations;
import org.ufl.hypogator.jackb.utils.adt.Triple;

import java.util.*;
import java.util.stream.Collectors;

public class DisambiguationAlgorithm<T extends Resolved,
        K extends DisambiguatedValue<T>>

        implements DisambiguatorForDimension<T, K> {

    static Logger logger = LoggerFactory.getLogger(DisambiguationAlgorithm.class);
    static boolean debugging = ConfigurationEntrypoint.getInstance().logging;

    final HashMap<String, K> memoization = new HashMap<>();
    final DisambiguatorForDimension<T, K> disambiguator;
    private final double maximumThereshold;
    final String[] allowedKBTypesForTypingExpansion;
    private boolean doReflexivity;
    private disambiguationFromKB expansionDisambiguation;

    public DisambiguationAlgorithm(DisambiguatorForDimension<T, K> disambiguator, double maximumThereshold, String[] allowedKBTypesForTypingExpansion, boolean doReflexivity) {
        this.disambiguator = disambiguator;
        this.maximumThereshold = maximumThereshold;
        this.allowedKBTypesForTypingExpansion = allowedKBTypesForTypingExpansion;
        this.doReflexivity = doReflexivity;
    }

    private double score(K term) {
        return term.disambiguation.stream().map(Triple::third).max(Double::compare).orElse(0.0);
    }

    /**
     * This class determines which is the substring matching the term to disambiguate. This term will define the
     * basic term from which all the remaining parts will be detected
     *
     * @param toDisambiguate
     * @return
     */
    public K checkDisambiguation(String toDisambiguate) {
        if (debugging) logger.debug("checkDisambiguation for "+toDisambiguate);

        if (memoization.containsKey(toDisambiguate)) {
            if (debugging)
                logger.debug(toDisambiguate+" is memoized as "+memoization.get(toDisambiguate));
            return memoization.get(toDisambiguate);
        }

        /*if (toDisambiguate.equals("buildings"))
            System.err.println("DEBUG");*/
        K term = disambiguator.disambiguate(toDisambiguate);
        for (String allowedTypes : allowedKBTypesForTypingExpansion) {
            for (String additionalExamples : expansionDisambiguation.getPossibleCandidatesFor(toDisambiguate, allowedTypes, doReflexivity)) {
                term.expandWith(disambiguator.disambiguate(additionalExamples));
            }
        }

        if (debugging)
            logger.debug("disambiguated with "+term);

        double score = score(term);
        if (debugging)
            logger.debug("score associated to the term: "+score);

        if (term.isCorrectDimension() && score > maximumThereshold) {
            return term;
        } else {
            // If the term did not met the threshold, then I split the original term
            List<String> ls = Arrays.asList(toDisambiguate.split("\\s"));
            for (int i = 0, lsSize = ls.size(); i < lsSize; i++) {
                ls.get(i).trim();
            }
            List<Pair<String, K>> d = new ArrayList<>();
            {
                List<List<String>> pow_ls = SetOperations.powerSet(ls);
                Set<Pair<String, K>> disambiguateds = new HashSet<>();
                for (List<String> lsx : pow_ls) {
                    String x = lsx.stream().collect(Collectors.joining(" ")).trim();
                    // I do not match the full word, because that was matched before and it failed. Now, I'm matching the subwords
                    if (!x.isEmpty() && !x.equals(toDisambiguate)) {
                        K dis = disambiguator.disambiguate(x);
                        if (score(dis) > maximumThereshold)
                            disambiguateds.add(new Pair<>(x, disambiguator.disambiguate(x)));
                    }
                }
                d.addAll(disambiguateds);
            }

            Pair<String, K> cp = null;
            if (!d.isEmpty()) {
                Collections.sort(d, new CmpSuper<>());
                cp = d.get(d.size() - 1);
            }

            if (cp == null || cp.getValue() == null) {
                //System.err.println(toDisambiguate);
                memoization.put(toDisambiguate, term);
                return term;
            }

            memoization.put(toDisambiguate, cp.getValue());
            return cp.getValue();
        }
    }

    @Override
    public K disambiguate(String str) {
        return checkDisambiguation(str);
    }

    @Override
    public DisambiguationAlgorithm<T, K> getAlgorithm(double threshold) {
        return new DisambiguationAlgorithm<>(disambiguator, threshold, allowedKBTypesForTypingExpansion, doReflexivity);
    }

    @Override
    public String[] allowedKBTypesForTypingExpansion() {
        return allowedKBTypesForTypingExpansion;
    }

    @Override
    public boolean allowReflexiveExpansion() {
        return doReflexivity;
    }

    public void setExpansionDisambiguation(disambiguationFromKB expansionDisambiguation) {
        this.expansionDisambiguation = expansionDisambiguation;
    }

    /**
     * This dimension elects as a maximum the element with maximum score that is possibly equivalent to the given substring
     *
     * @param <T>
     */
    public static class CmpIntern<T> implements Comparator<Triple<String, T, Double>> {

        private final String superString;

        public CmpIntern(String superString) {
            this.superString = superString;
        }

        @Override
        public int compare(Triple<String, T, Double> o1, Triple<String, T, Double> o2) {
            boolean left = o1.first.equals(superString);
            boolean right = o2.first.equals(superString);
            int cmp = Double.compare(o1.third, o2.third);
            if (left) {               // if left
                return right ? cmp : Integer.MAX_VALUE;  // if right
            } else {
                return !right ? cmp : Integer.MIN_VALUE; // if not right
            }
        }
    }

    public static class CmpSuper<T extends Resolved, K extends DisambiguatedValue<T>> implements Comparator<Pair<String, K>> {
        @Override
        public int compare(Pair<String, K> o1, Pair<String, K> o2) {
            Triple<String, T, Double> f1 = o1.getValue().disambiguation.stream().max(new CmpIntern<>(o1.getKey())).orElseGet(() -> new Triple<>(o1.getKey(), null, 0.0));
            boolean left = f1.first.equals(o1.getKey());
            Triple<String, T, Double> f2 = o2.getValue().disambiguation.stream().max(new CmpIntern<>(o2.getKey())).orElseGet(() -> new Triple<>(o2.getKey(), null, 0.0));
            boolean right = f2.first.equals(o2.getKey());
            int cmp = Double.compare(f1.third, f2.third);
            if (left) {               // if left
                if (right)
                    return cmp;  // if right
                else
                    return Integer.MAX_VALUE;  // if right
            } else {
                if (!right)
                    return cmp; // if not right
                else
                    return Integer.MIN_VALUE; // if not right
            }
        }
    }

}
