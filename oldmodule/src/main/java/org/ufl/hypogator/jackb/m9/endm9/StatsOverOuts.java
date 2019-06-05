package org.ufl.hypogator.jackb.m9.endm9;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.ArrayListMultimap;
import com.google.gson.Gson;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.Database;
import org.ufl.hypogator.jackb.inconsistency.AgileField;
import org.ufl.hypogator.jackb.inconsistency.legacy.TupleComparator;
import org.ufl.hypogator.jackb.m9.Hypotheses;
import org.ufl.hypogator.jackb.m9.StaticDatabaseClass;
import org.ufl.hypogator.jackb.ontology.TtlOntology;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class StatsOverOuts extends StaticDatabaseClass {

    public static Gson jsonSerializer = new Gson();
    static TtlOntology fringes = new TtlOntology("data/SeedlingOntology.ttl");
    public static final TupleComparator comparator = TupleComparator.getDefaultTupleComparator();

    public static void main(String args[]) throws IOException {
        loadProperties();
        Database opt = Database.openOrCreate(engine, dbname, username, password).get();
        //TuplesDao dao = new TuplesDao(opt.jooq().configuration());
        ObjectReader reader = new ObjectMapper().readerFor(new TypeReference<AgileField>() {});

        ArrayListMultimap<String, Double> score = ArrayListMultimap.create();

        for (File f : new File("/home/giacomo/Scrivania/Inconsistenza/out").listFiles()) {
            Reader content = new FileReader(f);
            Hypotheses cls = jsonSerializer.fromJson(content, Hypotheses.class);
            int noEdges = 0;
            String key = null;
            HashMap<Double, AtomicInteger> tree = null;
            if (f.getName().contains("BBN")) {
                key = "BBN";
            } else if (f.getName().contains("GAIA_1")) {
                key = "GAIA_1";
            } else if (f.getName().contains("GAIA_3")) {
                key = "GAIA_3";
            } else {
                System.exit(1);
            }

            Hypotheses.Subgraph[] subgraphs = cls.subgraphs;
            for (int i = 0, subgraphsLength = subgraphs.length; i < subgraphsLength; i++) {
                Hypotheses.Subgraph g = subgraphs[i];
                Hypotheses.Subgraph.Scorer[] scorers = g.scorers;
                for (int i1 = 0, scorersLength = scorers.length; i1 < scorersLength; i1++) {
                    Hypotheses.Subgraph.Scorer s = scorers[i1];
                    if (s.name.equals("scorer_li")) {
                        if (s.value == 2.0 && key.equals("GAIA_3"))
                            System.out.println(f+" with graph "+i);
                        score.put(key, s.value);
                    }
                }
            }
        }

        for (String db : score.keySet()) {
            List<Double> vals = score.get(db);
            System.out.println(db+"\n=============TOTAL="+vals.size()+"\n\n");
            Collections.sort(vals);

            Double min = vals.get(0);
            int minCount = 0;

            Double max = vals.get(vals.size()-1);
            int maxCount = 0;

            Double avg = vals.stream().mapToDouble(x->x).average().getAsDouble();

            boolean exact = false;
            Double median;
            int medianCount = 0;

            int N = vals.size();
            if (N % 2 == 0)
                median = (vals.get(N/2) + (double)vals.get(N/2-1))/2;
            else {
                median = vals.get(N / 2);
                exact = true;
            }

            // Minimum
            for (int i = 0; i<N; i++) {
                if (min.equals(vals.get(i)))
                    minCount++;
                else break;
            }
            System.out.println("Minimum number of errors per hypothesis: "+min+". #Hypotheses with such value = "+minCount);

            // Maximum
            for (int i = N-1; i>=0; i--) {
                if (max.equals(vals.get(i)))
                    maxCount++;
                else break; }

            System.out.println("Maximum number of errors per hypothesis: "+max+". #Hypotheses with such value = "+maxCount);

            // Median
            if (exact) {
                int pos = N/2;
                medianCount = count(vals, N, pos, median);
                System.out.println("Median number of errors per hypothesis: "+median+". #Hypotheses with such value = "+minCount);
            } else {
                Double val = Collections.min(vals, Comparator.comparingDouble(o -> Math.abs(o - median)));
                medianCount = count(vals, N, val);
                System.out.println("Median number of errors per hypothesis: "+median+". Nearest value to median = "+val+" #Hypotheses with such value = "+medianCount);
            }

            // Average

            Double val = Collections.min(vals, Comparator.comparingDouble(o -> Math.abs(o - avg)));
            medianCount = count(vals, N, val);
            System.out.println("Average number of errors per hypothesis: "+avg+". Nearest value to median = "+val+" #Hypotheses with such value = "+medianCount);
        }


    }

    private static int count(List<Double> vals, int n, int pos, Double val) {
        int medianCount = 1;
        for (int i = pos+1; i< n; i++) {
            if (val.equals(vals.get(i)))
                medianCount++;
            else break;
        }
        for (int i = pos-1; i>=0; i--) {
            if (val.equals(vals.get(i)))
                medianCount++;
            else break;
        }
        return medianCount;
    }

    private static int count(List<Double> vals, int n, Double val) {
        return count(vals, n, vals.indexOf(val), val);
    }

    private static int count(List<Double> vals, int n, int pos) {
        return count(vals, n, pos, vals.get(pos));
    }

}
