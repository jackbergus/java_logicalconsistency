/*
 * Schema.java
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

 
package types;

import org.antlr.v4.runtime.tree.TerminalNode;
import ref.schemaParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Schema {
    public String relationName;
    public ArrayList<String> arguments;

    public Schema(String associatedRelation, ArrayList<String> fromAngestrengend) {
        this.relationName = associatedRelation;
        this.arguments = fromAngestrengend;
    }

    public Clause asClause() {
        return new Clause(asProposition());
    }

    public Proposition asProposition() {
        ArrayList<PropArgument> pa;
        pa = new ArrayList<>(arguments.size());
        for (int i = 0, argumentsSize = arguments.size(); i < argumentsSize; i++) {
            String x = arguments.get(i);
            pa.add(PropArgument.var(x));
        }
        return new Proposition(relationName, pa);
    }

    public Schema(String predname, String... args) {
        this.relationName = predname;
        this.arguments = new ArrayList<>(args.length);
        arguments.addAll(Arrays.asList(args));
    }

    public Schema(TerminalNode string, List<schemaParser.AngestrengendContext> string1) {
        relationName = string.getText();
        arguments = new ArrayList<>(string1.size());
        for (int i = 0, string1Size = string1.size(); i < string1Size; i++) {
            arguments.add(((schemaParser.IsstringContext)string1.get(i)).STRING().getText());
        }
    }

    public Schema(String string, List<schemaParser.AngestrengendContext> string1) {
        relationName = string;
        arguments = new ArrayList<>(string1.size());
        for (int i = 0, string1Size = string1.size(); i < string1Size; i++) {
            arguments.add(((schemaParser.IsstringContext)string1.get(i)).STRING().getText());
        }
    }

    @Override
    public String toString() {
        return relationName+
                arguments.stream().collect(Collectors.joining(",","(",")"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Schema schema = (Schema) o;
        return Objects.equals(relationName, schema.relationName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relationName);
    }
}
