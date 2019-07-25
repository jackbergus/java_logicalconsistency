/*
 * TupleComparator.java
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

package org.ufl.hypogator.jackb.inconsistency.legacy;

import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.comparators.partialOrders.InformationPreservingComparator;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.DimConcepts;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.DimConceptsUnion;
import org.ufl.hypogator.jackb.disambiguation.dimension.space.DimLocation;
import org.ufl.hypogator.jackb.disambiguation.dimension.time.DimTime;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;
import org.ufl.hypogator.jackb.inconsistency.fieldgrouping.FieldGroupingPolicy;
import org.ufl.hypogator.jackb.inconsistency.fieldgrouping.FieldGroupingPolicyFactory;
import org.ufl.hypogator.jackb.traversers.conceptnet.Concept5ClientConfigurations;

import java.io.File;
import java.util.*;

public class TupleComparator extends InformationPreservingComparator<AgileRecord> {

    public final static String TIME = "Time";
    public final static String LOCATION = "Location";
    public final static String PLACE = "Place";

    private  static DimLocation l;
    private  static DimTime t;
    private final static HashMap<String, DimConceptsUnion> conceptFieldMap = new HashMap<>();
    private final static HashMap<String, DimConcepts> conceptTypeMap = new HashMap<>();

    private final static Collection<String> availableTypes = Concept5ClientConfigurations.instantiate().conceptnetResolvableTypes();
    public final static String[] relevantFields = new String[]{TIME, LOCATION, PLACE, "Origin", "Destination"};
    private final FieldGroupingPolicy groupingPolicy;

    public boolean doesInternalPolicyNotRequireExtendedComparison() {
        return groupingPolicy.doesPolicyNotRequireExtendedComparison();
    }

    private TupleComparator(FieldGroupingPolicy groupingPolicy) {
        this.groupingPolicy = groupingPolicy;
    }

    public static TupleComparator getDefaultTupleComparator() {
        return new TupleComparator(FieldGroupingPolicyFactory.getInstance().getPolicy(ConfigurationEntrypoint.getInstance().groupingPolicy));
    }

    public static TupleComparator getTupleComparatorWithCustomSettings(String settings) {
        return new TupleComparator(FieldGroupingPolicyFactory.getInstance().getPolicy(settings));
    }

    public final static boolean isFieldSaferThanType(String fieldName) {
        for (String x : relevantFields) {
            if (fieldName.endsWith(x)) return true;
        }
        return false;
    }

    public static InformationPreservingComparator<String> generateFromField(String field) {
        if (field.endsWith(TIME)) {
            if (t == null) t = new DimTime();
            return t;
        } else if (field.endsWith(LOCATION) || field.endsWith(PLACE) || field.endsWith("Origin") || field.endsWith("Destination")) {
            if (l == null) l = new DimLocation();
            return l;
        } else {
            DimConceptsUnion concept = conceptFieldMap.get(field);
            if (concept == null) {
                concept = new DimConceptsUnion(field);
                conceptFieldMap.put(field, concept);
            }
            return concept;
        }
    }

    public static InformationPreservingComparator<String> generateFromType(String type) {
        if (type.endsWith(TIME)) {
            if (t == null) t = new DimTime();
            return t;
        } else if (type.endsWith(LOCATION) || type.endsWith(PLACE) || type.endsWith("Origin") || type.endsWith("Destination")) {
            if (l == null) l = new DimLocation();
            return l;
        } else {
            // If the dimension is among the extracted ones,...
            if (availableTypes.contains(type)) {
                // Then load it, and use that as a mean of comparison
                DimConcepts concept = conceptTypeMap.get(type);
                if (concept == null) {
                    concept = new DimConcepts(type);
                    conceptTypeMap.put(type, concept);
                }
                return concept;
            } else {
                /*if (u == null)
                    u = new DimConcepts("Union");*/
                return null;
            }
        }
    }

    public static  InformationPreservingComparator<String> generateFromTypeAndField(String label, String type) {
        return isFieldSaferThanType(label) ? generateFromField(label) : generateFromType(type);
    }

    public static TupleComparator getTupleComparatorWithCustomSettings(FieldGroupingPolicy internal) {
        return new TupleComparator(internal);
    }

    @Override
    protected PartialOrderComparison nonNullCompare(AgileRecord left,
                                                    AgileRecord right) {
        HashSet<POCType> types = new HashSet<>();

        double score = groupingPolicy.fieldGroupingPolicy(left, right, types);

        // This outcome is not memoized
        if (types.contains(POCType.Uncomparable)) {
            return (new PartialOrderComparison(POCType.Uncomparable, score));
        } else if (types.contains(POCType.Lesser) && types.contains(POCType.Greater)) {
            return (new PartialOrderComparison(POCType.Uncomparable, score));
        } else if (types.contains(POCType.Lesser)) {
            return (new PartialOrderComparison(POCType.Lesser, score));
        } else if (types.contains(POCType.Greater)) {
            return (new PartialOrderComparison(POCType.Greater, score));
        } else if (types.contains(POCType.Equal)) {
            return (new PartialOrderComparison(POCType.Equal, score));
        } else {
            return (new PartialOrderComparison(POCType.Lesser, score));
        }
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public void serializeToDisk(File file) {
        File dimensionsNotFromField = new File(file, "nofields");
        File[] dimensions = dimensionsNotFromField.listFiles();
        if (dimensions != null) for (File dimension_d : dimensions) {
            String dimensionName = dimension_d.getName();
            InformationPreservingComparator<String> element = generateFromType(dimensionName);
            if (element != null) {
                element.loadFromDisk(dimension_d);
            }
        }
        // FIXME: todo (load fileds)

        //throw new UnsupportedOperationException("Error: this task must be still implemented. The data that is currently serialized won't have the expected data format");
        /*System.out.println("Serializing every component using "+file+" as a directory");
        if ((!file.exists()) || (file.exists() && file.isDirectory())) {
            l.serializeToDisk(new File(file, "dim_location.jbin"));
            t.serializeToDisk(new File(file, "dim_time.jbin"));
            conceptFieldMap.forEach((k, v) -> v.serializeToDisk(new File(file, "dim_"+k+".jbin")));
            conceptTypeMap.forEach((k, v) -> {
                File unionFolder = new File(file, "dim_"+k+"_union_folder");
                if (!unionFolder.exists())
                    unionFolder.mkdirs();
                if (!unionFolder.isDirectory())
                    System.err.println("The computation will halt soon: "+unionFolder+" is not a directory");
                v.serializeToDisk(new File(unionFolder, "dim_"+k+".jbin"));
            });
        }*/
    }

    @Override
    public void loadFromDisk(File file) {
        // FIXME: File dimensionsNotFromField = new File(file, "nofields");
        File[] dimensions = file.listFiles();
        if (dimensions != null) for (File dimension_d : dimensions) {
            String dimensionName = dimension_d.getName();
            InformationPreservingComparator<String> element = generateFromType(dimensionName);
            if (element != null) {
                element.loadFromDisk(dimension_d);
            }
        }
    }

    public void close() {
        conceptTypeMap.values().forEach(DimConcepts::close);
    }
}
