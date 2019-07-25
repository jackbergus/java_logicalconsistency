
package org.ufl.hypogator.jackb.fuzzymatching.cow;

import com.google.common.collect.HashMultimap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

/**
 * This HashMultimap class allows to extend only the elements loaded from the data, while preserving the one serialized externally without the need of copying those.
 * Waiver: this requires to allocate an extra set object.
 *
 * @param <S>
 * @param <K>
 */
public class HashMultimapWithHMSeed<S, K> {

    public HashMultimap<S, K> seed;
    public HashMultimap<S, K> extension;
    boolean isExternalSeedSet = false;

    public long getNoSeedSize() {
        return extension == null ? 0 : extension.size();
    }

    public HashMultimapWithHMSeed() {
        isExternalSeedSet = false;
        extension = HashMultimap.create();
    }

    public HashMultimapWithHMSeed(HashMultimap<S, K> gramToObjects) {
        this.isExternalSeedSet = true;
        this.seed = gramToObjects;
        extension = HashMultimap.create();
    }

    public void setSeed(HashMultimapWithHMSeed<S, K> gramToObjects) {
        this.isExternalSeedSet = true;
        this.seed = gramToObjects.seed;
        extension = HashMultimap.create();
    }

    public HashMultimapWithHMSeed<S, K> copyWithSameSeed() {
        return new HashMultimapWithHMSeed<>(this.seed);
    }

    public void put(S string, K object) {
        if (isExternalSeedSet || seed == null)
            extension.put(string, object);
        else
            seed.put(string, object);
    }

    public void putAll(S string, Iterable<K> iterable) {
        if (isExternalSeedSet || seed == null)
            extension.putAll(string, iterable);
        else
            seed.putAll(string, iterable);
    }

    public Set<K> get(S key) {
        Set<K> empty = new HashSet<>();
        if (isExternalSeedSet || seed == null) {
            empty.addAll(extension.get(key));
        }
        if (seed != null)
            empty.addAll(seed.get(key));
        return empty;
    }

    public Set<S> keySet() {
        HashSet<S> set = new HashSet<>();
        if (seed != null)
            set.addAll(seed.keySet());
        if (extension != null)
            set.addAll(extension.keySet());
        return set;
    }

    public Set<S> getExtendedKeySet() {
        return extension == null ? Collections.emptySet() : extension.keySet();
    }

    public boolean containsKey(S s) {
        return (seed != null && seed.containsKey(s)) || (extension != null && extension.containsKey(s));
    }
}
