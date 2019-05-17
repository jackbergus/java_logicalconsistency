/*
 * Admin5.java
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
import java.util.Scanner;

/**
 * This class adds some further hierarchical information to the space. In particular, it adds the association
 * of each country to a continent, and associates each continent to the Earth. By doing so, the hierarchy is
 * completed.
 */
public class Admin5 {
    private final HashMap<String, String> geoIdToAdmin5;

    private static final Admin5 adm5 = new Admin5();

    private Admin5() {
        // Retrieving the continent information
        geoIdToAdmin5 = new HashMap<>();
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("adminCode5.txt").getFile());
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] args = line.split("\t");
                geoIdToAdmin5.put(args[0].trim(), args[1].trim());
            }
            scanner.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Admin5 instance() {
        return adm5;
    }

    public String getAdmin5(String geoId) {
        return geoIdToAdmin5.get(geoId);
    }
}
