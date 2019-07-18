package org.ufl.hypogator.jackb.m9.endm9;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import it.giacomobergami.m18.TTLOntology2;
import it.giacomobergami.m18.Utils;
import javafx.util.Pair;
import org.postgresql.util.PGobject;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.Database;
import org.ufl.aida.ta2.tables.pojos.Tuples;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.inconsistency.AgileField;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;
import org.ufl.hypogator.jackb.inconsistency.legacy.TupleComparator;
import org.ufl.hypogator.jackb.m9.Hypotheses;
import org.ufl.hypogator.jackb.m18.LoadFact;
import org.ufl.hypogator.jackb.m9.SQLTuples;
import org.ufl.hypogator.jackb.m9.configuration.StaticDatabaseClass;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;

public class HypoAnalyse extends StaticDatabaseClass {

    public static Gson jsonSerializer = new Gson();
    static TTLOntology2 fringes = TTLOntology2.getInstance();
    public static final TupleComparator comparator = TupleComparator.getDefaultTupleComparator();

    // Returns the longest repeating non-overlapping
// substring in str

    public static String listRepeatedSubstring(String[] str) {
        int n = str.length;
        int LCSRe[][] = new int[n+1][n+1];

        String res = ""; // To store result
        int res_length  = 0; // To store length of result

        // building table in bottom-up manner
        int i, index = 0;
        for (i=1; i<=n; i++)
        {
            for (int j=i+1; j<=n; j++)
            {
                // (j-i) > LCSRe[i-1][j-1] to remove
                // overlapping
                if (str[i-1].equals(str[j-1]) &&
                        LCSRe[i-1][j-1] < (j - i))
                {
                    LCSRe[i][j] = LCSRe[i-1][j-1] + 1;

                    // updating maximum length of the
                    // substring and updating the finishing
                    // index of the suffix
                    if (LCSRe[i][j] > res_length)
                    {
                        res_length = LCSRe[i][j];
                        index = Math.max(i, index);
                    }
                }
                else
                    LCSRe[i][j] = 0;
            }
        }

        // If we have non-empty result, then insert all
        // characters from first character to last
        // character of string
        if (res_length > 1) {
            for (i = index - res_length + 1; i <= index; i++)
                res += (str[i-1]) + " ";
            return res;
        } else {
            return String.join(" ", str);
        }
    }


    public static String longestRepeatedSubstring(String str) {
        return listRepeatedSubstring(str.split("\\s+"));
        /*int n = str.length();
        int LCSRe[][] = new int[n+1][n+1];

        String res = ""; // To store result
        int res_length  = 0; // To store length of result

        // building table in bottom-up manner
        int i, index = 0;
        for (i=1; i<=n; i++)
        {
            for (int j=i+1; j<=n; j++)
            {
                // (j-i) > LCSRe[i-1][j-1] to remove
                // overlapping
                if (str.charAt(i-1) == str.charAt(j-1) &&
                        LCSRe[i-1][j-1] < (j - i))
                {
                    LCSRe[i][j] = LCSRe[i-1][j-1] + 1;

                    // updating maximum length of the
                    // substring and updating the finishing
                    // index of the suffix
                    if (LCSRe[i][j] > res_length)
                    {
                        res_length = LCSRe[i][j];
                        index = Math.max(i, index);
                    }
                }
                else
                    LCSRe[i][j] = 0;
            }
        }

        // If we have non-empty result, then insert all
        // characters from first character to last
        // character of string
        if (res_length > 1) {
            for (i = index - res_length + 1; i <= index; i++)
                res += (str.charAt(i - 1));
            return res;
        } else {
            return str;
        }*/
    }


    public static void main(String args[]) throws IOException {
        loadProperties();
        Database opt = Database.openOrCreate(engine, dbname, username, password).get();
        //TuplesDao dao = new TuplesDao(opt.jooq().configuration());
        ObjectReader reader = new ObjectMapper().readerFor(new TypeReference<AgileField>() {});

        for (File f : new File("/home/giacomo/Dropbox (UFL)/AIDA baseline/Shared V6/v6/visualization_output/10-18-2018").listFiles()) {

            Reader content = new FileReader(f);
            Hypotheses cls = jsonSerializer.fromJson(content, Hypotheses.class);
            int noEdges = 0;

            long start = System.currentTimeMillis();
            Hypotheses.Subgraph[] subgraphs = cls.subgraphs;
            for (int i = 0, subgraphsLength = subgraphs.length; i < subgraphsLength; i++) {
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

                    for (int k = 0, subgraph_plus_neighborsLength = subgraph_plus_neighbors.length; k < subgraph_plus_neighborsLength; k++) {
                        Hypotheses.Subgraph.Hypothesis_scorer.Subgraph_plus_neighbor neigh = subgraph_plus_neighbors[k];

                        if (neigh.node_text_1.trim().isEmpty()) {
                            associatedFieldsToER.put(neigh.node_id_1, new Pair<>(neigh.relation_text.split("-")[0], neigh.node_id_2));
                            if (! tp.contains(neigh.node_id_1)) {
                                List<Tuples> tuple =
                                        Utils.fetchTuplesByMID(opt.jooq(), neigh.node_id_1);//dao.fetchByMid(neigh.node_id_1);
                                tp.add(neigh.node_id_1);
                                for (Tuples t : tuple) {
                                    Object[] arrayAgg = t.getArrayAgg();
                                    SQLTuples tup = new SQLTuples();
                                    tup.tupleId = neigh.node_id_1;
                                    tup.tupleFields = new AgileField[arrayAgg.length];
                                    for (int i1 = 0, arrayAggLength = arrayAgg.length; i1 < arrayAggLength; i1++) {
                                        Object arg = arrayAgg[i1];
                                        tup.tupleFields[i1] = reader.readValue(((PGobject) arg).getValue());
                                        tup.tupleFields[i1].fieldString = longestRepeatedSubstring(tup.tupleFields[i1].fieldString.trim());
                                    }
                                    up.put(t.getNisttype(), tup);
                                }
                            }
                        } else {
                            associatedFieldsToER.put(neigh.node_id_2, new Pair<>(neigh.relation_text.split("-")[0], neigh.node_id_1));
                        }
                    }

                    double typeInconsistencyScore = 0;
                    double countRecords = 0;

                    double tupleInconsistencyScore = 0;
                    double countPairs = 0;
                    for (Map.Entry<String, Collection<SQLTuples>> te : up.asMap().entrySet()) {
                        String type = te.getKey();

                        // This hashmap will map each tuple to its equivalent representation
                        HashMultimap<AgileRecord, String> tupleToIds = HashMultimap.create();

                        for (SQLTuples tup : te.getValue()) {
                            AgileRecord rec = tup.asAgileRecord(type, associatedFieldsToER.get(tup.tupleId));
                            tupleToIds.put(rec, tup.tupleId);
                            typeInconsistencyScore += rec.getDegreeTypeInconsistency(fringes);
                            countRecords++;
                        }

                        // Creating the equivalence class
                        tupleToIds.forEach((tuple, id) -> tuple.mentionsId.add(id));

                        ArrayList<AgileRecord> records;
                        records = new ArrayList<>(tupleToIds.keySet().size());

                        // For each entry having the same type, I associate the schema to all the tuples sharing it
                        Iterator<Map.Entry<AgileRecord, Collection<String>>> it = tupleToIds.asMap().entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry<AgileRecord, Collection<String>> current = it.next();
                            records.add(current.getKey());
                            it.remove();
                        }

                        //records.forEach(x -> System.out.println("\t\t\t"+x));

                        int N = records.size();
                        countPairs += N*N;
                        for (int i1 = 0; i1 < N; i1++) {
                            AgileRecord ari = records.get(i1);
                            for (int j1=0; j1<i1; j1++) {
                                AgileRecord arj = records.get(j1);
                                PartialOrderComparison cmp = LoadFact.comparator.compare(ari, arj);
                                if (cmp.t.equals(POCType.Uncomparable)) {
                                    System.out.println("INCO == "+ari+" vs. "+arj);
                                    PartialOrderComparison cmp2 = LoadFact.comparator.compare(ari, arj);
                                    tupleInconsistencyScore += ari.mentionsId.size()*arj.mentionsId.size()*2;
                                }
                            }
                        }
                        //System.out.println("#incons added. = "+ tupleInconsistencyScore);
                    }
                    //System.out.println("typeConsistencyScore = "+Math.exp(-typeInconsistencyScore)+" v  = "+(1.0 - typeInconsistencyScore/countRecords));
                    //System.out.println("tupleConsistencyScore = "+Math.exp(-tupleInconsistencyScore/2.0)+" v  = "+(1.0 - tupleInconsistencyScore/countPairs));
                }
            }
            long end = System.currentTimeMillis();

            System.err.println("s = "+(end-start)/1000.0);
            System.err.println("# = "+noEdges);
        }
    }

}
