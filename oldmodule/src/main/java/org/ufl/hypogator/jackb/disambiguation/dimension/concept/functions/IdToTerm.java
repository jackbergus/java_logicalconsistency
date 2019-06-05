package org.ufl.hypogator.jackb.disambiguation.dimension.concept.functions;

import java.util.Set;
import java.util.function.Function;

public class IdToTerm implements Function<String, String> {

    Set<String> languages;
    boolean splitWithUnderscore;

    public IdToTerm(Set<String> languages, boolean splitWithUnderscore) {
        this.languages = languages;
        this.splitWithUnderscore = splitWithUnderscore;
    }

    public String apply(String id) {
        String args[] = id.split("/");
        if (args.length > 3) {
            if (languages == null || languages.contains(args[2])) {
                if (splitWithUnderscore) {
                    args[3] = args[3].replace('_', ' ');
                }
                return args[3];
            }
        }
        return null;
    }

}
