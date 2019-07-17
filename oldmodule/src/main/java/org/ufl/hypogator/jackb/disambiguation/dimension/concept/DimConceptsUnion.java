package org.ufl.hypogator.jackb.disambiguation.dimension.concept;

import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.comparators.partialOrders.POCType;
import org.ufl.hypogator.jackb.comparators.partialOrders.PartialOrderComparison;
import org.ufl.hypogator.jackb.comparators.partialOrders.policy.DisambiguationPolicy;
import org.ufl.hypogator.jackb.comparators.partialOrders.policy.DisambiguationPolicyFactory;
import org.ufl.hypogator.jackb.disambiguation.dimension.Dimension;
import org.ufl.hypogator.jackb.ontology.JsonOntologyLoader;
import org.ufl.hypogator.jackb.ontology.TypeSubtype;
import org.ufl.hypogator.jackb.m9.configuration.Concept5ClientConfigurations;

import java.io.File;
import java.util.*;

public class DimConceptsUnion  extends Dimension<ResolvedConcept, InformativeConcept> {
    private final static Collection<String> availableTypes = Concept5ClientConfigurations.instantiate().conceptnetResolvableTypes();
    private final static Boolean forceUnion = ConfigurationEntrypoint.getInstance().forceUnion;
    private final static JsonOntologyLoader l = JsonOntologyLoader.getInstance();
    private final static DisambiguationPolicy policy = DisambiguationPolicyFactory.getInstance().getPolicy(ConfigurationEntrypoint.getInstance().disambiguationPolicy);

    public final String fieldName;
    private final Set<DimConcepts> concepts;

    public DimConceptsUnion(String fieldName) {
        super(null, null);
        this.fieldName = fieldName;
        concepts = new HashSet<>();
        if (forceUnion) {
            concepts.add(new DimConcepts("Union"));
        } else {
            // Deprecated behaviour
            // Loading all the types associated to the label
            for (TypeSubtype x : l.getTypesForLabel(fieldName)) {
                if (availableTypes.contains(x.nistName))
                    concepts.add(new DimConcepts(x.nistName));
            }
        }
    }

    @Override
    public PartialOrderComparison nonNullCompare(String left, String right) {
        if (concepts.isEmpty()) {
            if (left.equals(right)) {
                //System.out.println("P("+left + " == "+ right+"|nodata)=1.0");
                return PartialOrderComparison.PERFECT_EQUAL;
            } else {
                //System.out.println("P("+left + " n.c. "+ right+"|nodata)=1.0");
                return PartialOrderComparison.PERFECT_UNCOMPARABLE;
            }
        } else {
            HashMultimap<POCType, Double> map = HashMultimap.create();
            for (DimConcepts concept : concepts) {
                PartialOrderComparison result = concept.compare(left, right);
                map.put(result.t, result.uncertainty);
            }
            PartialOrderComparison ls = policy.getDirection(map);
            System.out.println("P("+left + ls.t + right+"|data)="+ls.uncertainty);
            return ls;
        }
    }

    public void close() {
        concepts.forEach(DimConcepts::close);
    }

    @Override
    public String getName() {
        return fieldName;
    }

    @Override
    public void serializeToDisk(File file) {
        File unionFolder = new File(file, fieldName);
        if (!unionFolder.exists())
            unionFolder.mkdirs();
        concepts.forEach(x -> {
            File xfile = new File(unionFolder, x.getName());
            if (!xfile.exists())
                xfile.mkdirs();
            x.serializeToDisk(xfile);
        });
    }

    @Override
    public void loadFromDisk(File file) {
        File unionFolder = new File(file, fieldName);
        if (unionFolder.exists()) {
            File[] files = unionFolder.listFiles();
            if (files != null) for (int i = 0, filesLength = files.length; i < filesLength; i++) {
                File subDim = files[i];
                if (!subDim.isDirectory()) continue;
                concepts
                        // for each concept that is foreseen fot the given element
                        .stream()
                        // return the concept that has the current expected name, that is matching with the folder's
                        .filter(x -> x.getName().equals(subDim.getName()))
                        .findFirst()
                        // If that dimension is present, then load for it the content
                        .ifPresent(x -> x.loadFromDisk(subDim));
            }
        } else {
            throw new RuntimeException("The expected folder for dimension "+fieldName+" does not exists: "+unionFolder);
        }
    }

    String[] argumentsForPartof = new String[]{"partOf"};
    @Override
    public String[] allowedKBTypesForTypingExpansion() {
        return argumentsForPartof;
    }

    @Override
    public boolean allowReflexiveExpansion() {
        return false;
    }
}
