package eval;

import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;
import org.ufl.hypogator.jackb.m9.Hypotheses;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class LoadingHypothesesWithMap {

    public static HashMap<String, HashMap<String, Integer>> count(HashMap<String, HashMap<String, ArrayList<String>>> map, String id) {
        HashMap<String, HashMap<String, Integer>> toReturn = new HashMap<>();
        map.entrySet().forEach(cp -> {
            HashMap<String, Integer> mapInternal = new HashMap<>();
            cp.getValue().entrySet().forEach(values -> {
                if (values.getValue().contains(id)) {
                    mapInternal.put(values.getKey(), 1);
                }
            });
            toReturn.put(cp.getKey(), mapInternal);
        });
        return toReturn;
    }

    public static void clean(HashMap<String, HashMap<String, Integer>> map) {
        Set<String> toRemove = new HashSet<>();
        map.entrySet().forEach(cp -> {
            if (cp.getValue().isEmpty() || (cp.getValue().size() == 1 && cp.getValue().containsKey("")) || (!cp.getValue().containsKey("fully-relevant"))) {
                toRemove.add(cp.getKey());
            }
        });
        for (String x : toRemove)
            map.remove(x);
    }

    public static double getScore(HashMap<String, Integer> map) {
        double score = map.getOrDefault("contradicts", 0);
        if (score > 0) {
            return -score;
        }
        return map.getOrDefault("fully-relevant", 0)+map.getOrDefault("partially-relevant", 0)*0.001;
    }

    public static Pair<Set<String>, Double> getBestHypothesisMatch(HashMap<String, HashMap<String, Integer>> map) {
        String hypoMax = null;
        Set<String> allHypos = new HashSet<>();
        Double score = null;

        for (Map.Entry<String, HashMap<String, Integer>> cp : map.entrySet()) {
            if (hypoMax == null) {
                hypoMax = cp.getKey();
                allHypos.add(hypoMax);
                score = getScore(cp.getValue());
            } else {
                double tmpScore = getScore(cp.getValue());
                if (tmpScore > score) {
                    allHypos.clear();
                    hypoMax = cp.getKey();
                    allHypos.add(hypoMax);
                    score = tmpScore;
                } else if (tmpScore == score) {
                    allHypos.add(hypoMax);
                }
            }
        }
        return new Pair<>(allHypos, score);
    }

    public static HashMap<String, HashMap<String, Integer>> sum(Map<String, HashMap<String, Integer>> left, HashMap<String, HashMap<String, Integer>> right) {
        if (left == null || left.isEmpty()) return right;

        HashMap<String, HashMap<String, Integer>> toReturn = new HashMap<>();
        Set<String> keys = new HashSet<>(left.keySet());
        keys.addAll(right.keySet());
        for (String hypoId : keys) {
            Map<String, Integer> leftMap = left.get(hypoId);
            Map<String, Integer> rightMap = right.get(hypoId);
            HashMap<String, Integer> toReturnForHypo = new HashMap<>();
            Set<String> scoringSets = new HashSet<>();
            if (leftMap != null)
                scoringSets.addAll(leftMap.keySet());
            else
                leftMap = Collections.emptyMap();
            if (rightMap != null)
                scoringSets.addAll(rightMap.keySet());
            else
                rightMap = Collections.emptyMap();
            scoringSets.remove("");

            for (String classifications : scoringSets) {
                toReturnForHypo.put(classifications, leftMap.getOrDefault(classifications, 0)+rightMap.getOrDefault(classifications, 0));
            }
            toReturn.put(hypoId, toReturnForHypo);
        }
        return toReturn;
    }

    public static void main(String parg[]) throws IOException {

        // Number of the hypotheses that contain elements that contain no fully-relevant element among hypotheses
        double irrelevantHypotheses = 0;
        double numberHypotheses = 0;
        double numberExpectedHypothesesClass = 0;
        double numberGotHypotheses = 0;
        double sumZeroER = 0.0, sumAllER = 0.0;
        HashMultimap<String, String> forRecall = HashMultimap.create(), forPrecision = HashMultimap.create();

        // Loading query information
        String evalFile = "/home/giacomo/Dropbox (UFL)/AIDA baseline/LDC2018E76_AIDA_Month_9_Pilot_Eval_Annotation_Unsequestered_V1.0/data/P103/hypos.csv";
        HashMap<String, HashMap<String, ArrayList<String>>> scorings = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(evalFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] args = line.split(",");
                if (!scorings.containsKey(args[0])) {
                    scorings.put(args[0], new HashMap<>());
                }
                if (!scorings.get(args[0]).containsKey(args[1])) {
                    scorings.get(args[0]).put(args[1], new ArrayList<>());
                }
                String[] ids = args[2].split(";");
                for (String id : ids) {
                    scorings.get(args[0]).get(args[1]).add(id);
                }
            }
        }
        numberExpectedHypothesesClass = scorings.size();
        ////////////////////////////

        // Loading the scoring files
        String queryFile = "/home/giacomo/Dropbox (UFL)/AIDA baseline/LDC2018E76_AIDA_Month_9_Pilot_Eval_Annotation_Unsequestered_V1.0/data/P103/expected_types_per_query.txt";
        HashMap<String, List<String>> edgeLabels = new HashMap<>();
        HashMap<String, List<String>> erLabels = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(queryFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] args = line.split(",");
                String key = args[0].replace("P103_","");
                erLabels.put(key, Arrays.asList(args[1].split(";")));
                edgeLabels.put(key, Arrays.asList(args[2].split(";")));
            }
        }
        ////////////////////////////

        /// Reading the generated hypotheses
        Gson jsonSerializer = new Gson();
        Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
        Map<String, List<String>> translationId = (Map<String, List<String>>)jsonSerializer.fromJson(new FileReader("/home/giacomo/Scrivania/evaluation/linking/linking_outcome_coref3.json"), type);
        List<String> elist = Collections.emptyList();
        Set<String> gotHypos = new HashSet<>();
        for (File f : new File("/home/giacomo/Scrivania/desktop_trash/master/data/v6/visualization_output/03-06-2019").listFiles()) {
            String hypoFile = f.getName().replace("graph_output_coref_v3_sub_","");
            hypoFile = hypoFile.split("_")[0]+"_"+hypoFile.split("_")[1];
            List<String> eLabels = edgeLabels.get(hypoFile);
            List<String> erLabel = erLabels.get(hypoFile);

            Reader content = new FileReader(f);
            Hypotheses cls = jsonSerializer.fromJson(content, Hypotheses.class);
            int noEdges = 0;
            double maxsimpleERCoverage = 0.0;
            double minsimpleERCoverage = 0.0;
            double numZeroER = 0.0, numAllER = 0.0;

            Hypotheses.Subgraph[] subgraphs = cls.subgraphs;
            for (int i = 0, subgraphsLength = subgraphs.length; i < subgraphsLength; i++) {
                Hypotheses.Subgraph s = subgraphs[i];
                //System.out.println(i+" "+Arrays.toString(subgraphs[i].scorers));
                Hypotheses.Subgraph.Hypothesis_scorer[] hypothesis_scorers = s.hypothesis_scorers;

                for (int j = 0, hypothesis_scorersLength = hypothesis_scorers.length; j < 1; j++) {
                    //System.out.println("\t"+j);
                    Hypotheses.Subgraph.Hypothesis_scorer scorer = hypothesis_scorers[j];
                    Hypotheses.Subgraph.Hypothesis_scorer.Subgraph_plus_neighbor[] subgraph_plus_neighbors = scorer.subgraph_plus_neighbors;

                    noEdges += subgraph_plus_neighbors.length;
                    Set<String> erArguments = new HashSet<>();
                    List<String> eLabels1 = new ArrayList<>(eLabels);
                    List<String> erLabel1 = new ArrayList<>(erLabel);

                    for (int k = 0, subgraph_plus_neighborsLength = subgraph_plus_neighbors.length; k < subgraph_plus_neighborsLength; k++) {
                        Hypotheses.Subgraph.Hypothesis_scorer.Subgraph_plus_neighbor neigh = subgraph_plus_neighbors[k];

                        String edgeArgument = neigh.relation_text.split("-")[0];
                        String tmp = neigh.relation_text.split("-")[1];
                        String associatedER = tmp.split("_")[0]+"."+tmp.split("_")[1];
                        String rectifiedEdgeLabel = associatedER+"_"+ edgeArgument;

                        if (neigh.node_text_1.trim().isEmpty()) {
                            erArguments.addAll(translationId.getOrDefault(neigh.node_id_1, elist));
                            //erArguments.add(neigh.node_id_1);
                        } else if (neigh.node_text_2.trim().isEmpty()) {
                            erArguments.addAll(translationId.getOrDefault(neigh.node_id_2, elist));
                            //erArguments.add(neigh.node_id_2);
                        }

                        if (eLabels1.contains(rectifiedEdgeLabel))
                            eLabels1.remove(rectifiedEdgeLabel);
                        if (erLabel1.contains(associatedER))
                            erLabel1.remove(associatedER);
                    }
                    if (erArguments.isEmpty())
                        continue;

                    HashMap<String, HashMap<String, Integer>> cumulative = null;
                    for (String x : erArguments) {
                        HashMap<String, HashMap<String, Integer>> tmp = sum(cumulative, count(scorings, x));
                        cumulative = tmp;
                    }
                    clean(cumulative);

                   // String associatedHypothesis = null;
                    if (cumulative.isEmpty())
                        irrelevantHypotheses++;
                    else {
                        //System.err.println(cumulative);
                        ////
                        Pair<Set<String>, Double> cp = getBestHypothesisMatch(cumulative);
                        System.out.println(cp);
                        for (String associatedHypothesis : cp.getKey()) {
                            forRecall.putAll(associatedHypothesis, howManyFullyRelevant(scorings.get(associatedHypothesis), erArguments));
                            forPrecision.putAll(associatedHypothesis, howManyIrrelevant(scorings.get(associatedHypothesis), erArguments));
                            gotHypos.add(associatedHypothesis);
                        }
                    }
                    numberHypotheses++;

                    //double simpleEdgeCoverage = ((double)(eLabels.size()-eLabels1.size()))/((double)eLabels.size());
                    double simpleERCoverage = ((double)(erLabel.size()-erLabel1.size()))/((double)erLabel.size());
                    maxsimpleERCoverage = Double.max(simpleERCoverage, maxsimpleERCoverage);
                    minsimpleERCoverage = Double.min(simpleERCoverage, maxsimpleERCoverage);
                    if (simpleERCoverage <= 0.0) {
                        numZeroER++;
                        sumZeroER++;
                    } else if (simpleERCoverage == 1.0) {
                        numAllER++;
                        sumAllER++;
                    }
                    //System.out.println("Simple Edge Coverage = "+(simpleEdgeCoverage*100)+"  Simple ER Coverage="+(simpleERCoverage*100));
                }
            }
            //System.err.println("s = "+(end-start)/1000.0);
            //System.err.println("# = "+noEdges);

            System.out.println(hypoFile);
            System.out.println("MaxSimpleERCoverage = "+maxsimpleERCoverage +" (Number of expected events = "+erLabel.size()+")");
            System.out.println("MinSimpleERCoverage = "+minsimpleERCoverage +" (Number of expected events = "+erLabel.size()+")");
            System.out.println("#Event/Relationship Wise Irrelevant to the Query = "+numZeroER+" ("+(numZeroER /((double)subgraphs.length))*100+" %)");
            System.out.println("#Event/Relationship Wise Completely Relevant to the Query = "+numAllER+" ("+(numAllER /((double)subgraphs.length))*100+" %)");
            System.out.println("");
        }

        numberGotHypotheses = gotHypos.size();


        System.out.println("\n\n\n\n\n\n\n\n\n----");
        System.out.println("#Generated Hypotheses="+(numberHypotheses));
        System.out.println("#Irrelevant Hypotheses="+(irrelevantHypotheses)+" ("+(100*(irrelevantHypotheses/numberHypotheses))+" %)");
        System.out.println("----");
        System.out.println("#Topics to be covered="+numberExpectedHypothesesClass);
        System.out.println("Coverage = "+(100*(numberGotHypotheses/numberExpectedHypothesesClass))+"% ("+numberGotHypotheses+")");
        System.out.println("----");
        System.out.println("#Event/Relationship Wise Irrelevant to the Query = "+sumZeroER+" ("+(sumZeroER /((double)numberHypotheses))*100+" %)");
        System.out.println("#Event/Relationship Wise Completely Relevant to the Query = "+sumAllER+" ("+(sumAllER /((double)numberHypotheses))*100+" %)");


        for (String hypoId : scorings.keySet()) {
            System.out.println(hypoId);
            double recall = ((double)forRecall.get(hypoId).size())/((double)scorings.get(hypoId).getOrDefault("fully-relevant", eee).size());
            double precision = ((double)forRecall.get(hypoId).size())/((double) forRecall.get(hypoId).size() + forPrecision.get(hypoId).size());
            System.out.println("Recall = "+(100.0)*recall+"%");
            System.out.println("Precision = "+(100.0)*precision+"%");
            System.out.println("F1 = "+(2 * (precision*recall)/(precision+recall)));
        }
    }

    private static ArrayList<String> eee = new ArrayList<>();

    private static Set<String> howManyFullyRelevant(HashMap<String, ArrayList<String>> scorings, Set<String> erArguments) {
        ArrayList<String> ls = scorings.getOrDefault("fully-relevant", eee);
        if (ls == null || ls.isEmpty()) return Collections.emptySet();
        Set<String> ids = new HashSet<>(ls);
        ids.retainAll(erArguments);
        return ids;
    }

    private static Set<String> howManyIrrelevant(HashMap<String, ArrayList<String>> scorings, Set<String> erArguments) {
        ArrayList<String> ls = scorings.getOrDefault("fully-relevant", eee);
        if (ls == null || ls.isEmpty()) return Collections.emptySet();
        Set<String> ids = new HashSet<>(ls);
        ids.removeAll(erArguments);
        ls = scorings.getOrDefault("partially-relevant", eee);
        if (ls == null || ls.isEmpty()) return Collections.emptySet();
        ids.removeAll(erArguments);
        return ids;
    }

}
