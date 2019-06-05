package org.ufl.hypogator.jackb.m9;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashMultimap;
import org.jooq.Condition;
import org.jooq.Record3;
import org.jooq.util.postgres.PostgresDSL;
import org.postgresql.util.PGobject;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.Database;
import org.ufl.aida.ta2.Tables;
import org.ufl.hypogator.jackb.comparators.partialOrders.InformationPreservingComparator;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetVocabulary;
import org.ufl.hypogator.jackb.inconsistency.legacy.TupleComparator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class CompareOnce {

    public static void selectiveFieldMemoizationToDisk(Database database, String type) {
        InformationPreservingComparator<String> cmp = TupleComparator.generateFromType(type);
        selectiveTypeMemoization(database, type, null);
        cmp.serializeToDisk(new File(type+"_memoization.jbin"));
    }

    public static void memoizeFieldComparisons(Database database) {
        memoizeFieldComparisons(database, null, null);
    }

    /**
     * This class performs the comparisons of the elemtns only on the fields.
     * The fields for now are just time and space information.
     * Those dimensions do not use ConceptNet for querying and they all use different techniques, and therefore
     * their execution can be parallelized
     *
     * @param database      Database where the tuples are stored
     */
    public static void memoizeFieldComparisons(Database database, String type, String fieldToSearch) {
        HashMultimap<InformationPreservingComparator<String>, String> hmm = HashMultimap.create();
        for (String field : TupleComparator.relevantFields) {
            if (fieldToSearch != null && (!field.equals(fieldToSearch))) continue;
            InformationPreservingComparator<String> comparator = TupleComparator.generateFromField(field);
            if (type != null && !comparator.getName().equals(type)) continue;
            hmm.put(comparator, field);
        }
        hmm.asMap()
                .entrySet()
                .parallelStream()
                .forEach(cp -> {
                    List<String> elements = new ArrayList<>();
                    Condition[] arrays = new Condition[cp.getValue().size()];
                    {
                        int i = 0;
                        for (String v : cp.getValue()) {
                            if (v == null || v.isEmpty()) continue;
                            elements.add(v);
                            arrays[i++] = Tables.FACT.PARTIALLABEL.like("%"+v);
                        }
                    }
                    // RESOLVEDNAME -- instead of ARGUMENTRAWSTRING
                    database
                            .jooq()
                            .select(Tables.FACT.NISTTYPE, Tables.FACT.PARTIALLABEL, PostgresDSL.arrayAggDistinct(Tables.FACT.RESOLVEDNAME))
                            .from(Tables.FACT)
                            .where(PostgresDSL.or(arrays))
                            .groupBy(Tables.FACT.NISTTYPE, Tables.FACT.PARTIALLABEL)
                            .fetch()
                            .stream()
                            .map(Record3::value3)
                            .forEach(array -> {
                                int N = array.length;
                                long count = 0;
                                System.err.println("#"+cp.getKey().getName()+" = "+N+" "+elements);
                                for (int i = 0; i<N; i++) {
                                    for (int j = 0; j<i; j++) {
                                        cp.getKey().compare(array[i], array[j]);
                                        count++;
                                        if (count % 100 == 0)
                                            System.out.println(count+" for "+cp.getKey().getName());
                                    }
                                }
                            });
                });

    }

    //selectiveTypeMemoization
    public static void memoizeTypeComparisons(Database database) {
        selectiveTypeMemoization(database, null, null);
    }

    public static void selectiveTypeMemoizationToDisk(Database database, String type, File folderFile, String tuplesType) {
        InformationPreservingComparator<String> cmp = TupleComparator.generateFromType(type);
        selectiveTypeMemoization(database, type, tuplesType);
        cmp.serializeToDisk(new File(folderFile, type));
        cmp.close(); // Forcing to close the file descriptors, otherwise the processes are going to be without memory mapping addresses. The OS won't close them automatically when Java is closed.
    }

    public static void selectiveTypeMemoization(Database database, String filterer, String tuplesType) {

        // Reading one call
        ConceptNetVocabulary.readDefaultVocabulary();

        String element = null;
        try {
            element = String.join(" ", Files.readAllLines(new File("sql/TA204_tupleDedup.sql").toPath()));
        } catch (IOException e) {
            e.printStackTrace();
            element = "";
        }

        /*Condition[] arrays = new Condition[TupleComparator.relevantFields.length];
        {
            String[] relevantFields = TupleComparator.relevantFields;
            for (int i = 0, relevantFieldsLength = relevantFields.length; i < relevantFieldsLength; i++) {
                String x = relevantFields[i];
                arrays[i] = PostgresDSL.and(PostgresDSL.not(Tables.FACT.PARTIALLABEL.like("%"+x)),
                                            PostgresDSL.not(Tables.FACT.RESOLVEDTYPE.like("%"+x)),
                                            PostgresDSL.not(Tables.FACT.RNISTNAME.like("%"+x)));
            }
        }*/

        database
                .jooq()
                // Reducing the number of comparisons: getting all the possible arguments that appear argumentwise

                .fetch(element)
                /*
select "resolvedType", array_agg(arrays)
from (
select "resolvedType", json_build_object('nistType',"nistType", 'rn',array_agg(distinct "resolvedName")) as arrays
from fact
group by "nistType", "resolvedType") as Tauple
group by
"resolvedType"
                 */
                 /*
                .select(Tables.FACT.NISTTYPE, PostgresDSL.coalesce(Tables.FACT.RESOLVEDTYPE, Tables.FACT.RNISTNAME), PostgresDSL.arrayAgg(Tables.FACT.RESOLVEDNAME))
                .from(Tables.FACT)
                .where(PostgresDSL.and(arrays))
                .groupBy(Tables.FACT.NISTTYPE, Tables.FACT.RESOLVEDTYPE, Tables.FACT.RNISTNAME)
                */

                 // ALERT: this part cannot be done concurrently. JNI has some problems with dealing with concurrent C++ code, even though we do readonly accesses.
                .stream()
                .filter(cp -> filterer == null || cp.get("type").toString().equals(filterer))
                .forEach(cp -> {
                    String type = cp.get("type").toString();
                    // Returning all the elements that appear in the hierarchy, with an associated graph and
                    // a serialized fuzzy matching element
                    InformationPreservingComparator<String> cmp = TupleComparator.generateFromType(type);

                    // If I have no comparator for that, just ignore the comparison
                    if (cmp == null) return;

                    {
                        Object[] pj = (Object[])cp.get("arrays");
                        try {
                            for (Object pjo : pj) {
                                ArrayObject o = new ObjectMapper().readValue(((PGobject)pjo).getValue(), ArrayObject.class);
                                if (tuplesType != null && (!tuplesType.equals(o.nistType))) continue;
                                // Getting all the strings associated to that
                                String[] ls = o.rn;
                                {   // Now, reducing the number of the overall comparisons by comparing only distinct elements
                                    ArrayList<String> reducedComparisons = new ArrayList<>();
                                    for (String x : ls) if (x != null && (!x.isEmpty())) reducedComparisons.add(x);
                                    ls = reducedComparisons.toArray(new String[reducedComparisons.size()]);
                                }
                                if (ls.length <= 1) return;
                                int N = ls.length;

                                // Performing the comparisons argumentwise
                                System.err.println("#"+type+" = "+N+" for tuples of type "+o.nistType);
                                for (int k = 0; k<N; k++) {
                                    for (int j = 0; j<k; j++) {
                                        System.out.println(ls[k]+" -- "+ls[j]);
                                        cmp.compare(ls[k], ls[j]);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }


                });
    }


    public static class ArrayObject {
        @JsonProperty("nistType")
        public String nistType;

        @JsonProperty("rn")
        public String rn[];

        @JsonCreator
        public ArrayObject(@JsonProperty("nistType") String nistType, @JsonProperty("rn") String[] rn) {
            this.nistType = nistType;
            this.rn = rn;
        }
    }


}
