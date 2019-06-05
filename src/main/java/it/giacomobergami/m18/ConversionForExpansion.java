package it.giacomobergami.m18;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.HashMultimap;
import it.giacomobergami.m18.schemas.LoadSchemas;
import org.jooq.DSLContext;
import org.jooq.InsertValuesStepN;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.postgresql.util.PGobject;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.Database;
import org.ufl.aida.ta2.tables.Expansions;
import org.ufl.aida.ta2.tables.Tuples;
import org.ufl.aida.ta2.tables.Tuples2;
import org.ufl.aida.ta2.tables.records.ExpansionsRecord;
import org.ufl.aida.ta2.tables.records.Tuples2Record;
import org.ufl.aida.ta2.tables.records.TuplesRecord;
import org.ufl.hypogator.jackb.inconsistency.AgileField;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;
import org.ufl.hypogator.jackb.m9.SQLTuples;
import org.ufl.hypogator.jackb.m9.StaticDatabaseClass;
import queries.bitmaps.BitMap;
import types.Schema;

import java.io.IOException;
import java.util.*;

public class ConversionForExpansion extends StaticDatabaseClass {

    private static void batchInsertion(DSLContext jooq, Iterator<Optional<InsertValuesStepN<?>>> recordIterator, int limit) {
        ArrayList<InsertValuesStepN<?>> batchInsertion = new ArrayList<>(limit);
        //ArrayList<int[]> results = new ArrayList<>();

        while (recordIterator.hasNext()) {
            int count = batchInsertion.size();
            if (count >= limit) {
                // Performs the batch insertion to the database
                jooq.batch(batchInsertion).execute();
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
            jooq.batch(batchInsertion).execute();
        }
    }

    public static void main(String args[]) {
        int arg1Pos = 3;
        ObjectReader reader2 = new ObjectMapper().readerFor(new TypeReference<Map<String, List<AgileField>>>() {});
        //ObjectReader reader = new ObjectMapper().readerFor(new TypeReference<AgileField>() {});
        HashSet<String> variadicArguments = LoadSchemas.variadicArguments();
        StaticDatabaseClass.loadProperties();
        Database opt = Database.openOrCreate(StaticDatabaseClass.engine, "p103", StaticDatabaseClass.username, StaticDatabaseClass.password).get();

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

                    HashMultimap<String, AgileField> mm = HashMultimap.create();
                    //HashSet<String> constituents = new HashSet<>(Arrays.asList(tuples2Record.getConstituent()));
                    /*Object[] arrayAgg = tuples2Record.getArrayAgg();
                    SQLTuples tup = new SQLTuples();
                    tup.tupleId = tuples2Record.getMid();
                    tup.tupleFields = new AgileField[arrayAgg.length];
                    for (int i1 = 0, arrayAggLength = arrayAgg.length; i1 < arrayAggLength; i1++) {
                        Object arg = arrayAgg[i1];
                        try {
                            tup.tupleFields[i1] = reader.readValue(((PGobject) arg).getValue());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }*///
                    Map<String, List<AgileField>> mappa = null;
                    try {
                        mappa = reader2.readValue(((PGobject) tuples2Record.getJsonObjectAgg()).getValue());
                    } catch (IOException e) {
                        e.printStackTrace();
                        return Optional.empty();
                    }
                    mappa.forEach(mm::putAll);
                    AgileRecord ar = new AgileRecord(tuples2Record.getNisttype(), mm, new HashSet<>(Arrays.asList(tuples2Record.getConstituent())), tuples2Record.getNegated(), tuples2Record.getHedged(), tuples2Record.getScoreevent());
                    //AgileRecord ar = tup.asAgileRecord(type);

                    if (ar.schema.isEmpty()) {
                        // Ignoring the current record (do not expand it) if it has no relevant arguments to expand
                        return Optional.empty();
                    }


                    //ExpansionsRecord record = new ExpansionsRecord();
                    //record.setTypeEvent(type);
                    // Getting all the ids associated to the current record
                    //record.setEid();
                    // Getting the associated score
                    //record.setWeight(tuples2Record.getScore());

                    // Getting all the maps
                    // 1) null arguments
                    BitMap nulls = new BitMap(7);

                    BitMap neg = new BitMap(8);
                    if (tuples2Record.getNegated())
                        neg.on(0);

                    BitMap head = new BitMap(8);
                    if (tuples2Record.getHedged())
                        head.on(0);

                    // TODO: new approach, after getting the HDD linker

                    Schema s = associatedSchema.get();
                    String array[][] = new String[7][];
                    ArrayList<String> arguments = s.arguments;
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
                    InsertValuesStepN<?> arg = opt.jooq().insertInto(Expansions.EXPANSIONS).values(new String[]{tuples2Record.getMid()}, type, tuples2Record.getScoreevent(), array[0], array[1], array[2], array[3], array[4], array[5], array[6], nulls.toString2(), neg.toString2(), head.toString2());
                    return Optional.of(arg);
                }).iterator();

        batchInsertion(opt.jooq(), it, 100);
    }

}
