package org.ufl.hypogator.jackb.ontology.data;

import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.model.SQLType;

import java.util.ArrayList;

/**
 * Uniform representation for both events and relationships
 */
public class RawEventRelationship {
    ArrayList<String> pos;
    int n;
    private String longType;
    private String shortType;
    private HypoGatorKBTypes type;

    public RawEventRelationship() {
        pos = new ArrayList<>();
        n = 0;
    }

    public void put(int arg, String value) {
        if (arg < n) {
            pos.set(arg, value);
        } else {
            while (n < arg) {
                pos.add("");
                n++;
            }
            pos.add(value);
            n++;
        }
    }

    public void setNISTType(String kind, String longType) {
        this.longType = longType;
        if (kind.startsWith("event")) {
            this.type = HypoGatorKBTypes.Event;
        } else if (kind.startsWith("relation")) {
            this.type = HypoGatorKBTypes.Relationship;
        } else {
            this.type = null;
        }
    }

    public void setLDCType(String kind, String shortType) {
        this.shortType = shortType;
        if (kind.startsWith("event")) {
            this.type = HypoGatorKBTypes.Event;
        } else if (kind.startsWith("relation")) {
            this.type = HypoGatorKBTypes.Relationship;
        } else {
            this.type = null;
        }
    }

    @Override
    public String toString() {
        return "RawEventRelationship{" +
                "pos=" + pos +
                ", n=" + n +
                ", longType='" + longType + '\'' +
                ", shortType='" + shortType + '\'' +
                ", type=" + type +
                '}';
    }

    public String getShortType() {
        return shortType;
    }

    public String getSubType() {
        return longType;
        //Legacy:
        // String[] s = shortType.split(".");
        // return s[s.length - 1];
    }

    public String getLongType() {
        return longType;
    }

    public ArrayList<String> getArgumentLabels() {
        return pos;
    }
}
