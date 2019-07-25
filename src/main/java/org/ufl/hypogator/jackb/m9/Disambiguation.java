package org.ufl.hypogator.jackb.m9;

import com.google.common.base.CharMatcher;
import com.ibm.icu.text.Transliterator;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.AbstractVocabulary;
import org.ufl.hypogator.jackb.fuzzymatching.ldc.LDCMatching;
import org.ufl.hypogator.jackb.fuzzymatching.ldc.LDCResult;
import org.ufl.hypogator.jackb.ontology.data.HypoGatorTrimmedMentions;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class Disambiguation {


    public static final Transliterator t = Transliterator.getInstance("Cyrillic-Latin; Latin-Ascii");

    /**
     * If the only text avaliable for some text is the description, I want to use this description.
     * In some cases we have that text descriptions cannot be trimmed with regexes, and they must
     * be truncated. In order to do so, we can set a maximum number of words that can be used to
     * identify the target. This is mainly done to reduce the evaluation time for
     */
    public static final int MAXIMUM_REPRESENTATION = 5;

    public static LDCResult parseForDisplay(String str, boolean hasApproximatedInput, AbstractVocabulary<String> abstractVocabulary, int rec, LDCMatching extremaRatio) {
        if (str == null)
            return null;
        if (str.contains("a.k.a")) {
            int pos = str.indexOf("a.k.a");
            return parseForDisplay(str.substring(0, pos), hasApproximatedInput, abstractVocabulary, rec, extremaRatio);
        } else if (str.contains("- ")) {
            int pos = str.indexOf("- ");
            return parseForDisplay(str.substring(0, pos), hasApproximatedInput, abstractVocabulary, rec, extremaRatio);
        } else if (str.contains("(")) {
            int pos = str.indexOf('(');
            return parseForDisplay(str.substring(0, pos), hasApproximatedInput, abstractVocabulary, rec, extremaRatio);
        } else if (str.contains("[")) {
            int pos = str.indexOf('[');
            return parseForDisplay(str.substring(0, pos), hasApproximatedInput, abstractVocabulary, rec, extremaRatio);
        }

        // In some cases there are too much words
        // The best solution would have been to use NLP to detect the subject of the sentence.
        // TODO
        String[] wc = str.split("\\s+");
        if (wc.length > MAXIMUM_REPRESENTATION) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < MAXIMUM_REPRESENTATION; i++) {
                sb.append(wc[i]);
                if (i != MAXIMUM_REPRESENTATION - 1) sb.append(" ");
            }
            str = sb.toString();
        }

        String tmp = str;
        boolean asHeader = false;
        boolean asSet = false;

        // TODO: Latinization or disambiguation
        if ((hasApproximatedInput && abstractVocabulary != null) || Pattern.matches(".*\\p{InCyrillic}.*", str)) {
            asHeader = true;
            if (abstractVocabulary == null) {
                System.err.println("ERR");
            }
            Map<Double, Collection<String>> mentions = abstractVocabulary.fuzzyMatch(str, 1, 0.0);
            if (!mentions.isEmpty() && rec > 0) {
                // false = keep the disambiguation, without any further discussions
                String resolved = mentions.entrySet().iterator().next().getValue().iterator().next()/*.resolved(rec-1)*/;
                LDCResult strPremise = parseForDisplay(resolved, false, abstractVocabulary, rec-1, extremaRatio);
                asSet = true;
                if (strPremise != null && strPremise.resolved != null)
                    str = strPremise.resolved;
                    // false = keep the disambiguation, without any further discussions
                else if (str != null && resolved != null)
                    str = resolved;
            } else {
                // If I cannot process it anymore, then just use transliteration, and then try to do more disambiguation
                String resolved = mentions.entrySet().iterator().next().getValue().iterator().next()/*.resolved(rec -1)*/;
                str = t.transliterate(resolved);
            }
        }

        LDCResult matched = extremaRatio.bestFuzzyMatch(str);
        return matched == null ? new LDCResult(str) : matched;
    }

    public static LDCResult resolveBasicElement(boolean escapeOnlyAscii, Collection<String> handle, Collection<String> text_string, Collection<String> justification, Collection<String> description, AbstractVocabulary<String> av, int rec, LDCMatching extremaRatio) {
        if (!handle.isEmpty()) {
            Optional<String> optH = handle.stream()
                    .filter(s -> s != null && !s.isEmpty())
                    .filter(x -> escapeOnlyAscii || CharMatcher.ascii().matchesAllOf(x))
                    .min(Comparator.comparingInt(String::length));
            if (optH.isPresent()) return parseForDisplay(optH.get(), escapeOnlyAscii, av, rec-1, extremaRatio);
        }
        if (!text_string.isEmpty()) {
            Optional<String> optH = text_string.stream()
                    .filter(s -> s != null && !s.isEmpty())
                    .filter(x -> escapeOnlyAscii || CharMatcher.ascii().matchesAllOf(x))
                    .min(Comparator.comparingInt(String::length));
            if (optH.isPresent()) return parseForDisplay(optH.get(), escapeOnlyAscii, av, rec-1, extremaRatio);
        }

        if (!justification.isEmpty())
            return parseForDisplay(justification.iterator().next(), escapeOnlyAscii, av, rec-1, extremaRatio);

        if (description.isEmpty()) {
            return null;
        } else{
            Optional<String> optH = description.stream()
                    .filter(s -> s != null && !s.isEmpty())
                    .filter(x -> escapeOnlyAscii || CharMatcher.ascii().matchesAnyOf(x))
                    .min(Comparator.comparingInt(String::length));

            LDCResult elements = null;
            elements = optH.map(x -> parseForDisplay(x, escapeOnlyAscii, av, rec-1, extremaRatio)).orElse(null);
            /*if (elements == null)
                System.err.println("DEBUG -- null");*/
            return elements;
        }
    }

}
