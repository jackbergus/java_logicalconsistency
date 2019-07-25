package org.ufl.hypogator.jackb.inconsistency;

import com.google.common.collect.HashMultimap;
import javafx.util.Pair;
import org.ufl.hypogator.jackb.ontology.TtlOntology;
import org.ufl.hypogator.jackb.ontology.data.TypedValue;

import java.util.*;
import java.util.stream.Collectors;

public class AgileRecord {
    public final String nistType;
    public HashMultimap<String, AgileField> fieldList;
    public List<String> schema;
    public Set<String> mentionsId;
    public HashMultimap<String, String> allowedIdsPerField;
    public String id;

    /**
     * Initializes a new record, with no schema and fields
     * @param nistType  Type associated to the record
     * @param allowedArguments
     */
    public AgileRecord(String nistType, Set<Pair<String, String>> allowedArguments) {
        this.nistType = nistType;
        fieldList = HashMultimap.create();
        schema = new ArrayList<>();
        mentionsId = new HashSet<>();
        if (allowedArguments != null) {
            this.allowedIdsPerField = HashMultimap.create();
            allowedArguments.forEach(x -> this.allowedIdsPerField.put(x.getKey(), x.getValue()));
        } else {
            this.allowedIdsPerField = null;
        }
    }

    public AgileRecord(String nistType) {
        this(nistType, null);
    }

    public AgileRecord(String nistType, HashMultimap<String, AgileField> fieldList, List<String> schema, Set<String> mentionsId) {
        this.nistType = nistType;
        this.fieldList = fieldList;
        this.schema = schema;
        this.mentionsId = mentionsId;
    }

    public void addField(AgileField field) {
        if (field != null && field.fieldName != null && field.fieldType != null && field.fieldString != null &&
                (allowedIdsPerField == null || (allowedIdsPerField.get(field.fieldName).isEmpty()) ||  allowedIdsPerField.get(field.fieldName).contains(field.mid))) {
            fieldList.put(field.fieldName, field);
            if (!schema.contains(field.fieldName)) schema.add(field.fieldName);
        }
    }

    public void addField(String fieldName, String fieldType, String fieldString, boolean typeFromFuzzyMatch) {
        if (fieldName != null && fieldType != null && fieldString != null) {
            fieldList.put(fieldName, new AgileField(fieldName, fieldType, fieldString, typeFromFuzzyMatch, null));
            if (!schema.contains(fieldName)) schema.add(fieldName);
        }
    }

    public String toString() {
        return nistType+fieldList.asMap().entrySet().stream().map(x -> x.getValue().stream().map(y -> (y.typeFromFuzzyMatch ? y.fieldString.toUpperCase() : y.fieldString.toLowerCase()) +":"+y.fieldType).collect(Collectors.joining(", ", x.getKey()+"=[", "]"))).collect(Collectors.joining("; ", "(", ")"))+" ["+id+"]"/*+" "+mentionsId*/;
    }

    public List<String> asArrayList() {
        ArrayList<String> csvArray = new ArrayList<>();
        csvArray.add(nistType);
        fieldList.asMap().forEach((k, v) -> {
            csvArray.add(k+"= "+v.stream().map(y -> y.fieldString).collect(Collectors.joining(", ")));
        });
        while (csvArray.size() <= 6)
            csvArray.add("");
        return csvArray;
    }

    public void addField(String label, TypedValue value) {
        if (value != null && value.value() != null) {
            String type = value.value().nistType != null ? value.value().nistType : value.type();
            String valueResolved = value.value().resolved;
            boolean fromFuzzyMatch = value.doesTypeComesFromFuzzyMatch();
            addField(label, type, valueResolved, fromFuzzyMatch);
        }
    }

    public void setSimilarMentionsId(Set<String> similarMentionIds) {
        this.mentionsId = (similarMentionIds);
    }

    public int size() {
        return schema.size();
    }

    public Set<AgileField> ith(int j) {
        return fieldList.get(schema.get(j));
    }

    public double getDegreeTypeInconsistency(TtlOntology ontology, String tupleId) {
        double count = 0;
        for (int i = 0, n = size(); i<n; i++) {
            for (AgileField field : ith(i)) {
                if (!ontology.isTypeAllowedInField(this.nistType, field)) {
                    count++;
                    // System.out.println("Mention Id " + tupleId + " of type = " + this.nistType + ", wrong field= "+field);
                    //System.out.println(this.nistType + " =/> "+field);
                }
            }
        }
        return count == 0 ? 0 : 1.0/(count);
    }

    public AgileRecord projectWith(List<String> schemas2) {
        HashMultimap<String, AgileField> laf = HashMultimap.create(schemas2.size(), 1);
        List<String> newschema = new ArrayList<>(schemas2.size());

        for (String x : schemas2) {
            //int pos = schema.indexOf(x);
            Set<AgileField> ls = fieldList.get(x);
            if (ls != null && !ls.isEmpty()) {
                if (!newschema.contains(x)) newschema.add(x);
                laf.putAll(x, ls);
            }
        }
        return new AgileRecord(this.nistType, laf, newschema, this.mentionsId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgileRecord that = (AgileRecord) o;
        return Objects.equals(nistType, that.nistType) &&
                Objects.equals(fieldList, that.fieldList) /*&&
                Objects.equals(schema, that.schema)*/;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nistType, fieldList/*, schema*/);
    }
}
