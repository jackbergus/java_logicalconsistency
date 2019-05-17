package org.ufl.hypogator.jackb.inconsistency.fieldgrouping;

public class FieldGroupingPolicyFactory {

    private static final String FDEP = "FunctionalDependency";

    private static FieldGroupingPolicyFactory self = null;
    public static FieldGroupingPolicyFactory getInstance() {
        if (self == null)
            self = new FieldGroupingPolicyFactory();
        return self;
    }

    private static final NoFunctionalDependency nfd = new NoFunctionalDependency();
    public FieldGroupingPolicy getPolicy(String groupingPolicy) {
        if (groupingPolicy.startsWith(FDEP)) {
            return new FunctionalDependency(self.getPolicy(groupingPolicy.substring(FDEP.length())));
        } else {
            return nfd;
        }
    }

    public boolean doesPolicyNotRequireExtendedComparison(String groupingPolicy) {
        return getPolicy(groupingPolicy).doesPolicyNotRequireExtendedComparison();
    }
}
