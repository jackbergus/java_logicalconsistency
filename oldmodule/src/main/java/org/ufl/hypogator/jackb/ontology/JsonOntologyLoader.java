package org.ufl.hypogator.jackb.ontology;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.ConfigurationEntrypoint;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class JsonOntologyLoader {

    public Ontology self;
    private  HashMap<String, String> ldcToNist_map, nistToLDC_map;
    private HashMultimap<String, TypeSubtype> typeResolve;
    private HashMultimap<String, TypeSubtype> labelAcceptedArgumentResolve;
    private Set<String> entityOrFillers;
    private static JsonOntologyLoader l;

    public JsonOntologyLoader() throws IOException {
        ConfigurationEntrypoint instance = ConfigurationEntrypoint.getInstance();
        self = new ObjectMapper()
                .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
                .readerFor(Ontology.class)
                .readValue(instance.ontologyFile);
        ldcToNist_map = new HashMap<>();
        nistToLDC_map = new HashMap<>();
        typeResolve = HashMultimap.create();
        labelAcceptedArgumentResolve = HashMultimap.create();
        entityOrFillers = new HashSet<>();

        boolean isEntityOrFiller;
        for (Sort s : self.getSorts()) {
            isEntityOrFiller = s.equals(self.entity) || s.equals(self.filler);
            if (s.hasTypes != null)
            for (TypeSubtype ts : s.hasTypes) {
                if (isEntityOrFiller)
                    entityOrFillers.add(ts.nistName);
                addType(ts.nistName, ts);
                addType(s.name, ts);
                ldcToNist(ts.ldcName, ts.nistName);
            }
        }

        for (Sort s : self.getSorts()) {
            if (s.hasTypes != null)
                for (TypeSubtype ts : s.hasTypes) {
                    if (ts.argumentTypes != null)
                    for (Label label : ts.argumentTypes) {
                        //if (label.nistName.equals("GeneralAffiliation.Sponsorship.Entity"))
                        //    System.out.println("DEBUG");
                        if (label.allowedLDCTypes != null)
                        for (String ldc : label.allowedLDCTypes) {
                            String nist = resolveLDCToNist(ldc);
                            labelAcceptedArgumentResolve.putAll(label.nistName, resolveNISTTypes(nist));
                        }
                    }
                }
        }
    }


    public static JsonOntologyLoader getInstance() {
        if (l == null) {
            try {
                return (l = new JsonOntologyLoader());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return l;
    }


    /*public IteratorWithOperations<RawEventRelationship> asBackwardRepresentation() {
        return UnionIterator
                .with(new AlgebraSupport<>(new ArraySupport<>(self.event.hasTypes)).map(TypeSubtype::backwardCompatibility))
                .with(new AlgebraSupport<>(new ArraySupport<>(self.relation.hasTypes)).map(TypeSubtype::backwardCompatibility))
                .done();
    }*/

    // Ontology queries

    public Set<String> getEntityOrFillers() {
        return entityOrFillers;
    }

    public TypeSubtype resolveSingleLDCType(String ldcType) {
        return resolveSingleNISTType(resolveLDCToNist(ldcType));
    }

    public TypeSubtype resolveSingleNISTType(String nistType) {
        Set<TypeSubtype> ls = typeResolve.get(nistType);
        return ls == null || ls.isEmpty() ? null : ls.iterator().next();
    }

    /**
     * If the string is a category, then returns the set of all the possible types/subtypes associated to it.
     * Otherwise, it resolves the string
     *
     * @param nistType
     * @return
     */
    public Set<TypeSubtype> resolveNISTTypes(String nistType) {
        return typeResolve.get(nistType);
    }

    public String resolveLDCToNist(String ldcType) {
        String toget = ldcToNist_map.get(ldcType);
        return toget == null ? ldcType : toget;
    }

    public Set<TypeSubtype> getTypesForLabel(String nistLabel) {
        return labelAcceptedArgumentResolve.get(nistLabel);
    }

    // Initialization operations

    private void ldcToNist(String ldcName, String nistName) {
        ldcToNist_map.put(ldcName, nistName);
        nistToLDC_map.put(nistName, ldcName);
    }

    private void addType(String nistName, TypeSubtype typeSubtype) {
        typeResolve.put(nistName, typeSubtype);
    }

    private void addType(String nistName, Collection<TypeSubtype> typeSubtype) {
        typeResolve.putAll(nistName, typeSubtype);
    }

    public static void main(String args[]) {
        JsonOntologyLoader loader = JsonOntologyLoader.getInstance();
        System.out.println(loader.getTypesForLabel("GeneralAffiliation.Sponsorship.Entity").stream().map(x -> x.nistName).collect(Collectors.joining(",\n")));
    }
}
