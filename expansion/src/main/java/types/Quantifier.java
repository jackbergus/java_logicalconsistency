/*
 * Quantifier.java
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

public class Quantifier implements Substitutable<String>, ConformityCheck, CopyConstructor<Quantifier> {
    public boolean isUniversal;
    public String varName;
    public String varType;
    public ArrayList<Predicate> predicates;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(isUniversal ? "\\forall " : "\\exists ");
        sb.append(varName);
        if (varType != null && (!varType.trim().equals("?"))) {
            sb.append(" : ");
            sb.append(varType);
        }
        if (Main.isLatex)
            sb.append(".");
        if (predicates != null && !predicates.isEmpty()) {
            if (Main.isLatex)
                sb.append(" \\wedge ");
            else
                sb.append(" with ");
            for (Iterator<Predicate> iterator = predicates.iterator(); iterator.hasNext(); ) {
                Predicate p = iterator.next();
                sb.append(p.toString());
                if (Main.isLatex && iterator.hasNext())
                    sb.append(" \\wedge ");
            }
        }
        if (!Main.isLatex) sb.append('.');
        return sb.toString();
    }

    public Quantifier(boolean isUniversal, String varName, String varType, ArrayList<Predicate> predicates) {
        this.isUniversal = isUniversal;
        this.varName = varName;
        this.varType = varType;
        this.predicates = predicates;
    }

    public Quantifier(Quantifier quantifier) {
        isUniversal = quantifier.isUniversal;
        varName = new String(quantifier.varName);
        varType = new String(quantifier.varType);
        predicates = CopyConstructor.listCopy(quantifier.predicates);
    }

    public Quantifier(schemaParser.ForallContext x) {
        this.isUniversal = true;
        this.varName = x.STRING(0).getText();
        this.varType = x.STRING(1).getText();
        this.predicates = new ArrayList<>(x.predicates().size());
        List<schemaParser.PredicatesContext> predicates1 = x.predicates();
        for (int i = 0, predicates1Size = predicates1.size(); i < predicates1Size; i++) {
            schemaParser.PredicatesContext pred = predicates1.get(i);
            predicates.add(new Predicate(pred));
        }
    }

    public Quantifier(schemaParser.ExistsContext x) {
        this.isUniversal = true;
        this.varName = x.STRING(0).getText();
        this.varType = x.STRING(1).getText();
        this.predicates = new ArrayList<>(x.predicates().size());
        List<schemaParser.PredicatesContext> predicates1 = x.predicates();
        for (int i = 0, predicates1Size = predicates1.size(); i < predicates1Size; i++) {
            schemaParser.PredicatesContext pred = predicates1.get(i);
        }
    }

    public static Quantifier exists(String var, String type, ArrayList<Predicate> predicates) {
        return new Quantifier(false, var, type, predicates);
    }

    public static Quantifier forall(String var, String type, ArrayList<Predicate> predicates) {
        return new Quantifier(true, var, type, predicates);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quantifier that = (Quantifier) o;
        return isUniversal == that.isUniversal &&
                Objects.equals(varName, that.varName) &&
                Objects.equals(varType, that.varType) &&
                Objects.equals(predicates, that.predicates);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isUniversal, varName, varType, predicates);
    }

    @Override
    public boolean checkConformity() {
        // The non conformity to a quantifier is expressed as all the predicates must refer to the quantifier's variable
        for (Predicate pred : predicates) {
            if (!Objects.equals(pred.varName, this.varName)) {
                System.err.println("ERROR: predicate "+pred+" contains a variable which does not match with the quantifier's variable, "+varName);
                return false;
            }
        }
        // If I never skipped a for cycle, then every predicate was conformant to the quantifier's name
        return true;
    }

    @Override
    public int inductiveCasesSize() {
        return 0;
    }

    @Override
    public int boundedCasesSize() {
        return 0;
    }

    @Override
    public Substitutable<String> getInductiveCase(int i) {
        return null;
    }

    @Override
    public String getBoundedCases(int i) {
        return null;
    }

    @Override
    public void updateCaseWith(int i, String newCase) {

    }

    @Override
    public void updateBoundedCaseWith(int i, String newCase) {

    }

    /**
     * A quantifier has no free variables
     * @return
     */
    @Override @JsonIgnore
    public Set<String> getFreeVariables() {
        return Collections.emptySet();
    }

    @Override
    public Quantifier copy() {
        return new Quantifier(this);
    }
}
