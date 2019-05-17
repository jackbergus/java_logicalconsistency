package org.ufl.hypogator.jackb.streamutils.data;

import org.ufl.hypogator.jackb.streamutils.utils.MapOperations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Tuple extends HashMap<String, Value> {

    public static Tuple fromMap(Map<String, String> map) {
        Tuple t = new Tuple();
        for (Map.Entry<String, String> e : map.entrySet()) {
            t.put(e.getKey(), new Value(e.getValue()));
        }
        return t;
    }

    @Override
    public Value get(Object key) {
        Value toret = super.get(key);
        if (toret == null)
            return new Value(0);
        else return toret;
    }

    public boolean isEmptyValue() {
        if (isEmpty()) return true;
        for (Map.Entry<String, Value> e : entrySet()) {
            if (!e.getValue().isEmptyValue()) return false;
        }
        return true;
    }

    @Override
    public Value remove(Object key) {
        Value toret = super.remove(key);
        if (toret == null)
            return new Value(0);
        else return toret;
    }

    public Atom asAtom() {
        return new Atom(this);
    }

    public Value asValue() {
        return new Value(asAtom());
    }

    public Tuple rename(String key, String newKey) {
        Value v = get(key);
        if (v != null) {
            remove(key);
            put(newKey, v);
        }
        return this;
    }

    public Tuple clean() {
        ArrayList<String> al = new ArrayList<>();
        for (Map.Entry<String, Value> cp : entrySet()) {
            if (cp.getValue().isEmptyValue()) al.add(cp.getKey());
        }
        for (String keys : al) {
            remove(keys);
        }
        return this;
    }

    public Tuple putStream(String type, String entity) {
        put(type, new Value(entity));
        return this;
    }

    public Tuple putStream(String type, Value entity) {
        put(type, entity);
        return this;
    }

    public Tuple removeStream(String type) {
        remove(type);
        return this;
    }

    public Tuple removeAllStream(String... types) {
        for (int i = 0; i < types.length; i++) {
            remove(types[i]);
        }
        return this;
    }

    public Tuple expand(String kb_id, boolean preserve) {
        if (containsKey(kb_id)) {
            Value v = get(kb_id);
            if (v.isSinglet() || v.getArraySize() == 1) {
                Atom a = v.getAtom();
                if (!a.isString()) {
                    Tuple t = a.asTuple();
                    if (!preserve) remove(kb_id);
                    return MapOperations.combine(this, t);
                }
            }
        }
        return this;
    }

    public Tuple expand(String kb_id) {
        return expand(kb_id, false);
    }

    public Tuple replaceWith(String argId, Value eq, Value object) {
        if (containsKey(argId) && get(argId).equals(eq)) {
            put(argId, object);
        } else {
            values().forEach(x -> x.replaceInternallyWith(argId, eq, object));
        }
        return this;
    }

    public Tuple replaceWith(String argId, Value object) {
        if (containsKey(argId)) {
            put(argId, object);
        } else {
            values().forEach(x -> x.replaceInternallyWith(argId, object));
        }
        return this;
    }

    public Tuple replaceWith(String argId, Tuple object) {
        if (containsKey(argId)) {
            put(argId, object.asValue());
        } else {
            values().forEach(x -> x.replaceInternallyWith(argId, object));
        }
        return this;
    }

    public Tuple copyAs(String src, String newCopy) {
        if (containsKey(src)) {
            put(newCopy, get(src));
        }
        return this;
    }

    public Value extractFieldValues(String field) {
        if (containsKey(field)) {
            Value n = new Value(1);
            n.addValue(get(field));
            return n;
        } else {
            Value n = new Value(0);
            for (Value x : values()) {
                n.addAllValues(x.extractFieldValues(field));
            }
            return n;
        }
    }

    public boolean isKeyEmpty(String key) {
        Value v = get(key);
        return v == null || v.isEmptyValue();
    }

    public Tuple updateValueFromKey(String type, Function<Value, Value> o) {
        Value v = get(type);
        if (!(v == null || v.isEmptyValue())) {
            put(type, o.apply(v));
        }
        return this;
    }

    public void removeDuplicatedValues() {
        for (Map.Entry<String, Value> map : this.entrySet()) {
            map.getValue().removeDuplicatedValues();
        }
    }
}
