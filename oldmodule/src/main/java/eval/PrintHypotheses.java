package eval;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import javafx.util.Pair;
import org.jooq.DSLContext;
import org.postgresql.util.PGobject;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.Database;
import org.ufl.aida.ta2.Tables;
//import org.ufl.aida.ta2.tables.daos.TuplesDao;
import org.ufl.aida.ta2.tables.pojos.Tuples;
import org.ufl.hypogator.jackb.inconsistency.AgileField;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;
import org.ufl.hypogator.jackb.m9.Hypotheses;
import org.ufl.hypogator.jackb.m9.SQLTuples;
import org.ufl.hypogator.jackb.m9.StaticDatabaseClass;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class PrintHypotheses extends StaticDatabaseClass {

    public static void main(String[] args) throws FileNotFoundException {
        loadProperties();
        Gson jsonSerializer = new Gson();
        StaticDatabaseClass staticDatabaseClass;
        //TuplesDao dao;
        ObjectReader reader;

        Database opt = Database.openOrCreate(engine, "p103", username, password).get();
        //dao = new TuplesDao(opt.jooq().configuration());
        reader = new ObjectMapper().readerFor(new TypeReference<AgileField>() {});
        for (File f  : new File("/home/giacomo/Scrivania/desktop_trash/master/data/v6/visualization_output/02-28-2019").listFiles()) {
            String hypoFile = f.getName().replace("graph_output_P103_", "");
            hypoFile = hypoFile.split("_")[0] + "_" + hypoFile.split("_")[1];
            Reader content = new FileReader(f);
            System.out.println(f);

            Hypotheses cls = jsonSerializer.fromJson(content, Hypotheses.class);
            int noEdges = 0;
            StringBuilder sb = new StringBuilder();
            sb.append("{");

            long start = System.currentTimeMillis();
            Hypotheses.Subgraph[] subgraphs = cls.subgraphs;
            for (int i = 0, subgraphsLength = subgraphs.length; i < subgraphsLength; i++) {
                System.gc();
                Hypotheses.Subgraph s = subgraphs[i];
                //System.out.println(i+" "+Arrays.toString(subgraphs[i].scorers));
                Hypotheses.Subgraph.Hypothesis_scorer[] hypothesis_scorers = s.hypothesis_scorers;

                for (int j = 0, hypothesis_scorersLength = hypothesis_scorers.length; j < 1; j++) {
                    //System.out.println("\t"+j);
                    Hypotheses.Subgraph.Hypothesis_scorer scorer = hypothesis_scorers[j];
                    Hypotheses.Subgraph.Hypothesis_scorer.Subgraph_plus_neighbor[] subgraph_plus_neighbors = scorer.subgraph_plus_neighbors;

                    HashSet<String> tp = new HashSet<>();
                    HashMultimap<String, SQLTuples> up = HashMultimap.create();
                    HashMultimap<String, Pair<String, String>> associatedFieldsToER = HashMultimap.create();
                    noEdges += subgraph_plus_neighbors.length;

                    //System.err.println("Getting the tuples...");
                    for (int k = 0, subgraph_plus_neighborsLength = subgraph_plus_neighbors.length; k < subgraph_plus_neighborsLength; k++) {
                        Hypotheses.Subgraph.Hypothesis_scorer.Subgraph_plus_neighbor neigh = subgraph_plus_neighbors[k];

                        if (neigh.node_text_1.trim().isEmpty()) {
                            associatedFieldsToER.put(neigh.node_id_1, new Pair<>(neigh.relation_text.split("-")[0], neigh.node_id_2));
                            switched(opt.jooq(), reader, tp, up, neigh.node_id_1, neigh.node_text_2.replace(" nan", ""));
                        } else {
                            associatedFieldsToER.put(neigh.node_id_2, new Pair<>(neigh.relation_text.split("-")[0], neigh.node_id_1));
                            switched(opt.jooq(), reader, tp, up, neigh.node_id_2, neigh.node_text_1.replace(" nan", ""));
                        }
                    }


                    //System.err.println("..................... done");

                    //System.out.println(associatedFieldsToER);

                    ArrayList<String> toprint = new ArrayList<>();

                    double typeInconsistencyScore = 0;
                    double countRecords = 0;

                    double tupleInconsistencyScore = 0;
                    double countPairs = 0;
                    for (Iterator<Map.Entry<String, Collection<SQLTuples>>> te_iterator = up.asMap().entrySet().iterator(); te_iterator.hasNext(); ) {
                        Map.Entry<String, Collection<SQLTuples>> te = te_iterator.next();
                        String type = te.getKey();

                        ArrayList<AgileRecord> records;


                        //System.err.println("Mapping tuple to equivalent representation...");
                        {
                            // This hashmap will map each tuple to its equivalent representation
                            HashMultimap<AgileRecord, String> tupleToIds = HashMultimap.create();

                            //boolean incoDetected = false;
                            for (SQLTuples tup : te.getValue()) {
                                AgileRecord rec = tup.asAgileRecord(type, associatedFieldsToER.get(tup.tupleId));
                                tupleToIds.put(rec, tup.tupleId);
                            }

                            te_iterator.remove();

                            // Creating the equivalence class
                            tupleToIds.forEach((tuple, id) -> tuple.mentionsId.add(id));

                            records = new ArrayList<>(tupleToIds.keySet().size());

                            // For each entry having the same type, I associate the schema to all the tuples sharing it
                            Iterator<Map.Entry<AgileRecord, Collection<String>>> it = tupleToIds.asMap().entrySet().iterator();
                            while (it.hasNext()) {
                                Map.Entry<AgileRecord, Collection<String>> current = it.next();
                                records.add(current.getKey());
                                it.remove();
                            }
                            tupleToIds.clear();
                        }

                        for (AgileRecord r : records) {
                            toprint.addAll(r.asArrayList());
                        }
                    }

                    System.out.println(toprint.stream().collect(Collectors.joining("\t")));
                }
            }
        }


    }

    private static void switched(DSLContext dao, ObjectReader reader, HashSet<String> tp, HashMultimap<String, SQLTuples> up, String neigh_node_id_1, String string) {
        if (!tp.contains(neigh_node_id_1)) {
            List<Tuples> tuple = dao.selectFrom(Tables.TUPLES).where(Tables.TUPLES.MID.eq(neigh_node_id_1)).fetchInto(Tuples.class);
            tp.add(neigh_node_id_1);
            for (Tuples t : tuple) {
                Object[] arrayAgg = t.getArrayAgg();
                SQLTuples tup = new SQLTuples();
                tup.tupleId = neigh_node_id_1;
                tup.tupleFields = new AgileField[arrayAgg.length];
                for (int i1 = 0, arrayAggLength = arrayAgg.length; i1 < arrayAggLength; i1++) {
                    Object arg = arrayAgg[i1];
                    try {
                        tup.tupleFields[i1] = reader.readValue(((PGobject) arg).getValue());
                        tup.tupleFields[i1].fieldString = tup.tupleFields[i1].fieldString.trim();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                up.put(t.getNisttype(), tup);
            }
        }
    }

}
