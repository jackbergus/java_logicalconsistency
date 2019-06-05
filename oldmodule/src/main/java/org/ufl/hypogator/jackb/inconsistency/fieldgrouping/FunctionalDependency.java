package org.ufl.hypogator.jackb.inconsistency.fieldgrouping;

import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;
import org.ufl.hypogator.jackb.inconsistency.legacy.TupleComparator;
import org.ufl.hypogator.jackb.m9.fdep.FDep;
import org.ufl.hypogator.jackb.m9.fdep.FDepFile;

import java.util.HashSet;
import java.util.Set;

public class FunctionalDependency implements FieldGroupingPolicy {

    private final HashMultimap<String, FDep> file;
    private final TupleComparator compareHeadWithCustomConfiguration;
    private final FieldGroupingPolicy internal;

    public FunctionalDependency(FieldGroupingPolicy internal) {
        this.internal = internal;
        compareHeadWithCustomConfiguration = TupleComparator.getTupleComparatorWithCustomSettings(internal);
        file = new FDepFile(ConfigurationEntrypoint.getInstance().functionalDependencyFile).fDepMap;
    }

    public FunctionalDependency(FieldGroupingPolicy internal, HashMultimap<String, FDep> file) {
        this.internal = internal;
        compareHeadWithCustomConfiguration = TupleComparator.getTupleComparatorWithCustomSettings(internal);
        this.file = file;
    }

    public double compareRecursively(FDep dep, AgileRecord left, AgileRecord right, HashSet<POCType> types) {
        int rec = 0;
        while (rec < dep.size()) {
            if (rec == dep.size()-1) {
                AgileRecord lProj = left.projectWith(dep.get(rec));
                AgileRecord rProj = right.projectWith(dep.get(rec));

                // Everything reduces to the rest, the inconsistency included, unless some information is missing
                if (lProj.size() > 0 && rProj.size() > 0 && lProj.schema.containsAll(rProj.schema) && rProj.schema.containsAll(lProj.schema))
                return internal.fieldGroupingPolicy(lProj, rProj, types);

                // If some information is missing, nothing can be stated
                types.add(POCType.Equal); // forcing the fact that the two elements are not inconsistent
                return 1.0;
            } else {
                AgileRecord lProj = left.projectWith(dep.get(rec));
                AgileRecord rProj = right.projectWith(dep.get(rec));
                if (lProj.size() > 0 && rProj.size() > 0 && lProj.schema.containsAll(rProj.schema) && rProj.schema.containsAll(lProj.schema)) {
                    PartialOrderComparison result = compareHeadWithCustomConfiguration.compare(lProj, rProj);
                    if (!result.t.equals(POCType.Uncomparable)) {
                        rec++;
                        continue;
                    }
                }
                types.add(POCType.Equal); // forcing the fact that the two elements are not inconsistent
                return 1.0;
            }
        }
        types.add(POCType.Equal);
        return 1.0;
    }

    @Override
    public double fieldGroupingPolicy(AgileRecord left, AgileRecord right, HashSet<POCType> types) {
        if (left.nistType.equals(right.nistType)) {
            Set<FDep> deps = file.get(left.nistType);
            if (left.nistType.equals("Life.Die") && left.schema.contains("Agent") && left.schema.contains("Victim"))
                System.out.println("DEBUG");
            if (!deps.isEmpty()) {
                // Checking that all the functional dependencies must be satisfied for the given schema
                for (FDep dep : deps) {
                    double res = compareRecursively(dep, left, right, types);
                    if (types.contains(POCType.Uncomparable)) {
                        return res;
                    }
                }
                // If some functional dependencies were detected and everything was comparable,
                // then it meas that they are compatbile
                return 1.0;
            }
        }
        // If I cannot compare them, they are not inconsistent. Using equality to force this concept (it should be "uncomparable but not inconsistent").
        types.add(PartialOrderComparison.PERFECT_EQUAL.t);
        return 1.0;
    }

    @Override
    public String getName() {
        return "FunctionalDependency";
    }

    @Override
    public boolean doesPolicyNotRequireExtendedComparison() {
        return false;
    }
}
