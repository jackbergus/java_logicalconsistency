/*
 * SpaceDisambiguator.java
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

package org.ufl.hypogator.jackb.disambiguation.dimension.space;

import com.bericotech.clavin.ClavinException;
import com.bericotech.clavin.extractor.LocationOccurrence;
import com.bericotech.clavin.gazetteer.GeoName;
import com.bericotech.clavin.gazetteer.query.LuceneGazetteer;
import com.bericotech.clavin.resolver.ResolvedLocation;
import javafx.util.Pair;
import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.disambiguation.dimension.space.clavin.MoreRecallLocationresolver;
import org.ufl.hypogator.jackb.disambiguation.dimension.space.geonames.AdditionalSpaceHierarchy;
import org.ufl.hypogator.jackb.disambiguation.DisambiguatorForDimension;
import org.ufl.hypogator.jackb.utils.adt.Triple;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DisambiguatorForSpace implements DisambiguatorForDimension<ResolvedSpace, DisambiguatedSpace> {

    private MoreRecallLocationresolver clavinLocationResolver;
    private final boolean doFuzzyMatch;
    private final AdditionalSpaceHierarchy ash = AdditionalSpaceHierarchy.instance();

    public DisambiguatorForSpace() {
        ConfigurationEntrypoint ins = ConfigurationEntrypoint.getInstance();
        try {
            clavinLocationResolver = new MoreRecallLocationresolver(new LuceneGazetteer(ins.clavinFolder));
        } catch (ClavinException e) {
            e.printStackTrace();
            clavinLocationResolver = null;
        }
        this.doFuzzyMatch = ins.doClavinFuzzyMatch;
    }

    private static DisambiguatorForSpace instance;
    public static DisambiguatorForSpace getInstance() {
        if (instance == null) {
            instance = new DisambiguatorForSpace();
        }
        return instance;
    }

    public GeoName getDescription(Integer id) {
        try {
            return clavinLocationResolver.getGazetteer().getGeoName(id);
        } catch (ClavinException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getStringDescription(Integer id) {
        try {
            return clavinLocationResolver.getGazetteer().getGeoName(id).getName();
        } catch (Exception e) {
            return "";
        }
    }

    /*public DisambiguatorForSpace(File clavinIndex, boolean doFuzzyMatch) throws ClavinException {
        clavinLocationResolver = new MoreRecallLocationresolver(new LuceneGazetteer(clavinIndex));
        this.doFuzzyMatch = doFuzzyMatch;
    }*/

    public List<ResolvedSpace> detextWithClavinNerd(String sentence) throws ClavinException {
        clavinLocationResolver.getGazetteer().loadAncestry();
        String sLower = sentence.toLowerCase();
        // resolve location entities extracted from input text
        List<ResolvedLocation> ls =
                clavinLocationResolver.resolveLocations(new LocationOccurrence(sentence, 0), doFuzzyMatch);

        List<ResolvedSpace> erl = new ArrayList<>(ls.size());
        for (ResolvedLocation rl : ls) {
            erl.add(new ResolvedSpace(rl.getLocation(), sentence.length() - 1, rl.getGeoname(), rl.getMatchedName(), doFuzzyMatch));
            // Observer: there could be multiple elements disambiguated in the same term, but they may refer to different elements
            /*if (sLower.equals(rl.getMatchedName().toLowerCase()) || rl.getLocation().getText().toLowerCase().equals(sLower))
                break;*/
        }
        return erl;
    }

    @Override
    public DisambiguatedSpace disambiguate(String str) {
        DisambiguatedSpace si = new DisambiguatedSpace(str);
        Pair<String, Long> continent = ash.isContinent(str);
        if (continent != null) {
            si.setDisambiguation(str, new ResolvedSpace(str, continent), 1f);
        } else {
            try {
                List<ResolvedSpace> ls = detextWithClavinNerd(str);
                si.setDisambiguations(ls);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Iterator<Triple<String, ResolvedSpace, Double>> it = si.getDisambiguation().iterator();
        if (it.hasNext()) {
            Triple<String, ResolvedSpace, Double> elem = si.getDisambiguation().iterator().next();
            if (elem != null && elem.second.getId() == 2017370) {
                //There is only one Russia within this topic context
                si.clearDisambiguations();
                si.setDisambiguation(elem);
            }
        }
        return si;
    }

}
