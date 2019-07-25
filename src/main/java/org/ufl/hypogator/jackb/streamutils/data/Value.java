package org.ufl.hypogator.jackb.streamutils.data;

import com.google.common.collect.Lists;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.util.*;

/**
 * A value is either a string or a collection of values
 */
public class Value implements Iterable<Value> {
    private Atom atom;
    private ArrayList<Value> valueArray;

    public boolean isEmptyValue() {
        if (isSinglet()) {
            return atom.isEmptyValue();
        } else {
            return valueArray.isEmpty();
        }
    }

    public Value(int sizeExpected) {
        this.atom = null;
        this.valueArray = new ArrayList<>(sizeExpected);
    }

    public Value(String atom) {
        this(new Atom(atom));
    }

    public Value(Tuple t) {
        this(new Atom(t));
    }

    public Value(Atom atom) {
        this.atom = atom;
        this.valueArray = null;
    }

    public boolean isSinglet() {
        return valueArray == null;
    }

    public int getArraySize() {
        if (isSinglet()) {
            return 1;
        } else {
            return valueArray.size();
        }
    }

    public Value getSubValue(int i) {
        if (valueArray == null && i == 0) {
            return this;
        } else if (valueArray.size() > i) {
            return valueArray.get(i);
        } else return null;
    }

    public void addValue(Tuple element) {
        addValue(element.asValue());
    }

    public void addValue(String value) {
        addValue(new Value(value));
    }

    public void addValue(Value value) {
        if (isSinglet()) {
            valueArray = new ArrayList<>();
            valueArray.add(new Value(atom));
        }
        valueArray.add(value);
        atom = null;
    }

    public String getAtomAsString() {
        if (isSinglet()) {
            return atom.toString();
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append('[');
            Iterator<Value> it = valueArray.iterator();
            while (it.hasNext()) {
                sb.append(it.next().getAtomAsString());
                if (it.hasNext()) sb.append(',');
            }
            return sb.append(']').toString();
        }
    }

    public Atom getAtom() {
        if (isSinglet()) {
            return atom;
        } else if (valueArray.isEmpty()) {
            return null;
        } else
            return valueArray.get(0).getAtom();
    }

    @Override
    public String toString() {
        return getAtomAsString();
    }

    @Override
    public IteratorWithOperations<Value> iterator() {
        return new IteratorWithOperations<Value>() {
            int size = getArraySize();
            int counter = 0;

            @Override
            public boolean hasNext() {
                return counter < size;
            }

            @Override
            public Value next() {
                return getSubValue(counter++);
            }
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Value value = (Value) o;
        int as = getArraySize();
        if (as != value.getArraySize())
            return false;

        if (as == 1)
            return getAtom().equals(value.getAtom());
        else for (int i = 0; i < as; i++) {
            if (!getSubValue(i).equals(value.getSubValue(i)))
                return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return getArraySize() == 1 ? getSubValue(0).getAtomAsString().hashCode() : valueArray.hashCode();
    }

    public boolean isList() {
        return valueArray != null;
    }

    public void replaceInternallyWith(String argId, Value object) {
        if (isSinglet()) {
            if (atom.isString())
                return;
            else {
                atom.asTuple().replaceWith(argId, object);
            }
        } else {
            for (Value x : valueArray) {
                x.replaceInternallyWith(argId, object);
            }
        }
    }

    public void replaceInternallyWith(String argId, Tuple object) {
        if (isSinglet()) {
            if (atom.isString())
                return;
            else {
                atom.asTuple().replaceWith(argId, object);
            }
        } else {
            for (Value x : valueArray) {
                x.replaceInternallyWith(argId, object);
            }
        }
    }

    public Value extractFieldValues(String field) {
        Value v = new Value(0);
        if (isSinglet()) {
            if (atom.isString())
                return v;
            else {
                return atom.asTuple().extractFieldValues(field);
            }
        } else {
            for (Value x : valueArray) {
                v.addAllValues(x.extractFieldValues(field));
            }
            return v;
        }
    }

    public void addAllValues(Value value) {
        int n = value.getArraySize();
        for (int i = 0; i < n; i++) {
            addValue(value.getSubValue(i));
        }
    }

    public ArrayList<Value> asList() {
        if (isSinglet()) {
            return Lists.newArrayList(this);
        } else {
            return valueArray;
        }
    }

    public Collection<String> asStringList() {
        if (isSinglet()) {
            return Lists.newArrayList(getSubValue(0).getAtomAsString());
        } else {
            ArrayList<String> str = new ArrayList<>();
            int n = getArraySize();
            for (int i = 0; i < n; i++)
                str.add(getSubValue(i).getAtomAsString());
            return str;
        }
    }

    public void addAllValues(Collection<Value> hs) {
        for (Value v : hs) {
            addValue(v);
        }
    }

    public void replaceInternallyWith(String argId, Value eq, Value object) {
        if (isSinglet()) {
            if (atom.isString())
                return;
            else {
                atom.asTuple().replaceWith(argId, eq, object);
            }
        } else {
            for (Value x : valueArray) {
                x.replaceInternallyWith(argId, eq, object);
            }
        }
    }

    public void removeDuplicatedValues() {
        if (!isSinglet()) {
            HashSet<Value> values = new HashSet<>(valueArray);
            valueArray.clear();
            valueArray.addAll(values);
        }
    }

    public void sort(Comparator<Value> valueComparator) {
        valueArray.sort(valueComparator);
    }
}
