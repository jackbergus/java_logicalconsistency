/*
 * Concept5ClientConfigurations.java
 * This file is part of aida_scraper
 *
 * Copyright (C) 2018 giacomo
 *
 * aida_scraper is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * aida_scraper is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with aida_scraper. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.ufl.hypogator.jackb.m9.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import it.giacomobergami.m18.TTLOntology2;
import org.ufl.hypogator.jackb.disambiguation.dimension.concept.ConceptNetVocabulary;
import org.ufl.hypogator.jackb.traversers.conceptnet.RecordResultForSingleNode;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.*;

public class Concept5ClientConfigurations {

    private final String hierarchiesFolder;
    public final double threshold;
    private final boolean useRestfulConceptnetAPI;
    public boolean doClavinFuzzyMatch;
    public final String concpetNetConfURL;
    public final ObjectMapper jsonSerializer;
    public final String pathToWordnetRDF;
    public final String pathToLocalWordNetGloss;
    private String clavinIndexPath;
    private Properties props = new Properties();
    //public final static ConceptNet5Postgres scia = ConceptNet5Postgres.getInstance();

    private static Concept5ClientConfigurations self;
    private String pathToJNILIbrary;

    private static void addLibraryPath(String pathToAdd) throws Exception {
        final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        usrPathsField.setAccessible(true);

        //getPairwiseArgument array of paths
        final String[] paths = (String[]) usrPathsField.get(null);

        //check if the path to add is already present
        for (String path : paths) {
            if (path.equals(pathToAdd)) {
                return;
            }
        }

        //add the newDimensions path
        final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
        newPaths[newPaths.length - 1] = pathToAdd;
        usrPathsField.set(null, newPaths);
    }

    private Concept5ClientConfigurations(File f) throws IOException {
        //File f = newDimensions File("conf/conceptnet5_connector.properties");
        if (!f.exists()) {
            throw new RuntimeException("Error: this class requires the configuration file `conf/conceptnet5_connector.properties`");
        }
        props.load(new FileInputStream(f));
        this.concpetNetConfURL = props.getProperty("concpetNetConfURL");
        if (this.concpetNetConfURL == null) {
            throw new RuntimeException("Error: you must set the http connection to `concpetNetConfURL`");
        }
        jsonSerializer = new ObjectMapper();
        this.pathToWordnetRDF = props.getProperty("wordnetRDF");
        if (this.pathToWordnetRDF == null) {
            throw new RuntimeException("Error: you must set the path where the WordNet3 RDF in ttl syntax is provided in `wordnetRDF`");
        }
        this.pathToLocalWordNetGloss = props.getProperty("pathToLocalWordNetGloss");
        if (this.pathToLocalWordNetGloss == null) {
            throw new RuntimeException("Error: you must set the path where to store the WordNet lemmas in `pathToLocalWordNetGloss`");
        }
        this.pathToJNILIbrary = props.getProperty("pathToJNILibrary");
        if (this.pathToJNILIbrary == null) {
            System.out.println("Adding the default linux path: /usr/local/lib/");
            this.pathToJNILIbrary = "/usr/local/lib/";
        }
        this.clavinIndexPath = props.getProperty("clavinIndex");
        if (this.clavinIndexPath == null) {
            System.out.println("Adding the default linux path: /media/giacomo/OutputBlank/buffer/clavin-lucene-index");
            this.clavinIndexPath = "/media/giacomo/OutputBlank/buffer/clavin-lucene-index";
        }
        this.useRestfulConceptnetAPI = Boolean.valueOf(props.getProperty("useRestfulConceptnetAPI", "false"));
        this.doClavinFuzzyMatch = Boolean.valueOf(props.getProperty("fuzzyMatchGeonames", "false"));
        this.hierarchiesFolder = (props.getProperty("dumpHierarchies", "hierarchies"));
        this.threshold = Double.valueOf(props.getProperty("threshold", ".6"));
        try {
            addLibraryPath("/usr/local/lib/");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Using the default system paths");
        }
    }

    public static Concept5ClientConfigurations instantiate(File f) {
        if (self == null) {
            try {
                self = new Concept5ClientConfigurations(f);
            } catch (IOException e) {
                e.printStackTrace();
                self = null;
            }
        }
        return self;
    }

    public static Concept5ClientConfigurations instantiate() {
        if (self != null)
            return self;
        else return instantiate(new File("conf/conceptnet5_connector.properties"));
    }

    /*public Collection<String> getEntityTypes() {
        ArrayList<String> s = new ArrayList<>();
        if (props.containsKey("entities")) {
            for (String entity : props.getProperty("entities").split(",")) {
                s.add(entity);
            }
        } else {
            s.add("person");
            s.add("organization");
            s.add("geopolitical");
            s.add("entity");
            s.add("facility");
            s.add("location");
            s.add("monetary_unit");
            s.add("job");
            s.add("surname");
            s.add("weapon");
            s.add("vehicle");
            s.add("commodity");
            s.add("crime");
            s.add("part_person_plays");
        }
        return s;
    }*/

    public boolean retrieveOnlyEnglishConcepts() {
        if (props.containsKey("onlyEnglishConcepts")) {
            return Boolean.valueOf(props.getProperty("onlyEnglishConcepts"));
        } else {
            return true;
        }
    }

    public File getEntityMultimapFile() {
        if (props.containsKey("entityMultimap")) {
            return new File(props.getProperty("entityMultimap"));
        } else {
            return new File("entities-multimap.mmp");
        }
    }

    public File getConceptNetEntityList() {
        if (props.containsKey("conceptNetVoc")) {
            return new File(props.getProperty("conceptNetVoc"));
        } else {
            return new File("data/nodes/");
        }
    }

    public String[] getConceptNetEntityLanguages() {
        if (props.containsKey("conceptNetVocLang")) {
            return props.getProperty("conceptNetVocLang").split(",");
        } else {
            return new String[0];
        }
    }

    /*public File getClavinIndexPath() {
        return clavinIndexPath == null ? null : new File(clavinIndexPath);
    }

    public File getHierarchy5() {
        return clavinIndexPath == null ? null : new File(getClavinIndexPath().getParentFile(), "adminCode5.txt");
    }*/

    public int getLimit() {
        if (props.containsKey("limitRelatedness")) {
            return Integer.valueOf(props.getProperty("limitRelatedness"));
        } else {
            return -1;
        }
    }

    private static String rectifyElement(String term) {
        term = term.replaceAll("[$&\\-_+]", " ");
        term = term.replaceAll("\\s", "_");

        // Use the web UTF8 format only if I'm using the restful APIs
        if (self == null) instantiate();
        if (self.useRestfulConceptnetAPI)
        try {
            term = URLEncoder.encode(term, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        ////

        return term;
    }

    public static String rectifyTerm(String term) {
        if (term == null)
            System.err.println("NULL TERM");
        if (term.startsWith("/c/")) {
            String args[] = term.split("/");
            String c = args[1];
            String lang = args[2];
            String param = rectifyElement(args[3]);
            return "/" + c + "/" + lang + "/" + param;
        } else {

            if (!term.startsWith("/c/")) {
                term = "/c/en/" + rectifyElement(term);
            }
            return term;
        }
    }

    /**
     * The entry point can be either the union, or any element that serves as a hierarchical root. Therefore, I must
     * check whether the term appears in the hierarchy
     *
     * @param term
     * @return
     */
    public EdgeVertex resolveEntryPoint(String term, ConceptNetVocabulary vocabulary) {
        if (self.useRestfulConceptnetAPI) {
            System.err.println("Warning: it may not work");
        }
        if (term.equals("Union") || conceptnetResolvableTypes().contains(term))
            return EdgeVertex.generateSemanticRoot(term);
        else {
            // Checking whether the vocabulary has some graph term
            Collection<RecordResultForSingleNode> singleton = null;
            if (vocabulary != null)
                singleton = vocabulary.containsExactTerm2(term);
            EdgeVertex toReturn = null;
            // Extracting the graph-based element
            if (singleton != null && !singleton.isEmpty()) {
                toReturn = singleton.iterator().next().getParent();
            }
            return toReturn;// == null ? scia.queryNode(false, term) : toReturn;
        }
    }

    /*public JsonQuery ingoingRelDefaultEn(String term, String rel) {
        term = rectifyTerm(term);
        return term == null ? null : new JsonQuery(concpetNetConfURL + "/query?end=" + term + "&rel=" + (rel.startsWith("/r/") ? rel : "/r/"+rel),  useRestfulConceptnetAPI, retrieveOnlyEnglishConcepts()); ///c/en/
    }

    public JsonQuery outgoingRelDefaultEn(String term, String rel) {
        term = rectifyTerm(term);
        return term == null ? null : new JsonQuery(concpetNetConfURL + "/query?start=" + term + "&rel=" + (rel.startsWith("/r/") ? rel : "/r/"+rel),  useRestfulConceptnetAPI, retrieveOnlyEnglishConcepts()); ///c/en/
    }*/

    private Collection<String> memoizeCollection = null;
    public Collection<String> conceptnetResolvableTypes() {
        if (memoizeCollection == null) {
            File dir = new File(hierarchiesFolder);
            //TtlOntology loader = new TtlOntology("data/SeedlingOntology.ttl");
            Set<String> eF = TTLOntology2.getInstance().getEntityOrFillers();
            if (dir.exists()) {
                ArrayList<String> toret = new ArrayList<>();
                File[] files = dir.listFiles();
                if (files != null) {
                    for (File f : files) {
                        String fileName = null;
                        if (f.isFile() && (fileName = f.getName()).endsWith("_map.json")) {
                            fileName = (fileName.replace("_map.json", ""));

                            if (eF.contains(fileName)) // E.g., removing other generalizations, e.g. Union
                                toret.add(fileName.replace("_map.json", ""));
                        }
                    }
                }
                memoizeCollection = toret;
            } else {
                memoizeCollection = Collections.emptyList();
            }
        }
        return memoizeCollection;
    }

    public static boolean isNextPageFromWordnet(String nextPage) {
        return nextPage != null && nextPage.startsWith("http://wordnet-rdf.princeton.edu/wn31/");
    }

    /*public JsonQuery generateQuery(String nextPage) {
        if (isNextPageFromWordnet(nextPage)) {
            return null; // TODO
        } else {
            return nextPage == null ? null : new JsonQuery(concpetNetConfURL + nextPage, useRestfulConceptnetAPI, false);
        }
    }*/

    public String getHierarchiesFolder() {
        return hierarchiesFolder;
    }

    public static void main(String[] args) {
        System.out.println(Concept5ClientConfigurations.instantiate().conceptnetResolvableTypes());
    }
}
