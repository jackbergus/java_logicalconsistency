package org.ufl.hypogator.jackb.m9.inconsistencycomparisons;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.HashMultimap;
import org.jooq.Record;
import org.postgresql.util.PGobject;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;
import org.ufl.hypogator.jackb.m9.LoadFact;
import org.ufl.hypogator.jackb.m9.SQLTuples;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class shall be used when no functional dependencies are used within the comparison task. Therefore,
 * I shall compare only the tuples having always the same arguments together, and all the subtuples in order
 * to check whether the information contained in one of the tuples is contained also for the other.
 */
public class InconsistencyStructuralComparisons implements Consumer<Record> {

    @Override
    public void accept(Record r) {
            if (r == null) return;
            ObjectReader reader = new ObjectMapper().readerFor(new TypeReference<SQLTuples>() {
            });
            String typeElement = r.get(0).toString();
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(typeElement + ".out.txt");
                PrintWriter printWriter = new PrintWriter(fileWriter);

                ArrayList<AgileRecord> records;

                HashMultimap<List<String>, AgileRecord> hmm = HashMultimap.create();
                {
                    // This hashmap will map each tuple to its equivalent representation
                    HashMultimap<AgileRecord, String> tupleToIds = HashMultimap.create();

                    for (Object serializedTuple : (Object[]) r.get(1)) {
                        // List of tuples
                        String stringSerializedTuple = ((PGobject) serializedTuple).getValue();
                        try {
                            // Deserializing the tuples.
                            SQLTuples tuple = reader.readValue(stringSerializedTuple);
                            tupleToIds.put(tuple.asAgileRecord(typeElement), tuple.tupleId);
                        } catch (IOException e) {
                        }
                    }

                    // Creating the equivalence class
                    tupleToIds.forEach((tuple, id) -> {
                        tuple.mentionsId.add(id);
                    });

                    // For each entry having the same type, I associate the schema to all the tuples sharing it
                    Iterator<Map.Entry<AgileRecord, String>> it = tupleToIds.entries().iterator();
                    while (it.hasNext()) {
                        Map.Entry<AgileRecord, String> current = it.next();
                        hmm.put(current.getKey().schema, current.getKey());
                        it.remove();
                    }
                }
                System.gc();

                // Effectively comparing the tuples within the same schema
                hmm.asMap().forEach((s, lsR) -> {
                    List<AgileRecord> arl = new ArrayList<>(lsR);
                    memoryWriteInconsistencies(printWriter, typeElement, s, arl);
                });

                // Getting the inconsistencies between all the elements with subset schemas, but having not the same schema
                for (List<String> schemas1 : hmm.keySet()) {
                    for (List<String> schemas2 : hmm.keySet()) {
                        if (schemas1.containsAll(schemas2) && (!schemas2.containsAll(schemas1))) { // the first is a superset of the second, and they should not be equivalent sets
                            ArrayList<AgileRecord> lsL = new ArrayList<>(hmm.get(schemas1));
                            ArrayList<AgileRecord> lsR = new ArrayList<>(hmm.get(schemas2));
                            long count = 0;

                            LoadFact.LOGGER.out("\t ~~ " + schemas1 + " ^^ " + schemas2 + " [from " + typeElement + "]");
                            int N = lsL.size();
                            int M = lsR.size();
                            for (int i = 0; i < N; i++) {
                                AgileRecord ari = lsL.get(i);
                                if (!schemas2.containsAll(schemas1)) {
                                    ari = ari.projectWith(schemas2);
                                }
                                for (int j = 0; j < M; j++) {
                                    AgileRecord arj = lsR.get(j);

                                    // If the two records are the same after the projection, then skip
                                    if (ari.equals(arj)) continue;

                                    PartialOrderComparison cmp = LoadFact.comparator.compare(ari, arj);
                                    if (cmp.t.equals(POCType.Uncomparable)) {
                                        for (String idLeft : ari.mentionsId) {
                                            for (String idRight : arj.mentionsId) {
                                                printWriter.printf("[\"%s\", \"%s\"], ", idLeft, idRight);
                                                printWriter.flush();
                                                count++;
                                            }
                                        }
                                    }
                                }
                            }
                            System.out.println("#incons added. = " + count);
                        }
                    }
                }
                printWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    public static void memoryWriteInconsistencies(PrintWriter inconsistentPair, String schema, List<String> s, List<AgileRecord> arl) {
        LoadFact.LOGGER.out("\t"+s.toString()+" [equivalence of "+schema+"]");
        int N = arl.size();
        long count = 0;
        for (int i = 0; i < N; i++) {
            AgileRecord ari = arl.get(i);
            for (int j=0; j<i; j++) {
                AgileRecord arj = arl.get(j);
                PartialOrderComparison cmp = LoadFact.comparator.compare(ari, arj);
                if (cmp.t.equals(POCType.Uncomparable)) {
                    for (String idLeft : ari.mentionsId) {
                        for (String idRight : arj.mentionsId) {
                            inconsistentPair.printf("[\"%s\", \"%s\"], ", idLeft, idRight);
                            inconsistentPair.flush();
                            count++;
                        }
                    }
                }
            }
        }
        System.out.println("#incons added. = "+ count);
    }

}
