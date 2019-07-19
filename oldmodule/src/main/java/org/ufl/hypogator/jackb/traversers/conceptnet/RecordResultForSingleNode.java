package org.ufl.hypogator.jackb.traversers.conceptnet;

import org.ufl.hypogator.jackb.scraper.ScraperSources;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetDimensionDisambiguationOperations.unrectify;

public class RecordResultForSingleNode implements Serializable {
    public final String id;
    private EdgeVertex parent;
    public final String[] strings;
    public static final long serialVersionUID = 9061169524859839802L;

    public String[] getStrings() {
        return strings;
    }

    public EdgeVertex getParent() {
        if (parent == null) {
            this.parent = new EdgeVertex();
            this.parent.setGeneratingSource(ScraperSources.CONCEPTNET);
            this.parent.fromConceptNet(id, strings);
        }
        return parent;
    }

    public RecordResultForSingleNode(String id, String[] strings, EdgeVertex parent) {
        this.id = id;
        this.parent = parent;
        this.strings = strings == null ?
                new String[]{unrectify(id)} :
                Arrays.stream(strings).filter(s -> (s != null && s.length() > 0)).toArray(String[]::new);
    }

    public RecordResultForSingleNode(String id, Object[] strings) {
        this.id = id;
        List<String> array = new ArrayList<>(strings == null ? 0 : strings.length);
        if (strings != null) {
            for (int i = 0, stringsLength = strings.length; i < stringsLength; i++) {
                String o = (String)strings[i];
                if (o != null) {
                    o = o.trim();
                    if (o.length() > 0)
                        array.add(o);
                }
            }
        }
        this.strings = array.isEmpty() ? new String[]{unrectify(id)} : array.toArray(new String[array.size()]);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecordResultForSingleNode that = (RecordResultForSingleNode) o;
        return Objects.equals(id, that.id) &&
                Arrays.equals(strings, that.strings);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id);
        result = 31 * result + Arrays.hashCode(strings);
        return result;
    }

    @Override
    public String toString() {
        return "RecordResultForSingleNode{" +
                "id='" + id + '\'' +
                ", strings=" + Arrays.toString(strings) +
                '}';
    }
}
