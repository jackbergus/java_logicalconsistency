/*
 * ExtendedResolvedLocation.java
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

import com.bericotech.clavin.extractor.LocationOccurrence;
import com.bericotech.clavin.gazetteer.*;
import edu.stanford.nlp.trees.tregex.tsurgeon.FetchNode;
import javafx.util.Pair;
import org.ufl.hypogator.jackb.disambiguation.Resolved;
import org.ufl.hypogator.jackb.disambiguation.dimension.space.clavin.SerializableGeoName;
import org.ufl.hypogator.jackb.disambiguation.dimension.space.geonames.*;
import org.ufl.hypogator.jackb.fuzzymatching.LowConfidenceRank;
import org.ufl.hypogator.jackb.fuzzymatching.Similarity;
import org.ufl.hypogator.jackb.scraper.SemanticNetworkEntryPoint;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;
import org.ufl.hypogator.jackb.utils.adt.Triple;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Object produced by resolving a location name against gazetteer
 * records.
 * <p>
 * Encapsulates a {@link GeoName} object representing the best match
 * between a given location name and gazetter record, along with some
 * information about the geographic entity resolution process.
 */
public class ResolvedSpace implements Resolved {

    private final static AdditionalSpaceHierarchy ash = AdditionalSpaceHierarchy.instance();
    private final static Admin5 adm5 = Admin5.instance();
    private final static Admin1 adm1 = Admin1.instance();
    private final static Admin2 adm2 = Admin2.instance();
    private final static IsCountryDisambiguator countryCheck = IsCountryDisambiguator.instance();
    private final Similarity sim = LowConfidenceRank.getInstance();

    // geographic entity resolved from location name
    private SerializableGeoName geoname;
    private long continentId;
    public static final Long worldId = 6295630L;


    // original location name extracted from text
    private Triple<String, Integer, Integer> location;

    // name from gazetteer record that the inputName was matched against
    private String matchedName;

    // whether fuzzy matching was used
    private boolean fuzzy;

    // confidence score for resolution
    private double confidence;
    public String continentCode;
    private boolean isAdministrative;
    private boolean isEarth;
    private boolean isNoMansLand;
    private boolean isContinent;
    private boolean isCountry;
    private Long backupContinentId;

    public ResolvedSpace(String str, Pair<String, Long> continent) {
        this.matchedName = str;
        this.continentId = continent.getValue();
        this.fuzzy = false;
        this.confidence = 1.0;
        this.location = new Triple<>(str, 0, str.length() - 1);
        this.continentCode = continent.getKey();
        this.backupContinentId = continent.getValue();
        complete();
    }

    /**
     * Builds a {@link LocationOccurrence} from a document retrieved from
     * the Lucene index representing the geographic entity resolved
     * from a location name.
     *
     * @param location    the original location occurrence
     * @param end         final representation
     * @param geoname     the matched gazetteer record
     * @param matchedName the name that was matched by the search engine
     * @param fuzzy       was this a fuzzy match?
     */
    public ResolvedSpace(final LocationOccurrence location, int end, final GeoName geoname, final String matchedName, final boolean fuzzy) {
        this.geoname = new SerializableGeoName((BasicGeoName) geoname);
        this.location = new Triple<>(location.getText(), location.getPosition(), end);
        this.matchedName = matchedName;
        this.fuzzy = fuzzy;
        this.continentId = geoname.getGeonameID();
        // for fuzzy matches, confidence is based on the edit distance
        // between the given location name and the matched name
        String tmp = location.getText();
        double val = sim.sim(location.getText(), matchedName);
        this.confidence = fuzzy ? (val) : 1;
        this.continentId = -1;
        complete();
    }
    private void complete() {
        if (continentId == -1) {
            FeatureClass fc = getGeoname().getFeatureClass();
            isAdministrative = fc == FeatureClass.A || fc == FeatureClass.P;
            isEarth = getGeoname().getGeonameID() == worldId;
            isNoMansLand = getGeoname().getPrimaryCountryCode() == CountryCode.NULL;
            isContinent = isContinent();
            isCountry = countryCheck.isCountryId(getId());
        } else {
            isAdministrative = false;
            isEarth = false;
            isNoMansLand = false;
            isContinent = true;
            isCountry = false;
        }
    }

    /*public ResolvedSpace(String str, String continent, long continentId) {
        this.matchedName = str;
        this.continentId = continentId;
        this.fuzzy = false;
        this.confidence = 1.0;
        this.location = new Triple<>(str, 0, str.length()-1);
        this.continentCode = continent;
    }*/

    public long getId() {
        return geoname == null ? continentId : geoname.getGeonameID();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.geoname);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ResolvedSpace other = (ResolvedSpace) obj;
        if (this.geoname != other.geoname && (this.geoname == null || !this.geoname.equals(other.geoname))) {
            return false;
        }
        return true;
    }

    /**
     * For pretty-printing.
     */
    @Override
    public String toString() {
        return String.format("Resolved \"%s\" as: \"%s\" {%s}, position: %d-%d, confidence: %f, fuzzy: %s",
                location.first, matchedName, geoname == null ? continentCode : geoname, location.second, location.third, confidence, fuzzy);
    }

    /**
     * Get the geographic entity resolved from the location name.
     *
     * @return the geographic entity
     */
    public GeoName getGeoname() {
        return geoname;
    }

    /**
     * Get the original location name extracted from the text.
     *
     * @return the original occurrence of the location name
     */
    public Triple<String, Integer, Integer> getLocation() {
        return location;
    }

    /**
     * Get the name from the gazetteer record that the inputName was
     * matched against.
     *
     * @return the matched name
     */
    public String getMatchedName() {
        return matchedName;
    }

    /**
     * Was fuzzy matching used?
     *
     * @return <code>true</code> if fuzzy matching was used
     */
    public boolean isFuzzy() {
        return fuzzy;
    }

    /**
     * Get the confidence score for resolution.
     *
     * @return the confidence score
     */
    public double getConfidence() {
        return confidence;
    }

    public static final String ADMINISTRATIVE_HIERARCHY_MARKER = "_";

    @Override
    public List<String> generateDisambiguationPath() {
        List<String> array = new ArrayList<>();

        GeoName geoAndGeo = getGeoname();

        if (geoAndGeo != null) {
            String code;

            // The problem is that the current administrative code only provide the top level of the hierarchy where
            // a thing belongs, but administrative codes do not describe the entity per se. Therefore, I must add such
            // code only when the name doesn't already describe an administrative division

            if (!isEarth && !isAdministrative && !isContinent && !isCountry) {
                array.add(geoAndGeo.getGeonameID() + "");
            }

            // Delimiting the administrative part from the element
            //array.add(ADMINISTRATIVE_HIERARCHY_MARKER);
            if (!isContinent) {

                // Creating the hierarchy from the smallest to the coarsest description
                String id = geoAndGeo.getGeonameID() + "";
                FeatureCode fc = geoAndGeo.getFeatureCode();

                if (!(featureCodeEquals(fc, FeatureCode.ADM5)
                        || featureCodeEquals(fc, FeatureCode.ADM1) || featureCodeEquals(fc, FeatureCode.ADM1H)
                        || featureCodeEquals(fc, FeatureCode.ADM2) || featureCodeEquals(fc, FeatureCode.ADM2H)
                        || featureCodeEquals(fc, FeatureCode.ADM3) || featureCodeEquals(fc, FeatureCode.ADM3H)
                        || featureCodeEquals(fc, FeatureCode.ADM4) || featureCodeEquals(fc, FeatureCode.ADM4H))) {
                    array.add(id);
                }

                // TODO: is it necessary to gain more precision as in the visualization?

                code = adm5.getAdmin5(id);
                if (code != null && !code.isEmpty())
                    array.add(code);

                code = geoAndGeo.getAdmin4Code();
                if (code != null && !code.isEmpty())
                    array.add(code);

                code = geoAndGeo.getAdmin3Code();
                if (code != null && !code.isEmpty())
                    array.add(code);

                code = geoAndGeo.getAdmin2Code();
                if (code != null && !code.isEmpty())
                    array.add(code);

                code = geoAndGeo.getAdmin1Code();
                if (code != null && !code.isEmpty()) {
                    if (!code.equals("00"))
                        array.add(code);
                }
            }

            /*if (!isNoMansLand && !isEarth) {
                if (!isContinent) {
                    code = getGeoname().getPrimaryCountryCode().toString();
                    array.add(code);

                    String continent = ash.getContinentIdFromState(code);
                    array.add(continent);
                } else {
                    array.add(ash.getContinentFromGeonamesId(continentId));
                }
            }*/
            extendListWithContinent(array);

        } else {
            array.add(continentCode);
        }

        array.add("Earth/World");

        return array;
    }

    private static boolean featureCodeEquals(FeatureCode left, FeatureCode right) {
        return (left != null && right != null && Objects.equals(left.isHistorical(),right.isHistorical()) && Objects.equals(left.getType(), right.getType()) && Objects.equals(left.getDescription(), right.getDescription())) || (left == null && right == null);
    }

    private static final DisambiguatorForSpace res = DisambiguatorForSpace.getInstance();
    public List<SemanticNetworkEntryPoint> asSMEPList() {
        List<SemanticNetworkEntryPoint> semanticArray = new ArrayList<>();

        GeoName geoAndGeo = getGeoname();

        if (geoAndGeo != null) {
            String code;

            // The problem is that the current administrative code only provide the top level of the hierarchy where
            // a thing belongs, but administrative codes do not describe the entity per se. Therefore, I must add such
            // code only when the name doesn't already describe an administrative division

            if (!isEarth && !isAdministrative && !isContinent && !isCountry) {
                semanticArray.add(EdgeVertex.fromSpace(geoAndGeo.getGeonameID(), matchedName));
            }

            // Delimiting the administrative part from the element
            //array.add(ADMINISTRATIVE_HIERARCHY_MARKER);
            if (!isContinent) {

                // Creating the hierarchy from the smallest to the coarsest description
                String id = geoAndGeo.getGeonameID() + "";
                boolean isSet = false;
                FeatureCode fc = geoAndGeo.getFeatureCode();

                if (!(featureCodeEquals(fc, FeatureCode.ADM5)
                        || featureCodeEquals(fc, FeatureCode.ADM1) || featureCodeEquals(fc, FeatureCode.ADM1H)
                        || featureCodeEquals(fc, FeatureCode.ADM2) || featureCodeEquals(fc, FeatureCode.ADM2H)
                        || featureCodeEquals(fc, FeatureCode.ADM3) || featureCodeEquals(fc, FeatureCode.ADM3H)
                        || featureCodeEquals(fc, FeatureCode.ADM4) || featureCodeEquals(fc, FeatureCode.ADM4H))) {
                    semanticArray.add(EdgeVertex.fromSpace(id, matchedName));
                    isSet = true;
                }

                code = adm5.getAdmin5(id);
                if (code == null && featureCodeEquals(fc, FeatureCode.ADM5))
                    code = id;
                if (code != null && !code.isEmpty()) {
                    semanticArray.add(EdgeVertex.fromSpace(geoAndGeo.getAdmin1Code()+"."+geoAndGeo.getAdmin2Code()+"."+geoAndGeo.getAdmin3Code()+"."+geoAndGeo.getAdmin4Code()+"."+code, isSet ? null : matchedName));
                    isSet = true;
                }

                code = geoAndGeo.getAdmin4Code();
                if (code == null && featureCodeEquals(fc, FeatureCode.ADM4) || featureCodeEquals(fc, FeatureCode.ADM4H))
                    code = id;
                if (code != null && !code.isEmpty()) {
                    semanticArray.add(EdgeVertex.fromSpace(geoAndGeo.getAdmin1Code()+"."+geoAndGeo.getAdmin2Code()+"."+geoAndGeo.getAdmin3Code()+"."+code, isSet ? null : matchedName));
                    isSet = true;
                }

                code = geoAndGeo.getAdmin3Code();
                if (code == null && featureCodeEquals(fc, FeatureCode.ADM3) || featureCodeEquals(fc, FeatureCode.ADM3H))
                    code = id;
                if (code != null && !code.isEmpty()) {
                    semanticArray.add(EdgeVertex.fromSpace(geoAndGeo.getAdmin1Code()+"."+geoAndGeo.getAdmin2Code()+"."+geoAndGeo.getAdmin3Code(), isSet ? null : matchedName));
                }

                code = geoAndGeo.getAdmin2Code();
                if (code == null && featureCodeEquals(fc, FeatureCode.ADM2) || featureCodeEquals(fc, FeatureCode.ADM2H))
                    code = id;
                if (code != null && !code.isEmpty()) {
                    Long ids = getCountry() != null ? adm2.getAdmin2(getCountry().getKey()+"."+geoAndGeo.getAdmin1Code()+"."+code) : null;
                    if (ids != null) {
                        semanticArray.add(EdgeVertex.fromSpace(ids, res.getStringDescription(ids.intValue())));
                    } else {
                        String format = code;
                        try {
                            ids = Integer.valueOf(code).longValue();
                            format = res.getStringDescription(ids.intValue());
                        } catch (Exception e ) {
                            ids = 0L;
                        }
                        semanticArray.add(EdgeVertex.fromSpace(ids, format));
                    }
                }

                code = geoAndGeo.getAdmin1Code();
                if (code == null && featureCodeEquals(fc, FeatureCode.ADM1) || featureCodeEquals(fc, FeatureCode.ADM1H))
                    code = id;
                if (code != null && !code.isEmpty()) {
                    if (!code.equals("00")) {
                        Long ids = getCountry() != null ? adm1.getAdmin1(getCountry().getKey()+"."+code) : null;
                        if (ids != null)
                        semanticArray.add(EdgeVertex.fromSpace(ids, res.getStringDescription(ids.intValue())));
                        else {
                            String format = code;
                            try {
                                ids = Integer.valueOf(code).longValue();
                                format = res.getStringDescription(ids.intValue());
                            } catch (Exception e) {
                                ids = 0L;
                            }
                            semanticArray.add(EdgeVertex.fromSpace(ids, format));
                        }
                    }
                }
            }

            /*if (!isNoMansLand && !isEarth) {
                if (!isContinent) {
                    code = getGeoname().getPrimaryCountryCode().toString();
                    array.add(code);

                    String continent = ash.getContinentIdFromState(code);
                    array.add(continent);
                } else {
                    array.add(ash.getContinentFromGeonamesId(continentId));
                }
            }*/
            extendListWithContinent2(semanticArray);

        } else {
            semanticArray.add(EdgeVertex.fromSpace(backupContinentId, ash.getContinentFromGeonamesId(backupContinentId)));
        }

        semanticArray.add(EdgeVertex.fromSpace(worldId, "Earth/World"));

        return semanticArray;
    }


    public boolean isCountry() {
        return countryCheck.isCountryId(getId());
    }

    public FeatureClass getFeatureClass() {
        return geoname != null ? geoname.getFeatureClass() : null;
    }

    public boolean isContinent() {
        return continentId != -1;
    }

    public Pair<String, Long> getCountry() {
        if (!isNoMansLand && !isEarth) {
            if (!isContinent) {
                String code = geoname.getPrimaryCountryCode().toString();
                return new Pair<>(code, countryCheck.getCountryId(code));
            } else {
                return null;
            }
        }
        return null;
    }

    /**
     *
     * @param array
     * @return          The id of the associated continent
     */
    public Long extendListWithContinent(@Nullable List<String> array) {
        if (!isNoMansLand && !isEarth) {
            if (!isContinent) {
                String code = geoname.getPrimaryCountryCode().toString();
                if (array != null) array.add(code);

                String continent = ash.getContinentIdFromState(code);
                if (array != null) array.add(continent);
                return ash.getContinentLongFromstate(code);
            } else {
                if (array != null) array.add(ash.getContinentFromGeonamesId(continentId));
                return continentId;
            }
        }
        return null;
    }

    public Long extendListWithContinent2(@Nullable List<SemanticNetworkEntryPoint> array) {
        if (!isNoMansLand && !isEarth) {
            if (!isContinent) {
                String code = geoname.getPrimaryCountryCode().toString();
                if (array != null) {
                    array.add(EdgeVertex.fromSpace(geoname.getPrimaryCountryCode().geonameID, geoname.getPrimaryCountryName()));
                }

                if (array != null) {
                    Long value = ash.getContinentLongFromstate(code);
                    if (value != null) {
                        array.add(EdgeVertex.fromSpace(value.intValue(), ash.getContinentFromGeonamesId(value)));
                    } // problem with Kosovo
                }
                return ash.getContinentLongFromstate(code);
            } else {
                if (array != null) array.add(EdgeVertex.fromSpace((int)continentId, ash.getContinentFromGeonamesId(continentId)));
                return continentId;
            }
        }
        return null;
    }

    public String getAdmin1Code() {
        return geoname == null ? null : geoname.getAdmin1Code();
    }

    public String getAdmin2Code() {
        return geoname == null ? null : geoname.getAdmin2Code();
    }

    public String getAdmin5Code() {
        return adm5.getAdmin5(geoname.getGeonameID() + "");
    }

    public Long getAdmin1GeoNamesId() {
        return adm1.getAdmin1(getCountry().getKey() + "." + getAdmin1Code());
    }

    public Long getAdmin2GeoNamesId() {
        return adm2.getAdmin2(getCountry().getKey() + "." + getAdmin1Code() + "." + getAdmin2Code());
    }
}
