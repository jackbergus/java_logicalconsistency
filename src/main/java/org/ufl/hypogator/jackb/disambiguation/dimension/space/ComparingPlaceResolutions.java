/*
 * ComparingPlaceResolutions.java
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

package org.ufl.hypogator.jackb.disambiguation.dimension.space;

import javafx.util.Pair;
import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.comparators.partialOrders.InformationPreservingComparator;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.comparators.partialOrders.SubListComparator;
import org.ufl.hypogator.jackb.disambiguation.dimension.memoization.MemoizationLessData;
import org.ufl.hypogator.jackb.disambiguation.dimension.space.geonames.AdditionalSpaceHierarchy;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;
import org.ufl.hypogator.jackb.scraper.adt.DiGraph;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.ufl.hypogator.jackb.comparators.partialOrders.POCType.Greater;

/**
 * Class providing the spatial dimension for disambiguated toponyms
 */
public class ComparingPlaceResolutions extends InformationPreservingComparator<ResolvedSpace> {

    /**
     * Instance used to compare administrative specifications
     */
    private final static SubListComparator<String> cmp = SubListComparator.getInstance();
    protected final static AdditionalSpaceHierarchy countryCheck = AdditionalSpaceHierarchy.instance();
    public static final MemoizationLessData<ResolvedSpace> memoizerTester = new MemoizationLessData<>();

    private static DiGraph<Long> hg;

    static {
        try {
            /**
             * The enriched hierarchy, which uses both GeoNames hierarchical information and GeoNames' administrative
             * hierarchy.
             */
            hg = new DiGraph<Long>().loadFromFile(ConfigurationEntrypoint.getInstance().geonamesHierarchy, null, Long::valueOf);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final DisambiguatorForSpace res = DisambiguatorForSpace.getInstance();

    //private final static HashMap<Pair<ResolvedSpace, ResolvedSpace>, PartialOrderComparison> memoize = new HashMap<>();

    /**
     * Geographical comparison:
     * First, I try to check for a comparison within the hierarchy. This is the best option to get inclusion directions.
     * If there is no perfect path within such hierarchy, then we exploit the
     *
     * @param left
     * @param right
     * @return
     */
    @Override
    protected PartialOrderComparison nonNullCompare(ResolvedSpace left, ResolvedSpace right) {

        MemoizationLessData<ResolvedSpace> memoizationGeneralizer = memoizerTester.invoke(left, right);
        if (memoizationGeneralizer.hasResult())
            return memoizationGeneralizer.getResult();


        /*Pair<ResolvedSpace, ResolvedSpace> cp = new Pair<>(left, right);
        Pair<ResolvedSpace, ResolvedSpace> cpI = new Pair<>(right, left);*/
        /*PartialOrderComparison mres = memoize.get(cp);
        if (mres != null)
            return mres;*/


        double both = 1 - ((1-left.getConfidence())*(1-right.getConfidence()));
        PartialOrderComparison cpSole = new PartialOrderComparison(POCType.Uncomparable, both);

        // First I try to check whether there is a direct relationships between the geographical elements within the hierarchy
        // I perform this first attempt because this way is more precise than the one that wil<l follow.

        // Checking for x -> continent path can be gruesome. Therefore, I perform the
        // world and continent match before ever using the hierarchy

        // 1. World is always the everything's container
        Long leftId = left.getId();
        Long rightId = right.getId();
        if (Objects.equals(leftId, rightId)) {
            memoizerTester.memoizeAs(POCType.Equal, both);
            //memoize.put(cpI, new PartialOrderComparison(PartialOrderComparison.Type.Equal, both));
            return new PartialOrderComparison(POCType.Equal, both);
        }

        if (leftId.equals(ResolvedSpace.worldId)) {
            if (rightId.equals(ResolvedSpace.worldId)) {
                memoizerTester.memoizeAs(POCType.Equal, both);
                //memoize.put(cpI, new PartialOrderComparison(PartialOrderComparison.Type.Equal, both));
                return new PartialOrderComparison(POCType.Equal, both);
            } else {
                memoizerTester.memoizeAs(POCType.Greater, both);
                //memoize.put(cpI, new PartialOrderComparison(PartialOrderComparison.Type.Lesser, both));
                return new PartialOrderComparison(Greater, both);
            }
        } else {
            if (rightId.equals(ResolvedSpace.worldId)) {
                memoizerTester.memoizeAs(POCType.Equal, both);
                //memoize.put(cpI, new PartialOrderComparison(PartialOrderComparison.Type.Equal, both));
                return new PartialOrderComparison(POCType.Equal, both);
            }
        }

        // 2. Otherwise, I try to check if there is a place--> continent relationship
        {
            if (right.isContinent()) {
                Pair<String, Long> leftCountryCp = left.getCountry();
                String leftCountry = leftCountryCp == null ? null : leftCountryCp.getKey();
                Long leftContinent = countryCheck.getContinentLongFromstate(leftCountry);
                if (Objects.equals(leftContinent, rightId)) {
                    memoizerTester.memoizeAs(POCType.Lesser, both);
                    //memoize.put(cpI, new PartialOrderComparison(Greater, both));
                    return new PartialOrderComparison(POCType.Lesser, both);
                } else {
                    memoizerTester.memoizeAs(POCType.Uncomparable, both);
                    //memoize.put(cpI, cpSole);
                    return cpSole;
                }
            }

            if (left.isContinent()) {
                Pair<String, Long> rightCountryCp = right.getCountry();
                String rightCountry = rightCountryCp == null ? null : rightCountryCp.getKey();
                Long rightContinent = countryCheck.getContinentLongFromstate(rightCountry);
                if (Objects.equals(rightContinent, leftId)) {
                    memoizerTester.memoizeAs(POCType.Greater, both);
                    //memoize.put(cpI, new PartialOrderComparison(PartialOrderComparison.Type.Lesser, both));
                    return new PartialOrderComparison(Greater, both);
                } else {
                    memoizerTester.memoizeAs(POCType.Uncomparable, both);
                    //memoize.put(cpI, cpSole);
                    return cpSole;
                }
            }
        }

        // 3. If the entities belong to a different state (states included), they must differ, too.
        try {
            Long lcode = left.getCountry()== null ? null: left.getCountry().getValue();
            Long rcode = right.getCountry() == null ? null : right.getCountry().getValue();
            if (! Objects.equals(lcode, rcode)) {
                memoizerTester.memoizeAs(POCType.Uncomparable, both);
                //memoize.put(cpI, cpSole);
                return cpSole;
            } else {
                if (left.isCountry() && Objects.equals(rcode, left.getId())) {
                    memoizerTester.memoizeAs(POCType.Greater, both);
                    //memoize.put(cpI, new PartialOrderComparison(PartialOrderComparison.Type.Lesser, both));
                    return new PartialOrderComparison(Greater, both);
                } else if (right.isCountry() && Objects.equals(lcode, right.getId())) {
                    memoizerTester.memoizeAs(POCType.Lesser, both);
                    //memoize.put(cpI, new PartialOrderComparison(Greater, both));
                    return new PartialOrderComparison(POCType.Lesser, both);
                }
            }
        } catch (Exception e) {
            //System.err.println(e);
        }

        // Otherwise, I perform comparisons within the state
        if (Objects.equals(leftId, rightId)) {
            memoizerTester.memoizeAs(POCType.Equal, both);
            //memoize.put(cpI, new PartialOrderComparison(PartialOrderComparison.Type.Equal, both));
            return new PartialOrderComparison(POCType.Equal, both);
        }

        List<DiGraph<Long>.Vertex> path = hg.getPath(leftId, rightId);
        if (path != null && path.size() > 1) {
            memoizerTester.memoizeAs(POCType.Lesser, both);
            //memoize.put(cpI, new PartialOrderComparison(Greater, both));
            return new PartialOrderComparison(POCType.Lesser, both);
        }
        path = hg.getPath(rightId, leftId);
        if (path != null && path.size() > 1) {
            //printPath(path);
            memoizerTester.memoizeAs(POCType.Greater, both);
            //memoize.put(cpI, new PartialOrderComparison(PartialOrderComparison.Type.Lesser, both));
            return new PartialOrderComparison(Greater, both);
        }

        PartialOrderComparison fres = cmp.compare(left.asSMEPList().stream().map(SemanticNetworkEntryPoint::getValue).collect(Collectors.toList()), right.asSMEPList().stream().map(SemanticNetworkEntryPoint::getValue).collect(Collectors.toList()));
        PartialOrderComparison toret = new PartialOrderComparison(fres.t, both);
        /*PartialOrderComparison toret;
        switch (fres.t) {
            case Lesser: {
                toret = new PartialOrderComparison(fres.t, both);
                memoize.put(cp, toret);
                //memoize.put(cpI, new PartialOrderComparison(Greater, both));
            }
            case Greater: {
                toret = new PartialOrderComparison(fres.t, both);
                memoize.put(cp, toret);
                //memoize.put(cpI, new PartialOrderComparison(Lesser, both));
            }

            case Equal:
            case Uncomparable:
            default:{
                PartialOrderComparison cpr = toret = new PartialOrderComparison(fres.t, both);
                memoize.put(cp, cpr);
                //memoize.put(cpI, cpr);
            }
        }*/
        memoizerTester.memoizeAs(fres.t, both);
        return toret;
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public void serializeToDisk(File file) {
        memoizerTester.serializeToDisk(file);
    }

    @Override
    public void loadFromDisk(File file) {
        memoizerTester.loadFromDisk(file);
    }

    @Override
    public void close() {

    }

    public List<DiGraph<Long>.Vertex> getPath(Long leftId, Long rightId) {
        return hg.getPath(leftId, rightId);
    }

    public String getStringDescription(int intValue) {
        return res.getStringDescription(intValue);
    }

    public Long getContinentLongFromstate(String rightCountry) {
        return countryCheck.getContinentLongFromstate(rightCountry);
    }
}
