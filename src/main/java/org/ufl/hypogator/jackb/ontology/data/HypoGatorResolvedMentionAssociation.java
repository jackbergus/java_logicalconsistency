/*
 * HypoGatorResolvedMentionAssociation.java
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

package org.ufl.hypogator.jackb.ontology.data;

import javafx.util.Pair;

import java.util.Objects;

public class HypoGatorResolvedMentionAssociation {
    private String label;
    private final TypedValue value;
    private int position;
    private final boolean isNegated;
    private final boolean isHedged;

    private final String mentionId;

    public HypoGatorResolvedMentionAssociation(String label, int i) {
        this.label = label;
        this.value = null;
        this.position = i;
        this.isNegated = false;
        this.isHedged = false;
        this.mentionId = null;
    }


    public HypoGatorResolvedMentionAssociation(String label, TypedValue value, int position, String mentionId) {
        this.label = label;
        this.value = value;
        this.position = position;
        this.mentionId = mentionId;
        isHedged = false;
        isNegated = false;
    }

    public HypoGatorResolvedMentionAssociation(String label, TypedValue value, int position, String mentionId, boolean isNegated, boolean isHedged) {
        this.label = label;
        this.value = value;
        this.position = position;
        this.mentionId = mentionId;
        this.isHedged = isHedged;
        this.isNegated = isNegated;
    }

    public HypoGatorResolvedMentionAssociation(String label, TypedValue value, int position, Pair<String,String> mentionId) {
        this.label = label;
        this.value = value;
        this.position = position;
        this.mentionId = mentionId.getKey();
        isNegated = mentionId.getValue().contains("not");
        isHedged = mentionId.getValue().contains("hedged");
    }

    @Override
    public String toString() {
        return "#"+position+"="+(value != null ? value.value() : "?")+":"+ label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HypoGatorResolvedMentionAssociation that = (HypoGatorResolvedMentionAssociation) o;
        return position == that.position &&
                Objects.equals(label, that.label) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label, value, position);
    }

    public String getLabel() {
        return label;
    }

    public TypedValue getValue() {
        return value;
    }

    public boolean isNegated() {
        return isNegated;
    }

    public boolean isHedged() {
        return isHedged;
    }

    public String getMentionId() {
        return mentionId;
    }

    public void setLabel(String s) {
        this.label = s;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
