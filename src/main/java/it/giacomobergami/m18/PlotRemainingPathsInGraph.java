/*
 * Main.java
 * This file is part of KnowledgeBaseExpansion
 *
 * Copyright (C) 2019 - Giacomo Bergami
 *
 * KnowledgeBaseExpansion is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * KnowledgeBaseExpansion is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with KnowledgeBaseExpansion. If not, see <http://www.gnu.org/licenses/>.
 */
package it.giacomobergami.m18;

import it.giacomobergami.m18.graph_run.RunQuery;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

/**
 * Plotting the remaining vertices and edges that are not covered by the path search
 */
public class PlotRemainingPathsInGraph {

    static void parseRule(final Reader code) throws IOException {
        RunQuery runQuery = new RunQuery(code);
        runQuery.debugPlot();
    }

    public static void main(String args[]) throws IOException {
        parseRule(new FileReader("schema_definition3.txt"));
    }

}
