package it.giacomobergami.m18;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.HashMultimap;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.impl.DSL;
import org.jooq.util.postgres.PostgresDSL;
import org.postgresql.util.PGobject;
import org.ufl.aida.ta2.Tables;
import org.ufl.aida.ta2.tables.pojos.Tuples;
import org.ufl.aida.ta2.tables.pojos.Tuples2;
import org.ufl.aida.ta2.tables.records.Tuples2Record;
import org.ufl.hypogator.jackb.inconsistency.AgileField;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;
import org.ufl.hypogator.jackb.m9.SQLTuples;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Utils {

    private static ObjectReader reader;
    static  {
        reader = new ObjectMapper().readerFor(new TypeReference<AgileField>() {});
    }

    public static <T, C> Condition isContainedBy(
            Field<? extends C> left,
            C right
    ) {
        PostgresDSL.val(new String[]{});
        return DSL.condition("{0} <@ {1}", left, DSL.val(right, left.getDataType()));
    }

    public static List<Tuples> fetchTuplesByMID(DSLContext jooq, String id) {
        return jooq.selectFrom(Tables.TUPLES).where(Tables.TUPLES.MID.eq(id)).fetchInto(Tuples.class);
    }

    public static List<Tuples> fetchTuplesByExpansionId(DSLContext jooq, String[] id) {
        return jooq.selectFrom(Tables.TUPLES).where(isContainedBy(Tables.TUPLES.CONSTITUENT, id)).fetchInto(Tuples.class);
    }

    public static List<Tuples2> fetchTuples2ByExpansionId(DSLContext jooq, String[] id) {
        return jooq.selectFrom(Tables.TUPLES2).where(isContainedBy(Tables.TUPLES2.CONSTITUENT, id)).fetchInto(Tuples2.class);
    }

    public static List<AgileRecord> fetchAgileRecordsByExpansionId(DSLContext jooq, String[] id) {
        return jooq.selectFrom(Tables.TUPLES2).where(isContainedBy(Tables.TUPLES2.CONSTITUENT, id))
                .stream()
                .map(Utils::asAgileRecord)
                .collect(Collectors.toList());
    }

    public static AgileRecord defaultTupleRepresentation(Tuples x) {
        Object[] arrayAgg = x.getArrayAgg();
        SQLTuples tup = new SQLTuples();
        tup.tupleFields = new AgileField[arrayAgg.length];
        tup.tupleId = x.getMid();

        for (int i1 = 0, arrayAggLength = arrayAgg.length; i1 < arrayAggLength; i1++) {
            Object arg = arrayAgg[i1];
            try {
                tup.tupleFields[i1] = reader.readValue(((PGobject) arg).getValue());
                tup.tupleFields[i1].fieldString = (tup.tupleFields[i1].fieldString.trim());
                // Resolution
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return tup.asAgileRecord(x.getNisttype());
    }


    private static ObjectReader reader2 = new ObjectMapper().readerFor(new TypeReference<Map<String, List<AgileField>>>() {});
    public static AgileRecord asAgileRecord(Tuples2Record tuples2Record) {
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
            return null;
        }
        mappa.forEach(mm::putAll);
        AgileRecord ar = new AgileRecord(tuples2Record.getNisttype(), mm, new HashSet<>(Arrays.asList(tuples2Record.getConstituent())), tuples2Record.getNegated(), tuples2Record.getHedged(), tuples2Record.getScoreevent());
        return ar;
    }
}
