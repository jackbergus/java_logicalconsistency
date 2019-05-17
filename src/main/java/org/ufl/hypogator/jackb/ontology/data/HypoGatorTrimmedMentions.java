package org.ufl.hypogator.jackb.ontology.data;

import com.ibm.icu.text.Transliterator;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.AbstractVocabulary;
import org.ufl.hypogator.jackb.fuzzymatching.ldc.LDCResult;
import org.ufl.hypogator.jackb.ontology.JsonOntologyLoader;
import org.ufl.hypogator.jackb.ontology.data.tuples.projections.AIDATuple;
import com.google.common.base.CharMatcher;
import org.ufl.hypogator.jackb.streamutils.data.DataIterator;
import org.ufl.hypogator.jackb.streamutils.data.Tuple;
import org.ufl.hypogator.jackb.streamutils.data.Value;
import org.ufl.hypogator.jackb.streamutils.functions.ObjectCombiner;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;
import org.ufl.hypogator.jackb.streamutils.iterators.operations.UnionIterator;

import java.util.*;

import static org.ufl.hypogator.jackb.ontology.data.tuples.projections.AIDATuple.parseForDisplay;

public class HypoGatorTrimmedMentions implements ObjectCombiner<HypoGatorTrimmedMentions>, TypedValue {
    public Set<String> kbIds;
    private Set<String> attributes, handle, description, justification, text_string;
    public Set<String> mentionedInId;
    public Set<String> mentionId;
    public String[] precomputedElements;
    public final String dimension;
    private boolean escapeOnlyAscii;
    private final AbstractVocabulary<HypoGatorTrimmedMentions> av;
    private String type;
    private boolean fromFuzzyMatch = false;

    public boolean isEscapeOnlyAscii() {
        return escapeOnlyAscii;
    }

    public IteratorWithOperations<String> arguments() {
        return UnionIterator
                .with(new DataIterator<>(handle))
                .with(new DataIterator<>(description))
                .with(new DataIterator<>(justification))
                .with(new DataIterator<>(text_string))
                .done();
    }

    public String[] getPrecomputedElements() {
        if (precomputedElements == null) {
            Set<String> sets = new HashSet<>();
            arguments().forEachRemaining(x -> {
                if (x != null && x.trim().length() > 0) {
                    sets.add(x);
                }
            });
            precomputedElements = sets.toArray(new String[sets.size()]);
        }
        return precomputedElements;
    }

    public HypoGatorTrimmedMentions(Tuple t, AbstractVocabulary<HypoGatorTrimmedMentions> av) {
        this(t, false, av);
    }

    public HypoGatorTrimmedMentions(Tuple t, boolean escapeOnlyAscii, AbstractVocabulary<HypoGatorTrimmedMentions> av) {
        this.escapeOnlyAscii = escapeOnlyAscii;
        this.av = av;
        kbIds = new HashSet<>();
        attributes = new HashSet<>();

        this.dimension = t.get("dimension").getAtomAsString();
        Value kb = t.remove("kb_id2");
        if (kb != null && !kb.isEmptyValue()) {
            kbIds.add(kb.getAtomAsString());
        }
        kb = t.get("kb_id3");
        if (kb != null && !kb.isEmptyValue()) {
            kbIds.add(kb.getAtomAsString());
        }

        kb = t.remove("attribute");
        int n = kb.getArraySize();
        for (int i = 0; i < n; i++) {
            attributes.add(kb.getSubValue(i).getAtomAsString());
        }
        kb = t.remove("attribute2");
        n = kb.getArraySize();
        for (int i = 0; i < n; i++) {
            attributes.add(kb.getSubValue(i).getAtomAsString());
        }

        type = t.remove("type").getAtomAsString();
        mentionedInId = new HashSet<>();
        mentionedInId.addAll(t.remove("id").asStringList());
        mentionedInId.removeIf(x -> x == null || x.trim().isEmpty());
        handle = new HashSet<>();
        handle.addAll(t.remove("handle").asStringList());
        handle.removeIf(x -> x == null || x.trim().isEmpty());
        description = new HashSet<>();
        description.addAll(t.remove("description").asStringList());
        description.removeIf(x -> x == null || x.trim().isEmpty());
        justification = new HashSet<>();
        justification.addAll(t.remove("justification").asStringList());
        justification.removeIf(x -> x == null || x.trim().isEmpty());
        text_string = new HashSet<>();
        text_string.addAll(t.remove("text_string").asStringList());
        text_string.removeIf(x -> x == null || x.trim().isEmpty());
    }

    public HypoGatorTrimmedMentions(String dimension, String kbIds, String mentionedId, String handle, AbstractVocabulary<HypoGatorTrimmedMentions> av, Object... justification) {
        this.escapeOnlyAscii = true;
        this.av = av;
        this.kbIds = new HashSet<>();
        this.kbIds.add(kbIds);
        this.mentionedInId = new HashSet<>();
        this.mentionedInId.add(mentionedId);
        this.dimension = dimension;
        this.handle = new HashSet<>();
        this.handle.add(handle);
        this.justification = new HashSet<>();
        for (int i = 0, justificationLength = justification.length; i < justificationLength; i++) {
            Object x = justification[i];
            this.justification.add(x.toString());
        }
        text_string = new HashSet<>();
        description = new HashSet<>();
        this.type = JsonOntologyLoader.getInstance().resolveNISTTypes(dimension).iterator().next().kind;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "HypoGatorTrimmedMentions{" +
                "kbIds=" + kbIds +
                ", attributes=" + attributes +
                ", handle=" + handle +
                ", description=" + description +
                ", justification=" + justification +
                ", text_string=" + text_string +
                ", mentionId=" + mentionedInId +
                ", dimension='" + dimension + '\'' +
                '}';
    }

    @Override
    public HypoGatorTrimmedMentions apply(HypoGatorTrimmedMentions x) {
        kbIds.addAll(x.kbIds);
        mentionedInId.addAll(x.mentionedInId);
        attributes.addAll(x.attributes);
        handle.addAll(x.handle);
        description.addAll(x.description);
        justification.addAll(x.justification);
        text_string.addAll(x.text_string);
        return this;
    }

    /*public static LDCResult resolveBasicElement(boolean escapeOnlyAscii, Value handle, Value text_string, Value justification, Value description, AbstractVocabulary<HypoGatorTrimmedMentions> av) {
        return resolveBasicElement(escapeOnlyAscii, handle.asStringList(), text_string.asStringList(), justification.asStringList(), description.asStringList(), av);
    }*/

    public static LDCResult resolveBasicElement(boolean escapeOnlyAscii, Collection<String> handle, Collection<String> text_string, Collection<String> justification, Collection<String> description, AbstractVocabulary<HypoGatorTrimmedMentions> av, int rec) {
        if (!handle.isEmpty()) {
            Optional<String> optH = handle.stream()
                    .filter(s -> s != null && !s.isEmpty())
                    .filter(x -> escapeOnlyAscii || CharMatcher.ascii().matchesAllOf(x))
                    .min(Comparator.comparingInt(String::length));
            if (optH.isPresent()) return parseForDisplay(optH.get(), escapeOnlyAscii, av, rec-1);
        }
        if (!text_string.isEmpty()) {
            Optional<String> optH = text_string.stream()
                    .filter(s -> s != null && !s.isEmpty())
                    .filter(x -> escapeOnlyAscii || CharMatcher.ascii().matchesAllOf(x))
                    .min(Comparator.comparingInt(String::length));
            if (optH.isPresent()) return parseForDisplay(optH.get(), escapeOnlyAscii, av, rec-1);
        }

        if (!justification.isEmpty())
            return parseForDisplay(justification.iterator().next(), escapeOnlyAscii, av, rec-1);

        if (description.isEmpty()) {
            return null;
        } else{
            Optional<String> optH = description.stream()
                    .filter(s -> s != null && !s.isEmpty())
                    .filter(x -> escapeOnlyAscii || CharMatcher.ascii().matchesAnyOf(x))
                    .min(Comparator.comparingInt(String::length));

            LDCResult elements = null;
            elements = optH.map(x -> AIDATuple.parseForDisplay(x, escapeOnlyAscii, av, rec-1)).orElse(null);
            /*if (elements == null)
                System.err.println("DEBUG -- null");*/
            return elements;
        }
    }

    LDCResult result = null;
    boolean resolved = false;
    @Override
    public LDCResult value() {
        if (!resolved) {
            result = resolveBasicElement(escapeOnlyAscii, handle, text_string, justification, description, av, 4);
            resolved = true;
        }
        return result;
    }

    public LDCResult value(int rec) {
        if (resolved) return result;
        result = resolveBasicElement(escapeOnlyAscii, handle, text_string, justification, description, av, rec-1);
        return result;
    }

    public String resolved() {
        LDCResult res = value();
        return res != null ? res.resolved : null;
    }

    public String resolved(int rec) {
        LDCResult res = value(rec);
        return res != null ? res.resolved : null;
    }

    @Override
    public String type() {
        return type;
    }

    @Override
    public void setTypeComingFromFuzzyMatch() {
        this.fromFuzzyMatch = true;
    }

    @Override
    public boolean doesTypeComesFromFuzzyMatch() {
        return this.fromFuzzyMatch;
    }

    @Override
    public void setType(String nistType) {
        this.type = nistType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HypoGatorTrimmedMentions that = (HypoGatorTrimmedMentions) o;
        return Objects.equals(kbIds, that.kbIds) &&
                Objects.equals(attributes, that.attributes) &&
                Objects.equals(handle, that.handle) &&
                Objects.equals(description, that.description) &&
                Objects.equals(justification, that.justification) &&
                Objects.equals(text_string, that.text_string) &&
                Objects.equals(mentionedInId, that.mentionedInId) &&
                Objects.equals(dimension, that.dimension);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kbIds, attributes, handle, description, justification, text_string, mentionedInId, dimension);
    }
}
