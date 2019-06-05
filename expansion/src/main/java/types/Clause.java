/*
 * Clause.java
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

import algos.ConformityCheck;
import algos.CopyConstructor;
import algos.Substitutable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import main.Main;
import ref.schemaParser;

import java.util.*;

public class Clause implements Substitutable<String>, ConformityCheck, CopyConstructor<Clause> {
    public ArrayList<Quantifier> foralls;
    public ArrayList<Quantifier> exists;
    public boolean isNegated;
    public Proposition prop;
    transient public ArrayList<Predicate> refactoredPredicates;
    transient private boolean isRefactored;

    @Override
    public String toString() {
        refactor();
        StringBuilder sb = new StringBuilder();
        for (Quantifier x : foralls) {
            sb.append(x.toString()).append(" ");
        }
        for (Quantifier x : exists) {
            sb.append(x.toString()).append(" ");
        }
        if (isNegated)
            sb.append(Main.isLatex ? " \\neg " : "¬");
        sb.append(prop.toString());
        return sb.toString();
    }

    public void valuesAsVariables() {
        prop.valuesAsVariables();
    }

    public Clause(Proposition prop) {
        this.foralls = new ArrayList<>();
        this.exists = new ArrayList<>();
        this.isNegated = false;
        this.prop = prop;
        this.refactoredPredicates = new ArrayList<>();
    }

    public Clause(ArrayList<Quantifier> foralls, ArrayList<Quantifier> exists, boolean isNegated, Proposition prop, ArrayList<Predicate> refactoredPredicates) {
        this.foralls = foralls;
        this.exists = exists;
        this.isNegated = isNegated;
        this.prop = prop;
        this.refactoredPredicates = refactoredPredicates;
    }

    public Clause(Clause clause) {
        foralls = CopyConstructor.listCopy(clause.foralls);
        exists = CopyConstructor.listCopy(clause.exists);
        isNegated = clause.isNegated;
        prop = clause.prop.copy();
        refactoredPredicates = CopyConstructor.listCopy(clause.refactoredPredicates);
        isRefactored = clause.isRefactored;
    }

    public Clause(String propName, String... propArgs) {
        this(Collections.emptyList(), Collections.emptyList(), false, new Proposition(propName, propArgs), new ArrayList<>());
    }

    public Clause(String propName, ArrayList<PropArgument> propArgs) {
        this(Collections.emptyList(), Collections.emptyList(), false, new Proposition(propName, propArgs), new ArrayList<>());
    }

    public Clause(List<schemaParser.ForallContext> forall, List<schemaParser.ExistsContext> exists, boolean isNegated, Proposition prop, ArrayList<Predicate> refactoredPredicates) {
        this.foralls = new ArrayList<>(forall.size());
        for (schemaParser.ForallContext x : forall) {
            this.foralls.add(new Quantifier(x));
        }

        this.exists = new ArrayList<>(exists.size());
        for (schemaParser.ExistsContext x : exists) {
            this.exists.add(new Quantifier(x));
        }

        this.isNegated = isNegated;
        this.prop = prop;
        this.refactoredPredicates = refactoredPredicates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Clause clause = (Clause) o;
        return isNegated == clause.isNegated &&
                Objects.equals(foralls, clause.foralls) &&
                Objects.equals(exists, clause.exists) &&
                Objects.equals(prop, clause.prop) &&
                Objects.equals(refactoredPredicates, clause.refactoredPredicates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(foralls, exists, isNegated, prop, refactoredPredicates);
    }

    @Override
    public int inductiveCasesSize() {
        return 1;
    }

    @Override
    public Substitutable<String> getInductiveCase(int i) {
        return i == 0 ? prop : null;
    }

    @Override
    public int boundedCasesSize() {
        return foralls.size()+exists.size();
    }

    @Override
    public String getBoundedCases(int i) {
        return i < foralls.size() ? foralls.get(i).varName : exists.get(i - foralls.size()).varName;
    }

    @Override
    public void updateCaseWith(int i, String newCase) {
        throw new RuntimeException("Error: Rule has not cases to replace. The induction should be transferred deeper at the proposition level");
    }

    @Override
    public void updateBoundedCaseWith(int i, String newCase) {
        if (i < foralls.size()) {
            foralls.get(i).varName = newCase;
            foralls.get(i).predicates.forEach(x -> x.varName = newCase);
        } else {
            exists.get(i - foralls.size()).varName = newCase;
            exists.get(i - foralls.size()).predicates.forEach(x -> x.varName = newCase);
        }
    }

    @Override @JsonIgnore
    public Set<String> getFreeVariables() {
        Set<String> fv = new LinkedHashSet<>(prop.getVariables());
        foralls.forEach(x -> fv.remove(x.varName));
        exists.forEach(x -> fv.remove(x.varName));
        return fv;
    }

    @Override
    public boolean checkConformity() {
        Set<String> variables = prop.getVariables();

        // Before refactoring, I check the conformity of each argument.
        for (int i = 0, forallsSize = foralls.size(); i < forallsSize; i++) {
            Quantifier x = foralls.get(i);
            if (!x.checkConformity())
                return false;
            if (!variables.contains(x.varName)) {
                System.err.println("ERROR: the universal quantifier "+x+" bounds a varName "+x.varName+" that does not appear in the proposition's variables ("+variables+")");
                return false;
            }
        }
        for (int i = 0, forallsSize = exists.size(); i < forallsSize; i++) {
            Quantifier x = exists.get(i);
            if (!x.checkConformity())
                return false;
            if (!variables.contains(x.varName)) {
                System.err.println("ERROR: the existential quantifier "+x+" bounds a varName "+x.varName+" that does not appear in the proposition's variables ("+variables+")");
                return false;
            }
        }

        // At this step, every subcase is compliant.
        // If the current clause is not refactored, do it!
        refactor();

        //After the refactoring, the clause is really conformant.
        return true;
    }

    private void refactor() {
        if (!isRefactored) {
            foralls.forEach(x -> refactoredPredicates.addAll(x.predicates));
            exists.forEach(x -> refactoredPredicates.addAll(x.predicates));
            isRefactored = true;
        }
    }

    @Override
    public Clause copy() {
        return new Clause(this);
    }
}
