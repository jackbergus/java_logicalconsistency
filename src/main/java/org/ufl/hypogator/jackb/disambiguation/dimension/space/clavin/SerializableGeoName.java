/*
 * SerializableGeoName.java
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

import com.bericotech.clavin.gazetteer.BasicGeoName;
import com.bericotech.clavin.gazetteer.CountryCode;
import com.bericotech.clavin.gazetteer.FeatureClass;
import com.bericotech.clavin.gazetteer.FeatureCode;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SerializableGeoName extends BasicGeoName {

    /**
     * Sole constructor for {@link BasicGeoName} class.
     * <p>
     * Encapsulates a gazetteer record from the GeoNames database.
     *
     * @param geonameID             unique identifier
     * @param name                  name of this location
     * @param asciiName             plain text version of name
     * @param alternateNames        list of alternate names, if any
     * @param preferredName         the preferred name, if known
     * @param latitude              lat coord
     * @param longitude             lon coord
     * @param featureClass          general type of feature (e.g., "Populated place")
     * @param featureCode           specific type of feature (e.g., "capital of a political entity")
     * @param primaryCountryCode    ISO country code
     * @param alternateCountryCodes list of alternate country codes, if any (i.e., disputed territories)
     * @param admin1Code            FIPS code for first-level administrative subdivision (e.g., state or province)
     * @param admin2Code            second-level administrative subdivision (e.g., county)
     * @param admin3Code            third-level administrative subdivision
     * @param admin4Code            fourth-level administrative subdivision
     * @param population            number of inhabitants
     * @param elevation             elevation in meters
     * @param digitalElevationModel another way to measure elevation
     * @param timezone              timezone for this location
     * @param modificationDate      date of last modification for the GeoNames record
     * @param gazetteerRecord       the gazetteer record
     */
    @JsonCreator
    public SerializableGeoName(@JsonProperty("geonameID") int geonameID,
                               @JsonProperty("name") String name,
                               @JsonProperty("asciiName") String asciiName,
                               @JsonProperty("alternateNames") List<String> alternateNames,
                               @JsonProperty("preferredName") String preferredName,
                               @JsonProperty("latitude") Double latitude,
                               @JsonProperty("longitude") Double longitude,
                               @JsonProperty("featureClass") FeatureClass featureClass,
                               @JsonProperty("featureCode") FeatureCode featureCode,
                               @JsonProperty("primaryCountryCode") CountryCode primaryCountryCode,
                               @JsonProperty("alternateCountryCodes") List<CountryCode> alternateCountryCodes,
                               @JsonProperty("admin1Code") String admin1Code,
                               @JsonProperty("admin2Code") String admin2Code,
                               @JsonProperty("admin3Code") String admin3Code,
                               @JsonProperty("admin4Code") String admin4Code,
                               @JsonProperty("population") Long population,
                               @JsonProperty("elevation") Integer elevation,
                               @JsonProperty("digitalElevationModel") Integer digitalElevationModel,
                               @JsonProperty("timezone") TimeZone timezone,
                               @JsonProperty("modificationDate") Date modificationDate,
                               @JsonProperty("gazetteerRecord") String gazetteerRecord) {
        super(geonameID, name, asciiName, alternateNames, preferredName, latitude, longitude, featureClass, featureCode, primaryCountryCode, alternateCountryCodes, admin1Code, admin2Code, admin3Code, admin4Code, population, elevation, digitalElevationModel, timezone, modificationDate, gazetteerRecord);
    }

    public SerializableGeoName(BasicGeoName fromSelf) {
        this(fromSelf.getGeonameID(),
                fromSelf.getName(),
                fromSelf.getAsciiName(),
                fromSelf.getAlternateNames(),
                fromSelf.getPreferredName(),
                fromSelf.getLatitude(),
                fromSelf.getLongitude(),
                fromSelf.getFeatureClass(),
                fromSelf.getFeatureCode(),
                fromSelf.getPrimaryCountryCode(),
                fromSelf.getAlternateCountryCodes(),
                fromSelf.getAdmin1Code(),
                fromSelf.getAdmin2Code(),
                fromSelf.getAdmin3Code(),
                fromSelf.getAdmin4Code(),
                fromSelf.getPopulation(),
                fromSelf.getElevation(),
                fromSelf.getDigitalElevationModel(),
                fromSelf.getTimezone(),
                fromSelf.getModificationDate(),
                fromSelf.getGazetteerRecord());
    }

    public SerializableGeoName() {
        this(0, "", "", new ArrayList<>(), "", 0.0, 0.0,
                FeatureClass.NULL, FeatureCode.NULL, CountryCode.NULL, new ArrayList<>(),
                "", "", "", "",
                0L, 0, 0, TimeZone.getDefault(), new Date(), "");
    }

}
