package it.giacomobergami.m18;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import it.giacomobergami.m18.concrete.Baseline3;
import it.giacomobergami.m18.schemas.LoadSchemas;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStepN;
import org.jooq.RecordMapper;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.Database;
import org.ufl.aida.ta2.tables.Expansions;
import org.ufl.aida.ta2.tables.Tuples2;
import org.ufl.aida.ta2.tables.records.ExpansionsRecord;
import org.ufl.aida.ta2.tables.records.Tuples2Record;
import org.ufl.hypogator.jackb.inconsistency.AgileField;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;
import org.ufl.hypogator.jackb.m9.StaticDatabaseClass;
import queries.bitmaps.BitMap;
import types.Schema;

import java.util.*;
import java.util.stream.Collectors;

public class ConversionForExpansion extends StaticDatabaseClass {


    static int max_inference_tuple_arguments = 7;
    private static ObjectReader reader2 = new ObjectMapper().readerFor(new TypeReference<Map<String, List<AgileField>>>() {});

    /**
     * Performs the batch insertion of the transformed elements provided by the iterator, and creates directly the database
     * object. Please note that
     * @param connection            Connection to the database where the tuples need to be inserted
     * @param recordIterator        Iterator containing the Optional elements: if the optional is empty, the translation
     *                              failed and no element is going to be inserted. Otherwise, I get the insertion query that
     *                              is going to be forwarded to the relational datbase
     * @param limit                 Number of record limit before flushing the insertion towards the relational database
     */
    private static void batchInsertion(DSLContext connection, Iterator<Optional<InsertValuesStepN<?>>> recordIterator, int limit) {
        ArrayList<InsertValuesStepN<?>> batchInsertion = new ArrayList<>(limit);
        //ArrayList<int[]> results = new ArrayList<>();

        while (recordIterator.hasNext()) {
            int count = batchInsertion.size();
            if (count >= limit) {
                // Performs the batch insertion to the database
                connection.batch(batchInsertion).execute();
                // Remove all the previous insertions
                batchInsertion.clear();
            } else {
                // adding the number of insertions for the current command
                Optional<InsertValuesStepN<?>> element = recordIterator.next();
                if (element != null) element.ifPresent(batchInsertion::add);
            }
        }
        if (!batchInsertion.isEmpty()) {
            // Finalize the last insertion
            connection.batch(batchInsertion).execute();
        }
    }

    public static void main(String args[]) throws Exception {
        int arg1Pos = 3;
        String dbName = "p103";

        // TODO: create the expansions table if does not exist already

        /*HashSet<String> variadicArguments = LoadSchemas.variadicArguments();

        // Reloading the configuration file properties
        StaticDatabaseClass.loadProperties();

        // Loading the relational database
        Database opt = Database.openOrCreate(StaticDatabaseClass.engine, dbName, StaticDatabaseClass.username, StaticDatabaseClass.password).get();

        Iterator<Optional<InsertValuesStepN<?>>> it = opt.jooq()
                .selectFrom(Tuples2.TUPLES2)
                // Not considering for the expansion all the elements that
                .where(DSL.field("array_upper((array_agg),1)").ge(0))
                .fetch((RecordMapper<Tuples2Record, Optional<InsertValuesStepN<?>>>) tuples2Record -> {
                    // Getting the associated type
                    String type = tuples2Record.getNisttype();
                    Optional<Schema> associatedSchema = LoadSchemas.getSchemaDefinition(type);

                    if (!associatedSchema.isPresent()) {
                        System.err.println("ERROR: no schema associated to " + type);
                        System.exit(1);
                        // Ignoring the current record if it has a wrong type description
                        return Optional.empty();
                    }

                    AgileRecord ar = Utils.asAgileRecord(tuples2Record);
                    if (ar == null) return null;
                    //AgileRecord ar = tup.asAgileRecord(type);

                    if (ar.schema.isEmpty()) {
                        // Ignoring the current record (do not expand it) if it has no relevant arguments to expand
                        return Optional.empty();
                    }
                    InsertValuesStepN<?> arg = convertAgileRecordToDatabaseInsertion(variadicArguments, opt, tuples2Record, type, associatedSchema.get(), ar);
                    return Optional.of(arg);
                }).iterator();

        batchInsertion(opt.jooq(), it, 100);

        // End
        opt.close();*/
        expand(dbName, null);
    }

    public static void expand(java.lang.String dbName, java.lang.String[] idList) throws Exception {
        HashSet<java.lang.String> variadicArguments = LoadSchemas.variadicArguments();

        // Reloading the configuration file properties
        StaticDatabaseClass.loadProperties();

        // Loading the relational database
        Database opt = Database.openOrCreate(StaticDatabaseClass.engine, dbName, StaticDatabaseClass.username, StaticDatabaseClass.password).get();

        SelectConditionStep<Tuples2Record> untilWhere = opt.jooq()
                .selectFrom(Tuples2.TUPLES2)
                // Not considering for the expansion all the elements that
                .where(DSL.field("array_upper((array_agg),1)").ge(0));

        if (idList != null && idList.length != 0) {
            untilWhere = untilWhere.and(Tuples2.TUPLES2.MID.eq(DSL.any(idList)));
        }

        Iterator<Optional<InsertValuesStepN<?>>> it = untilWhere
                .fetch((RecordMapper<Tuples2Record, Optional<InsertValuesStepN<?>>>) tuples2Record -> {
                    // Getting the associated type
                    String type = tuples2Record.getNisttype();
                    Optional<Schema> associatedSchema = LoadSchemas.getSchemaDefinition(type);

                    if (!associatedSchema.isPresent()) {
                        System.err.println("ERROR: no schema associated to " + type);
                        System.exit(1);
                        // Ignoring the current record if it has a wrong type description
                        return Optional.empty();
                    }

                    AgileRecord ar = Utils.asAgileRecord(tuples2Record);
                    if (ar == null) return null;
                    //AgileRecord ar = tup.asAgileRecord(type);

                    if (ar.schema.isEmpty()) {
                        // Ignoring the current record (do not expand it) if it has no relevant arguments to expand
                        return Optional.empty();
                    }
                    InsertValuesStepN<?> arg = convertAgileRecordToDatabaseInsertion(variadicArguments, opt, tuples2Record, type, associatedSchema.get(), ar);
                    return Optional.of(arg);
                }).iterator();

        batchInsertion(opt.jooq(), it, 100);

        // End
        opt.close();
    }

    /**
     * Operation performing a transformation from the best format to load the data, towards the one required to do inconsistency detection
     * Todo: directly transform the loaded data towards the inference structure without intermediate steps.
     *
     * @param variadicArguments     Information concerning which elements are variadic (e.g., Time information, that has both a Begin and an End).
     * @param connection            Database connection from which the data is obtained, and towards which the new data is inputed.
     * @param tuples2Record         Tuples2 information, that is the original form how the information is lodead.
     * @param type                  Full type information associated to the recrod.
     * @param associatedSchema      Schema associated to this specific record. This information is obtained from the schema information, and not from the data itself (e.g., for missing arguments)
     * @param ar                    Transformed internal representation
     * @return
     */
    private static InsertValuesStepN<?> convertAgileRecordToDatabaseInsertion(HashSet<String> variadicArguments, Database connection, Tuples2Record tuples2Record, String type, Schema associatedSchema, AgileRecord ar) {
        //ExpansionsRecord record = new ExpansionsRecord();
        //record.setTypeEvent(type);
        // Getting all the ids associated to the current record
        //record.setEid();
        // Getting the associated score
        //record.setWeight(tuples2Record.getScore());

        // Getting all the maps
        // 1) null arguments
        BitMap nulls = new BitMap(7);

        // 2) negated arguments
        BitMap neg = new BitMap(8);
        if (tuples2Record.getNegated())
            neg.on(0);

        // 3) hedged arguments
        BitMap head = new BitMap(8);
        if (tuples2Record.getHedged())
            head.on(0);

        String array[][] = new String[max_inference_tuple_arguments][];
        ArrayList<String> arguments = associatedSchema.arguments;
        HashSet<String> hasMetVariadic = new HashSet<>();
        for (int i = 0, argumentsSize = arguments.size(); i < argumentsSize; i++) {
            String schema_argument = arguments.get(i);
            // if the current argument is a variadic one,
            String substringForVariadic = schema_argument.substring(0, schema_argument.length()-1);
            boolean prevMet = false;
            boolean isVariadic = false;
            if (variadicArguments.contains(substringForVariadic)) {
                prevMet = !hasMetVariadic.add(substringForVariadic);
                isVariadic = true;
            } else {
                substringForVariadic = schema_argument;
            }

            {
                boolean isHedged = false;
                boolean isNegated = false;

                // Otherwise, simple access it
                Set<AgileField> k = ar.fieldList.get(substringForVariadic);

                // If there are no arguments for time information, try to backup with a single time information, with no interval associated to it
                if (k == null || k.isEmpty()) {
                    if (substringForVariadic.equals(LoadSchemas.startTimeName()) || substringForVariadic.equals(LoadSchemas.endTimeName())) {
                        k = ar.fieldList.get("Time");
                    }
                }

                if (k == null || k.isEmpty()) {
                    nulls.on(i); // Missing argument: I need to set the bitmap
                    array[i] = new String[]{};
                } else {
                    array[i] = new String[k.size()];
                    int j = 0;
                    for (Iterator<AgileField> iterator = k.iterator(); iterator.hasNext(); ) {
                        AgileField x = iterator.next();
                        isHedged = isHedged || x.isHedged;
                        isNegated = isNegated || x.isNegated;
                        array[i][j++] = x.fieldString;
                        if (isVariadic && (!prevMet)) {
                            k.remove(x);
                            break;
                        }
                    }
                    // Remember: the first (zero) element is the tuple itself. Therefore, the arguments are incremented by one.
                    if (isHedged) head.on(i+1);
                    if (isNegated) neg.on(i+1);
                }
            }
        }

        // Inserting the new value
        return connection.jooq().insertInto(Expansions.EXPANSIONS).values(new String[]{tuples2Record.getMid()}, type, tuples2Record.getScoreevent(), array[0], array[1], array[2], array[3], array[4], array[5], array[6], nulls.toString2(), neg.toString2(), head.toString2());
    }

    public static List<String> reconstructList(String[] array) {
        if (array == null || array.length == 0)
            return Collections.emptyList();
        else
            return Arrays.asList(array);
    }

    /**
     * Converting the records that were expanded into AgileRecords.
     *
     * @param jooq                  Database connection that is required to reconstruct the original information
     *                              from the tuple information, that is the associated type, the entity id, the score etc.
     * @param expansionsRecord      Original record that has been undergone the expansion, and that is going to be
     *                              represented as an AgileRecord. Given that the final record could be very different
     *                              from the original data sources that were reqiored to create it, I need to fetch
     *                              back the original information and to reconstruct it. The only thing that will change
     *                              will be the final field information
     *
     * @return  Final representation, that is the original one over which I'm going to perform all the scorings
     */
    public static AgileRecord reconstructRecordFromExpansions(DSLContext jooq, ExpansionsRecord expansionsRecord) {
        String typeEvent = expansionsRecord.getTypeEvent();
        Optional<Schema> associatedSchema = LoadSchemas.getSchemaDefinition(typeEvent);

        // Returning the elements that are only having reliable type schema
        if (!associatedSchema.isPresent())
            return null;

        Schema rawSchema = associatedSchema.get();

        BitMap nulled = BitMap.fromString(expansionsRecord.getBitmapNull());
        BitMap hedged = BitMap.fromString(expansionsRecord.getBitmapHed());
        BitMap negate = BitMap.fromString(expansionsRecord.getBitmapNeg());
        String[] possibleRecordId = expansionsRecord.getEid();
        List<AgileRecord> possibleAssociatedRecords = Baseline3.fetchAgileRecordsByExpansionId(jooq, possibleRecordId);

        List<List<String>> toScanOver = Arrays.asList(reconstructList(expansionsRecord.getArg1()), reconstructList(expansionsRecord.getArg2()), reconstructList(expansionsRecord.getArg3()), reconstructList(expansionsRecord.getArg4()), reconstructList(expansionsRecord.getArg5()), reconstructList(expansionsRecord.getArg6()), reconstructList(expansionsRecord.getArg7()));
        AgileRecord ar = new AgileRecord(typeEvent);

        for (int i = 0; i<possibleRecordId.length; i++)
            ar.mentionsId.add(possibleRecordId[i]);
        ar.id = ar.mentionsId.stream().collect(Collectors.joining("_"));

        ar.hedged = hedged.bitmap[0] == 1;
        ar.negated = negate.bitmap[0] == 1;
        ar.score = expansionsRecord.getWeight();

        for (int i=0; i<toScanOver.size(); i++) {
            List<String> ls = toScanOver.get(i);
            ls.removeIf(x -> x == null || x.trim().isEmpty());
            if (nulled.bitmap[i] == 0) {
                List<AgileField> field = new ArrayList<>();
                List<AgileField> candidateBestField = possibleAssociatedRecords.stream().flatMap(x -> x.fieldList.values().stream().filter(y -> ls.contains(y.fieldString)))
                        .collect(Collectors.toList());

                if (!possibleAssociatedRecords.isEmpty()) {
                    Optional<Double> max = candidateBestField.stream().map(x -> x.score).max(Comparator.comparingDouble(a -> a));
                    if (max.isPresent()) {
                        Double currMax = max.get();
                        for (int j = 0; j<candidateBestField.size(); j++) {
                            AgileField f = candidateBestField.get(j);
                            if (f.score.equals(currMax))
                                // Changing the field to the one that is provided by the resulting schema. All the remaining information has still to be the same
                                ar.addField(f.copyWithDifferentFieldName(rawSchema.arguments.get(i), hedged.bitmap[i+1] == 1, negate.bitmap[i+1] == 1));
                        }
                    }
                }
            }
        }

        // It passed the following test. Check each time you're changing something.
        // -- assert possibleAssociatedRecords.stream().anyMatch(x -> x.equals(ar));
        return ar;
    }

    public static class Test {

        public static void main(String args[]) {
            int arg1Pos = 3;
            String dbName = "p103";


            HashSet<String> variadicArguments = LoadSchemas.variadicArguments();

            // Reloading the configuration file properties
            StaticDatabaseClass.loadProperties();

            // Loading the relational database
            Database opt = Database.openOrCreate(StaticDatabaseClass.engine, dbName, StaticDatabaseClass.username, StaticDatabaseClass.password).get();

            opt.jooq().selectFrom(Expansions.EXPANSIONS)
                    .fetchLazy()
                    .forEach(expansionRecord -> System.out.println(reconstructRecordFromExpansions(opt.jooq(), expansionRecord)));
        }

    }

}
