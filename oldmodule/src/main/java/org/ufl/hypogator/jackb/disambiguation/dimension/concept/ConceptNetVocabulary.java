

/*
 * ConceptNetVocabulary.java
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

package org.ufl.hypogator.jackb.disambiguation.dimension.concept;

import com.google.common.collect.HashMultimap;
import org.ufl.hypogator.jackb.fuzzymatching.FuzzyMatcher;
import org.ufl.hypogator.jackb.fuzzymatching.TwoGramIndexer;
import org.ufl.hypogator.jackb.traversers.conceptnet.Concept5ClientConfigurations;
import org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNet5Dump;
import org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNet5Postgres;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;
import org.ufl.hypogator.jackb.utils.adt.HashMultimapSerializer;

import java.io.*;
import java.util.*;
import java.util.function.Predicate;

import static org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNet5Dump.removeSuffix;

public class ConceptNetVocabulary extends AbstractVocabulary<ConceptNet5Postgres.RecordResultForSingleNode> {

    private final static ConceptNet5Dump dump = ConceptNet5Dump.getInstance();

    public ConceptNetVocabulary(FuzzyMatcher<ConceptNet5Postgres.RecordResultForSingleNode> vocabulary) {
        super(vocabulary);
    }
    /*private ConceptNetVocabulary(HashMultimap<String, String> vocabulary) {
        super(vocabulary);
    }*/

    /*public void addTermsFromVertex(String x) {
        String u = ConceptNetDimensionDisambiguationOperations.unrectify(x);
        if (!vocabulary.containsKey(u)) {
            vocabulary.put(u, "/c/en/"+x);
        }
    }*/

    public static ConceptNetVocabulary readVocabulary(File f, String... languages) {
        TwoGramIndexer<ConceptNet5Postgres.RecordResultForSingleNode> ls;

        // Either allows to store the files, or loads them as serialized
        if (languages.length == 0) {
            ls = getInstance().listEntries(f, new HashSet<>(Collections.singletonList("en")));
        } else {
            HashSet<String> s = new HashSet<>(Arrays.asList(languages));
            ls = getInstance().listEntries(f, s);
        }

        // Serialized if the data has not been already serialized
        ls.serialize();
        return new ConceptNetVocabulary(ls);
    }

    private static ConceptNetVocabulary voc;

    /**
     * This method reads all the entities stored within ConceptNet. By doing so, the vocabulary is shared among
     * different types/dimensions.
     *
     * IMPORTANT: either this method should be synchronized, or it must be called only once
     *
     * @return
     */
    public static ConceptNetVocabulary readDefaultVocabulary() {
        if (voc == null) {
            System.err.println("[ConceptNetVocabulary::readDefaultVocabulary] Reading Concepts Vocabulary for FuzzyMatching...");

            // Loading the default path containing all ConceptNet's entities
            Concept5ClientConfigurations conf = Concept5ClientConfigurations.instantiate();
            File path = conf.getConceptNetEntityList();

            // Extract only the concepts that belong to the languages within this context (e.g., English, Russian, Urkrainian)
            String[] lang = conf.getConceptNetEntityLanguages();

            // Read all the elements within the vocabulary
            voc = readVocabulary(path, lang);

            System.err.println("[ConceptNetVocabulary::readDefaultVocabulary]... Reading done");
        }
        return voc;
    }

    public static void main(String args[]) throws IOException, ClassNotFoundException {
        System.err.println("LOADING");

        FileInputStream file = new FileInputStream("data/nodes/termToObjects.ser");
        ObjectInputStream in = new ObjectInputStream(file);
        BufferedWriter writer = new BufferedWriter(new FileWriter("out.txt"));
        HashMultimap<String, ConceptNet5Postgres.RecordResultForSingleNode> mm = HashMultimapSerializer.unserialize(new File("data/nodes/termToObjects.ser"));
            Iterator<Map.Entry<String, Collection<ConceptNet5Postgres.RecordResultForSingleNode>>> line = mm.asMap().entrySet().iterator();
            while ((line.hasNext())) {
                writer.write(line.next().toString());
                writer.newLine();
            }
        in.close();
        file.close();
        writer.close();
        System.out.println("press enter");
        new Scanner(System.in).nextLine();


        ConceptNetVocabulary voc = ConceptNetVocabulary.readDefaultVocabulary();


        System.err.println("QUERYING");
        System.out.println(voc.containsExactTerm("buk"));
        System.out.println(voc.containsExactTerm("Buk"));
        System.out.println(voc.fuzzyMatch("buk", 3, .6));
        System.out.println(voc.fuzzyMatch("buk 332", 3, .6));
        System.err.println("DONE");
    }

    private static CNVocabularyEntry e = null;
    public static CNVocabularyEntry getInstance() {
        if (e == null)
            e = new CNVocabularyEntry(AbstractVocabulary.getIsStopwordPredicate());
        return e;
    }

    public void addTermsFromVertex(Object apply) {
        if (apply instanceof EdgeVertex) {
            EdgeVertex ap = ((EdgeVertex) apply);
            if (ap.id.contains("9N314M")) {
                System.err.println(ap);
            }
            this.forcePut(((EdgeVertex) apply).asRecordResultForSingleNode());
        }
    }

    public ConceptNetVocabulary copy() {
        if (vocabulary instanceof TwoGramIndexer)
            return new ConceptNetVocabulary(((TwoGramIndexer<ConceptNet5Postgres.RecordResultForSingleNode>)vocabulary).copy());
        else return new ConceptNetVocabulary(vocabulary);
    }

    private static class CNVocabularyEntry {
        /*String queryTerm;
        String term;*/
        ConceptNet5Postgres voc = ConceptNet5Postgres.getInstance();
        final Predicate<String> isStopWord;

        CNVocabularyEntry(Predicate<String> isStopWord) {
            /*this.queryTerm = queryTerm;
            this.term = term;*/
            this.isStopWord = isStopWord;
        }

        public TwoGramIndexer<ConceptNet5Postgres.RecordResultForSingleNode> listEntries(File folder, Set<String> languages) {
            //HashMultimap<String, String> term_to_id = HashMultimap.create();
            String[] languagesUrlConceptnet = new String[languages.size()];

            Iterator<String> it = languages.iterator();
            for (int i = 0, n = languagesUrlConceptnet.length; i<n; i++) {
                languagesUrlConceptnet[i] = "/c/"+it.next()+"/%";
            }

            TwoGramIndexer<ConceptNet5Postgres.RecordResultForSingleNode> twi =
                    new TwoGramIndexer<>(ConceptNet5Postgres.RecordResultForSingleNode::getStrings, folder);

            // Loading the files only if they have not been loaded already
            if (!folder.exists()) {
                List<ConceptNet5Postgres.RecordResultForSingleNode> ls = voc.rawQueryNode(true, languagesUrlConceptnet);
                twi.addAll(ls, AbstractVocabulary.getIsStopwordPredicate());
            }

            return twi;
        }
    }

    /**
     *
     * @param key
     * @return      Null if the object is not allowed. Otherwise, it is mapped to a ConcpetNet5 object
     */
    private Long asLong(ConceptNet5Postgres.RecordResultForSingleNode key) {
        String id = key.id;
        id = removeSuffix(id, "/n");
        if (id.endsWith("/a") || id.endsWith("/r")) return null;
        return dump.nodeIdToOffset(id);
    }

    public void serializeToFolder(File folder, ConceptNet5Dump javaPersister) throws IOException {
        if (vocabulary instanceof TwoGramIndexer)
            TwoGramIndexer.serializeToCSVFolder(this::asLong, folder, ((TwoGramIndexer<ConceptNet5Postgres.RecordResultForSingleNode>)this.vocabulary), javaPersister);
    }
}