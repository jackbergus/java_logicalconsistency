package org.ufl.hypogator.jackb.fuzzymatching;

import cz.adamh.utils.NativeUtils;
import javafx.util.Pair;
import org.ufl.hypogator.jackb.scraper.adt.DiGraphEquivalenceClass;
import org.ufl.hypogator.jackb.m9.configuration.Concept5ClientConfigurations;
import org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNet5Dump;
import org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNet5Postgres;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * This indexer allows to perform the fuzzy matching in a more efficient way than comparing all the possible string
 * within the collection. In particular, this index allow to avoid the comparison between such strings that would
 * bring to a zero-valued comparison
 *
 */
public class TwoGramIndexerJNI implements AutoCloseable {

    private static final ConceptNet5Dump dump = ConceptNet5Dump.getInstance();

    static boolean correctlyInitialized = false;
    static {
        try {
            NativeUtils.loadLibraryFromJar("/libfuzzymatching.so");
            correctlyInitialized = true;
        } catch (Exception e) {
            e.printStackTrace();
            correctlyInitialized = false;
        }
    }

    private final String dir;
    private final Map<String, TwoGramIndexerForDimension> map;

    private TwoGramIndexerJNI() {
        this.dir = Concept5ClientConfigurations.instantiate().getHierarchiesFolder();
        if (correctlyInitialized) openDirectory(this.dir);
        map = new HashMap<>();
    }

    private static TwoGramIndexerJNI self = null;
    public static TwoGramIndexerJNI getInstance() {
        if (self == null)
            self = new TwoGramIndexerJNI();
        return self;
    }

    /**
     * Associates the basic path folder where all the binaries are serialized. At the jni level, sets the provided
     * directory as the main one.
     *
     * @param bd        Basic directory
     */
    native synchronized void openDirectory(String bd);

    native public synchronized void openDimensioN(String dim);
    /**
     * Performs the fuzzy match over the given vocabulary
     * @param dimension     Allows to select the vocabulary associated to the dimension
     * @param threshold     Threshold
     * @param topk          TopK element to be returned
     * @param term          Term to be matched
     */
    native public void fuzzyMatch(String dimension, double threshold, int topk, String term);
    native public boolean hasCurrent(String dimension);
    native public double getCurrentKey(String dimension);
    native public long getCurrentValue(String dimension);
    native public boolean next(String dimension);
    native public long[] containsExactTerm(String dimension, String term);
    native public void closeDimension(String dimension);
    native public synchronized void closeDirectory();

    /**
     *
     * @param dim       Dimension to be opened
     * @return
     */
    public TwoGramIndexerForDimension openDimension(String dim) {
        TwoGramIndexerForDimension elem = map.get(dim);
        if (elem == null) {
            elem = new TwoGramIndexerForDimension(dim, this);
            map.put(dim, elem);
        }
        return elem;
    }

    @Override
    public void close() {
        map.forEach((x, y) -> closeDimension(x));
        closeDirectory();
    }

    @Override
    protected void finalize() {
        close();
    }

    public class TwoGramIndexerForDimension implements Iterator<Pair<Double, ConceptNet5Postgres.RecordResultForSingleNode>> {
        private String dimension;
        DiGraphEquivalenceClass hierarchyGraph;
        private File hierarchyFile;
        private TwoGramIndexerJNI parent;
        private boolean current;
        private boolean isFirst;
        HashSet<Long> visitedLong;
        private Pair<Double, ConceptNet5Postgres.RecordResultForSingleNode> cursor;

        TwoGramIndexerForDimension(String dimension, TwoGramIndexerJNI parent) {
            this.dimension = dimension;
            this.parent = parent;
            if (correctlyInitialized) parent.openDimensioN(dimension);
            current = false;
            isFirst = true;
            hierarchyGraph = new DiGraphEquivalenceClass();
            hierarchyFile = new File(dir, dimension+"_map.json");
            try {
                hierarchyGraph.loadFromFile2(hierarchyFile, null, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            visitedLong = new HashSet<>();
            cursor = null;
        }

        public void fuzzyMatch(double threshold, int topk, String term) {
            if (correctlyInitialized) {
                visitedLong.clear();
                parent.fuzzyMatch(dimension, threshold, topk, term);
                current = true;
            }
        }

        private boolean hasInternalNext() {
            return correctlyInitialized && (current = current && parent.hasCurrent(dimension));
        }

        public boolean containsExactTerm(String term) {
            long ids[] = null;
            if (correctlyInitialized)
                ids = parent.containsExactTerm(dimension, term);
            return ids != null && ids.length > 0;
        }

        public Set<ConceptNet5Postgres.RecordResultForSingleNode> containsExactTerm2(String term) {
            long[] ids = null;
            if (correctlyInitialized)
                ids = parent.containsExactTerm(dimension, term);
            if (ids == null || ids.length == 0)
                return Collections.emptySet();
            HashSet<ConceptNet5Postgres.RecordResultForSingleNode> me = new HashSet<>();
            for (int i = 0, idsLength = ids.length; i < idsLength; i++) {
                long x = ids[i];
                me.add(resolveId(x));
            }
            return me;
        }

        @Override
        public boolean hasNext() {
            return (hasInternalNext() && ((cursor = next()) != null));
        }

        @Override
        public Pair<Double, ConceptNet5Postgres.RecordResultForSingleNode> next() {
            Long x;
            Double d;

            if (!correctlyInitialized)
                return null;

            if (cursor == null) {
                if (!hasInternalNext()) {
                    cursor = null;
                    return cursor;
                }

                do {
                    d = getCurrentKey(dimension);
                    x = getCurrentValue(dimension);
                    parent.next(dimension);
                } while ((!visitedLong.add(x)) && hasInternalNext());

                cursor = new Pair<>(d, resolveId(x));
            }
            Pair<Double, ConceptNet5Postgres.RecordResultForSingleNode> value = cursor;
            cursor = null;
            return value;
        }

        @Override
        protected void finalize() {
            if (correctlyInitialized)
                parent.closeDimension(dimension);
        }

        /**
         * This function converts the long-id (offset) into a vertex representation
         * @param id    Long to be converted
         * @return      Internal memory-costly representation
         */
        private ConceptNet5Postgres.RecordResultForSingleNode resolveId(long id) {
            String stringId = dump.offsetToNodeId(id);
            EdgeVertex v = (EdgeVertex) hierarchyGraph.resolveId(stringId);
            if (v == null) {
                v = dump.queryNode(false, stringId);
            }
            return v.asRecordResultForSingleNode();
        }

        public DiGraphEquivalenceClass getGraph() {
            return hierarchyGraph;
        }

        public File getHierarchyFile() {
            return hierarchyFile;
        }

        public FuzzyMatcher<ConceptNet5Postgres.RecordResultForSingleNode> getEnrichedVocabulary() {
            return new JNIFuzzyMatcher(this);
        }

        public class JNIFuzzyMatcher implements FuzzyMatcher<ConceptNet5Postgres.RecordResultForSingleNode> {
            private final TwoGramIndexerForDimension parent;

            public JNIFuzzyMatcher(TwoGramIndexerForDimension parent) {
                this.parent = parent;
            }

            @Override
            public Map<Double, Collection<ConceptNet5Postgres.RecordResultForSingleNode>> fuzzyMatch(Double threshold, Integer topK, Similarity sim, String objectStrings) {
                TreeMap<Double, Collection<ConceptNet5Postgres.RecordResultForSingleNode>> map = new TreeMap<>();
                if (correctlyInitialized) {
                    this.parent.fuzzyMatch(threshold, topK, objectStrings);
                    while (this.parent.hasNext()) {
                        Pair<Double, ConceptNet5Postgres.RecordResultForSingleNode> cp = this.parent.next();
                        if (!map.containsKey(cp.getKey())) map.put(cp.getKey(), new ArrayList<>());
                        map.get(cp.getKey()).add(cp.getValue());
                    }
                }
                return map;
            }

            @Override
            public Collection<ConceptNet5Postgres.RecordResultForSingleNode> containsExactTerm2(String term) {
                return correctlyInitialized ? parent.containsExactTerm2(term) : Collections.emptyList();
            }

            @Override
            public boolean containsExactTerm(String term) {
                return correctlyInitialized ? parent.containsExactTerm(term) : false;
            }
        }
    }

    /*public static void main(String args[]) throws IOException {
        TwoGramIndexerJNI server = TwoGramIndexerJNI.getInstance();
        TwoGramIndexerForDimension weapon = server.openDimension("Weapon");
        //weapon.containsExactTerm("Su-25").forEach(System.out::println);

        weapon.fuzzyMatch(0.6, -3, "surfac to ai missil");
        while (weapon.hasNext()) {
            System.out.println(weapon.next());
        }

        server.close();
    }*/

}
