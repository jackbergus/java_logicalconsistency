package org.ufl.hypogator.jackb.m9.endm9;

import au.com.bytecode.opencsv.CSVReader;
import com.google.common.collect.ArrayListMultimap;
import com.google.gson.Gson;
//import org.ufl.aida.ta2.tables.daos.TuplesDao;
import org.ufl.hypogator.jackb.inconsistency.legacy.TupleComparator;
import org.ufl.hypogator.jackb.m9.configuration.StaticDatabaseClass;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StatsOverAlls extends StaticDatabaseClass {

    public static Gson jsonSerializer = new Gson();
    //static TtlOntology fringes = new TtlOntology("data/SeedlingOntology.ttl");
    public static final TupleComparator comparator = TupleComparator.getDefaultTupleComparator();

    // Returns the longest repeating non-overlapping
// substring in str
    public static String longestRepeatedSubstring(String str) {
        int n = str.length();
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
        }
    }


    public static void main(String args[]) throws IOException {
        ArrayListMultimap<String, Double> score = ArrayListMultimap.create();
        for (File f : new File("/home/giacomo/Scrivania/Inconsistenza/in/desktop_output_csv_files").listFiles()) {
            CSVReader reader = new CSVReader(new FileReader(f), '\t');
            String [] nextLine;
            String [] header = reader.readNext();
            int posScorer = -1;
            for (int i = 0; i<header.length; i++) {
                if (header[i].equals("scorer_li")) {
                    posScorer = i;
                    break;
                }
            }
            if (posScorer == -1)
                System.exit(1);

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

            while ((nextLine = reader.readNext()) != null) {
                score.put(key, Double.valueOf(nextLine[posScorer]));
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
