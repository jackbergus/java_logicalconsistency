package it.giacomobergami.m18.graph_run;

import com.google.common.collect.HashMultimap;
import it.giacomobergami.m18.configuration.QueryGenerationConfiguration;
import org.ufl.hypogator.jackb.disambiguation.disambiguationFromKB;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;
import types.Schema;

import java.util.*;
import java.util.stream.Collectors;

/**
 * This class provides the expansion for the elements that are not in the hierarchy, and for which a consistent typing
 * could be only inferred from the extracted data.
 */
public class ExpansionForApproximation implements disambiguationFromKB {

    private static QueryGenerationConfiguration qgc = QueryGenerationConfiguration.getInstance();

    final HashMultimap<String, AgileRecord> agileElements;

    /**
     *
     * @param agileElements     Current map extracted from the current Hypothesis
     */
    public ExpansionForApproximation(HashMultimap<String, AgileRecord> agileElements) {
        this.agileElements = agileElements;
    }

    @Override
    public Collection<String> getPossibleCandidatesFor(String entityFillerName, String erType, boolean doReflexivity) {
        Set<String> arrayList = new HashSet<>();
        arrayList.add(entityFillerName);

        Optional<Schema> schema = qgc.getSchemaDefinition(erType);
        if (schema != null && schema.isPresent()) {
            ArrayList<String> arguments = schema.get().arguments;
            if (arguments.size() >= 2) {
                String keyField = arguments.get(0);
                String valueField = arguments.get(1);

                keyFromValue(entityFillerName, erType, arrayList, keyField, valueField);
                if (doReflexivity) {
                    keyFromValue(entityFillerName, erType, arrayList, valueField, keyField);
                }
            }
        }

        return arrayList;
    }

    public void keyFromValue(String entityFillerName, String erType, Set<String> arrayList, String keyField, String valueField) {
        for (AgileRecord record : agileElements.get(erType)) {
            //System.out.println(record.fieldList.get(valueField).stream().map(x->x.fieldString).collect(Collectors.joining()));
            if (record.fieldList.get(keyField).stream().anyMatch(x -> x.fieldString.toLowerCase().equals(entityFillerName.toLowerCase()))) {
                record.fieldList.get(valueField).forEach(x -> arrayList.add(x.fieldString));
            }
        }
    }

}
