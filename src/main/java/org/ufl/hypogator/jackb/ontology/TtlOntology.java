package org.ufl.hypogator.jackb.ontology;

import com.google.common.collect.HashMultimap;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shared.impl.PrefixMappingImpl;
import org.apache.jena.sparql.core.Prologue;
import org.ufl.hypogator.jackb.inconsistency.AgileField;
import org.ufl.hypogator.jackb.ontology.data.RawEventRelationship;
import org.ufl.hypogator.jackb.streamutils.data.AlgebraSupport;
import org.ufl.hypogator.jackb.streamutils.data.ArraySupport;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;
import org.ufl.hypogator.jackb.streamutils.iterators.operations.UnionIterator;
import scala.reflect.internal.Names;

import java.util.*;
import java.util.stream.Collectors;

public class TtlOntology {
    private static final String sparql = "SELECT * { ?s ?p ?o }";
    private Model model;
    private Ontology self;

    private HashMap<String, String> ldcToNist_map, nistToLDC_map;
    private HashMultimap<String, TypeSubtype> typeResolve;
    private HashMultimap<String, TypeSubtype> labelAcceptedArgumentResolve;
    private Set<String> entityOrFillers;

    public TtlOntology(String url) {
        this.model = RDFDataMgr.loadModel(url);
        ldcToNist_map = new HashMap<>();
        nistToLDC_map = new HashMap<>();
        typeResolve = HashMultimap.create();
        labelAcceptedArgumentResolve = HashMultimap.create();
        entityOrFillers = new HashSet<>();
        load();
    }

    /**
     * Creates a resources using the jena definitions
     *
     * @param prefix        Prefix associated to the resource
     * @param name          Value associated to the resource having a given prefic
     * @return
     */
    public Resource createResource(String prefix, String name) {
        return ResourceFactory.createProperty(model.getNsPrefixURI(prefix), name);
    }

    public ResultSet query(Resource s, Resource p, Resource o) {
        // Resolving the variables when values are there
        QuerySolutionMap initialBinding = new QuerySolutionMap();
        if (s != null) initialBinding.add("s", s);
        if (p != null) initialBinding.add("p", p);
        if (o != null) initialBinding.add("o", o);

        // The main sparql query
        Query query = QueryFactory.create(sparql);

        // Loading the prologue
        PrefixMappingImpl prologue = new PrefixMappingImpl();
        model.getNsPrefixMap().forEach(prologue::setNsPrefix);
        query.usePrologueFrom(new Prologue(prologue.lock()));

        QueryExecution qe = QueryExecutionFactory.create(query, model, initialBinding);
        return qe.execSelect();
    }

    private Ontology load() {
        Resource category = createResource("jackb", "Category");
        Resource subclassof = createResource("rdfs", "subClassOf");
        Resource domain = createResource("rdfs", "domain");
        Resource label = createResource("rdfs", "label");
        Resource allowedNist = createResource("schema", "rangeIncludes");
        self = new Ontology();

        Iterable<QuerySolution> it = () -> query(null, subclassof, category);
        for (QuerySolution kind : it) {
            RDFNode sort = kind.get("s");
            String sortName = sort.toString().split("#")[1].replaceAll("Type$","");

            Iterable<QuerySolution> it2 = () ->  query(null, subclassof, sort.asResource());
            ArrayList<TypeSubtype> ls = new ArrayList<>();
            for (QuerySolution type : it2) {
                TypeSubtype ts = new TypeSubtype();
                ts.kind = sortName;
                RDFNode typeInstance = type.get("s");
                ts.nistName = typeInstance.toString().split("#")[1].replaceAll("Type$","");

                Iterable<QuerySolution> it3 = () ->  query(null, domain, typeInstance.asResource());
                ArrayList<Label> lbs = new ArrayList<>();
                for (QuerySolution field: it3) {
                    Label l = new Label();
                    l.fromNISTType = ts.nistName;
                    RDFNode fieldGen = field.get("s");
                    RDFNode shortLabel;
                    Iterable<QuerySolution> itI = () -> query(fieldGen.asResource(), label, null);
                    for (QuerySolution io : itI) {
                        shortLabel = io.get("o");
                        l.nistName = l.fromNISTType+"."+shortLabel.toString();
                        //System.out.println("\t\t"+shortLabel.toString());
                        break;
                    }

                    Iterable<QuerySolution> it4 = () ->  query(fieldGen.asResource(), allowedNist, null);
                    ArrayList<String> nistTypes = new ArrayList<>();
                    for (QuerySolution args2 : it4) {
                        RDFNode fieldType = args2.get("o");
                        String str = fieldType.toString().split("#")[1];
                        if (str.endsWith("Type"))
                            str = str.replaceAll("Type$","");
                        nistTypes.add(str);
                    }
                    l.allowedLDCTypes = nistTypes.toArray(new String[nistTypes.size()]);
                    lbs.add(l);
                }
                ts.argumentTypes = lbs.toArray(new Label[lbs.size()]);
                ls.add(ts);
            }

            Sort s = new Sort(sortName, ls.toArray(new TypeSubtype[ls.size()]));
            if (sortName.toLowerCase().equals("entity"))
                self.entity = s;
            else if (sortName.toLowerCase().equals("relation"))
                self.relation = s;
            else if (sortName.toLowerCase().equals("filler"))
                self.filler = s;
            else if (sortName.toLowerCase().equals("event"))
                self.event = s;
        }
        //return self;

        boolean isEntityOrFiller;
        for (Sort s : self.getSorts()) {
            isEntityOrFiller = s.equals(self.entity) || s.equals(self.filler);
            if (s.hasTypes != null)
                for (TypeSubtype ts : s.hasTypes) {
                    if (isEntityOrFiller) {
                        entityOrFillers.add(ts.nistName);
                    }
                    addType(ts.nistName, ts);
                    addType(s.name, ts);
                    //ldcToNist(ts.ldcName, ts.nistName);
                }
        }

        for (Sort s : self.getSorts()) {
            if (s.hasTypes != null)
                for (TypeSubtype ts : s.hasTypes) {
                    /*System.out.println(ts.nistName);
                    System.out.println("\t"+Arrays.stream(ts.argumentTypes).map(x -> {
                        int lchar = x.nistName.lastIndexOf('.');
                        return x.nistName.substring(lchar+1);
                    }).collect(Collectors.joining(", "))+"\n");*/
                    if (ts.argumentTypes != null)
                        for (Label labelts : ts.argumentTypes) {
                            if (labelts.allowedLDCTypes != null)
                                for (String ldc : labelts.allowedLDCTypes) {
                                    labelAcceptedArgumentResolve.putAll(labelts.nistName, resolveNISTTypes(ldc));
                                }
                        }
                }
        }
        return self;
    }

    public Set<TypeSubtype> resolveNISTTypes(String nistType) {
        return typeResolve.get(nistType);
    }

    private void addType(String nistName, TypeSubtype typeSubtype) {
        typeResolve.put(nistName, typeSubtype);
    }

    private void addType(String nistName, Collection<TypeSubtype> typeSubtype) {
        typeResolve.putAll(nistName, typeSubtype);
    }

    public String resolveLDCToNist(String ldcType) {
        return ldcType;
    }

    public Set<String> getEntityOrFillers() {
        return entityOrFillers;
    }

    public Set<TypeSubtype> getTypesForLabel(String nistLabel) {
        return labelAcceptedArgumentResolve.get(nistLabel);
    }

    public IteratorWithOperations<RawEventRelationship> asBackwardRepresentation() {
        return UnionIterator
                .with(new AlgebraSupport<>(new ArraySupport<>(self.event.hasTypes)).map(TypeSubtype::backwardCompatibility))
                .with(new AlgebraSupport<>(new ArraySupport<>(self.relation.hasTypes)).map(TypeSubtype::backwardCompatibility))
                .done();
    }

    public boolean isTypeAllowedInField(String recordType, AgileField field) {
        for (TypeSubtype ts : labelAcceptedArgumentResolve.get(recordType + "." + field.fieldName)) {
            if (ts.nistName.equals(field.fieldType)) return true;
        }
        return false;
    }

    public static void main(String args[]) {
        TtlOntology fringes = new TtlOntology("data/SeedlingOntology.ttl");
        printSchema(fringes.self.relation);
        printSchema(fringes.self.event);
    }

    private static void printSchema(Sort relation) {
        TypeSubtype[] hasTypes = relation.hasTypes;
        for (int i = 0, hasTypesLength = hasTypes.length; i < hasTypesLength; i++) {
            TypeSubtype x = hasTypes[i];
            String relationName = x.nistName+
                    Arrays.stream(x.argumentTypes).map(y -> y.nistName.replace(x.nistName+".","")).collect(Collectors.joining(",","(",")"));
            System.out.println(relationName);
        }
    }
}
