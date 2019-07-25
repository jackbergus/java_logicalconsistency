/*
 * PollMap.java
 * This file is part of aida_scraper
 *
 * Copyright (C) 2018 giacomo
 *
 * aida_scraper is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * aida_scraper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aida_scraper. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.ufl.hypogator.jackb.utils.adt;

import com.google.common.collect.Ordering;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PollMap<K extends Comparable<K>, V> {
    private ConcurrentHashMap<K, Collection<V>> poll;
    private ConcurrentHashMap<V, K> valueScore;
    private TreeSet<K> keys;
    private boolean sanitize = false;
    private Integer k;

    public PollMap(Integer topK) {
        this.k = topK;
        poll = new ConcurrentHashMap<>();
        keys = new TreeSet<>();
        valueScore = new ConcurrentHashMap<>();
    }

    private void addKeyValue(K key, V value) {
        sanitize = false;
        K oldScore = valueScore.get(value);
        if (oldScore != null && oldScore.compareTo(key) < 0) {
            valueScore.put(value, key);
            poll.remove(oldScore, value);
        }
        if (!keys.contains(key)) {
            ArrayList<V> ls = new ArrayList<>();
            ls.add(value);
            poll.put(key, ls);
            valueScore.put(value, key);
        } else {
            poll.get(key).add(value);
            valueScore.put(value, key);
        }
    }

    private void addKeyValue(K key, Collection<V> value) {
        sanitize = false;
        for (V val : value) {
            K oldScore = valueScore.get(val);
            if (oldScore != null && oldScore.compareTo(key) < 0) {
                value.forEach(x -> valueScore.put(x, key));
                poll.remove(oldScore, value);
            }
        }
        if (!keys.contains(key)) {
            ArrayList<V> ls = new ArrayList<>(value);
            poll.put(key, ls);
            value.forEach(val -> valueScore.put(val, key));
        } else {
            poll.get(key).addAll(value);
            value.forEach(val -> valueScore.put(val, key));
        }
    }

    public void add(K key, V value) {
        if (keys.size() < k) {
            addKeyValue(key, value);
        } else {
            K cmp = keys.first();
            if (cmp.compareTo(key) < 0) {
                keys.pollFirst();
                poll.remove(cmp);
                addKeyValue(key, value);
            }
        }
    }

    public TreeMap<K, Collection<V>> getPoll() {
        if (!sanitize) {
            valueScore.clear();
            TreeMap<K, Collection<V>> sort = new TreeMap<>(Comparator.reverseOrder());
            sort.putAll(poll);
            poll.clear();
            for (Map.Entry<K, Collection<V>> kv : sort.entrySet()) {
                K key = kv.getKey();
                for (V value : kv.getValue()) {
                    // If it already contains it, the current element has a lower score. I'll prefer higher scores whenever possilble
                    if (!valueScore.containsKey(value)) {
                        valueScore.put(value, key);
                        if (!poll.containsKey(key))
                            poll.put(key, new ArrayList<>());
                        poll.get(key).add(value);
                    }
                }
            }
            sanitize = true;
        }
        TreeMap<K, Collection<V>> toretMap = new TreeMap<>(Ordering.natural().reverse());
        toretMap.putAll(poll);
        return toretMap;
    }

    public void add(K x, Set<V> strings) {
        if (k != null && keys.size() < k) {
            addKeyValue(x, strings);
        } else {
            K cmp = keys.first();
            if (cmp.compareTo(x) < 0) {
                keys.pollFirst();
                poll.remove(cmp);
                addKeyValue(x, strings);
            }
        }
    }
}
