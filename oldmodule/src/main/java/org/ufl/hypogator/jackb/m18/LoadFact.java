package org.ufl.hypogator.jackb.m18;

import org.jooq.Record1;
import org.jooq.impl.DSL;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.Database;
import org.ufl.aida.ldc.dbloader.tmptables.SourceTabLoader;
import org.ufl.aida.ta2.Tables;
import org.ufl.aida.ta2.tables.MentionsForUpdate;
import org.ufl.aida.ta2.tables.records.MentionsForUpdateRecord;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.AbstractVocabulary;
import org.ufl.hypogator.jackb.fuzzymatching.ldc.LDCMatching;
import org.ufl.hypogator.jackb.fuzzymatching.ldc.LDCResult;
import org.ufl.hypogator.jackb.m9.Benchmark;
import org.ufl.hypogator.jackb.m9.configuration.StaticDatabaseClass;
import org.ufl.hypogator.jackb.m9.endm9.HypoAnalyse;
import org.ufl.hypogator.jackb.streamutils.data.ArraySupport;
import org.ufl.hypogator.mr.Fact;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            database.rawSqlCommand("DROP TABLE IF EXISTS fact CASCADE");
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
        //Set<String> forPrintingClusters = new HashSet<>();

        // Problem: this approach assumed that Ta2 always performs a good clustering, which it isnt. Therefore, I'm forced to
        //          deteriorate the computational complexity, and move this phase into the next one.
        {
            // performing the fuzzy maching. This is performed in a block, so that the memory can be freed
            Set<String> toExpandUsingTA2 = disambiguateWithLDC(database, checkStopwords);

            if (doExpansionFromTA2) {
                System.out.println("Performing the expansion over the TA2 clustering data");
                for (String nullDisambiguated : toExpandUsingTA2) {
                    System.err.println("\tDisambiguating for: "+nullDisambiguated);
                    MentionsForUpdate sidekick_table = Tables.MENTIONS_FOR_UPDATE.as("sidekick");
                    MentionsForUpdate lower_table = Tables.MENTIONS_FOR_UPDATE.as("lower");

                    // Collecting all the strings associated to this disambiguation, and expanded using the TA2 elements
                    Stream<String> stringStream = database.jooq().select(sidekick_table.RESOLVED_STRING)
                            .from(sidekick_table)
                            .where(sidekick_table.ACID.in(
                                    DSL.selectDistinct(lower_table.ACID)
                                            .from(lower_table)
                                            .where(DSL.not(lower_table.FROMFUZZYMATCHING))
                                            .and(lower_table.RESOLVED_STRING.eq(DSL.val(nullDisambiguated)))
                                    )
                            ).and(DSL.not(sidekick_table.FROMFUZZYMATCHING))
                            .fetchLazy()
                            .stream()
                            .map(Record1::component1)
                            ;
                    List<String> singleList = streamStringsWithSplitAndClean(checkStopwords, stringStream);

                    database.jooq().selectFrom(Tables.MENTIONS_FOR_UPDATE)
                            .where(Tables.MENTIONS_FOR_UPDATE.AMID.in(DSL.selectDistinct(sidekick_table.AMID)
                                    .from(sidekick_table)
                                    .where(sidekick_table.ACID.in(
                                            DSL.selectDistinct(lower_table.ACID)
                                                    .from(lower_table)
                                                    .where(DSL.not(lower_table.FROMFUZZYMATCHING))
                                                    .and(lower_table.RESOLVED_STRING.eq(DSL.val(nullDisambiguated)))
                                            )
                                    ).and(DSL.not(sidekick_table.FROMFUZZYMATCHING))))
                            .forUpdate()
                            .fetch()
                            .forEach(x -> {
                                updateRecordFromList(null, null, x, singleList);
                            });
                }
            }

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
        }

        System.gc();

        // Grouping all the elements by tuple id, tree and type
        {
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
    }

    public static List<String> streamStringsWithSplitAndClean(AbstractVocabulary.IsStopwordPredicate checkStopwords, Stream<String> stream) {
        return stream.flatMap(z -> {
            HashSet<String> dedup = new HashSet<>();
            new ArraySupport<>(z.split("\\|")).forEachRemaining(y -> {
                // Update: removing the stopwords from the to-disambiguate set
                if ((!y.isEmpty())&&(!checkStopwords.test(y))) {

                    dedup.add(y);
                }
            });
            return dedup.stream();
        })
                .distinct()
                .map(HypoAnalyse::longestRepeatedSubstring)
                .collect(Collectors.toList());
    }

    public static Set<String> disambiguateWithLDC(Database database, AbstractVocabulary.IsStopwordPredicate checkStopwords) {
        LDCMatching ldcDisambiguator = LDCMatching.getInstance();
        Set<String> notClusteredElements = new HashSet<>();

        database.jooq().selectFrom(Tables.MENTIONS_FOR_UPDATE)
                .forUpdate()
                .fetch()
                .forEach(x -> {
                    List<String> singleList = streamStringsWithSplitAndClean(checkStopwords,Arrays.stream( x.getStrings()));
                    updateRecordFromList(ldcDisambiguator, notClusteredElements, x, singleList);
                });
        return notClusteredElements;
    }

    public static void updateRecordFromList(LDCMatching ldcDisambiguator, Set<String> notClusteredElements, MentionsForUpdateRecord record, List<String> list) {
        DisambiguateListOfStringsViaLDC disambiguateListOfStringsViaLDC = new DisambiguateListOfStringsViaLDC(ldcDisambiguator, record, list).invoke();
        if (disambiguateListOfStringsViaLDC.is()) return;// continue
        LDCResult res = disambiguateListOfStringsViaLDC.getRes();
        String resolved = disambiguateListOfStringsViaLDC.getResolved();
        boolean fromFuzzyMatching = disambiguateListOfStringsViaLDC.isFromFuzzyMatching();

        if ((notClusteredElements != null) && (res.kbId == null))
            notClusteredElements.add(resolved);
        record.setResolvedString(resolved);
        record.setResolvedType(res == null ? "" : res.nistType );
        record.setResolvedScore(res == null ? BigDecimal.ONE : BigDecimal.valueOf(res.score));
        record.setFromfuzzymatching(fromFuzzyMatching);
        record.update();
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
