package org.ufl.hypogator.jackb.fuzzymatching.cow;

import java.util.*;

public class MapWithHMSeed<T, T1> {

    public Map<T, T1> seed;
    public HashMap<T, T1> extension;
    private boolean isExternalSeedSet = false;

    public long getExtensionSize() {
        return extension == null ? 0 : extension.size();
    }

    public MapWithHMSeed(Map<T, T1> unserializeMap) {
        this.seed = unserializeMap;
        this.extension = new HashMap<>();
        this.isExternalSeedSet = true;
    }

    public MapWithHMSeed() {
        this.seed = null;
        this.extension = new HashMap<>();
        this.isExternalSeedSet = false;
    }

    public void setSeed(MapWithHMSeed<T, T1> objectGramSize) {
        this.seed = objectGramSize.seed;
        this.extension = new HashMap<>();
        this.isExternalSeedSet = true;
    }

    public void put(T string, T1 object) {
        if (isExternalSeedSet || seed == null)
            extension.put(string, object);
        else
            seed.put(string, object);
    }

    public T1 get(T key) {
        T1 result = null;
        if (isExternalSeedSet || seed == null) {
            result = extension.get(key);
        }
        if (result == null && seed != null) {
            result = seed.get(key);
        }
        return result;
    }

    public boolean contains(T key) {
        return get(key) != null;
    }

    public Set<T> keySet() {
        HashSet<T> set = new HashSet<>();
        if (seed != null)
            set.addAll(seed.keySet());
        if (extension != null)
            set.addAll(extension.keySet());
        return set;
    }
}
