/*
 * ComparingConceptResolution.java
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

package org.ufl.hypogator.jackb.disambiguation.dimension.concept;

import com.google.common.collect.HashMultimap;
import javafx.util.Pair;
import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.comparators.partialOrders.InformationPreservingComparator;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.comparators.partialOrders.policy.DisambiguationPolicy;
import org.ufl.hypogator.jackb.comparators.partialOrders.policy.DisambiguationPolicyFactory;
import org.ufl.hypogator.jackb.disambiguation.dimension.memoization.MemoizationLessData;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;
import org.ufl.hypogator.jackb.utils.adt.HashMultimapSerializer;
import org.ufl.hypogator.jackb.utils.adt.Triple;

import java.io.*;

public class ComparingConceptResolution extends InformationPreservingComparator<ResolvedConcept> {

    //private final static DisambiguationPolicy dpf = DisambiguationPolicyFactory.getInstance().getPolicy(ConfigurationEntrypoint.getInstance().disambiguationPolicy);
    private final String dimension;
    private DisambiguatorForDimensionForConcept disambiguator;
    private final MemoizationLessData<String> memoizerTester = new MemoizationLessData<>();
    FileWriter fw;
    BufferedWriter bw;
    PrintWriter out;

    public ComparingConceptResolution(String dimension) {
        this.dimension = dimension;
        try {
            fw = new FileWriter("prediction.txt", true);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        bw = new BufferedWriter(fw);
        out = new PrintWriter(bw);
    }

    @Override
    protected PartialOrderComparison nonNullCompare(ResolvedConcept cpT, ResolvedConcept cpU) {
        if (cpT.str.equals(cpU.str)) {
            out.println("P("+cpT.str + " == "+ cpU.str + ")="+(cpT.score+cpU.score-cpT.score*cpU.score) );
            out.flush();
            return new PartialOrderComparison(POCType.Equal, cpT.score+cpU.score-cpT.score*cpU.score);
        } else {
            MemoizationLessData<String> memoizationGeneralizer = memoizerTester.invoke(cpT.str, cpU.str);
            if (memoizationGeneralizer.hasResult())
                return memoizationGeneralizer.getResult();
            Pair<String, String> cp = memoizationGeneralizer.getCp();

            // If t and u have a positive score, that means, they somehow belong to the hierarchy
            if (cpT.getScore() > 0 && cpU.getScore() > 0 && (!cpT.list.isEmpty()) && (!cpU.list.isEmpty())) {
                //HashMultimap<POCType, Double> map = HashMultimap.create();
                SemanticNetworkEntryPoint le = cpT.list.get(0);
                SemanticNetworkEntryPoint re = cpU.list.get(0);
                double leS = cpT.score /** (1.0/((double)left.disambiguation.size()))*/;
                //for (SemanticNetworkEntryPoint le : cpT.list) {
                    //for (SemanticNetworkEntryPoint re : cpU.list) {
                        double leSOrR = ((leS /* * (1.0/((double)right.disambiguation.size()))*/) * (cpU.score));
                        PartialOrderComparison cmp = disambiguator.getDirectionWithMemoization2(le, re);
                        //if (cmp.t != PartialOrderComparison.Type.Uncomparable)
                        //map.put(cmp.t, cmp.uncertainty * leSOrR);
                    //}
                //}

                out.println("P("+cpT.str + cmp.t + cpU.str + ")="+cmp.uncertainty);
                out.flush();
                memoizerTester.memoizeAs(cp, cmp.t, cmp.uncertainty);
                return cmp;

                // Check the subtyping direction
                //return disambiguator.getDirectionWithMemoization(cpT.getDisambiguated(), cpU.getDisambiguated());
                // Converting the direction into an optional numeric score
                /*switch (ret.getKey()) {
                    case LEFT_TYPE_RIGHT_SUBTYPE:
                        return PartialOrderComparison.PERFECT_GREATER;
                    case RIGHT_TYPE_LEFT_SUBTYPE:
                        return PartialOrderComparison.PERFECT_LESSER;
                    case BOTH:
                        return PartialOrderComparison.PERFECT_EQUAL;
                    case NONE:
                    default:
                        return PartialOrderComparison.PERFECT_UNCOMPARABLE;
                }*/
            } else {
                memoizerTester.memoizeAsNone(cp);
                return PartialOrderComparison.PERFECT_UNCOMPARABLE; // The caller will retrieve if the two strings are approximated, and therefore similar
            }
        }
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public void serializeToDisk(File file) {
        disambiguator.serializeToDisk(file);
        memoizerTester.serializeToDisk(new File(file.getAbsolutePath()+"_forStrings"));
    }

    @Override
    public void loadFromDisk(File file) {
        disambiguator.loadFromDisk(file);
        memoizerTester.loadFromDisk(new File(file.getAbsolutePath()+"_forStrings"));
        File[] folders = file.listFiles();
        if (folders != null) for (File subFolder : folders) {
            if (!subFolder.isDirectory()) continue;
            String baseFileName = file.getName()+"_memoization.jbin";
            disambiguator.appendFromDisk(new File(subFolder, baseFileName));
            memoizerTester.appendFromDisk(new File(subFolder, baseFileName+"_forStrings"));
        }
    }

    @Override
    public void close() {
        out.close();
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ComparingConceptResolution setDisabiguator(DisambiguatorForDimensionForConcept disambiguator) {
        this.disambiguator = disambiguator;
        return this;
    }

    @Override
    public void finalize() {
        close();
    }

}
