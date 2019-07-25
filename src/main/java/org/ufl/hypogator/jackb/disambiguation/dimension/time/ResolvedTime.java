/*
 * ISO8601Date.java
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

package org.ufl.hypogator.jackb.disambiguation.dimension.time;


import org.ufl.hypogator.jackb.disambiguation.Resolved;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResolvedTime implements Comparable<ResolvedTime>, Resolved {

    public final String timeISO;
    public final String matched;
    public Integer year;
    public Integer month;
    public Integer day;
    public Integer hour;
    public Integer min;
    private List<String> argumentParse;
    public static final PartialTimeComparator ptc = new PartialTimeComparator();

    private static Integer integerValue(String str) {
        try {
            return Integer.valueOf(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public ResolvedTime(String timeISO, String mathcedString) {
        this.timeISO = timeISO;
        matched = mathcedString;
        argumentParse = new ArrayList<>();
        if (timeISO != null && timeISO.contains("T")) {
            String time = timeISO.substring(timeISO.indexOf('T') + 1);
            String[] hour_min = time.split(":");
            if (hour_min.length == 2) {
                hour = integerValue(hour_min[0]);
                min = integerValue(hour_min[1]);
                timeISO = timeISO.substring(0, timeISO.indexOf('T'));
                argumentParse.add(min + "");
                argumentParse.add(hour + "");
            } else {
                argumentParse.add("XX");
                argumentParse.add("XX");
            }
        } else {
            argumentParse.add("XX");
            argumentParse.add("XX");
        }
        String[] date = timeISO != null ? timeISO.split("-") : new String[]{};


        if (date.length >= 3) {
            day = integerValue(date[2]);
            if (day != null)
                argumentParse.add(date[2]);
            else
                argumentParse.add("XX");
        } else {
            argumentParse.add("XX");
        }

        if (date.length >= 2) {
            month = integerValue(date[1]);
            if (month != null)
                argumentParse.add(date[1]);
            else
                argumentParse.add("XX");
        } else {
            argumentParse.add("XX");
        }

        if (date.length > 0) {
            year = integerValue(date[0]);
            if (year != null)
                argumentParse.add(date[0]);
            else
                argumentParse.add("XX");
        }
    }

    public boolean precedesEq(ResolvedTime time) {
        if (time == null) return false;

        int minCmp = Integer.min(getMin(), time.getMin());
        if (year == null || time.year == null) return false;
        int cmp = Integer.compare(year, time.year);
        if (cmp < 0) return true;
        else if (cmp > 0) return false;

        if (month == null || time.month == null) return minCmp == 4;
        cmp = Integer.compare(month, time.month);
        if (cmp < 0) return true;
        else if (cmp > 0) return false;

        if (day == null || time.day == null) return minCmp == 3;
        cmp = Integer.compare(day, time.day);
        if (cmp < 0) return true;
        else if (cmp > 0) return false;

        if (hour == null || time.hour == null) return minCmp == 2;
        cmp = Integer.compare(hour, time.hour);
        if (cmp < 0) return true;
        else if (cmp > 0) return false;

        if (min == null || time.min == null) return minCmp == 1;
        return Integer.compare(min, time.min) <= 0;
    }

    public boolean precedes(ResolvedTime time) {
        if (time == null) return false;
        return (!equals(time)) && precedesEq(time);
    }

    public boolean greaterEq(ResolvedTime time) {
        return time == null ? false : time.precedesEq(this);
    }

    public boolean greater(ResolvedTime time) {
        if (time == null) return false;
        return (!equals(time)) && greaterEq(time);
    }

    public int getMin() {
        if (min != null)
            return 0;
        else if (hour != null)
            return 1;
        else if (day != null)
            return 2;
        else if (month != null)
            return 3;
        else return 5;
    }

    public List<String> toHierarchy() {
        List<String> hierarchy = new ArrayList<>();
        boolean preserve = false;
        if (min != null) {
            preserve = true;
            hierarchy.add(argumentParse.get(4) + "-" + argumentParse.get(3) + "-" + argumentParse.get(2) + "T" + argumentParse.get(1) + "-" + argumentParse.get(0));
        }
        if (preserve || hour != null) {
            preserve = true;
            hierarchy.add(argumentParse.get(4) + "-" + argumentParse.get(3) + "-" + argumentParse.get(2) + "T" + argumentParse.get(1));
        }
        if (preserve || day != null) {
            preserve = true;
            hierarchy.add(argumentParse.get(4) + "-" + argumentParse.get(3) + "-" + argumentParse.get(2));
        }
        if (preserve || month != null) {
            hierarchy.add(argumentParse.get(4) + "-" + argumentParse.get(3));
        }
        if (argumentParse.size() >= 5) hierarchy.add(argumentParse.get(4));
        return hierarchy;
    }

    @Override
    public int compareTo(ResolvedTime o) {
        return ptc.semiCompare(this, o).get();
    }

    @Override
    public List<String> generateDisambiguationPath() {
        return toHierarchy();
    }

    public boolean equals(ResolvedTime o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return Objects.equals(year, o.year) &&
                Objects.equals(month, o.month) &&
                Objects.equals(day, o.day) &&
                Objects.equals(hour, o.hour) &&
                Objects.equals(min, o.min);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, month, day, hour, min);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ResolvedTime)) return false;
        return equals((ResolvedTime)o);
    }

}
