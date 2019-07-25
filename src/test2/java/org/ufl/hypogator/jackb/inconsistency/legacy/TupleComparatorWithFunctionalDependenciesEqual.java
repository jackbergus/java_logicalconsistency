package org.ufl.hypogator.jackb.inconsistency.legacy;

import org.junit.Assert;
import org.junit.Test;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;
import org.ufl.hypogator.jackb.inconsistency.fieldgrouping.FieldGroupingPolicyFactory;
import org.ufl.hypogator.jackb.inconsistency.fieldgrouping.FunctionalDependency;
import org.ufl.hypogator.jackb.m9.fdep.FDepFile;

import java.io.File;

public class TupleComparatorWithFunctionalDependenciesEqual {

    @Test
    public void testFunctionalDependenciesDifferentType() {
        // If the functional dependency does not pertain to the same type, two elements shall not be compared
        FDepFile file = new FDepFile(new File("data/fdep_test.txt"));

        AgileRecord ar1 = new AgileRecord("t1");
        ar1.addField("a", "Type", "String1", true);
        ar1.addField("b", "Type", "String1", true);
        ar1.addField("c", "Type", "String1", true);

        AgileRecord ar2 = new AgileRecord("t2");
        ar2.addField("a", "Type", "String2", true);
        ar2.addField("b", "Type", "String2", true);
        ar2.addField("c", "Type", "String2", true);

        FunctionalDependency fdep = new FunctionalDependency(FieldGroupingPolicyFactory.getInstance().getPolicy("NoFunctonalDependency"), file.fDepMap);
        TupleComparator tc = TupleComparator.getTupleComparatorWithCustomSettings(fdep);

        Assert.assertEquals(tc.compare(ar1, ar2).t, POCType.Equal);
        Assert.assertEquals(tc.compare(ar2, ar1).t, POCType.Equal);
    }

    @Test
    public void testFunctionalDependenciesSameType() {
        // The functional dependency shall recognize two strings with different values belonging to the same type
        FDepFile file = new FDepFile(new File("data/fdep_test.txt"));

        AgileRecord ar1 = new AgileRecord("t1");
        ar1.addField("a", "Type", "String1", true);
        ar1.addField("b", "Type", "String1", true);
        ar1.addField("c", "Type", "String1", true);

        AgileRecord ar2 = new AgileRecord("t1");
        ar2.addField("a", "Type", "String1", true);
        ar2.addField("b", "Type", "String1", true);
        ar2.addField("c", "Type", "String2", true);

        FunctionalDependency fdep = new FunctionalDependency(FieldGroupingPolicyFactory.getInstance().getPolicy("NoFunctonalDependency"), file.fDepMap);
        TupleComparator tc = TupleComparator.getTupleComparatorWithCustomSettings(fdep);

        Assert.assertEquals(tc.compare(ar1, ar2).t, POCType.Uncomparable);
        Assert.assertEquals(tc.compare(ar2, ar1).t, POCType.Uncomparable);
    }

    @Test
    public void testFunctionalDependenciesSameTypeDifferentHead() {
        // The functional dependency shall detect as distinct two records having different keys
        FDepFile file = new FDepFile(new File("data/fdep_test.txt"));

        AgileRecord ar1 = new AgileRecord("t1");
        ar1.addField("a", "Type", "String1", true);
        ar1.addField("b", "Type", "String1", true);
        ar1.addField("c", "Type", "String1", true);

        AgileRecord ar2 = new AgileRecord("t1");
        ar2.addField("a", "Type", "String1", true);
        ar2.addField("b", "Type", "String2", true);
        ar2.addField("c", "Type", "String3", true);

        FunctionalDependency fdep = new FunctionalDependency(FieldGroupingPolicyFactory.getInstance().getPolicy("NoFunctonalDependency"), file.fDepMap);
        TupleComparator tc = TupleComparator.getTupleComparatorWithCustomSettings(fdep);

        Assert.assertEquals(tc.compare(ar1, ar2).t, POCType.Equal);
        Assert.assertEquals(tc.compare(ar2, ar1).t, POCType.Equal);

        AgileRecord ar3 = new AgileRecord("t1");
        ar3.addField("a", "Type", "String2", true);
        ar3.addField("b", "Type", "String1", true);
        ar3.addField("c", "Type", "String3", true);


        Assert.assertEquals(tc.compare(ar1, ar3).t, POCType.Equal);
        Assert.assertEquals(tc.compare(ar3, ar1).t, POCType.Equal);
    }

    @Test
    public void testFunctionalDependenciesDifferentSchemaSameValue() {
        // The functional dependency is approximated ignoring the missing values
        FDepFile file = new FDepFile(new File("data/fdep_test.txt"));

        AgileRecord ar1 = new AgileRecord("t1");
        ar1.addField("a", "Type", "String1", true);
        ar1.addField("b", "Type", "String1", true);
        ar1.addField("c", "Type", "String1", true);

        AgileRecord ar2 = new AgileRecord("t1");
        ar2.addField("a", "Type", "String1", true);
        ar2.addField("c", "Type", "String1", true);

        FunctionalDependency fdep = new FunctionalDependency(FieldGroupingPolicyFactory.getInstance().getPolicy("NoFunctonalDependency"), file.fDepMap);
        TupleComparator tc = TupleComparator.getTupleComparatorWithCustomSettings(fdep);

        Assert.assertEquals(tc.compare(ar1, ar2).t, POCType.Equal);
        Assert.assertEquals(tc.compare(ar2, ar1).t, POCType.Equal);
    }

    @Test
    public void testFunctionalDependenciesDifferentSchemaDiffValue() {
        // The functional dependency is approximated ignoring the missing values, and shall not detect inconsistencies if we have missing data
        FDepFile file = new FDepFile(new File("data/fdep_test.txt"));

        AgileRecord ar1 = new AgileRecord("t1");
        ar1.addField("a", "Type", "String1", true);
        ar1.addField("b", "Type", "String1", true);
        ar1.addField("c", "Type", "String2", true);

        AgileRecord ar2 = new AgileRecord("t1");
        ar2.addField("a", "Type", "String1", true);
        ar2.addField("c", "Type", "String1", true);

        FunctionalDependency fdep = new FunctionalDependency(FieldGroupingPolicyFactory.getInstance().getPolicy("NoFunctonalDependency"), file.fDepMap);
        TupleComparator tc = TupleComparator.getTupleComparatorWithCustomSettings(fdep);

        Assert.assertEquals(tc.compare(ar1, ar2).t, POCType.Equal);
        Assert.assertEquals(tc.compare(ar2, ar1).t, POCType.Equal);
    }

    @Test
    public void testFunctionalDependenciesExistsAllSemantics() {
        // The functional dependency shall check the hypotheses as a disjunction, and therefore shall check if all the arguments are inconsistent, then it is inconsistent
        FDepFile file = new FDepFile(new File("data/fdep_test.txt"));

        AgileRecord ar1 = new AgileRecord("t1");
        ar1.addField("a", "Type", "String1", true);
        ar1.addField("b", "Type", "String1", true);
        ar1.addField("c", "Type", "String1", true);

        AgileRecord ar2 = new AgileRecord("t1");
        ar2.addField("a", "Type", "String1", true);
        ar2.addField("b", "Type", "String1", true);
        ar2.addField("c", "Type", "String2", true);
        ar2.addField("c", "Type", "String3", true);

        FunctionalDependency fdep = new FunctionalDependency(FieldGroupingPolicyFactory.getInstance().getPolicy("NoFunctonalDependency"), file.fDepMap);
        TupleComparator tc = TupleComparator.getTupleComparatorWithCustomSettings(fdep);

        Assert.assertEquals(tc.compare(ar1, ar2).t, POCType.Uncomparable);
        Assert.assertEquals(tc.compare(ar2, ar1).t, POCType.Uncomparable);

        AgileRecord ar3 = new AgileRecord("t1");
        ar3.addField("a", "Type", "String1", true);
        ar3.addField("b", "Type", "String1", true);
        ar3.addField("c", "Type", "String1", true);
        ar3.addField("c", "Type", "String3", true);

        Assert.assertEquals(tc.compare(ar1, ar3).t, POCType.Equal);
        Assert.assertEquals(tc.compare(ar3, ar1).t, POCType.Equal);
    }

}