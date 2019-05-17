package it.giacomobergami.m18;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.collect.HashMultimap;
import org.jooq.DSLContext;
import org.jooq.Record2;
import org.jooq.RecordMapper;
import org.jooq.impl.DSL;
import org.postgresql.util.PGobject;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.Database;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.rdbms.DBMSInterfaceFactory;
import org.ufl.aida.ldc.jOOQ.model.Tables;
import org.ufl.aida.ldc.jOOQ.model.tables.Hypothesis;
import org.ufl.aida.ta2.tables.daos.TuplesDao;
import org.ufl.aida.ta2.tables.pojos.Tuples;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.fuzzymatching.ldc.LDCMatching;
import org.ufl.hypogator.jackb.inconsistency.AgileField;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;
import org.ufl.hypogator.jackb.m9.LoadFact;
import org.ufl.hypogator.jackb.m9.SQLTuples;
import org.ufl.hypogator.jackb.m9.StaticDatabaseClass;
import org.ufl.hypogator.jackb.streamutils.data.Tuple;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static org.ufl.hypogator.jackb.m9.endm9.HypoAnalyse.longestRepeatedSubstring;

public class LDCAnnotations extends StaticDatabaseClass {

    public static void main(String args[]) {

        // Database from which we should get this hypothesis
        String databaseId = "p103";
        // Hypothesis to get
        for (String hypothesisID : new String[] {"P103_Q002_H001",
                "P103_Q002_H002",
                "P103_Q002_H003",
                "P103_Q002_H004",
                "P103_Q002_H005",
                "P103_Q004_H001",
                "P103_Q004_H002",
                "P103_Q004_H003",
                "P103_Q004_H004",
                "P103_Q005_H001",
                "P103_Q005_H002",
                "P103_Q005_H003",
                "P103_Q005_H004",
                "P103_Q005_H005"}) {

            System.out.println(hypothesisID);
            System.out.println("");
            System.out.println("");

            DSLContext annotation = LDCMatching.databaseConnection().get().jooq();
            HashMap<String,String> map = annotation.selectDistinct(Tables.HYPOTHESIS.MENTIONID, Tables.HYPOTHESIS.VALUE)
                    .from(Tables.HYPOTHESIS)
                    .where(Tables.HYPOTHESIS.HYPOTHESIS_ID.eq(hypothesisID))
                    .collect(new Collector<Record2<String, String>, HashMap<String,String>, HashMap<String,String>>() {
                        @Override
                        public Supplier<HashMap<String, String>> supplier() {
                            return HashMap::new;
                        }
                        @Override
                        public BiConsumer<HashMap<String, String>, Record2<String, String>> accumulator() {
                            return (map, stringStringRecord2) -> map.put(stringStringRecord2.value1(), stringStringRecord2.value2());
                        }
                        @Override
                        public BinaryOperator<HashMap<String, String>> combiner() {
                            return new BinaryOperator<HashMap<String, String>>() {
                                @Override
                                public HashMap<String, String> apply(HashMap<String, String> map, HashMap<String, String> map2) {
                                    map.putAll(map2);
                                    return map;
                                }
                            };
                        }
                        @Override
                        public Function<HashMap<String, String>, HashMap<String, String>> finisher() {
                            return x -> x;
                        }
                        @Override
                        public Set<Characteristics> characteristics() {
                            return Collections.emptySet();
                        }
                    });


            ObjectReader reader;
            reader = new ObjectMapper().readerFor(new TypeReference<AgileField>() {});


            HashMultimap<String, AgileRecord> fullyRelevant = HashMultimap.create(), partRelevant = HashMultimap.create(), contradictory = HashMultimap.create();
            loadProperties();
//        DSLContext knowledge_base = Database.openOrCreate(StaticDatabaseClass.engine, databaseId, StaticDatabaseClass.username, StaticDatabaseClass.password).get().jooq();
            TuplesDao dao = new TuplesDao(Database.open(StaticDatabaseClass.engine, databaseId, StaticDatabaseClass.username, StaticDatabaseClass.password).get().jooq().configuration());
            for (Tuples x : dao.fetchByMid(map.keySet().toArray(new String[map.size()]))) {
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
                String value = map.get(x.getMid());
                if (value.equals("contradicts")) {
                    contradictory.put(x.getNisttype(), tup.asAgileRecord(x.getNisttype()));
                } else if (value.equals("fully-relevant")) {
                    fullyRelevant.put(x.getNisttype(), tup.asAgileRecord(x.getNisttype()));
                }else if (value.equals("partially-relevant")) {
                    partRelevant.put(x.getNisttype(), tup.asAgileRecord(x.getNisttype()));
                }
            }
            INCO(fullyRelevant, partRelevant, contradictory);
        }
    }

    private static void INCO(HashMultimap<String, AgileRecord> fullyRelevant, HashMultimap<String, AgileRecord> partRelevant, HashMultimap<String, AgileRecord> contradictory) {
        fullyRelevant.putAll(partRelevant);
        Set<String> commonKeys = new HashSet<>(fullyRelevant.keySet());
        commonKeys.retainAll(contradictory.keySet());
        for (String key : commonKeys) {
            for (AgileRecord ari : fullyRelevant.get(key)) {
                for (AgileRecord arj : contradictory.get(key)) {
                    PartialOrderComparison cmp = LoadFact.comparator.compare(ari, arj);
                    if (cmp.t.equals(POCType.Uncomparable)) {
                        System.out.println("INCO ==== " + ari + "\n\tvs. " + arj);
                    }
                }
            }
        }
    }

    private static void print(HashMultimap<String, AgileRecord> fullyRelevant, HashMultimap<String, AgileRecord> partRelevant, HashMultimap<String, AgileRecord> contradictory) {
        System.out.println("Fully Relevant");
        System.out.println("==============");
        fullyRelevant.asMap().forEach((k,v) -> {
            System.out.println(k);
            v.forEach(y -> System.out.println("\t"+y.toString()));
        });
        System.out.println("");
        System.out.println("");

        System.out.println("Partially Relevant");
        System.out.println("==============");
        partRelevant.asMap().forEach((k,v) -> {
            System.out.println(k);
            v.forEach(y -> System.out.println("\t"+y.toString()));
        });
        System.out.println("");
        System.out.println("");

        System.out.println("Contradictory");
        System.out.println("==============");
        contradictory.asMap().forEach((k,v) -> {
            System.out.println(k);
            v.forEach(y -> System.out.println("\t"+y.toString()));
        });
    }

}
