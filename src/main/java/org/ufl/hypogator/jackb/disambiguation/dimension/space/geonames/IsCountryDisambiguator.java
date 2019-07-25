/*
 * IsCountryDisambiguator.java
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 * This class checks whether the current hierarchy element is a country or not. This utility element is required for
 * providing a correct hierarchical representation.
 */
public class IsCountryDisambiguator {
    public final HashSet<Long> geoIdToAdmin5;
    public final HashMap<String, Long> countryIdToLong;

    private static final IsCountryDisambiguator adm5 = new IsCountryDisambiguator();

    private IsCountryDisambiguator() {
        // Retrieving the continent information
        geoIdToAdmin5 = new HashSet<>();
        countryIdToLong = new HashMap<>();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("countryInfo.txt").getFile());
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] args = line.split("\t");
                countryIdToLong.put(args[0], Long.valueOf(args[16]));
                geoIdToAdmin5.add(Long.valueOf(args[16]));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static IsCountryDisambiguator instance() {
        return adm5;
    }

    public Long getCountryId(String countryCodeName) {
        return this.countryIdToLong.get(countryCodeName);
    }

    public boolean isCountryId(long geoId) {
        return geoIdToAdmin5.contains(geoId);
    }

        /*
ISO	ISO3	ISO-Numeric	fips	Country	Capital	Area(in sq km)	Population	Continent	tld	CurrencyCode	CurrencyName	Phone	Postal Code Format	Postal Code Regex	Languages	geonameid	neighbours	EquivalentFipsCode
         */
}