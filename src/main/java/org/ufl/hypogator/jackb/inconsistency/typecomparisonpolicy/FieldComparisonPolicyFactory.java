package org.ufl.hypogator.jackb.inconsistency.typecomparisonpolicy;

import org.apache.jena.reasoner.rulesys.builtins.Equal;

public class FieldComparisonPolicyFactory  {


    private static final ScoreFieldRelyOnTA2Type relyType = new ScoreFieldRelyOnTA2Type();
    private static final BroadClassApproximation approx = new BroadClassApproximation();
    private static final MixtureModel mixture = new MixtureModel();
    private static final EqualityApproximation eqapprox = new EqualityApproximation();
    private FieldComparisonPolicyFactory() { }

    private static FieldComparisonPolicyFactory self;

    public static FieldComparisonPolicyFactory getInstance() {
        if (self == null)
             self = new FieldComparisonPolicyFactory();
        return self;
    }

    public FieldComparisonPolicy getPolicy(String str) {
        if (str.equals(approx.getName()))
            return approx;
        if (str.equals(mixture.getName()))
            return mixture;
        if (str.equals(eqapprox.getName()))
            return eqapprox;
        else
            return relyType;
    }

    public boolean doesFieldComparisonPolicyRequireDimensionMemoization(String str) {
        return !str.equals(eqapprox.getName());
    }

}
