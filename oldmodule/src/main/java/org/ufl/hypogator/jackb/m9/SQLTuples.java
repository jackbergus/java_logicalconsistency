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

    @JsonProperty("negated")
    public Boolean negated;

    @JsonProperty("hedged")
    public Boolean hedged;

    @JsonProperty("score")
    public Double score;


    public AgileRecord asAgileRecord(String type) {
        return asAgileRecord(type, null);
    }

    public AgileRecord asAgileRecord(String type, Set<Pair<String, String>> allowedArguments) {
        AgileRecord toreturn = new AgileRecord(type, allowedArguments);
        for (int i = 0, tupleFieldsLength = tupleFields.length; i < tupleFieldsLength; i++) {
            AgileField x = tupleFields[i];
            if (!x.fieldString.trim().isEmpty()) // adding the field if and only if it is not empty. Therefore, I cannot detect beforehand whether the elements are empty elements or not
                toreturn.addField(x);
        }
        toreturn.id = tupleId;
        toreturn.negated = negated;
        toreturn.hedged = hedged;
        toreturn.score = score;
        return toreturn;
    }
}
