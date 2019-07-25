package org.ufl.hypogator.jackb.comparators.partialOrders.policy;

public class DisambiguationPolicyFactory {
    private static final MaximumDisambiguationComparator mdc = new MaximumDisambiguationComparator();
    private static final AtLeastOneNonUncompatible aws = new AtLeastOneNonUncompatible();
    private DisambiguationPolicyFactory() { }

    private static DisambiguationPolicyFactory self;

    public static DisambiguationPolicyFactory getInstance() {
        if (self == null)
             self = new DisambiguationPolicyFactory();
        return self;
    }

    public DisambiguationPolicy getPolicy(String str) {
        if (str.equals(aws.getName()))
            return aws;
        else
            return mdc;
    }

}