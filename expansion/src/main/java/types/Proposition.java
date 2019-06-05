/*
 * Proposition.java
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

import algos.CopyConstructor;
import algos.Substitutable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.antlr.v4.runtime.tree.TerminalNode;
import ref.schemaParser;

import java.util.*;
import java.util.stream.Collectors;

public class Proposition implements Substitutable<String>, CopyConstructor<Proposition> {
    public String relName;
    public ArrayList<PropArgument> args;

    public Proposition(String relName, ArrayList<PropArgument> args) {
        this.relName = relName;
        this.args = args;
    }

    public Proposition(Proposition proposition) {
        relName = new String(proposition.relName);
        args = CopyConstructor.listCopy(proposition.args);
    }

    public Proposition(String propName, String... propVars) {
        relName = new String(propName);
        args = new ArrayList<>(propVars.length);
        for (String x : propVars)
            args.add(PropArgument.var(x));
    }

    public void valuesAsVariables() {
        args.forEach(PropArgument::asVariable);
    }

    @Override
    public String toString() {
        return relName+"("+args.stream().map(x -> x.isVariable ? x.value : "\""+x.value+"\"").collect(Collectors.joining(","))+")";
    }

    public Proposition(TerminalNode string, schemaParser.StringlistContext stringlist) {
        relName = string.getText();
        args = new ArrayList<>(
                stringlist.angestrengend().size());
        for (schemaParser.AngestrengendContext x :
                stringlist.angestrengend()) {
            if (x instanceof schemaParser.IsstringContext) {
                args.add(PropArgument.var(
                        ((schemaParser.IsstringContext)x).STRING().getText()));
            } else {
                String value = ((schemaParser.IsvalueContext) x).VALUE().getText();
                args.add(PropArgument.val(value.substring(1, value.length()-1)));
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proposition that = (Proposition) o;
        return Objects.equals(relName, that.relName) &&
                Objects.equals(args, that.args);
    }

    @Override
    public int hashCode() {
        return Objects.hash(relName, args);
    }

    @JsonIgnore
    public Set<String> getVariables() {
        LinkedHashSet<String> toReturn = new LinkedHashSet<>();
        args.forEach(x -> {
            if (x.isVariable)
                toReturn.add(x.value);
        });
        return toReturn;
    }

    @JsonIgnore
    public List<String> getValues() {
        List<String> toReturn = new ArrayList<>();
        args.forEach(x -> {
            if (!x.isVariable)
                toReturn.add(x.value);
        });
        return toReturn;
    }

    @Override
    public Proposition copy() {
        return new Proposition(this);
    }

    @Override
    public int inductiveCasesSize() {
        return args.size();
    }

    @Override
    public int boundedCasesSize() {
        return 0;
    }

    @Override
    public Substitutable<String> getInductiveCase(int i) {
        return args.get(i);
    }

    @Override
    public String getBoundedCases(int i) {
        return null;
    }

    @Override
    public void updateCaseWith(int i, String newCase) {
        args.get(i).value = newCase;
    }

    @Override
    public void updateBoundedCaseWith(int i, String newCase) {
        //noop
    }

    @Override @JsonIgnore
    public Set<String> getFreeVariables() {
        Set<String> fv = new LinkedHashSet<>();
        for (PropArgument x : args)
            if (x.isVariable) fv.add(x.value);
        return fv;
    }
}
