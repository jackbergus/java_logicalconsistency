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
import java.util.*;
import java.util.function.Consumer;

public class InconsistencyExtensiveComparison implements Consumer<Record> {

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

                records = new ArrayList<>(tupleToIds.keySet().size());

                // For each entry having the same type, I associate the schema to all the tuples sharing it
                Iterator<Map.Entry<AgileRecord, Collection<String>>> it = tupleToIds.asMap().entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry<AgileRecord, Collection<String>> current = it.next();
                    records.add(current.getKey());
                    it.remove();
                }
            }
            System.gc();

            memoryWriteInconsistencies(printWriter, typeElement, Collections.emptyList(), records);

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
                            inconsistentPair.printf("(\"%s\", \"%s\"), ", idLeft, idRight);
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
