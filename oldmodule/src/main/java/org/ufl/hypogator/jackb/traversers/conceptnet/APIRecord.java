package org.ufl.hypogator.jackb.traversers.conceptnet;

import org.postgresql.util.PGobject;

public class APIRecord {
    private String uri;
    private Float edgeScore;
    private Object pgObject;
    private boolean isString;

    public APIRecord() { }

    public APIRecord(String uri, Float edgeScore, Object pgObject) {
        this.uri = uri;
        this.edgeScore = edgeScore;
        this.pgObject = pgObject;
        isString = false;
    }

    public APIRecord(String uri, String pgObject, Float edgeScore) {
        this.uri = uri;
        this.edgeScore = edgeScore;
        this.pgObject = pgObject;
        isString = false;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public Float getEdgeScore() {
        return edgeScore;
    }

    public void setEdgeScore(Float edgeScore) {
        this.edgeScore = edgeScore;
    }

    public String getPgObject() {
        return isString ? ((String) pgObject) : ((PGobject)pgObject).getValue();
    }

    public void setPgObject(Object pgObject) {
        this.pgObject = pgObject;
    }

    @Override
    public String toString() {
        return "APIRecord{" +
                "uri='" + uri + '\'' +
                ", edgeScore=" + edgeScore +
                ", pgObject=" + pgObject +
                '}';
    }
}
