package org.ufl.hypogator.jackb.m18;

import com.ibm.icu.text.Transliterator;
import org.ufl.aida.ta2.tables.records.MentionsForUpdateRecord;
import org.ufl.hypogator.jackb.fuzzymatching.ldc.LDCMatching;
import org.ufl.hypogator.jackb.fuzzymatching.ldc.LDCResult;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

class DisambiguateListOfStringsViaLDC {
    private boolean myResult;
    private LDCMatching ldcDisambiguator;
    private MentionsForUpdateRecord x;
    private List<String> singleList;
    private LDCResult res;
    private String resolved;
    private boolean fromFuzzyMatching;
    public static final Transliterator t = Transliterator.getInstance("Cyrillic-Latin; Latin-Ascii");

    public DisambiguateListOfStringsViaLDC(LDCMatching ldcDisambiguator, MentionsForUpdateRecord x, List<String> singleList) {
        this.ldcDisambiguator = ldcDisambiguator;
        this.x = x;
        this.singleList = singleList;
    }

    boolean is() {
        return myResult;
    }

    public LDCResult getRes() {
        return res;
    }

    public String getResolved() {
        return resolved;
    }

    public boolean isFromFuzzyMatching() {
        return fromFuzzyMatching;
    }

    public DisambiguateListOfStringsViaLDC invoke() {
        double longest;
        longest = singleList.stream().mapToDouble(String::length).max().orElse(1.0);
        res = null;
        double stringLength = 0;
        double stringLengthReliability = 0.7;
        double scoreReliability = 1.0 - stringLengthReliability;

        if (singleList.isEmpty()) {
            x.delete();
            myResult = true;
            return this;
        }

        for (String y : singleList) {
            LDCResult result = null;
            if (ldcDisambiguator != null)
                result = ldcDisambiguator.bestFuzzyMatch(y);
            else
                result = new LDCResult(y);
            if (res == null) {
                stringLength = y.length();
                res = result;
            } else if ((res.score * scoreReliability + (stringLength/longest) * stringLengthReliability) < (result.score * scoreReliability + (y.length()/longest) * stringLengthReliability)) {
                stringLength = y.length();
                res = result;
            }
        }
        resolved = res.resolved;
        fromFuzzyMatching = res.nistType != null;

        // Attempting to use transliteration when the fuzzy match fails
        if (/*resolved.equals(x.getArgumentrawstring()) &&*/ Pattern.matches(".*\\p{InCyrillic}.*", resolved) && res.kbId == null) {
            HashMap<String, AtomicInteger> mm = new HashMap<>();
            for (String y : x.getEnstrings()) {
                if (y == null || y.equals("NA")) continue;
                y = y.trim();
                if (y.isEmpty()) continue;
                AtomicInteger ai = mm.get(y);
                if (ai == null) {
                    ai = new AtomicInteger(0);
                    mm.put(y, ai);
                }
                ai.incrementAndGet();
            }
            Optional<Map.Entry<String, AtomicInteger>> opt = mm.entrySet().stream().max(Comparator.comparingInt(o -> o.getValue().get()));
            if (opt.isPresent()) {
                resolved = opt.get().getKey();
            } else {
                resolved = t.transliterate(resolved);
            }
            fromFuzzyMatching = false;

            LDCResult result = null;
            if (ldcDisambiguator != null)
                result = ldcDisambiguator.bestFuzzyMatch(resolved);
            if (result == null || result.kbId == null) {
                fromFuzzyMatching = false;
            } else {
                fromFuzzyMatching = true;
                res = result;
                resolved = result.resolved;
            }
        }
        myResult = false;
        return this;
    }
}
