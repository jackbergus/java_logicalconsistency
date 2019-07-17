package org.ufl.hypogator.jackb.disambiguation;

import java.util.Collection;

public interface disambiguationFromKB {
    Collection<String> getPossibleCandidatesFor(String entityFillerName, String erType, boolean doReflexivity);
}
