package org.ufl.hypogator.jackb.server.handlers.concrete;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import it.giacomobergami.m18.Utils;
import javafx.util.Pair;
import org.postgresql.util.PGobject;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.Database;
import org.ufl.aida.ta2.tables.pojos.Tuples;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.inconsistency.AgileField;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;
import org.ufl.hypogator.jackb.m9.Hypotheses;
import org.ufl.hypogator.jackb.m9.LoadFact;
import org.ufl.hypogator.jackb.m9.SQLTuples;
import org.ufl.hypogator.jackb.m9.configuration.StaticDatabaseClass;
import org.ufl.hypogator.jackb.ontology.TtlOntology;
import org.ufl.hypogator.jackb.server.handlers.abstracts.SimplePostRequest;

import java.io.IOException;
import java.util.*;

import static org.ufl.hypogator.jackb.m9.endm9.HypoAnalyse.longestRepeatedSubstring;

/**
 * TODO: this needs to be changed due to the specs on the inference.
 */
public class Baseline2 extends SimplePostRequest {

    public static Gson jsonSerializer = new Gson();
    static TtlOntology fringes = new TtlOntology("data/SeedlingOntology.ttl");

    public Baseline2() {
    }

    @Override
    public String handleContent(String content, HashMultimap<String,String> arguments) {
        //TuplesDao dao;

        // Reload the properties from the configuration file, so that I can edit the arguments are re-load them at each new request.
        StaticDatabaseClass.loadProperties();


        ObjectReader reader;
        Set<String> dbName_string = arguments.get("dbname");
        if (dbName_string == null || dbName_string.isEmpty()) {
            return "ERROR: no database was opened because the dbname argument was not set up";
        }
        String dbName = dbName_string.iterator().next();
        Database opt = Database.openOrCreate(StaticDatabaseClass.engine, dbName, StaticDatabaseClass.username, StaticDatabaseClass.password).get();
        //dao = new TuplesDao(opt.jooq().configuration());
        reader = new ObjectMapper().readerFor(new TypeReference<AgileField>() {});
        setContentType("application/json");
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
                System.out.println("\n\n\t Hypothesis Id: "+j);
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
                        if (! tp.contains(neigh.node_id_1)) {
                            List<Tuples> tuple =
                                    Utils.fetchTuplesByMID(opt.jooq(), neigh.node_id_1);
                            tp.add(neigh.node_id_1);
                            for (Tuples t : tuple) {
                                Object[] arrayAgg = t.getArrayAgg();
                                SQLTuples tup = new SQLTuples();
                                tup.tupleId = neigh.node_id_1;
                                tup.tupleFields = new AgileField[arrayAgg.length];
                                for (int i1 = 0, arrayAggLength = arrayAgg.length; i1 < arrayAggLength; i1++) {
                                    Object arg = arrayAgg[i1];
                                    try {
                                        tup.tupleFields[i1] = reader.readValue(((PGobject) arg).getValue());
                                        tup.tupleFields[i1].fieldString = longestRepeatedSubstring(tup.tupleFields[i1].fieldString.trim());
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                System.out.println(tup);
                                up.put(t.getNisttype(), tup);
                            }
                        }
                    } else {
                        associatedFieldsToER.put(neigh.node_id_2, new Pair<>(neigh.relation_text.split("-")[0], neigh.node_id_1));
                    }
                }

                //System.err.println("..................... done");

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
                            double x = rec.getDegreeTypeInconsistency(fringes, tup.tupleId);
                            if (x > 0) {
                                typeInconsistencyScore += x;
                                //incoDetected = true;
                            }
                            countRecords++;
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

                    //

                    int N = records.size();
                    countPairs += N * N;
                    for (int i1 = 0; i1 < N; i1++) {
                        AgileRecord ari = records.get(i1);
                        for (int j1 = 0; j1 < i1; j1++) {
                            AgileRecord arj = records.get(j1);
                            PartialOrderComparison cmp = LoadFact.comparator.compare(ari, arj);
                            if (cmp.t.equals(POCType.Uncomparable)) {
                                System.out.println("INCO == " + ari + " vs. " + arj);
                                tupleInconsistencyScore += ari.mentionsId.size() * arj.mentionsId.size() * 2;
                                //incoDetected = false;
                            }
                        }
                    }
                    /*if (!incoDetected) {
                        System.out.println(i);
                        records.forEach(x -> System.out.println("\t\t\t" + x));
                    }*/
                    //System.out.println("#incons added. = "+ tupleInconsistencyScore);
                }
                sb.append("\""+i+"\" : "+(typeInconsistencyScore+(tupleInconsistencyScore/2.0)));
                if (i != subgraphsLength -1 )
                    sb.append(", ");

                //System.out.println("typeConsistencyScore = "+Math.exp(-typeInconsistencyScore)+" v  = "+(1.0 - typeInconsistencyScore/countRecords));
                //System.out.println("tupleConsistencyScore = "+Math.exp(-tupleInconsistencyScore/2.0)+" v  = "+(1.0 - tupleInconsistencyScore/countPairs));
            }
        }
        long end = System.currentTimeMillis();

        System.err.println("s = "+(end-start)/1000.0);
        System.err.println("# = "+noEdges);

        //setAnswerBody("{}");
        sb.append("}");
        System.out.println(sb.toString());

        // Closing the connection once and for all
        try {
            opt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}
