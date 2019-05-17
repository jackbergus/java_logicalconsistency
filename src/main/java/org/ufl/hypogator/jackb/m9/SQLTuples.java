package org.ufl.hypogator.jackb.m9;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.util.Pair;
import org.ufl.hypogator.jackb.inconsistency.AgileField;
import org.ufl.hypogator.jackb.inconsistency.AgileRecord;

import java.util.Set;

/**
 * Deserializing the PostgreSQL representation of the tuples in here
 */
public class SQLTuples {

    @JsonProperty("tupleId")
    public String tupleId;

    @JsonProperty("arguments")
    public AgileField[] tupleFields;


    public AgileRecord asAgileRecord(String type) {
        return asAgileRecord(type, null);
    }

    public AgileRecord asAgileRecord(String type, Set<Pair<String, String>> allowedArguments) {
        AgileRecord toreturn = new AgileRecord(type, allowedArguments);
        for (int i = 0, tupleFieldsLength = tupleFields.length; i < tupleFieldsLength; i++) {
            AgileField x = tupleFields[i];
            if (!x.fieldString.trim().isEmpty())
                toreturn.addField(x);
        }
        toreturn.id = tupleId;
        return toreturn;
    }
}
