package it.giacomobergami.m18;


import com.google.common.collect.HashMultimap;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.shared.impl.PrefixMappingImpl;
import org.apache.jena.sparql.core.Prologue;

// Require classes in the new project
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.ListSingleSourcePathsImpl;
import org.jgrapht.alg.shortestpath.TreeSingleSourcePathsImpl;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.GraphWalk;
import org.ufl.hypogator.jackb.inconsistency.AgileField;
import org.ufl.hypogator.jackb.ontology.Label;
import org.ufl.hypogator.jackb.ontology.Ontology;
import org.ufl.hypogator.jackb.ontology.Sort;
import org.ufl.hypogator.jackb.ontology.TypeSubtype;
import org.ufl.hypogator.jackb.ontology.data.RawEventRelationship;
import org.ufl.hypogator.jackb.scraper.adt.DiGraph;
import org.ufl.hypogator.jackb.streamutils.data.AlgebraSupport;
import org.ufl.hypogator.jackb.streamutils.data.ArraySupport;
import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;
import org.ufl.hypogator.jackb.streamutils.iterators.operations.UnionIterator;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


public class TTLOntology2 {
    private static final String sparql = "SELECT * { ?s ?p ?o }";
    private static final String isa_sparql = "SELECT * { ?s a ?o }";
    private Model model;
    private Ontology self;

    private HashMap<String, String> ldcToNist_map, nistToLDC_map;
    private HashMap<String, TypeSubtype> typeResolve;
    private HashMultimap<String, TypeSubtype> labelAcceptedArgumentResolve;
    private Set<String> entityOrFillers;
    private Set<String> eventOrRelationship;
    DiGraph<String> top_down_hierarchy;

    public TTLOntology2(String url) {
        this.model = RDFDataMgr.loadModel(url);
        ldcToNist_map = new HashMap<>();
        nistToLDC_map = new HashMap<>();
        typeResolve = new HashMap<>();
        eventOrRelationship = new HashSet<>();
        labelAcceptedArgumentResolve = HashMultimap.create();
        entityOrFillers = new HashSet<>();
        self = new Ontology();
        category = createResource("jackb", "Category");
        subclassof = createResource("rdfs", "subClassOf");
        domain = createResource("rdfs", "domain");
        label = createResource("rdfs", "label");
        allowedNist = createResource("schema", "rangeIncludes");
        top_down_hierarchy = new DiGraph<>();
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


    Resource category;
    Resource subclassof;
    Resource domain;
    Resource label;
    Resource allowedNist;
    private Ontology load() {

        Iterable<QuerySolution> getKindFromSorts = () -> query(null, subclassof, category);
        for (QuerySolution kind : getKindFromSorts) {
            RDFNode currKind = kind.get("s");
            String  kindName = currKind.toString().split("#")[1].replaceAll("Type$","");
            ArrayList<TypeSubtype> ls = new ArrayList<>();

            toAddTypes(currKind, kindName, ls);

            Sort s = new Sort(kindName, ls.toArray(new TypeSubtype[ls.size()]));
            if (kindName.toLowerCase().equals("entity"))
                self.entity = s;
            else if (kindName.toLowerCase().equals("relation"))
                self.relation = s;
            else if (kindName.toLowerCase().equals("filler"))
                self.filler = s;
            else if (kindName.toLowerCase().equals("event"))
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
                    } else {
                        eventOrRelationship.add(ts.nistName);
                    }
                    addType(ts.nistName, ts);
                    addType(s.name, ts);
                    //ldcToNist(ts.ldcName, ts.nistName);
                }
        }
        extractHierarchy(true, new File("test.txt"));
        expandArgumentTypes();

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
                                    TypeSubtype type = resolveNISTTypes(ldc);
                                    if (type != null) {
                                        labelAcceptedArgumentResolve.put(labelts.nistName, type);
                                    }
                                }
                        }
                }
        }
        return self;
    }

    private void expandArgumentTypes() {
        if (self.getSorts() != null) {
            Set<String> vertices = top_down_hierarchy.graph.vertexSet();
            for (Sort s : self.getSorts()) {
                if (s.hasTypes != null)
                    for (TypeSubtype ts : s.hasTypes) {
                        for (Label l : ts.argumentTypes) {
                            ArrayList<String> expandedTypes = new ArrayList<>();
                            for (String allowedType : l.allowedLDCTypes) {
                                expandedTypes.add(allowedType);
                                new AllDirectedPaths<>(top_down_hierarchy.graph).getAllPaths(Collections.<String>singleton(allowedType), vertices, true, null).forEach(
                                        path -> {
                                            expandedTypes.add(path.getEndVertex());
                                        }
                                );
                            }
                            l.allowedLDCTypes = expandedTypes.toArray(new String[expandedTypes.size()]);
                        }
                    }
            }
        }
    }

    private void toAddTypes(RDFNode currKind, String kindName, ArrayList<TypeSubtype> ls) {
        Iterable<QuerySolution> getRootTypesFromKind = () ->  query(null, subclassof, currKind.asResource());
        for (QuerySolution type : getRootTypesFromKind) {
            TypeSubtype currentType = new TypeSubtype();
            currentType.kind = kindName;
            RDFNode typeInstance = type.get("s");
            currentType.nistName = typeInstance.toString().split("#")[1].replaceAll("Type$","");

            // Recursion
            toAddTypes(typeInstance, kindName, ls);

            Iterable<QuerySolution> it3 = () ->  query(null, domain, typeInstance.asResource());
            ArrayList<Label> lbs = new ArrayList<>();
            for (QuerySolution field: it3) {
                Label l = new Label();
                l.fromNISTType = currentType.nistName;
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
            currentType.argumentTypes = lbs.toArray(new Label[lbs.size()]);
            ls.add(currentType);
        }
    }

    public TypeSubtype resolveNISTTypes(String nistType) {
        return typeResolve.get(nistType);
    }

    private void addType(String nistName, TypeSubtype typeSubtype) {
        typeResolve.put(nistName, typeSubtype);
    }

    /*private void addType(String nistName, Collection<TypeSubtype> typeSubtype) {
        typeResolve.putAll(nistName, typeSubtype);
    }*/

    public String resolveLDCToNist(String ldcType) {
        return ldcType;
    }

    public boolean isAllowedEventRel(String x) {
        return eventOrRelationship.contains(x);
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

    public static void main(String args[]) throws IOException {
        TTLOntology2 fringes = new TTLOntology2("data/SeedlingOntology2.ttl");
        //System.out.println(fringes.resolveNISTTypes("ORG"));
        printSchema(fringes.self.entity);
        System.out.println();
        printSchema(fringes.self.filler);
        // TODO: move to a dedicated method. Creating a union hierarchy for all those concepts.
        //fringes.extractHierarchy(true);
    }

    /**
     *
     * @param top_down
     * @param filename      Optional argument providing the location where to store the hierarchy
     * @return              The return value returns whether some failuer occurred during the method invocation.
     *                      If the filename has been specified, it returns whether the file was written with success.
     *                      Returns always true otherwise.
     */
    private boolean extractHierarchy(boolean top_down, File filename) {
        for (Sort s : self.getSorts()) {//entityOrFillers
            if (s.hasTypes != null)
                for (TypeSubtype ts : s.hasTypes) {
                    String x = ts.nistName;
                    String arguments[] = x.split("\\.");
                    String builtUp = "";
                    for (int i = 0, n = arguments.length; i<n; i++) {
                        String current = builtUp + (builtUp.length() > 0 ? "." : "") + arguments[i];
                        String parent = builtUp.length() == 0 ? s.name : builtUp;
                        top_down_hierarchy.add(top_down ? parent : current, top_down ? current : parent, 1.0);
                        builtUp = current;
                    }
                }
        }

        if (filename != null) {
            try {
                top_down_hierarchy.writeToFile(filename, String::toString);
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return true;
        }
    }

    private static void printSchema(Sort relation) {
        TypeSubtype[] hasTypes = relation.hasTypes;
        Set<String> ls = new TreeSet<>();
        for (int i = 0, hasTypesLength = hasTypes.length; i < hasTypesLength; i++) {
            TypeSubtype x = hasTypes[i];
            String relationName = x.nistName+
                    Arrays.stream(x.argumentTypes).map(y -> y.nistName.replace(x.nistName+".","")).collect(Collectors.joining(",","(",")"));
            ls.add(relationName);
        }
        ls.forEach(System.out::println);
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n\n");
    }
}
