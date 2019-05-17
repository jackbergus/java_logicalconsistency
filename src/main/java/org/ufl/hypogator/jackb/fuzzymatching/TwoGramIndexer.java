package org.ufl.hypogator.jackb.fuzzymatching;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.stream.JsonWriter;
import javafx.util.Pair;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetDimensionDisambiguationOperations;
import org.ufl.hypogator.jackb.fuzzymatching.cow.HashMultimapWithHMSeed;
import org.ufl.hypogator.jackb.fuzzymatching.cow.MapWithHMSeed;
import org.ufl.hypogator.jackb.streamutils.data.ArraySupport;
import org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNet5Dump;
import org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNet5Postgres;
import org.ufl.hypogator.jackb.utils.adt.HashMultimapSerializer;
import org.ufl.hypogator.jackb.utils.adt.PollMap;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This indexer allows to perform the fuzzy matching in a more efficient way than comparing all the possible string
 * within the collection. In particular, this index allow to avoid the comparison between such strings that would
 * bring to a zero-valued comparison
 *
 * @param <K>
 */
@Deprecated
public class TwoGramIndexer<K> implements FuzzyMatcher<K> {

    /**
     * Associates each 2gram to the strings that contain it
     */
    public HashMultimapWithHMSeed<String, K> gramToObjects;
    public HashMultimapWithHMSeed<String, K> termToObjects;
    public HashMultimapWithHMSeed<K, String> objectToMultipleStrings;
    public static final char SEPARATOR = '\t';
    private File folder;

    @Override
    public Map<Double, Collection<K>> fuzzyMatch(Double threshold, Integer topK, Similarity sim, String objectStrings) {
        return this.fuzzyMatch(threshold, topK, sim, new String[]{objectStrings});
    }

    public Map<Double, Collection<K>> fuzzyMatch(Double threshold, Integer topK, Similarity sim, String... objectStrings) {
        if (objectStrings == null || objectStrings.length == 0)
            return Collections.emptyMap();
        PollMap<Double, K> toReturnTop = new PollMap<>(topK);
        for (int i = 0, objectStringsLength = objectStrings.length; i < objectStringsLength; i++) {
            String objectString = objectStrings[i];

            ArrayList<String> objectGrams = LowConfidenceRank.compareString_wordLetterPairs(objectString);
            HashSet<K> candidates = new HashSet<>();
            for (String gram : objectGrams) {
                candidates.addAll(gramToObjects.get(gram));
            }
            HashMap<String, Integer> m1 = LowConfidenceRank.compareStringHashMap(objectString).getKey();

            rankCollectionOf(objectString, candidates, m1, objectGrams.size(), threshold, toReturnTop, sim);
        }
        //TreeMap<Double, Collection<K>> toReturn2 = new TreeMap<>();
        //toReturnTop.asMap().forEach(toReturn2::put);
        return toReturnTop.getPoll();
    }

    public boolean containsExactTerm(String s) {
        return termToObjects.containsKey(s);
    }

    public Set<K> containsExactTerm2(String s) {
        return termToObjects.get(s);
    }

    ////////////////
    // modifiable //
    ///////////////

    /**
     * Populates the index from the string description
     * @param string    One of the string representations of the object
     * @param object    Object containing the string representation as "string"
     */
    public void addGramsToMap(String string, K object) {
        //String string = stringExtractor.apply(object);
        if (string == null)
            return;
        //if (string.equals("buk"))
        //    System.err.println("BUK(g)h CHECK");
        termToObjects.put(string, object);
        objectToMultipleStrings.put(object, string);
        objectToMultipleStrings.putAll(object, () -> new ArraySupport<>(stringExtractor.apply(object)));
        ArrayList<String> grams = LowConfidenceRank.compareString_wordLetterPairs(string);
        for (int i = 0, compareString_wordLetterPairsSize = grams.size(); i < compareString_wordLetterPairsSize; i++) {
            Pair<HashMap<String, Integer>, List<Integer>> dt = LowConfidenceRank.compareStringHashMap(string);
            Integer sum = 0;
            for (Integer j : dt.getValue()) {
                sum += j;
            }
            this.objectGramSize.put(string, sum);
            HashMap<String, Integer> cp = dt.getKey();
            twogramAndStringToMultiplicity.put(string, cp);
            for (String me : cp.keySet()) {
                gramToObjects.put(me, object);
            }
        }
    }

    public void addGramsToMap(K object, Predicate<String> isStopWord) {
        String[] apply = this.stringExtractor.apply(object);
        for (int i = 0, applyLength = apply.length; i < applyLength; i++) {
            String x = apply[i];
            if (!isStopWord.test(x)) addGramsToMap(x, object);
        }
    }

    public void addAll(List<K> objects, Predicate<String> isStopWord) {
        for (int i = 0, objectsSize = objects.size(); i < objectsSize; i++) {
            K object = objects.get(i);
            addGramsToMap(object, isStopWord);
        }
    }


    ///////////////////
    // serializables //
    ///////////////////


    private Set<K> ces = null;
    Set<K> commonExtensionSet(Function<K, Long> objectMap) {
        if (ces == null) {
            ces = new HashSet<>();
            for (K keys : objectToMultipleStrings.getExtendedKeySet()) {
                Long offset = objectMap.apply(keys);
                if (offset == null) {
                    ces.add(keys);
                }
            }
        }
        return ces;
    }

    void serializeMapStringToObject(Function<K, Long> objectMap, HashMultimapWithHMSeed<String, K> map, File csvFile, ConceptNet5Dump javaPersister) throws IOException {
        FileWriter fw = new FileWriter(csvFile);
        for (String key : map.keySet()) {
            for (K values : map.get(key)) {
                Long val = objectMap.apply(values);
                if (val == null || (!javaPersister.hasInId(val))) {
                    if (ces == null) commonExtensionSet(objectMap);
                    if (!ces.contains(values)) continue;
                    val = javaPersister.addToPersistance(values, val);
                    if (val == null) continue;
                }
                String valueString = val.toString();
                fw.write(key);
                fw.write(SEPARATOR);
                fw.write(valueString);
                fw.write('\n');
            }
        }
        fw.close();
    }

    void serializeMapObjectToString(Function<K, Long> objectMap, HashMultimapWithHMSeed<K, String> map, File csvFile, ConceptNet5Dump javaPersister) throws IOException {
        FileWriter fw = new FileWriter(csvFile);
        for (K key : map.keySet()) {
            Long lKey = objectMap.apply(key);
            if (lKey == null || (!javaPersister.hasInId(lKey))) {
                if (ces == null) commonExtensionSet(objectMap);
                if (!ces.contains(key)) continue;
                lKey = javaPersister.addToPersistance(key, lKey);
                if (lKey == null) continue;
            }
            String kStirng = lKey.toString();
            for (String values : map.get(key)) {
                fw.write(kStirng);
                fw.write(SEPARATOR);
                fw.write(values);
                fw.write('\n');
            }
        }
        fw.close();
    }

    /**
     * Associations between the string and the number of  grams containing it
     */
    public MapWithHMSeed<String, Integer> objectGramSize;

    public void serializeObjectGramSize(File f) throws IOException {
        FileWriter fw = new FileWriter(f);
        for (String keys : objectGramSize.keySet()) {
            fw.write(keys);
            fw.write(SEPARATOR);
            fw.write(objectGramSize.get(keys).toString());
            fw.write('\n');
        }
        fw.close();
    }

    /**
     * Representing the object as multiple possible strings
     */
    final Function<K, String[]> stringExtractor;

    /**
     * Finer gram association
     */
    public Map<String, Map<String, Integer>> twogramAndStringToMultiplicity;

    private void serializeTwogramAndStringToMultiplicity(File csvFile) throws IOException {
        FileWriter fw = new FileWriter(csvFile);
        for (Map.Entry<String, Map<String, Integer>> e : twogramAndStringToMultiplicity.entrySet()) {
            String string = e.getKey();
            for (Map.Entry<String, Integer> i : e.getValue().entrySet()) {
                fw.write(string);
                fw.write(SEPARATOR);
                fw.write(i.getKey());
                fw.write(SEPARATOR);
                fw.write(i.getValue().toString());
                fw.write('\n');
            }
        }
        fw.close();
    }

    public static void serializeToCSVFolder(Function<ConceptNet5Postgres.RecordResultForSingleNode, Long> objectMap, File folder, TwoGramIndexer<ConceptNet5Postgres.RecordResultForSingleNode> multiIndices, ConceptNet5Dump javaPersister) throws IOException {
        if (folder == null || multiIndices == null)
            return;
        if (!folder.exists())
            folder.mkdirs();

        File gramToObjects = new File(folder, "gramToObjects.csv");
        multiIndices.serializeMapStringToObject(objectMap, multiIndices.gramToObjects, gramToObjects, javaPersister);

        File termToObjects = new File(folder, "termToObjects.csv");
        multiIndices.serializeMapStringToObject(objectMap, multiIndices.termToObjects, termToObjects, javaPersister);

        File objectToMultipleStrings = new File(folder, "objectToMultipleStrings.csv");
        multiIndices.serializeMapObjectToString(objectMap, multiIndices.objectToMultipleStrings, objectToMultipleStrings, javaPersister);

        File objectGramSize = new File(folder, "objectGramSize.csv");
        multiIndices.serializeObjectGramSize(objectGramSize);

        File twogramAndStringToMultiplicity = new File(folder, "twogramAndStringToMultiplicity.csv");
        multiIndices.serializeTwogramAndStringToMultiplicity(twogramAndStringToMultiplicity);

        javaPersister.serialize();
    }

    public long getExtensionSize() {
        return gramToObjects.getNoSeedSize()+termToObjects.getNoSeedSize()+objectToMultipleStrings.getNoSeedSize()+twogramAndStringToMultiplicity.size()+objectGramSize.getExtensionSize();
    }

    public TwoGramIndexer<K> copy() {
        TwoGramIndexer<K> toret = new TwoGramIndexer<>(stringExtractor, null);
        toret.gramToObjects.setSeed(gramToObjects);
        toret.termToObjects.setSeed(termToObjects);
        toret.objectToMultipleStrings.setSeed(objectToMultipleStrings);
        toret.folder = folder;
        toret.objectGramSize.setSeed(objectGramSize);
        return toret;
    }


    /**
     *
     * @param objectToString     Function providing the
     */
    public TwoGramIndexer(Function<K, String[]> objectToString, File folder) {
        if (folder != null && folder.exists() && folder.isDirectory()) {
            System.err.println("[TwoGramIndexer::new] Loading "+folder.toString()+"... "); // Loading in parallel all the objects
            Thread obj1 = new Thread(() -> gramToObjects = new HashMultimapWithHMSeed<>(HashMultimapSerializer.unserialize(new File(folder, "gramToObjects.ser"))));
            obj1.start();
            Thread obj2 = new Thread(() -> termToObjects = new HashMultimapWithHMSeed<>(HashMultimapSerializer.unserialize(new File(folder, "termToObjects.ser"))));
            obj2.start();
            Thread obj3 = new Thread(() -> objectToMultipleStrings = new HashMultimapWithHMSeed<>(HashMultimapSerializer.unserialize(new File(folder, "objectToMultipleStrings.ser"))));
            obj3.start();
            Thread obj4 = new Thread(() -> objectGramSize = new MapWithHMSeed<>(HashMultimapSerializer.unserializeMap(new File(folder, "objectGramSize.ser"))));
            obj4.start();
            Thread obj5 = new Thread(() -> twogramAndStringToMultiplicity = HashMultimapSerializer.unserializeMap(new File(folder, "twogramAndStringToMultiplicity.ser")));
            obj5.start();

            try { // Barrier
                obj1.join();
                obj2.join();
                obj3.join();
                obj4.join();
                obj5.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.err.println("[TwoGramIndexer::new] S... done");
        } else {
            gramToObjects = new HashMultimapWithHMSeed<>();
            termToObjects = new HashMultimapWithHMSeed<>();
            objectGramSize = new MapWithHMSeed<>();
            objectToMultipleStrings = new HashMultimapWithHMSeed<>();
            twogramAndStringToMultiplicity = new HashMap<>();
        }
        stringExtractor = objectToString;
        this.folder = folder;
    }

    public static void serialize_to_json(MapWithHMSeed<String, Integer> file, JsonWriter writer) {
        file.seed.forEach((k, v) -> {
            try {
                writer.name(k);
                writer.value(v);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    public static void serialize_to_json(Map<String, Map<String, Integer>> map, JsonWriter writer) {
        map.forEach((k, m) -> {
            try {
                writer.name(k);
                writer.beginObject();
                m.forEach((k2, v)-> {
                    try {
                        writer.name(k2);
                        writer.value(v);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                });
                writer.endObject();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    public static void serialize_to_json2(HashMultimap<ConceptNet5Postgres.RecordResultForSingleNode, String> file, JsonWriter writer) {
        file.asMap().forEach((k, v) -> {
            try {
                writer.name(k.id);
                writer.beginObject();

                writer.name("strings");
                writer.beginArray();
                if (k.strings == null) {
                    writer.value(ConceptNetDimensionDisambiguationOperations.unrectify(k.id));
                } else {
                    for (String vl : k.strings) {
                        writer.value(vl);
                    }
                }
                writer.endArray();

                writer.name("values");
                writer.beginArray();
                for (String x : v) {
                    writer.value(x);
                }
                writer.endArray();
                writer.endObject();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    public static void serialize_to_json(HashMultimap<String, ConceptNet5Postgres.RecordResultForSingleNode> file, JsonWriter writer) {
        file.asMap().forEach((k, v) -> {
            try {
                writer.name(k);
                writer.beginArray();
                for (ConceptNet5Postgres.RecordResultForSingleNode x : v) {
                    writer.beginObject();
                    writer.name("id");
                    writer.value(x.id);
                    writer.name("strings");
                    writer.beginArray();
                    if (x.strings == null) {
                        writer.value(ConceptNetDimensionDisambiguationOperations.unrectify(x.id));
                    } else {
                        for (String vl : x.strings) {
                            writer.value(vl);
                        }
                    }
                    writer.endArray();
                    writer.endObject();
                }
                writer.endArray();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    public static void main(String[] args) throws IOException {
        TwoGramIndexer<ConceptNet5Postgres.RecordResultForSingleNode> l = new TwoGramIndexer<>(ConceptNet5Postgres.RecordResultForSingleNode::getStrings, new File("/media/giacomo/Biggus/project_dir/data/nodes"));

        //Gson gson = new Gson();
        /*{
            FileWriter fw = new FileWriter("/media/giacomo/Biggus/project_dir/data/nodes/gramToObjects.json");
            JsonWriter writer = new JsonWriter(fw);
            writer.beginObject();
            serialize_to_json(l.gramToObjects.seed, writer);
            writer.endObject();
            writer.close();
        }

        {
            FileWriter fw = new FileWriter("/media/giacomo/Biggus/project_dir/data/nodes/termToObjects.json");
            JsonWriter writer = new JsonWriter(fw);
            writer.beginObject();
            serialize_to_json(l.termToObjects.seed, writer);
            writer.endObject();
            writer.close();
        }

        {
            FileWriter fw = new FileWriter("/media/giacomo/Biggus/project_dir/data/nodes/objectGramSize.json");
            JsonWriter writer = new JsonWriter(fw);
            writer.beginObject();
            serialize_to_json(l.objectGramSize, writer);
            writer.endObject();
            writer.close();
        }*/

        {
            FileWriter fw = new FileWriter("/media/giacomo/Biggus/project_dir/data/nodes/objectToMultipleStrings.json");
            JsonWriter writer = new JsonWriter(fw);
            writer.beginObject();
            serialize_to_json2(l.objectToMultipleStrings.seed, writer);
            writer.endObject();
            writer.close();
        }

        {
            FileWriter fw = new FileWriter("/media/giacomo/Biggus/project_dir/data/nodes/twogramAndStringToMultiplicity.json");
            JsonWriter writer = new JsonWriter(fw);
            writer.beginObject();
            serialize_to_json(l.twogramAndStringToMultiplicity, writer);
            writer.endObject();
            writer.close();
        }
    }

    public TwoGramIndexer(Function<K, String[]> objectToString) {
        this(objectToString, null);
    }

    public void serialize() {
        // Write the folder only if it has been set
        if (folder != null) {
            // Starts the writing if it does not exist already
            if (!folder.exists()) {
                // Create the folder and start writing
                folder.mkdirs();
                if (folder.isDirectory()) {
                    // Serializing only if the files do not already exist
                    File f;

                    f = new File(folder, "gramToObjects.ser");
                    System.err.println("Serializing " + f.toString());
                    if (!f.exists()) HashMultimapSerializer.serialize(gramToObjects.seed, f);

                    f = new File(folder, "termToObjects.ser");
                    System.err.println("Serializing " + f.toString());
                    if (!f.exists()) HashMultimapSerializer.serialize(termToObjects.seed, f);

                    f = new File(folder, "objectToMultipleStrings.ser");
                    System.err.println("Serializing " + f.toString());
                    if (!f.exists()) HashMultimapSerializer.serialize(objectToMultipleStrings.seed, f);

                    f = new File(folder, "objectGramSize.ser");
                    System.err.println("Serializing " + f.toString());
                    if (!f.exists()) HashMultimapSerializer.serializeMap(objectGramSize.seed, f);

                    f = new File(folder, "twogramAndStringToMultiplicity.ser");
                    System.err.println("Serializing " + f.toString());
                    if (!f.exists()) HashMultimapSerializer.serializeMap(twogramAndStringToMultiplicity, f);
                } else {
                    System.err.println("Error serializing the map: " + folder + " exists and it is not a folder");
                }
            }
        }
    }

    //////////////
    // privates //
    //////////////

    private void memoizeAssociatedElement(String string, K object) {
        Pair<HashMap<String, Integer>, List<Integer>> dt = LowConfidenceRank.compareStringHashMap(string);
        Integer sum = 0;
        for (Integer j : dt.getValue()) {
            sum += j;
        }
        this.objectGramSize.put(string, sum);
        HashMap<String, Integer> cp = dt.getKey();
        twogramAndStringToMultiplicity.put(string, cp);
        for (String me : cp.keySet()) {
            gramToObjects.put(me, object);
        }
    }


    private Map<String, Integer> getTwoGramAndString(String associatedToElement) {
        Map<String, Integer> tret = this.twogramAndStringToMultiplicity.get(associatedToElement);
        // memoization
        if (tret == null) {
            tret = LowConfidenceRank.compareStringHashMap(associatedToElement).getKey();
            twogramAndStringToMultiplicity.put(associatedToElement, tret);
        }
        return tret;
    }

    /**
     * Retrieves the elements from the fuzzy match by
     * @param k         Candidates to be scored
     * @param m1        Multiplicity values for each gram associated to the element to be computed
     * @param sizeL     Number of the contained grams
     * @param sim       Optional similarity function
     * @return
     */
    private void rankCollectionOf(String term, HashSet<K> k, HashMap<String, Integer> m1, int sizeL, Double threshold, PollMap<Double, K> toReturnTop, Similarity sim) {
        Multimap<Double, K> toReturn = null;
        //PollMap<Double, K> toReturnTop = null;
        if (toReturnTop == null) {
            toReturn = HashMultimap.create();
        } /*else {
            toReturnTop = new PollMap<>(topK);
        }*/
        for (K element : k) {
            String[] apply = this.stringExtractor.apply(element);
            for (int i = 0, applyLength = apply.length; i < applyLength; i++) {
                String associatedToElement = apply[i];
                // obtaining all the grams associated to the element
                Map<String, Integer> m2 = getTwoGramAndString(associatedToElement);
                HashSet<String> keySet = new HashSet<>(m1.keySet());
                keySet.retainAll(m2.keySet());
                double score = 0;
                if (sim == null) {
                    double e = 0;
                    for (String key : keySet) {
                        e += Double.min(m1.get(key), m2.get(key));
                    }
                    // Coping with missing elements:
                    if (this.objectGramSize.get(associatedToElement) == null) {
                        memoizeAssociatedElement(associatedToElement, element);
                    }
                    score = (e * 2.0) / (this.objectGramSize.get(associatedToElement) + sizeL);
                } else {
                    score = sim.sim(term, associatedToElement);
                }
                //double score = (e*2.0)/(this.objectGramSize.get(element)+sizeL);
                if (threshold != null && score >= threshold) {
                    if (toReturnTop == null) {
                        toReturn.put(score, element);
                    } else {
                        toReturnTop.add(score, element);
                    }
                }
            }


        }
        // If the map is null, then I have to return a new one
        //TreeMap<Double, Collection<K>> toReturn2 = new TreeMap<>();
        //toReturn.asMap().forEach(toReturn2::put);
        // Otherwise, the above map is updated at each iteration.
    }


}
