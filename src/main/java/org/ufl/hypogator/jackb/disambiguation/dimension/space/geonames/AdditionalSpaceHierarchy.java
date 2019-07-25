/*
 * AdditionalSpaceHierarchy.java
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

package org.ufl.hypogator.jackb.disambiguation.dimension.space.geonames;

import javafx.util.Pair;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * This class adds some further hierarchical information to the space. In particular, it adds the association
 * of each country to a continent, and associates each continent to the Earth. By doing so, the hierarchy is
 * completed.
 */
public class AdditionalSpaceHierarchy {
    private final HashMap<String, String> country_to_continent;
    private final HashMap<String, String> continent_name_to_continentstringid;
    private final HashMap<String, Long> continent_name_to_geonamesid;
    private final HashMap<Long, String> continent_geonames_to_id;

    private static final AdditionalSpaceHierarchy ash = new AdditionalSpaceHierarchy();

    private AdditionalSpaceHierarchy() {
        // Retrieving the continent information
        country_to_continent = new HashMap<>();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("continent_to_country.txt").getFile());
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] args = line.split("\t");
                country_to_continent.put(args[1].trim(), args[0].trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Retrieving the continent information
        continent_name_to_continentstringid = new HashMap<>();
        continent_geonames_to_id = new HashMap<>();
        continent_name_to_geonamesid = new HashMap<>();
        file = new File(classLoader.getResource("continent_to_text.txt").getFile());
        //System.err.println(file);
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] args = line.split("\t");
                String[] continent_with_code = args[1].trim().split(",");
                continent_name_to_continentstringid.put(continent_with_code[0].trim(), args[0]);
                continent_name_to_geonamesid.put(args[0], Long.valueOf(continent_with_code[1]));
                continent_geonames_to_id.put(Long.valueOf(continent_with_code[1]), continent_with_code[0].trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static AdditionalSpaceHierarchy instance() {
        return ash;
    }

    public Pair<String, Long> isContinent(String continentString) {
        String k = continent_name_to_continentstringid.get(continentString);
        if (k == null)
            return null;
        return new Pair<>(k, continent_name_to_geonamesid.get(k));
    }

    public String getContinentFromGeonamesId(long stateCode) {
        return continent_geonames_to_id.get(stateCode);
    }

    public String getContinentIdFromState(String stateCode) {
        return country_to_continent.get(stateCode);
    }

    public Long getContinentLongFromstate(String stateCode) {
        String continentWord = getContinentIdFromState(stateCode);
        if (continentWord == null)
            return null;
        return continent_name_to_geonamesid.get(continentWord);
    }
}
