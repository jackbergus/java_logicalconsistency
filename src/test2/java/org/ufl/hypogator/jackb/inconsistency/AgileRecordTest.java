package org.ufl.hypogator.jackb.inconsistency;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Testing the correctness of the projection method
 */
public class AgileRecordTest {

    private static final String type = "TYPE";

    @Test
    public void projectWithEmpty1() {
        AgileRecord left = new AgileRecord(type);
        AgileRecord right = new AgileRecord(type);
        right.addField("label1", "type1", "string1", true);

        // Projecting the empty on element returns empty
        AgileRecord result1 = left.projectWith(right.schema);
        assertEquals(left, result1);

        // Projecting the element on empty returns empty
        AgileRecord result2 = right.projectWith(left.schema);
        assertEquals(left, result2);
    }

    @Test
    public void projectWithUnmatchingTypes() {
        AgileRecord empty = new AgileRecord(type);
        AgileRecord left = new AgileRecord(type);
        AgileRecord right = new AgileRecord(type);
        right.addField("label1", "type1", "string1", true);
        left.addField("label2", "type2", "string2", true);

        // Projecting the empty on element returns empty
        AgileRecord result1 = left.projectWith(right.schema);
        assertEquals(empty, result1);

        // Projecting the element on empty returns empty
        AgileRecord result2 = right.projectWith(left.schema);
        assertEquals(empty, result2);
    }

    @Test
    public void projectSubtupleType() {
        AgileRecord left = new AgileRecord(type);
        AgileRecord right = new AgileRecord(type);
        right.addField("label1", "type1", "string1R", true);
        right.addField("label2", "type2", "string2R", true);
        right.addField("label3", "type3", "string3R", true);
        left.addField("label2", "type2", "string2L", true);

        // Projecting the empty on element returns empty
        AgileRecord result1 = left.projectWith(right.schema);
        AgileRecord expected1 = new AgileRecord(type);
        expected1.addField("label2", "type2", "string2L", true);
        assertEquals(expected1, result1);

        // Projecting the element on empty returns empty
        AgileRecord result2 = right.projectWith(left.schema);
        AgileRecord expected2 = new AgileRecord(type);
        expected2.addField("label2", "type2", "string2R", true);
        assertEquals(expected2, result2);
    }

    public void projectIntersectionType() {
        AgileRecord left = new AgileRecord(type);
        AgileRecord right = new AgileRecord(type);
        right.addField("label1", "type1", "string1R", true);
        right.addField("label2", "type2", "string2R", true);
        right.addField("label3", "type3", "string3R", true);
        left.addField("label2", "type2", "string2L", true);
        left.addField("label3", "type3", "string3L", true);
        left.addField("label4", "type4", "string4L", true);
        left.addField("label5", "type5", "string5L", true);

        // Projecting the empty on element returns empty
        AgileRecord result1 = left.projectWith(right.schema);
        AgileRecord expected1 = new AgileRecord(type);
        expected1.addField("label2", "type2", "string2L", true);
        expected1.addField("label3", "type3", "string3L", true);
        assertEquals(expected1, result1);

        // Projecting the element on empty returns empty
        AgileRecord result2 = right.projectWith(left.schema);
        AgileRecord expected2 = new AgileRecord(type);
        expected2.addField("label2", "type2", "string2R", true);
        expected2.addField("label3", "type3", "string3R", true);
        assertEquals(expected2, result2);
    }
}