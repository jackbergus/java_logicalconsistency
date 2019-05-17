/*
 * MoreRecallLocationresolver.java
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

package org.ufl.hypogator.jackb.disambiguation.dimension.space.clavin;

import com.bericotech.clavin.ClavinException;
import com.bericotech.clavin.extractor.LocationOccurrence;
import com.bericotech.clavin.gazetteer.query.AncestryMode;
import com.bericotech.clavin.gazetteer.query.FuzzyMode;
import com.bericotech.clavin.gazetteer.query.Gazetteer;
import com.bericotech.clavin.gazetteer.query.QueryBuilder;
import com.bericotech.clavin.resolver.ResolvedLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

/**
 * Resolves location names into GeoName objects.
 * <p>
 * Takes location names extracted from unstructured text documents by
 * {@link com.bericotech.clavin.extractor.LocationExtractor} and resolves them into the appropriate
 * geographic entities (as intended by the document's author based on
 * context) by finding the best match in a gazetteer.
 */
public class MoreRecallLocationresolver {
    /**
     * The default number of candidate matches to consider.
     */
    public static final int DEFAULT_MAX_HIT_DEPTH = 5;

    /**
     * The default context window to consider when resolving matches.
     */
    public static final int DEFAULT_MAX_CONTEXT_WINDOW = 5;

    /**
     * The default ancestry loading mode.
     */
    public static final AncestryMode DEFAULT_ANCESTRY_MODE = AncestryMode.LAZY;

    /**
     * The Gazetteer.
     */
    private final Gazetteer gazetteer;

    /**
     * Set of demonyms to filter out from extracted location names.
     */
    private static HashSet<String> demonyms;

    /**
     * Create a newDimensions MoreRecallLocationresolver.
     *
     * @param gazetteer the Gazetteer to query
     */
    public MoreRecallLocationresolver(final Gazetteer gazetteer) {
        this.gazetteer = gazetteer;
    }

    /**
     * Get the Gazetteer used by this resolver.
     *
     * @return the configured gazetteer
     */
    public Gazetteer getGazetteer() {
        return gazetteer;
    }

    /**
     * Resolves the supplied list of location names into
     * {@link ResolvedLocation}s containing {@link com.bericotech.clavin.gazetteer.GeoName} objects
     * using the defaults for maxHitDepth and maxContentWindow.
     * <p>
     * Calls {@link Gazetteer#getClosestLocations} on
     * each location name to find all possible matches, then uses
     * heuristics to select the best match for each by calling
     *
     * @param locations list of location names to be resolved
     * @param fuzzy     switch for turning on/off fuzzy matching
     * @return list of {@link ResolvedLocation} objects
     * @throws ClavinException if an error occurs parsing the search terms
     **/
    public List<ResolvedLocation> resolveLocations(final LocationOccurrence locations, final boolean fuzzy)
            throws ClavinException {
        return resolveLocations(locations, fuzzy, DEFAULT_ANCESTRY_MODE);
    }


    /**
     * Resolves the supplied list of location names into
     * {@link ResolvedLocation}s containing {@link com.bericotech.clavin.gazetteer.GeoName} objects.
     * <p>
     * Calls {@link Gazetteer#getClosestLocations} on
     * each location name to find all possible matches, then uses
     * heuristics to select the best match for each by calling
     *
     * @param location     list of location names to be resolved
     * @param fuzzy        switch for turning on/off fuzzy matching
     * @param ancestryMode the ancestry loading mode
     * @return list of {@link ResolvedLocation} objects
     * @throws ClavinException if an error occurs parsing the search terms
     **/
    @SuppressWarnings("unchecked")
    public List<ResolvedLocation> resolveLocations(final LocationOccurrence location,
                                                   final boolean fuzzy, final AncestryMode ancestryMode) throws ClavinException {
        // are you forgetting something? -- short-circuit if no locations were provided
        if (location == null) {
            return Collections.EMPTY_LIST;
        }

        /* Various named entity recognizers tend to mistakenly extract demonyms
         * (i.e., names for residents of localities (e.g., American, British))
         * as place names, which tends to gum up the works, so we make sure to
         * filter them out from the list of {@link LocationOccurrence}s passed
         * to the resolver.
         */
        //List<LocationOccurrence> filteredLocations = newDimensions ArrayList<>();
        if (isDemonym(location))
            return Collections.EMPTY_LIST;

        // did we filter *everything* out?
        /*if (filteredLocations.isEmpty()) {
            return Collections.EMPTY_LIST;
        }*/

        QueryBuilder builder = new QueryBuilder()
                //.maxResults(maxHitDepth)
                // translate CLAVIN 1.x 'fuzzy' parameter into NO_EXACT or OFF; it isn't
                // necessary, or desirable to support FILL for the CLAVIN resolution algorithm
                .fuzzyMode(fuzzy ? FuzzyMode.NO_EXACT : FuzzyMode.OFF)
                .ancestryMode(ancestryMode)
                .includeHistorical(true);

        // InternalError can be raised when the hard disk is not working
        return gazetteer.getClosestLocations(builder.location(location).build());
    }

    /**
     * Various named entity recognizers tend to mistakenly extract
     * demonyms (i.e., names for residents of localities (e.g.,
     * American, British)) as place names, which tends to gum up the
     * works for the resolver, so this method filters them out from
     * the list of {@link LocationOccurrence}s passed to the resolver.
     *
     * @param extractedLocation extraction location name to filter
     * @return true if input is a demonym, false otherwise
     */
    public static boolean isDemonym(LocationOccurrence extractedLocation) {
        // lazy load set of demonyms
        if (demonyms == null) {
            // populate set of demonyms to filter out from results, source:
            // http://en.wikipedia.org/wiki/List_of_adjectival_and_demonymic_forms_for_countries_and_nations
            demonyms = new HashSet<>();

            BufferedReader br = new BufferedReader(new InputStreamReader(MoreRecallLocationresolver.class.getClassLoader().getResourceAsStream("Demonyms.txt")));

            String line;
            try {
                while ((line = br.readLine()) != null)
                    demonyms.add(line);
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return demonyms.contains(extractedLocation.getText());
    }
}
