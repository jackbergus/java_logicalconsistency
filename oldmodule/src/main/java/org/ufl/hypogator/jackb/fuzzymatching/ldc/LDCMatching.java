package org.ufl.hypogator.jackb.fuzzymatching.ldc;

import com.google.common.collect.HashMultimap;
import javafx.util.Pair;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDSL;
import org.ufl.aida.ldc.dbloader.FileUtils;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.Database;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.rdbms.DBMSInterface;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.rdbms.DBMSInterfaceFactory;
import org.ufl.aida.ldc.dbloader.tmptables.Hypotheses;
import org.ufl.aida.ldc.dbloader.tmptables.SourceTabLoader;
import org.ufl.aida.ldc.dbloader.tmptables.mentions.EntMentions;
import org.ufl.aida.ldc.dbloader.tmptables.mentions.EvtMentions;
import org.ufl.aida.ldc.dbloader.tmptables.mentions.RelMentions;
import org.ufl.aida.ldc.dbloader.tmptables.miniKB;
import org.ufl.aida.ldc.dbloader.tmptables.slots.EvtSlots;
import org.ufl.aida.ldc.dbloader.tmptables.slots.RelSlots;
import org.ufl.aida.ldc.jOOQ.model.Tables;
import org.ufl.aida.ldc.jOOQ.model.tables.records.EntityresolverRecord;
import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.fuzzymatching.MultiWordSimilarity;
import org.ufl.hypogator.jackb.ontology.JsonOntologyLoader;
import org.ufl.hypogator.jackb.streamutils.data.ArraySupport;
import org.ufl.hypogator.jackb.utils.ArgMaxCollector;
import org.ufl.hypogator.jackb.utils.adt.Triple;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class LDCMatching {

    private static Properties properties = new Properties();
    private static String dbname, username, password;
    private static Integer batchSize;
    private static File ldcData;
    private static DBMSInterface engine;
    //private HashMultimap<String, String> dictionary_ToKBID;
    private HashMap<String, String> kbidToHandle;
    private HashMultimap<String, String> kbidToTerms;
    private HashMap<String, Pair<Long, String>> kbtoNistType;
    private static MultiWordSimilarity sim = new MultiWordSimilarity();
    private static JsonOntologyLoader ontologyEntrypoint = JsonOntologyLoader.getInstance();

    private static boolean loadProperties() {
        if (properties.isEmpty()) try {
            properties.load(new FileInputStream("conf/postgresql.properties"));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } else
            return true;
    }

    public static boolean isNotNullAscii(String str) {
        return str != null && str.length() > 0 && !Pattern.matches(".*\\p{InCyrillic}.*", str);
    }

    private void resolveTypeForRow(EntityresolverRecord record) {
        for (String kbid : record.getKbids()) {
            Pair<Long, String> element = kbtoNistType.get(kbid);
            long len = (record.getIds() == null ? 0 : record.getIds().length)+(record.getMids() == null ? 0 : record.getMids().length);
            if (element == null) {
                kbtoNistType.put(kbid, new Pair<>(len, ontologyEntrypoint.resolveLDCToNist(record.getType())));
            } else {
                if (element.getKey() < len) {
                    kbtoNistType.put(kbid, new Pair<>(len, ontologyEntrypoint.resolveLDCToNist(record.getType())));
                }
            }
        }
    }

    private final static String cleanHandleString(String str) {
            if (str == null)
                return null;
            if (str.contains("a.k.a")) {
                int pos = str.indexOf("a.k.a");
                return cleanHandleString(str.substring(0, pos));
            } else if (str.contains("- ")) {
                int pos = str.indexOf("- ");
                return cleanHandleString(str.substring(0, pos));
            } else if (str.contains("(")) {
                int pos = str.indexOf('(');
                return cleanHandleString(str.substring(0, pos));
            } else if (str.contains("[")) {
                int pos = str.indexOf('[');
                return cleanHandleString(str.substring(0, pos));
            } else{
                return str;
            }
    }

    private LDCMatching() {
        // Load the database if not there
        loadLDCToDatabase();
        //dictionary_ToKBID = HashMultimap.create();
        kbidToHandle = new HashMap<>();
        kbidToTerms = HashMultimap.create();
        kbtoNistType = new HashMap<>();

        Optional<Database> conn = databaseConnection();
        if (conn.isPresent()) {
            Database db = conn.get();
            Iterator<EntityresolverRecord> it = db.jooq().selectFrom(Tables.ENTITYRESOLVER).where(Tables.ENTITYRESOLVER.CATEGORY.equal("Filler")).or(Tables.ENTITYRESOLVER.CATEGORY.equal("Entity"))
                    .iterator();
            while (it.hasNext()) {
                EntityresolverRecord row = it.next();
                String[] kbIds = row.component7();
                String rowType = row.component3();
                String handle = cleanHandleString(row.component1());
                //dictionary_ToKBID.putAll(handle, idIterable);
                for (int i = 0, kbIdsLength = kbIds.length; i < kbIdsLength; i++) {
                    String id = kbIds[i];
                    kbidToHandle.put(id, handle);
                }
                resolveTypeForRow(row);
                populateToKBID(row.getKbids(), new String[]{handle});
                populateToKBID(row.getKbids(), row.component8());
                populateToKBID(row.getKbids(), row.component9());
                populateToKBID(row.getKbids(), row.component10());

            }
        }
    }

    private static LDCMatching self = null;
    public static LDCMatching getInstance() {
        if (self == null) {
            self = new LDCMatching();
        }
        return self;
    }

    /**
     * Returns all the set of fuzzy matches for the query
     * @param query
     * @return
     */
    public List<LDCResult> fuzzyMatch(String query) {
        List<LDCResult> triples = new ArrayList<>(kbidToTerms.keySet().size());
        for (Map.Entry<String, Collection<String>> x : kbidToTerms.asMap().entrySet()) {
            String kbId = x.getKey();
            List<String> stringList = x.getValue().stream().filter(y -> y!=null && y.length() > 0).collect(Collectors.toList());
            Pair<Double, List<String>> cpMax = stringList.stream().collect(ArgMaxCollector.collector(z -> sim.sim(query, z), Double::compareTo));
            if (cpMax != null && cpMax.getKey() != null && cpMax.getKey() > 0.5) {
                if (kbId.startsWith("NIL")) {
                    List<String> ls = cpMax.getValue().stream().filter(LDCMatching::isNotNullAscii).sorted(Comparator.comparingInt(String::length)).collect(Collectors.toList());
                    if (ls.isEmpty()) {
                        ArrayList<String> cpLs = new ArrayList<>(stringList);
                        String candidate = query;
                        try {
                            candidate = Collections.max(cpLs, Comparator.comparingInt(String::length));
                        } catch (NullPointerException e){
                            e.printStackTrace();
                        }
                        triples.add(new LDCResult(kbId, candidate, cpMax.getKey(), kbtoNistType.get(kbId).getValue()));
                    } else {
                        triples.add(new LDCResult(kbId, ls.get(0), cpMax.getKey(), kbtoNistType.get(kbId).getValue()));
                    }
                } else {
                    String handle = kbidToHandle.get(kbId);
                    double score;
                    if (Pattern.matches(".*\\p{InCyrillic}.*", handle)) {
                        score = sim.sim(query, handle)*0.4+cpMax.getKey()*0.6;
                    } else {
                        score = cpMax.getKey();
                    }
                    triples.add(new LDCResult(kbId, kbidToHandle.get(kbId), score, kbtoNistType.get(kbId).getValue()));
                }
            }
        }
        return triples;
        /*
        List<LDCResult> triples = new ArrayList<>(kbidToTerms.keySet().size());
        for (Map.Entry<String, Collection<String>> x : kbidToTerms.asMap().entrySet()) {
            String kbId = x.getKey();
            List<String> stringList = x.getValue().stream().filter(y -> y!=null && y.length() > 0).collect(Collectors.toList());
            Pair<Double, List<String>> cpMax = stringList.stream().collect(ArgMaxCollector.collector(z -> sim.sim(query, z), Double::compareTo));
            if (cpMax != null && cpMax.getKey() != null && cpMax.getKey() > 0.5) {
                if (kbId.startsWith("NIL")) {
                    List<String> ls = cpMax.getValue().stream().filter(LDCMatching::isNotNullAscii).sorted(Comparator.comparingInt(String::length)).collect(Collectors.toList());
                    if (ls.isEmpty()) {
                        ArrayList<String> cpLs = new ArrayList<>(stringList);
                        String candidate = query;
                        try {
                            candidate = Collections.max(cpLs, Comparator.comparingInt(String::length));
                        } catch (NullPointerException e){
                            e.printStackTrace();
                        }
                        triples.add(new LDCResult(kbId, candidate, cpMax.getKey(), kbtoNistType.get(kbId).getValue()));
                    } else {
                        triples.add(new LDCResult(kbId, ls.get(0), cpMax.getKey(), kbtoNistType.get(kbId).getValue()));
                    }
                } else {
                    double val = (sim.sim(kbidToHandle.get(kbId), query)*0.4+cpMax.getKey()*0.6);
                    if (val >= 0.9)
                        triples.add(new LDCResult(kbId, kbidToHandle.get(kbId), cpMax.getKey(), kbtoNistType.get(kbId).getValue()));
                }
            }
        }
        return triples;
         */
    }

    /**
     * String that best matches with the query.
     * @param query
     * @return          A triple, where the first element is the LDC kbID, the second is the translated string, and the last one is the score.
     *                  If no plausible match is given (> 0), then the query is returned instead.
     */
    private HashMap<String, LDCResult> memoization = new HashMap<>();

    public LDCResult bestFuzzyMatch(String query) {
        if (query.trim().isEmpty())
            return new LDCResult( query);
        LDCResult t;
        if ((t = memoization.get(query)) == null) {
            List<LDCResult> ls = fuzzyMatch(query);
            if (ls == null || ls.isEmpty()) {
                t = new LDCResult( query);
                memoization.put(query, t);
            } else {
                t = Collections.max(ls, Comparator.comparing(LDCResult::score));
                if (t.score > 0.659) {
                    if (t.resolved.contains(query)) {
                        t = t.copyExceptResolved(query);
                    }
                } else {
                    t = new LDCResult(query);
                }
                memoization.put(query, t);
            }
        }
        return t;
    }

    private void populateToKBID(String[] kbids, String[] elements) {
        for (int i = 0, kbidsLength = kbids.length; i < kbidsLength; i++) {
            String kbId = kbids[i];
            kbidToTerms.putAll(kbId, () -> new ArraySupport<>(elements));
        }
    }



    /**
     * Establishing an LDC connection to the database
     * @return
     */
    public static Optional<Database> databaseConnection() {
        if (!loadProperties()) return Optional.empty();
        engine = DBMSInterfaceFactory.generate(properties.getProperty("engine", "PostgreSQL"));
        dbname = properties.getProperty("dbname", "ldc");
        username = properties.getProperty("username", System.getProperty("user.name"));
        password = properties.getProperty("password", "password");
        batchSize = Integer.valueOf(properties.getProperty("batchSize", "1000"));
        ldcData = new File(properties.getProperty("ldcData"));
        return Database.open(engine, dbname, username, password);
    }

    // Loading the database into PostgreSQL
    public static boolean loadLDCToDatabase() {
        if (!databaseConnection().isPresent()) {
            if (!loadProperties()) return false;
            Optional<Database> opt = Database.openOrCreate(engine, dbname, username, password);

                if (opt.isPresent()) {
                    Database database = opt.get();
                    System.out.println("Generating the tables and loading the data");
                    createLDCDataSchema(database, ldcData, batchSize);

                    createLinkedmentionsTable(database);
                    aggregateLink(database);
                    resolveMentions(database);
                } else {
                    System.err.println("Error creating the new database (");
                }
                return true;
        } else return false;
    }

    private static void resolveMentions(Database database) {
        System.out.println("Associating the mentions to the slots");
        // Creating the slots with fields
        database.rawSqlStatement(new File("sql/mentions_with_slots.sql"));
        // Creating the slots with no fields
        database.rawSqlStatement(new File("sql/mentions_with_noslots.sql"));
        // Merging the two tables together (the creating using a unique SELECT operation is more inefficient)
        database.rawSqlStatement(new File("sql/uniontables.sql"));

        // Removing the two non-merged intermediate tables
        System.out.println("Removing the temporarly tables");
        //TODO: database.jooq().dropTable(Tables.MENTIONWITHSLOTSB).execute();
        //TODO: database.jooq().dropTable(Tables.MENTIONWITHSLOTS).execute();

        // Adding searching indices in order to enhance the performances
        System.out.println("Indexing the table: mentionslot");

        database.jooq()
                .createUniqueIndex("mentionidKey")
                .on(Tables.MENTIONSLOT)
                .include(Tables.MENTIONSLOT.TREE_ID, Tables.MENTIONSLOT.ID,      Tables.MENTIONSLOT.MENTIONID,
                        Tables.MENTIONSLOT.TYPE,    Tables.MENTIONSLOT.SUBTYPE, Tables.MENTIONSLOT.ATTRIBUTES)
                .execute();

        database.jooq()
                .createIndex("searchIndex")
                .on(Tables.MENTIONSLOT)
                .include(Tables.MENTIONSLOT.MENTIONID)
                .execute();
    }

    /**
     *
     * @param database      Database connection
     * @param dirs          List of directories over which load the data
     * @param clazz         Stub class that will be used to lad the data from the file and to provide it to the relational database
     * @param batchSize     Size for the batch insertion
     * @param <T>           Type of the stub class
     * @param <Sub>
     */
    private static <T extends SourceTabLoader, Sub extends T> void instantiateTable(Database database, List<File> dirs, Class<Sub> clazz, int batchSize) {
        System.out.println("Creating table: "+clazz.getName());
        database.createTableFromClass(clazz);

        try {
            // For each file within the directory
            for (File path : dirs) {
                // Dummy object allowing to using reflection for loading data as records
                Sub object = clazz.newInstance();

                // Creating a data iterator
                Iterator<Sub> iterator = SourceTabLoader
                        // Loading the data from the current file
                        .loadFromFolder(object, path);

                // Performing a bacth insertion each
                database.batchInsertion(iterator, batchSize);
            }
        } catch (InstantiationException | IllegalAccessException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads each possible class
     * @param database
     * @param ldcData
     */
    private static void createLDCDataSchema(Database database, File ldcData, int batchSize) {
        // Each subdirectory of the LDC data is a topic id folder.
        List<File> ls = FileUtils.getSubDirectories(ldcData);

        // Loading the data from all of these
        instantiateTable(database, ls, EntMentions.class, batchSize);
        instantiateTable(database, ls, EvtMentions.class, batchSize);
        instantiateTable(database, ls, RelMentions.class, batchSize);
        instantiateTable(database, ls, EvtSlots.class,    batchSize);
        instantiateTable(database, ls, RelSlots.class,    batchSize);
        instantiateTable(database, ls, Hypotheses.class,  batchSize);
        instantiateTable(database, ls, miniKB.class,      batchSize);
    }

    /**
     * Links the mention table to the miniKB
     * @param db
     */
    private static void createLinkedmentionsTable(Database db){
        System.out.println("Linking the mentions (creating linkedmentions)");
        DSLContext create = db.jooq();
        create.createTable("linkedmentions")
                .as(create.select(Tables.MENTION.TREE_ID,
                        Tables.MENTION.MENTIONID,
                        Tables.MENTION.ID,
                        Tables.MENTION.PROVENANCE,

                        // Textual representation of the entity
                        Tables.MINIKB.HANDLE,
                        Tables.MENTION.TEXT_STRING,
                        Tables.MENTION.JUSTIFICATION,
                        Tables.MINIKB.DESCRIPTION,

                        // Category might be null: use python data to integrate
                        Tables.MINIKB.CATEGORY,
                        Tables.MENTION.TYPE,
                        Tables.MENTION.SUBTYPE,

                        Tables.MENTION.ATTRIBUTE,
                        Tables.MENTION.ATTRIBUTE2,

                        // Temporal information
                        Tables.MENTION.START_DATE_TYPE,
                        Tables.MENTION.START_DATE,
                        Tables.MENTION.END_DATE_TYPE,
                        Tables.MENTION.END_DATE,


                        Tables.MENTION.KB_ID,
                        Tables.MINIKB.TOPIC_ID,
                        Tables.MENTION.TEXTOFFSET_STARTCHAR,
                        Tables.MENTION.TEXTOFFSET_ENDCHAR

                ).from(Tables.MENTION.naturalLeftOuterJoin(Tables.MINIKB)))
                .execute();
        create.close();
    }

    /**
     * Aggregates the LDC Data that was already linked by createLinkedmentionsTable
     * @param db
     */
    private static void aggregateLink(Database db) {
        System.out.println("Aggregating the mentions (creating entityResolver)");
        DSLContext create = db.jooq();
        create.createTable("entityResolver")
                .as(
                        // Either we have a miniKB description, and then the same entity is described by the same hanlde
                        create.select(Tables.LINKEDMENTIONS.HANDLE,
                                Tables.LINKEDMENTIONS.CATEGORY,
                                Tables.LINKEDMENTIONS.TYPE,
                                Tables.LINKEDMENTIONS.SUBTYPE,
                                PostgresDSL.arrayAggDistinct(Tables.LINKEDMENTIONS.ATTRIBUTE).as("attributes"),
                                PostgresDSL.arrayAggDistinct(Tables.LINKEDMENTIONS.ATTRIBUTE2).as("attributes2"),
                                PostgresDSL.arrayAggDistinct(Tables.LINKEDMENTIONS.KB_ID).as("kbids"),
                                PostgresDSL.arrayAggDistinct(Tables.LINKEDMENTIONS.DESCRIPTION).as("descriptions"),
                                PostgresDSL.arrayAggDistinct(Tables.LINKEDMENTIONS.JUSTIFICATION).as("justifications"),
                                PostgresDSL.arrayAggDistinct(Tables.LINKEDMENTIONS.TEXT_STRING).as("text_strings"),
                                PostgresDSL.arrayAggDistinct(Tables.LINKEDMENTIONS.ID).as("ids"),
                                PostgresDSL.arrayAggDistinct(Tables.LINKEDMENTIONS.MENTIONID).as("mids"))
                                .from(Tables.LINKEDMENTIONS)
                                .where(Tables.LINKEDMENTIONS.HANDLE.isNotNull())
                                .groupBy(Tables.LINKEDMENTIONS.HANDLE,Tables.LINKEDMENTIONS.CATEGORY,Tables.LINKEDMENTIONS.TYPE, Tables.LINKEDMENTIONS.SUBTYPE)
                                .union(

                                        // Otherwise, we have a null handle and everythin is described by the NIL_#KBID
                                        create.select(DSL.val((String) null).as("handle"),
                                                Tables.LINKEDMENTIONS.CATEGORY,
                                                Tables.LINKEDMENTIONS.TYPE,
                                                Tables.LINKEDMENTIONS.SUBTYPE,
                                                PostgresDSL.arrayAggDistinct(Tables.LINKEDMENTIONS.ATTRIBUTE).as("attributes"),
                                                PostgresDSL.arrayAggDistinct(Tables.LINKEDMENTIONS.ATTRIBUTE2).as("attributes2"),
                                                PostgresDSL.arrayAggDistinct(Tables.LINKEDMENTIONS.KB_ID).as("kbids"),
                                                PostgresDSL.arrayAggDistinct(Tables.LINKEDMENTIONS.DESCRIPTION).as("descriptions"),
                                                PostgresDSL.arrayAggDistinct(Tables.LINKEDMENTIONS.JUSTIFICATION).as("justifications"),
                                                PostgresDSL.arrayAggDistinct(Tables.LINKEDMENTIONS.TEXT_STRING).as("text_strings"),
                                                PostgresDSL.arrayAggDistinct(Tables.LINKEDMENTIONS.ID).as("ids"),
                                                PostgresDSL.arrayAggDistinct(Tables.LINKEDMENTIONS.MENTIONID).as("mids"))
                                                .from(Tables.LINKEDMENTIONS)
                                                .where(Tables.LINKEDMENTIONS.HANDLE.isNull())
                                                .groupBy(Tables.LINKEDMENTIONS.KB_ID,Tables.LINKEDMENTIONS.CATEGORY,Tables.LINKEDMENTIONS.TYPE, Tables.LINKEDMENTIONS.SUBTYPE)
                                )
                )
                .execute();

        // Creating the index for text search
        create.createIndex("handleSearch").on(Tables.ENTITYRESOLVER).include(Tables.ENTITYRESOLVER.HANDLE).execute();
    }

    public static void main(String args[]) {
        System.out.println(
                LDCMatching.getInstance().bestFuzzyMatch("fighters"));
        System.out.println(
                LDCMatching.getInstance().bestFuzzyMatch("війська"));
        System.out.println(
                LDCMatching.getInstance().bestFuzzyMatch("airport"));
        System.out.println(
                LDCMatching.getInstance().bestFuzzyMatch("airfield"));
        System.out.println(
                LDCMatching.getInstance().bestFuzzyMatch("fields"));
        System.out.println(
                LDCMatching.getInstance().bestFuzzyMatch("aircraft"));
        System.out.println(MultiWordSimilarity.getInstance().sim("airport", "Kramatorsk Airport"));
    }
}
