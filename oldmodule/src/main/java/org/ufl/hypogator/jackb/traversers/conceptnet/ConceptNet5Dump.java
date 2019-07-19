package org.ufl.hypogator.jackb.traversers.conceptnet;

import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetDimensionDisambiguationOperations;
import org.ufl.hypogator.jackb.m9.configuration.Concept5ClientConfigurations;
import org.ufl.hypogator.jackb.scraper.ScraperSources;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.ConceptNet5Interface;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;
import org.ufl.hypogator.jackb.utils.FileChannelLinesSpliterator;
import org.ufl.hypogator.jackb.utils.adt.HashMultimapSerializer;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


public class ConceptNet5Dump implements ConceptNet5Interface {

    private final File folder;
    // TODO: initialize with external data
    private HashMultimap<String, String> clangToSeed;
    private Map<String, Long> idToOffset;
    private Map<Long, String> offsetToId;
    private Set<Long> hashSet;
    private Long getMax;
    private boolean initialized;

    private ConceptNet5Dump(File folder) {
        this.folder = folder;
        File a = new File(folder, "clangToSeed.ser");
        File b = new File(folder, "idToOffset.ser");
        if (folder != null && folder.exists() && folder.isDirectory() && a.exists() && b.exists()) {
            System.err.println("[ConceptNet5Dump::new] Loading serialized ConceptNet5 dictionary..."); // Loading in parallel all the objects
            Thread obj1 = new Thread(() -> clangToSeed = HashMultimapSerializer.unserialize(a));
            obj1.start();
            /*Thread obj2 = new Thread(() -> termToObjects = new HashMultimapWithHMSeed<>(HashMultimapSerializer.unserialize(new File(folder, "termToObjects.ser"))));
            obj2.start();
            Thread obj3 = new Thread(() -> objectToMultipleStrings = new HashMultimapWithHMSeed<>(HashMultimapSerializer.unserialize(new File(folder, "objectToMultipleStrings.ser"))));
            obj3.start();
            Thread obj4 = new Thread(() -> objectGramSize = new MapWithHMSeed<>(HashMultimapSerializer.unserializeMap(new File(folder, "objectGramSize.ser"))));
            obj4.start();*/
            Thread obj5 = new Thread(() -> idToOffset = HashMultimapSerializer.unserializeMap(b));
            obj5.start();

            try { // Barrier
                obj1.join();
                /*obj2.join();
                obj3.join();
                obj4.join();*/
                obj5.join();
                initialized = true;
            } catch (InterruptedException e) {
                e.printStackTrace();
                initialized = false;
            }
            System.err.println("[ConceptNet5Dump]... done");
        } else {
            clangToSeed = HashMultimap.create();
            idToOffset = new HashMap<>();
            initialize();
            serialize();
        }
        hashSet = new HashSet<>(idToOffset.values());
        getMax = hashSet.stream().max(Long::compare).orElse(0L);
        reserialize = false;
        offsetToId = new HashMap<>();
        if (initialized) {
            for (Map.Entry<String, Long> entry : idToOffset.entrySet()) {
                offsetToId.put(entry.getValue(), entry.getKey());
            }
        }
    }

    public static String removeSuffix(final String s, final String suffix) {
        if (s != null && suffix != null && s.endsWith(suffix)) {
            return s.substring(0, s.length() - suffix.length());
        }
        return s;
    }

    private void initialize() {
        reserialize = true;
        File pgDump = ConfigurationEntrypoint.getInstance().postgresDump;
        System.err.println("[ConceptNet5Dump::initialize] Concurrent loading data");
        File nodes = new File(pgDump, "pg_nodes.csv");
        File offsets = new File(pgDump, "ng_noes_name_to_offset.csv");
        if (nodes.exists() && offsets.exists()) {
            Thread t1 = new Thread(() -> {
                try {
                    FileChannelLinesSpliterator.lines(nodes.toPath(), Charset.defaultCharset()).forEach(x -> {
                        String l[] = x.split("\t");
                        String k = l[0];
                        k = removeSuffix(k, "/n");
                        if (k.endsWith("/a") || k.endsWith("/r")) return;
                        for (int i = 1, lLength = l.length; i < lLength; i++) {
                            String arg = l[i];
                            clangToSeed.put(k, arg);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            t1.start();
            Thread t2 = new Thread(() -> {
                // Reading the C++-generated file during the serialization of the nodes of Pos
                try {
                    FileChannelLinesSpliterator.lines(offsets.toPath(), Charset.defaultCharset()).forEach(x -> {
                        int idx = x.lastIndexOf(',');
                        try {
                            if (idx > 0) {
                                String id = x.substring(0, idx);
                                id = removeSuffix(id, "/n");
                                if (id.endsWith("/a") || id.endsWith("/r")) return;
                                idToOffset.put(x.substring(0, idx), Long.valueOf(x.substring(idx + 1)));
                            }
                        } catch (NumberFormatException e) {
                            System.err.println("Error opening the file: " + offsets.toPath());
                            //e.printStackTrace();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            t2.start();
            try { // Barrier
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            initialized = true;
        } else {
            initialized = false;
        }
    }

    public void serialize() {
        if (!reserialize) return;
        File f = new File(folder, "clangToSeed.ser");
        File f2 = new File(folder, "idToOffset.ser");
        if (initialized && f.exists() && f2.exists()) {
            // Serializing only if the files do not already exist
            System.err.println("[ConceptNet5Dump::serialize] Concurrent serializing");
            Thread uno = new Thread(() -> {
                {
                    HashMultimapSerializer.serialize(clangToSeed, f);
                }
            });
            Thread due = new Thread(() -> {
                {
                    HashMultimapSerializer.serializeMap(idToOffset, f2);
                }
            });
            uno.start();
            due.start();
            try {
                uno.join();
                due.join();
                reserialize = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static ConceptNet5Dump self = null;

    public static ConceptNet5Dump getInstance() {
        if (self == null)
            self = new ConceptNet5Dump(ConfigurationEntrypoint.getInstance().postgresDump);
        return self.initialized ? self : null;
    }

    public Long nodeIdToOffset(String node) {
        return idToOffset.get(node);
    }

    @Override
    public EdgeVertex queryNode(boolean like, String node) {
        EdgeVertex ev = new EdgeVertex();
        node = Concept5ClientConfigurations.rectifyTerm(node);
        ev.setGeneratingSource(ScraperSources.CONCEPTNET);
        ev.fromConceptNet(node, clangToSeed.get(node));

        // Just in case that ConceptNet had no associated raw edges, we're going to express it not as a node, but via its relatiionships later on. For the moment, just return a bogus element
        if (ev.id == null) {
            ev.id = node;
            ev.term = ConceptNetDimensionDisambiguationOperations.unrectify(ev.id);
            ev.label = ev.term;
            String[] split = ev.id.split("/");
            ev.language = split[2];
            ev.sense_label = split.length == 5 ? split[4] : null;
            ev.setGeneratingSource(ScraperSources.AIDA);
        }
        return ev;
    }

    /**
     * Performs the dumping of the data structures anew.
     *
     * @param args ignored parameter arguments
     */
    public static void main(String args[]) {
        File folder = ConfigurationEntrypoint.getInstance().postgresDump;
        File a = new File(folder, "clangToSeed.ser");
        if (a.exists()) a.delete();
        File b = new File(folder, "idToOffset.ser");
        if (b.exists()) b.delete();
        new ConceptNet5Dump(folder).serialize();
    }

    private boolean reserialize;

    public boolean hasInId(Long id) {
        return offsetToId.containsKey(id);
    }

    public Long addToPersistance(Object values, Long theLong) {
        if (values instanceof RecordResultForSingleNode) {
            RecordResultForSingleNode val = (RecordResultForSingleNode) values;
            String id = val.id;
            id = removeSuffix(id, "/n");
            if (id.endsWith("/a") || id.endsWith("/r")) return null;
            // I have to reserialize iff something has been added anew to the data structure
            reserialize = true;
            long newLong; // Using the native object. If you use the class, the reference that is Kept is always the one of the last object's assignment
            if (theLong == null) {
                newLong = getMax + 1;
                getMax++;
            } else {
                newLong = theLong;
            }
            idToOffset.put(id, newLong);
            String[] strings = val.strings;
            for (int i = 0, stringsLength = strings.length; i < stringsLength; i++) {
                String x = strings[i];
                clangToSeed.put(id, x);
            }
            return newLong; // forcing to cast the native into an object
        }
        return null;
    }

    public String offsetToNodeId(long element) {
        return offsetToId.get(element);
    }
}
