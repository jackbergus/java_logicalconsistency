package org.ufl.hypogator.jackb.utils.adt;

import com.google.common.collect.HashMultimap;

import java.io.*;
import java.util.*;

public class HashMultimapSerializer<K, V> {

    public static <K, V> boolean serialize(HashMultimap<K, V> mmap, File filename) {
        // Serialization
        try {
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);
            HashMap<K, Collection<V>> copy = new HashMap<>();
            mmap.asMap().forEach((k, v) -> copy.put(k, new ArrayList<>(v)));
            out.writeObject(copy);
            out.close();
            file.close();
            return true;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static <K, V> boolean serializeMap(Map<K, V> mmap, File filename) {
        // Serialization
        try {
            FileOutputStream file = new FileOutputStream(filename);
            ObjectOutputStream out = new ObjectOutputStream(file);
            out.writeObject(mmap);
            out.close();
            file.close();
            return true;
        } catch(IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public static <K, V> HashMultimap<K, V> unserialize(File filename) {
        try
        {
            // Reading the object from a file
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            Map<K, Collection<V>> object1 = (Map<K, Collection<V>>) in.readObject();
            HashMultimap<K, V> mmap = HashMultimap.create();
            Iterator<Map.Entry<K, Collection<V>>> iter = object1.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry<K, Collection<V>> entry = iter.next();
                mmap.putAll(entry.getKey(), entry.getValue());
                iter.remove();
            }
            in.close();
            file.close();
            return mmap;
        } catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public static <K, V> Map<K, V> unserializeMap(File filename) {
        try
        {
            // Reading the object from a file
            FileInputStream file = new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(file);

            // Method for deserialization of object
            return (Map<K, V>) in.readObject();
        } catch(Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public static void main(String args[]) {
        HashMultimapSerializer.unserializeMap(new File("/home/giacomo/configuration_server/tutus/Movement.TransportArtifact/Vehicle_memoization.jbin_forStrings")).entrySet().forEach(System.out::println);
    }

}
