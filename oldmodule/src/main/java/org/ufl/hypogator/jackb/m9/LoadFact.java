package org.ufl.hypogator.jackb.m9;

import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.Database;
import org.ufl.aida.ldc.dbloader.tmptables.SourceTabLoader;
import org.ufl.aida.ta2.Tables;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.AbstractVocabulary;
import org.ufl.hypogator.jackb.fuzzymatching.ldc.LDCMatching;
import org.ufl.hypogator.jackb.fuzzymatching.ldc.LDCResult;
import org.ufl.hypogator.jackb.m9.configuration.StaticDatabaseClass;
import org.ufl.hypogator.jackb.m9.endm9.HypoAnalyse;
import org.ufl.hypogator.jackb.ontology.data.tuples.projections.AIDATuple;
import org.ufl.hypogator.jackb.streamutils.data.ArraySupport;
import org.ufl.hypogator.mr.Fact;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LoadFact extends StaticDatabaseClass {

    public static boolean load(File ldcData) {
        if (!databaseConnection().isPresent()) {
            if (!loadProperties()) return false;
            Optional<Database> opt = Database.openOrCreate(engine, dbname, username, password);

            if (opt.isPresent()) {
                Database database = opt.get();
                loadForcefully(database, ldcData);
            } else {
                System.err.println("Error creating the new database (");
            }
            return true;
        } else return false;
    }

    private static void createLDCDataSchema(Database database, File ldcData, Integer batchSize) {
        instantiateTable(database, ldcData, Fact.class, batchSize);
    }

    private static <T extends SourceTabLoader, Sub extends T> void instantiateTable(Database database, File path, Class<Sub> clazz, Integer batchSize) {
        database.createTableFromClass(clazz);

        try {
            // For each file within the directory

            // Dummy object allowing to using reflection for loading data as records
            Sub object = clazz.newInstance();

            // Creating a data iterator
            Iterator<Sub> iterator = SourceTabLoader
                    // Loading the data from the current file
                    .loadFromFolder(object, path);
            iterator.next(); // removing the first line

            // Performing a bacth insertion each
            database.batchInsertion(iterator, batchSize);
        } catch (InstantiationException | IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }



    public static void loadForcefully(Database database, File ldcData) {

        AbstractVocabulary.IsStopwordPredicate checkStopwords = AbstractVocabulary.getIsStopwordPredicate();

        Double d;
        if (true) {
            database.rawSqlCommand("DROP TABLE IF EXISTS fact");
            // Loading the ta2 database as a table of rows
            System.out.println("Generating the tables and loading the data");
            d = new Benchmark<Object, Object>() {
                @Override
                public Object function(Object input) {
                    createLDCDataSchema(database, ldcData, batchSize);
                    return null;
                }
            }.apply(null).getKey();
            System.out.println("s = " + d);
        }

        // Define the array_intersect function
        database.rawSqlStatement("CREATE FUNCTION array_intersect(anyarray, anyarray)\n" +
                "  RETURNS anyarray\n" +
                "  language sql\n" +
                "as $FUNCTION$\n" +
                "    SELECT ARRAY(\n" +
                "        SELECT UNNEST($1)\n" +
                "        INTERSECT\n" +
                "        SELECT UNNEST($2)\n" +
                "    );\n" +
                "$FUNCTION$;");
        database.rawSqlStatement("CREATE FUNCTION array_union(anyarray, anyarray)\n" +
                "  RETURNS anyarray\n" +
                "  language sql\n" +
                "as $FUNCTION$\n" +
                "    SELECT ARRAY(\n" +
                "        SELECT UNNEST($1)\n" +
                "        UNION\n" +
                "        SELECT UNNEST($2)\n" +
                "    );\n" +
                "$FUNCTION$;");

        /*if (true) {
            // associating to each clusterId the element that has more occurences. Therefore, we're going to
            // disambiguate only this element,..
            System.out.println("Generating the table of the most frequent term associated within the cluster");
            d = new Benchmark<Object, Object>() {
                @Override
                public Object function(Object input) {
                    database.rawSqlStatement(new File("sql/TA201_MostFrequentInstance.sql"));
                    return null;
                }
            }.apply(null).getKey();
            System.out.println("s = "+d);

            database.rawSqlStatement("create unique index on entity_resolver_count (\"argumentClusterId\", \"argumentRawString\");");
            database.rawSqlStatement("alter table entity_resolver_count ADD CONSTRAINT PK_entity_resolver_count PRIMARY KEY USING INDEX \"entity_resolver_count_argumentClusterId_argumentRawString_idx\";");
        }*/

        // UPDATE: alternative
        if (true) {
            System.out.println("Generating the table of the most frequent term associated within the cluster");
            database.rawSqlCommand("DROP TABLE IF EXISTS mentions_for_update");
            d = new Benchmark<Object, Object>() {
                @Override
                public Object function(Object input) {
                    database.rawSqlStatement(new File("sql/TA211_NewGroupByForChar.sql"));
                    database.rawSqlStatement("select * from mentions_for_update b  where 0 < (select count(x) from (select unnest(strings) as x from mentions_for_update a where b.amid = a.amid  ) foo where NOT ((x = '') IS NOT FALSE));");
                    database.rawSqlStatement("create unique index on mentions_for_update (amid);");
                    database.rawSqlStatement("alter table mentions_for_update ADD CONSTRAINT PK_mentions_for_update PRIMARY KEY USING INDEX mentions_for_update_amid_idx;");
                    return null;
                }
            }.apply(null).getKey();
            System.out.println("s = "+d);
        }

        // Force garbage collection
        System.gc();
        Set<String> forPrintingClusters = new HashSet<>();

        // Problem: this approach assumed that Ta2 always performs a good clustering, which it isnt. Therefore, I'm forced to
        //          deteriorate the computational complexity, and move this phase into the next one.
        if (true) {
            // performing the fuzzy maching. This is performed in a block, so that the memory can be freed
            long start = System.currentTimeMillis();
            LDCMatching ldcDisambiguator = LDCMatching.getInstance();

            /*// This allows to avoid simple degeneralizations
            final AbstractVocabulary<HypoGatorTrimmedMentions> stringToMention =
                    new AbstractVocabulary<>(new TwoGramIndexer<>(HypoGatorTrimmedMentions::getPrecomputedElements));*/
            //EntityResolverCountDao dao = new EntityResolverCountDao(database.jooq().configuration());

            /*// Loading the disambiguation dictionary
            database.jooq().select(Tables.FACT.ARGUMENTID, Tables.FACT.ARGUMENTNISTTYPE, Tables.FACT.ARGUMENTCLUSTERID, PostgresDSL.arrayAgg(PostgresDSL.field("fact.\"argumentRawString\"")), PostgresDSL.arrayAgg(PostgresDSL.field("entity_resolver_count.\"argumentRawString\"")))
                    .from(Tables.FACT).leftJoin(Tables.ENTITY_RESOLVER_COUNT).on(Tables.FACT.ARGUMENTCLUSTERID.equal(Tables.ENTITY_RESOLVER_COUNT.ARGUMENTCLUSTERID))
                    .groupBy(Tables.FACT.ARGUMENTID, Tables.FACT.ARGUMENTNISTTYPE, Tables.FACT.ARGUMENTCLUSTERID)
                    .forEach(x -> {
                        String kbId = x.component3();
                        String mid = x.component1();
                        String type = x.value2();
                        Object[] description = x.value4();
                        String hanlde = x.value5()[0].toString();
                        //String dimension, String kbIds, String mentionedId, String handle, AbstractVocabulary<HypoGatorTrimmedMentions> av, String... justification
                        HypoGatorTrimmedMentions htm = new HypoGatorTrimmedMentions(type, kbId, mid, hanlde, stringToMention, description);
                        stringToMention.forcePut(hanlde, htm);
                        stringToMention.fuzzyMatch()
                    });*/

            /*Set<Record1<String>> elem = database.jooq().select(Tables.ENTITY_RESOLVER_COUNT.ARGUMENTRAWSTRING)
                    .from(Tables.ENTITY_RESOLVER_COUNT)
                    .collect(Collectors.toSet());*/

            database.jooq().selectFrom(Tables.MENTIONS_FOR_UPDATE)
                    .forUpdate()
                    .fetch()
                    .forEach(x -> {
                        double longest;
                        //if (x.getAmid().equals("E994790.00346"))
                        //    System.err.println("DEBUG");
                        List<String> singleList = Arrays.stream( x.getStrings())
                                .flatMap(z -> {
                                    HashSet<String> dedup = new HashSet<>();
                                    new ArraySupport<>(z.split("\\|")).forEachRemaining(y -> {
                                        // Update: removing the stopwords from the to-disambiguate set
                                        if ((!y.isEmpty())&&(!checkStopwords.test(y)))
                                            dedup.add(y);
                                    });
                                    return dedup.stream();
                                })
                                .distinct()
                                .map(HypoAnalyse::longestRepeatedSubstring)
                                .collect(Collectors.toList());
                        longest = singleList.stream().mapToDouble(String::length).max().orElse(1.0);

                        /*if (singleList.contains("Краматорского гражданского аэропорта"))
                            System.err.println("BREAK");*/

                        LDCResult res = null;
                        double stringLength = 0;
                        double stringLengthReliability = 0.7;
                        double scoreReliability = 1.0 - stringLengthReliability;

                        if (singleList.isEmpty()) {
                            x.delete();
                            return;// continue
                        }

                        for (String y : singleList) {
                            LDCResult result = ldcDisambiguator.bestFuzzyMatch(y);
                            if (res == null) {
                                stringLength = y.length();
                                res = result;
                            } else if ((res.score * scoreReliability + (stringLength/longest) * stringLengthReliability) < (result.score * scoreReliability + (y.length()/longest) * stringLengthReliability)) {
                                stringLength = y.length();
                                res = result;
                            }
                        }
                        String resolved = res.resolved;
                        boolean fromFuzzyMatching = res.nistType != null;

                        // Attempting to use transliteration when the fuzzy match fails
                        if (/*resolved.equals(x.getArgumentrawstring()) &&*/ Pattern.matches(".*\\p{InCyrillic}.*", resolved) && res.kbId == null) {
                            HashMap<String, AtomicInteger> mm = new HashMap<>();
                            for (String y : x.getEnstrings()) {
                                if (y == null || y.equals("NA")) continue;
                                y = y.trim();
                                if (y.isEmpty()) continue;
                                AtomicInteger ai = mm.get(y);
                                if (ai == null) {
                                    ai = new AtomicInteger(0);
                                    mm.put(y, ai);
                                }
                                ai.incrementAndGet();
                            }
                            Optional<Map.Entry<String, AtomicInteger>> opt = mm.entrySet().stream().max(Comparator.comparingInt(o -> o.getValue().get()));
                            if (opt.isPresent()) {
                                resolved = opt.get().getKey();
                            } else {
                                resolved = AIDATuple.t.transliterate(resolved);
                            }
                            fromFuzzyMatching = false;

                            LDCResult result = ldcDisambiguator.bestFuzzyMatch(resolved);
                            if (result == null || result.kbId == null) {
                                fromFuzzyMatching = false;
                            } else {
                                fromFuzzyMatching = true;
                                res = result;
                                resolved = result.resolved;
                            }
                        }

                        String test = res.kbId+"\t"+res.resolved;
                        if (forPrintingClusters.add(test));
                            System.out.println(test+" "+resolved);
                        //System.out.println(AIDATuple.t.transliterate(res.resolved));
                        x.setResolvedString(resolved);
                        x.setResolvedType(res == null ? "" : res.nistType );
                        x.setResolvedScore(res == null ? BigDecimal.ONE : BigDecimal.valueOf(res.score));
                        x.setFromfuzzymatching(fromFuzzyMatching);
                        x.update();
                    });

            System.out.println("Updating the fuzzy match in the original data");
            d = new Benchmark<Object, Object>() {
                @Override
                public Object function(Object input) {
                    database.rawSqlStatement(new File("sql/TA202_JoinUpdateLDCResolution.sql"));
                    return null;
                }
            }.apply(null).getKey();
            System.out.println("s = "+d);

            database.jooq().close();

            /*for (Record1<String> sRecord : elem) {
                String sSingle = sRecord.component1();
                ArrayList<String> singleList = null;
                double longest = 0;
                if (!sSingle.contains("|")) {
                    singleList = new ArrayList<>();
                } else {
                    HashSet<String> dedup = new HashSet<>();
                    new ArraySupport<>(sSingle.split("\\|")).forEachRemaining(x -> {
                        // Update: removing the stopwords from the to-disambiguate set
                        if ((!x.isEmpty())&&(!checkStopwords.test(x)))
                            dedup.add(x);
                    });
                    singleList = new ArrayList<>(dedup);
                    longest = singleList.stream().mapToDouble(String::length).max().orElse(1.0);
                }

                // Calculating the fuzzy matching for each possible string representation
                // The longer the string, the better the representation, because I can
                LDCResult res = null;
                double stringLength = 0;
                double stringLengthReliability = 0.7;
                double scoreReliability = 1.0 - stringLengthReliability;

                for (String y : singleList) {
                    LDCResult result = ldcDisambiguator.bestFuzzyMatch(y);
                    if (res == null) {
                        stringLength = y.length();
                        res = result;
                    } else if ((res.score * scoreReliability + (stringLength/longest) * stringLengthReliability) < (result.score * scoreReliability + (y.length()/longest) * stringLengthReliability)) {
                        stringLength = y.length();
                        res = result;
                    }
                }

                List<EntityResolverCount> fetchByArgumentclusterid = dao.fetchByArgumentrawstring(sSingle);
                for (int j = 0, fetchByArgumentclusteridSize = fetchByArgumentclusterid.size(); j < fetchByArgumentclusteridSize; j++) {
                    EntityResolverCount x = fetchByArgumentclusterid.get(j);

                    // Update: remove all the elements that have stopwords
                    if (singleList.isEmpty()) {
                        dao.delete(x);
                        continue;
                    }

                    // BUG: distinguish between BBN, where we had no |, and GAIA, where we might have multiple strings.
                    String resolved = null;
                    LDCResult result = null;
                    if (! x.getArgumentrawstring().contains("|")) {
                        LDCResult internal = ldcDisambiguator.bestFuzzyMatch(x.getArgumentrawstring());
                        if (res != null && (res.score >= internal.score)) {
                            result = internal;
                        }
                    } else {
                        result = res;
                    }

                    if (result != null)
                        resolved = result.resolved;
                    if (resolved == null)
                        resolved = "";

                    if (resolved.equals(x.getArgumentrawstring()) && Pattern.matches(".*\\p{InCyrillic}.*", resolved)) {
                        HashMap<String, AtomicInteger> mm = new HashMap<>();
                        for (String y : x.getEnstrings()) {
                            if (y == null || y.equals("NA")) continue;
                            y = y.trim();
                            if (y.isEmpty()) continue;
                            AtomicInteger ai = mm.get(y);
                            if (ai == null) {
                                ai = new AtomicInteger(0);
                                mm.put(y, ai);
                            }
                            ai.incrementAndGet();
                        }
                        Optional<Map.Entry<String, AtomicInteger>> opt = mm.entrySet().stream().max(Comparator.comparingInt(o -> o.getValue().get()));
                        if (opt.isPresent()) {
                            resolved = opt.get().getKey();
                        } else {
                            resolved = AIDATuple.t.transliterate(resolved);
                        }
                    }

                    if (resolved.equals("Donetsk"))
                        System.err.println("DEBUG");
                    x.setResolvedString(resolved);
                    x.setArgumentnisttype(result == null ? "" : result.nistType );
                    x.setResolvedScore(result == null ? BigDecimal.ONE : BigDecimal.valueOf(result.score));
                    x.setFromfuzzymatching(true);
                }
                dao.update(fetchByArgumentclusterid);
            }
            long end = System.currentTimeMillis();
            System.out.println("s = "+((end-start)/1000));

            */
        }

        System.gc();

        // Memoizing the types' comparison
        /*if (doMemoization) {
            if (true) {
                long start = System.currentTimeMillis();
                CompareOnce.memoizeFieldComparisons(database);
                long end = System.currentTimeMillis();
                System.out.println("Memoizing fields = " + ((end - start) / 1000));
                System.gc();
            }
            database.jooq().update(Tables.FACT).set(Tables.FACT.RESOLVEDTYPE, PostgresDSL.coalesce(Tables.FACT.RESOLVEDTYPE, Tables.FACT.RNISTNAME)).execute();

            long start = System.currentTimeMillis();
            CompareOnce.memoizeTypeComparisons(database);
            long end = System.currentTimeMillis();
            System.out.println("Memoizing types = "+((end-start)/1000));
            System.gc();
        }*/

        if (true) /*try*/ {
            // Grouping all the elements by tuple id, tree and type
            if (true) {
                long start = System.currentTimeMillis();
                database.rawSqlCommand("DROP TABLE IF EXISTS tuples");
                database.rawSqlStatement(new File("sql/TA203_createdTuples.sql"));
                database.rawSqlStatement("alter table tuples ADD CONSTRAINT PK_tuples PRIMARY KEY USING INDEX tuples_mid_idx;");
                long end = System.currentTimeMillis();
                System.out.println("Creating tuples in Postgres = " + ((end - start) / 1000));
            }

            long start = System.currentTimeMillis();
            database.rawSqlCommand("DROP VIEW IF EXISTS tuples2");
            database.rawSqlStatement(new File("sql/TA301_tuples2.sql"));
            database.rawSqlStatement(" create unique index on tuples2 (mid);");
            long end = System.currentTimeMillis();
            System.out.println("View creation for KB Expansion = " + ((end - start) / 1000));



            /*if (true) {
                System.out.println("Whole KB Inconsistency Detection");
                String element = String.join(" ", Files.readAllLines(new File("sql/TA203_groupby.sql").toPath()));
                long start = System.currentTimeMillis();
                database
                        .jooq()
                        .fetch(element)
                        .parallelStream()
                        .forEach(comparator.doesInternalPolicyNotRequireExtendedComparison() ? new InconsistencyStructuralComparisons() : new InconsistencyExtensiveComparison());
                long end = System.currentTimeMillis();
                System.out.println("Required time = " + ((end - start) / 1000) + " s = " + ((end - start) / 60000) + " min = " + ((end - start) / 3600000) + " h");
            }*/
        } /*catch (IOException e) {
            e.printStackTrace();
        }*/

        // 1) Deleating each file written in parallel, and
        // 2) Merging it into one single json file, with one bogus final element
        //MergingResult.writeFilesInParallel();

    }

    public static void main(String args[]) throws IOException {
        loadProperties();
        Database opt = Database.openOrCreate(engine, "p103", username, password).get();
        //String t101 = "/media/giacomo/Data/Progetti/hypogator/data/T101.all.int";
        String ta2 = "/home/giacomo/Scrivania/P103.csv";
        //String ta2 = "./run2.csv";
        loadForcefully(opt, new File(ta2));
        /*Files.readAllLines(Paths.get("/home/giacomo/Scrivania/evaluation/linking/manual_linking.csv")).stream().forEach(x -> {
            System.out.println(AIDATuple.t.transliterate(x));
        });*/
    }

    private static class StringPair {
        public String key;
        public String value;

        public StringPair(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }


}
