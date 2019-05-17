package org.ufl.hypogator.jackb.streamutils.data;

import java.util.Objects;

public class Atom {
    private String string;
    private Tuple tuple;
    private boolean isString;

    public Atom(String string) {
        this.string = string;
        this.tuple = null;
        this.isString = true;
    }

    public Atom(Tuple t) {
        this.tuple = t;
        this.string = null;
        this.isString = false;
    }

    public boolean isString() {
        return isString;
    }

    public boolean isTuple() {
        return !isString;
    }

    public Tuple asTuple() {
        if (isString) {
            Tuple t = new Tuple();
            t.put("string", new Value(string));
            return t;
        } else {
            return tuple;
        }
    }

    @Override
    public String toString() {
        return isString ? string : tuple.toString();
    }

    public boolean isEmptyValue() {
        return isString ? string.trim().isEmpty() : tuple.isEmptyValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Atom atom = (Atom) o;
        return isString == atom.isString &&
                Objects.equals(string, atom.string) &&
                Objects.equals(tuple, atom.tuple);
    }

    @Override
    public int hashCode() {

        return Objects.hash(string, tuple, isString);
    }
}
