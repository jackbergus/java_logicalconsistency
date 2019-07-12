package it.giacomobergami.m18.concrete;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import it.giacomobergami.m18.ConversionForExpansion;
import it.giacomobergami.m18.TTLOntology2;
import it.giacomobergami.m18.Utils;
import javafx.util.Pair;
import org.jooq.DSLContext;
import org.postgresql.util.PGobject;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.Database;
import org.ufl.aida.ta2.tables.Expansions;
import org.ufl.aida.ta2.tables.pojos.Tuples;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.inconsistency.AgileField;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;
import org.ufl.hypogator.jackb.m9.Hypotheses;
import org.ufl.hypogator.jackb.m9.LoadFact;
import org.ufl.hypogator.jackb.m9.SQLTuples;
import org.ufl.hypogator.jackb.m9.StaticDatabaseClass;
import org.ufl.hypogator.jackb.ontology.TtlOntology;
import org.ufl.hypogator.jackb.server.handlers.abstracts.SimplePostRequest;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static it.giacomobergami.m18.Utils.isContainedBy;
import static org.ufl.hypogator.jackb.m9.endm9.HypoAnalyse.longestRepeatedSubstring;

/**
 * TODO: this needs to be changed due to the specs on the inference.
 */
public class Baseline3 extends SimplePostRequest {

    public static Gson jsonSerializer = new Gson();
    static TTLOntology2 fringes = new TTLOntology2("data/SeedlingOntology2.ttl");
    static ObjectReader reader = new ObjectMapper().readerFor(new TypeReference<AgileField>() {});

    public static List<AgileRecord> fetchAgileRecordsByExpansionId(DSLContext jooq, String[] id) {
        return jooq.selectFrom(Expansions.EXPANSIONS)
                .where(isContainedBy(Expansions.EXPANSIONS.EID, id))
                .fetch(expansionsRecord -> ConversionForExpansion.reconstructRecordFromExpansions(jooq, expansionsRecord));
    }

    public Baseline3() {
    }

    /**
     * Returns a set of event and relationship id that must undergo the expansion phase
     *
     * @param subgraph_plus_neighbors       Current hypothesis
     * @return                              Set of Ids of events and relationships that
     */
    public static HashSet<String> getEventRelationshipIdFromHypothesis(Hypotheses.Subgraph.Hypothesis_scorer.Subgraph_plus_neighbor[] subgraph_plus_neighbors,
                                                                       HashMultimap<String, String> associatedFieldsToER) {
        HashSet<String> tp = new HashSet<>();
        HashMultimap<String, SQLTuples> up = HashMultimap.create();
        //noEdges += subgraph_plus_neighbors.length;

        //System.err.println("Getting the tuples...");
        for (int k = 0, subgraph_plus_neighborsLength = subgraph_plus_neighbors.length; k < subgraph_plus_neighborsLength; k++) {
            Hypotheses.Subgraph.Hypothesis_scorer.Subgraph_plus_neighbor neigh = subgraph_plus_neighbors[k];
            if (neigh.node_text_1.trim().isEmpty()) {
                tp.add(neigh.node_id_1);
                if (associatedFieldsToER != null) associatedFieldsToER.put(neigh.node_id_1, neigh.node_id_2);
            }
            else if (neigh.node_text_2.trim().isEmpty()) {
                tp.add(neigh.node_id_2);
                if (associatedFieldsToER != null) associatedFieldsToER.put(neigh.node_id_2, neigh.node_id_1);
            }
        }
        return tp;
    }

    /**
     * Given a collection of tuples, performs the final data cleaning before performing the inconsistency detection among all the hypotheses
     *
     * @param target        Where to store all the elements sorted by event/relationship type
     * @param tuple         All the tuples pertaining to one hypothesis after conversion
     */
    public static void uploadByTupleList(HashMultimap<String, SQLTuples> target, Collection<Tuples> tuple) {
        for (Tuples t : tuple) {
            Object[] arrayAgg = t.getArrayAgg();
            SQLTuples tup = new SQLTuples();
            tup.tupleId = t.getMid();//neigh.node_id_1;
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
            target.put(t.getNisttype(), tup);
        }
    }

    public List<Hypotheses.Subgraph.Hypothesis_scorer.Subgraph_plus_neighbor[]> getHyotheses(String content) {
        Hypotheses cls = jsonSerializer.fromJson(content, Hypotheses.class);
        Hypotheses.Subgraph[] subgraphs = cls.subgraphs;
        ArrayList<Hypotheses.Subgraph.Hypothesis_scorer.Subgraph_plus_neighbor[]> lis = new ArrayList<>();
        for (int i = 0, subgraphsLength = subgraphs.length; i < subgraphsLength; i++) {
            System.gc();
            Hypotheses.Subgraph s = subgraphs[i];
            //System.out.println(i+" "+Arrays.toString(subgraphs[i].scorers));
            Hypotheses.Subgraph.Hypothesis_scorer[] hypothesis_scorers = s.hypothesis_scorers;
            for (int j = 0, hypothesis_scorersLength = hypothesis_scorers.length; j < 1; j++) {
                System.out.println("\n\n\t Hypothesis Id: " + j);
                Hypotheses.Subgraph.Hypothesis_scorer scorer = hypothesis_scorers[j];
                Hypotheses.Subgraph.Hypothesis_scorer.Subgraph_plus_neighbor[] subgraph_plus_neighbors = scorer.subgraph_plus_neighbors;
                lis.add(subgraph_plus_neighbors);
            }
        }
        return lis;
    }

    @Override
    public String handleContent(String content, HashMultimap<String,String> arguments) {
        //TuplesDao dao;

        // Reload the properties from the configuration file, so that I can edit the arguments are re-load them at each new request.
        StaticDatabaseClass.loadProperties();


        Set<String> dbName_string = arguments.get("dbname");
        if (dbName_string == null || dbName_string.isEmpty()) {
            return "ERROR: no database was opened because the dbname argument was not set up";
        }
        String dbName = dbName_string.iterator().next();
        Database opt = Database.openOrCreate(StaticDatabaseClass.engine, dbName, StaticDatabaseClass.username, StaticDatabaseClass.password).get();
        //dao = new TuplesDao(opt.jooq().configuration());
        setContentType("application/json");
        Hypotheses cls = jsonSerializer.fromJson(content, Hypotheses.class);
        int noEdges = 0;
        StringBuilder sb = new StringBuilder();
        sb.append("{");

        long start = System.currentTimeMillis();
        List<Hypotheses.Subgraph.Hypothesis_scorer.Subgraph_plus_neighbor[]> hypotheses = getHyotheses(content);

        // Getting all the elements that need to be expanded.
        String[] finalIdsToExpand;
        {
            HashSet<String> idsToExpand = new HashSet<>();
            for (Hypotheses.Subgraph.Hypothesis_scorer.Subgraph_plus_neighbor[] hypo : hypotheses) {
                idsToExpand.addAll(getEventRelationshipIdFromHypothesis(hypo, null));
            }
            finalIdsToExpand = idsToExpand.toArray(new String[idsToExpand.size()]);
        }

        // Clearing all the results
        opt.jooq().dropTableIfExists(Expansions.EXPANSIONS);

        // Creating the table, before loading the results
        opt.rawSqlStatement(new File("sql/TA401_create_expansions_table.sql"));
        opt.rawSqlStatement(new File("sql/TA402_create_table_constraint.sql"));

        // Performing the expansions over these ids for all the hypotheses
        // 1. Loading the elements to the expansion module
        try {
            ConversionForExpansion.expand(dbName, finalIdsToExpand);
        } catch (Exception e) {
            System.err.println("ERROR: while converting into the expansion format");
            e.printStackTrace();
            sb.append("}");
            return sb.toString();
        }

        // 2. TODO: performing the actual expansion

        // 3. As before, loading the elements from the subgraphs
        int i = 0, M = hypotheses.size();
        for (Hypotheses.Subgraph.Hypothesis_scorer.Subgraph_plus_neighbor[] subgraph_plus_neighbors : hypotheses) {
            String[] strings;
            HashMultimap<String, String> associatedFieldsToER = HashMultimap.create();
            {
                Set<String> elements = getEventRelationshipIdFromHypothesis(subgraph_plus_neighbors, associatedFieldsToER);
                strings = elements.toArray(new String[elements.size()]);
            }
            List<AgileRecord> tuple = fetchAgileRecordsByExpansionId(opt.jooq(), strings);
            HashMultimap<String, AgileRecord> up = HashMultimap.create();
            tuple.forEach(x -> {
                Set<String> allowed = new HashSet<>(associatedFieldsToER.get(x.id));
                x.mentionsId.forEach(y -> allowed.addAll(associatedFieldsToER.get(y)));
                // Update the element with only the extracted arguments, and permit only the common substring
                x.updateWithSelectedArguments(allowed);
                up.put(x.nistType, x);
            });

            // TODO: for each AgileRecord, only extract the elements that are allowed by the extracted arguments. tuple.get(0).

            double typeInconsistencyScore = 0;
            double tupleInconsistencyScore = 0;
            for (Iterator<Map.Entry<String, Collection<AgileRecord>>> te_iterator = up.asMap().entrySet().iterator(); te_iterator.hasNext(); ) {
                Map.Entry<String, Collection<AgileRecord>> te = te_iterator.next();
                String type = te.getKey();

                ArrayList<AgileRecord> records;


                //System.err.println("Mapping tuple to equivalent representation...");
                {
                    // This hashmap will map each tuple to its equivalent representation
                    HashMultimap<AgileRecord, String> tupleToIds = HashMultimap.create();

                    //boolean incoDetected = false;
                    for (AgileRecord rec : te.getValue()) {
                        double x = rec.getDegreeTypeInconsistency(fringes);
                        if (x > 0) {
                            typeInconsistencyScore += x;
                            //incoDetected = true;
                        }
                        tupleToIds.put(rec, rec.id);
                        tupleToIds.putAll(rec, rec.mentionsId);
                    }

                    te_iterator.remove();

                    // Creating the equivalence class
                    tupleToIds.forEach((y, id) -> y.mentionsId.add(id));

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

            }

            sb.append("\""+i+"\" : "+(typeInconsistencyScore+(tupleInconsistencyScore/2.0)));
            if (i != M -1) sb.append(", ");
            i++;
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