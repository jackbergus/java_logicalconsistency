package eval;

import com.google.common.collect.HashMultimap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.util.Pair;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class LoadingHypothesesFromTSVWithMap {

    public enum cases {
        FULLY_RELEVANT("green"),
        PARTIALLY_RELEVANT("yellow"),
        CONTRADICTORY("red"),
        NA("grey");

        public String value;
        cases(String green) {
            value = green;
        }

        public static cases fromString(String x) {
            if (x.toLowerCase().equals("fully-relevant")) {
                return FULLY_RELEVANT;
            } else if (x.toLowerCase().equals("partially-relevant")) {
                return PARTIALLY_RELEVANT;
            } else if (x.toLowerCase().equals("contradicts")) {
                return CONTRADICTORY;
            } else {
                return NA;
            }
        }
    }

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
        double irrelevantHypotheses;
        double numberHypotheses;
        double numberExpectedHypothesesClass;
        double numberGotHypotheses;
        double sumZeroER;
        double sumAllER;
        HashMultimap<String, String> forRecall;
        HashMultimap<String, String> forPrecision;

        TreeSet<String> lines = new TreeSet<>();

        // Loading query information
        String evalFile = "/home/giacomo/Dropbox (UFL)/AIDA baseline/LDC2018E76_AIDA_Month_9_Pilot_Eval_Annotation_Unsequestered_V1.0/data/P103/hypos.csv";
        HashMap<String, HashMap<String, ArrayList<String>>> scorings = new HashMap<>();
        HashMap<Pair<String, String>, String> classification = new HashMap<>();
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
                    classification.put(new Pair<>(args[0], id), args[1].trim().isEmpty() ? "NA" : args[1]);
                }
            }
        }


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
        boolean fromGreater = false;
        String path = "/home/giacomo/Scrivania/evaluation/pipeline_outcome_TA2bis_linking/outcome_coref3";
        List<Double> valueRange = Files.readAllLines(new File(path,"range.txt").toPath(),  StandardCharsets.UTF_8).stream().map(Double::valueOf).sorted((o1, o2) -> fromGreater ? Double.compare(o2,o1) : Double.compare(o1,o2)).collect(Collectors.toList());
        BiFunction<String, Double, Boolean> testing_function = (tsvHypoScore, currentValue) -> fromGreater ? Double.valueOf(tsvHypoScore) >= currentValue : Double.valueOf(tsvHypoScore) <= currentValue;
        Gson jsonSerializer = new Gson();
        Type type = new TypeToken<Map<String, List<String>>>(){}.getType();
        Map<String, List<String>> translationId = (Map<String, List<String>>)jsonSerializer.fromJson(new FileReader("/home/giacomo/Scrivania/evaluation/linking/linking_outcome_coref3.json"), type);
        List<String> elist = Collections.emptyList();

        for (Double currentRange : valueRange) {
            irrelevantHypotheses = 0;
            numberHypotheses = 0;
            sumZeroER = 0.0;
            sumAllER = 0.0;
            forRecall = HashMultimap.create();
            forPrecision = HashMultimap.create();
            numberExpectedHypothesesClass = scorings.size();
            //Set<Set<String>> disambiguate = new HashSet<>();
            Set<Set<String>> disambiguateWithArgs = new HashSet<>();

            /// Reading the generated hypotheses
            Set<String> gotHypos = new HashSet<>();
            for (File f : Objects.requireNonNull(new File(path).listFiles())) {
                if (!f.getName().endsWith(".tsv"))
                    continue;

                String hypoFile = f.getName().replace(".tsv","");
                int hypothesisIdByCounting = -1;

                Scanner s = new Scanner(f);
                while (s.hasNextLine()) {
                    String[] line = s.nextLine().split("\t");
                    hypothesisIdByCounting++;
                    if (testing_function.apply(line[2], currentRange)) {
                        Set<String> erArguments = new HashSet<>();
                        for (String eventId : line[1].split("\\|")) {
                            erArguments.addAll(translationId.getOrDefault(eventId, elist));
                        }
                        if (erArguments.isEmpty())
                            continue;

                        if (!disambiguateWithArgs.add(erArguments))
                            continue;

                        HashMap<String, HashMap<String, Integer>> cumulative = null;
                        for (String x : erArguments) {
                            HashMap<String, HashMap<String, Integer>> tmp = sum(cumulative, count(scorings, x));
                            cumulative = tmp;
                        }
                        clean(cumulative);

                        // String associatedHypothesis = null;
                        if (cumulative.isEmpty()) {
                            irrelevantHypotheses++;
                            for (String eventIds : erArguments) {
                                lines.add(hypoFile+","+hypothesisIdByCounting+",NA,"+eventIds+",NA");
                            }
                        } else {
                            ////
                            Pair<Set<String>, Double> cp = getBestHypothesisMatch(cumulative);
                            for (String associatedHypothesis : cp.getKey()) {
                                for (String eventIds : erArguments) {
                                    lines.add(hypoFile+","+hypothesisIdByCounting+","+associatedHypothesis+","+eventIds+","+classification.get(new Pair<>(associatedHypothesis, eventIds)));
                                }
                                forRecall.putAll(associatedHypothesis, howManyFullyRelevant(scorings.get(associatedHypothesis), erArguments));
                                forPrecision.putAll(associatedHypothesis, howManyIrrelevant(scorings.get(associatedHypothesis), erArguments));
                                gotHypos.add(associatedHypothesis);
                            }
                        }
                        numberHypotheses++;

                        //double simpleEdgeCoverage = ((double)(eLabels.size()-eLabels1.size()))/((double)eLabels.size());
                        /*double simpleERCoverage = ((double)(erLabel.size()-erLabel1.size()))/((double)erLabel.size());
                        maxsimpleERCoverage = Double.max(simpleERCoverage, maxsimpleERCoverage);
                        minsimpleERCoverage = Double.min(simpleERCoverage, maxsimpleERCoverage);
                        if (simpleERCoverage <= 0.0) {
                            numZeroER++;
                            sumZeroER++;
                        } else if (simpleERCoverage == 1.0) {
                            numAllER++;
                            sumAllER++;
                        }*/
                    }
                }


                //System.err.println("s = "+(end-start)/1000.0);
                //System.err.println("# = "+noEdges);

                //System.out.println(hypoFile);
                //System.out.println("MaxSimpleERCoverage = "+maxsimpleERCoverage +" (Number of expected events = "+erLabel.size()+")");
                //System.out.println("MinSimpleERCoverage = "+minsimpleERCoverage +" (Number of expected events = "+erLabel.size()+")");
                //System.out.println("#Event/Relationship Wise Irrelevant to the Query = "+numZeroER+" ("+(numZeroER /((double)subgraphs.length))*100+" %)");
                //System.out.println("#Event/Relationship Wise Completely Relevant to the Query = "+numAllER+" ("+(numAllER /((double)subgraphs.length))*100+" %)");
                //System.out.println("");
            }

            numberGotHypotheses = gotHypos.size();

            System.out.println("\n\n\n\n\n\n\n\n\nFor Score "+currentRange);
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

        PrintWriter pw = new PrintWriter(new FileOutputStream(new File(path, "classification.csv")));
        pw.println("query_id,subgraph_id,matched_hypothesis,er,annotated_as");
        for (String x : lines)
            pw.println(x);
        pw.close();

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
