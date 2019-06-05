/*
 * Rule.java
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

import algos.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import main.Main;

import java.util.*;
import java.util.stream.Collectors;

public class Rule implements Substitutable<String>, ConformityCheck, CopyConstructor<Rule> {
    public boolean isFinalBottom;
    public ArrayList<Clause> body;
    public ArrayList<Predicate> joinPredicates;
    public ArrayList<Clause> head;
    transient Substitute<String, Rule> l;

    public void valuesAsVariables() {
        body.forEach(Clause::valuesAsVariables);
        head.forEach(Clause::valuesAsVariables);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(Main.isLatex ? "\\begin{multline}\n" : "rule ");
        for (Iterator<Clause> iterator = body.iterator(); iterator.hasNext(); ) {
            Clause clause = iterator.next();
            sb.append(clause).append(" ");
            if (Main.isLatex /*&& iterator.hasNext()*/)
                sb.append("\\wedge \\\\\n");
        }
        if (joinPredicates!= null && !joinPredicates.isEmpty()) {
            sb.append(Main.isLatex ? " \\wedge " : " with ");
            for (Iterator<Predicate> iterator = joinPredicates.iterator(); iterator.hasNext(); ) {
                Predicate x = iterator.next();
                sb.append(x).append(" ");
                if (Main.isLatex && iterator.hasNext())
                    sb.append("\\wedge ");
            }
        }
        sb.append(Main.isLatex ? "\\\\\n \\Rightarrow " : " => ");
        if (isFinalBottom) {
            sb.append(Main.isLatex ? "\\bot" : "False");
        } else {
            for (Iterator<Clause> iterator = head.iterator(); iterator.hasNext(); ) {
                Clause x = iterator.next();
                sb.append(x).append(" ");
                if (Main.isLatex && iterator.hasNext())
                    sb.append("\\vee \\\\\n\\quad");
            }
        }
        if (Main.isLatex) sb.append("\n\\end{multline}\n\n");
        return sb.toString();
    }

    public String getHumanReadable() {
        return toString();
    }

    public Rule(ArrayList<Clause> body, ArrayList<Predicate> joinPredicates, ArrayList<Clause> head) {
        this.isFinalBottom = false;
        this.body = body;
        this.joinPredicates = joinPredicates;
        this.head = head;
    }

    public Rule(boolean isFinalBottom, ArrayList<Clause> body, ArrayList<Predicate> joinPredicates, ArrayList<Clause> head) {
        this.isFinalBottom = isFinalBottom;
        this.body = body;
        this.joinPredicates = joinPredicates;
        this.head = head;
    }

    public Rule(Rule copy) {
        isFinalBottom = copy.isFinalBottom;
        body = CopyConstructor.listCopy(copy.body);
        joinPredicates = CopyConstructor.listCopy(copy.joinPredicates);
        head = CopyConstructor.listCopy(copy.head);
    }

    /**
     * Returns whether the current object is well formed, that is whether all the predicates refer to elements in the rules
     * that are actually provided in the body.
     *
     * @return
     */
    @Override
    public boolean checkConformity() {
        // Checking the conformity in the body
        for (int i = 0, bodySize = body.size(); i < bodySize; i++) {
            Clause x = body.get(i);
            if (!x.checkConformity())
                return false;
        }

        // Checking the conformity in the head
        for (int i = 0, bodySize = head.size(); i < bodySize; i++) {
            Clause x = head.get(i);
            if (!x.checkConformity())
                return false;
        }

        // Checking whether the predicates
        Set<String> expectedFreeVariables = joinPredicates.stream().map(x -> x.varName).collect(Collectors.toSet());
        Set<String> actualFreeVariables = new HashSet<>();
        head.forEach(x -> actualFreeVariables.addAll(x.getFreeVariables()));
        return actualFreeVariables.containsAll(expectedFreeVariables);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rule rule = (Rule) o;
        return isFinalBottom == rule.isFinalBottom &&
                Objects.equals(body, rule.body) &&
                Objects.equals(joinPredicates, rule.joinPredicates) &&
                Objects.equals(head, rule.head);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isFinalBottom, body, joinPredicates, head);
    }

    @Override
    public int inductiveCasesSize() {
        return body.size()+head.size()+joinPredicates.size();
    }

    @Override
    public Substitutable<String> getInductiveCase(int i) {
        return i < body.size() ? body.get(i) : (i < body.size() + head.size() ? head.get(i - body.size()) : joinPredicates.get(i - body.size() - head.size()));
    }

    @Override
    public int boundedCasesSize() {
        // noop
        return 0;
    }

    @Override
    public String getBoundedCases(int i) {
        // noop
        return null;
    }

    @Override
    public void updateCaseWith(int i, String newCase) {
        throw new RuntimeException("Error: Rule has not cases to replace. The induction should be transferred deeper at the clause level");
    }

    @Override
    public void updateBoundedCaseWith(int i, String newCase) {
        // noop
    }

    @Override @JsonIgnore
    public Set<String> getFreeVariables() {
        LinkedHashSet<String> fvs = new LinkedHashSet<>();
        body.forEach(x -> fvs.addAll(x.getFreeVariables()));
        head.forEach(x -> fvs.addAll(x.getFreeVariables()));
        return fvs;
    }

    public Rule normalizeForDetection(HashMap<String, String> variablePredicateToExpandedName) {
        VariableGenerator gen = new VariableGenerator();
        Rule copy = this.copy();
        copy.valuesAsVariables();
        Set<String> gfv = copy.getFreeVariables();
        if (l == null) {
            l = new Substitute<>(null);
        }
        for (String fv : gfv) {
            l.setSubstitute(fv);
            l.accept(new Substitute.SubPair<>(copy, gen.get()));
        }
        PredicateGenerator pgen = new PredicateGenerator();
        copy.body.forEach(clause -> {
            String p = pgen.get();
            variablePredicateToExpandedName.put(p, clause.prop.relName);
            clause.prop.relName = p;
        });
        copy.head.forEach(clause -> {
            String p = pgen.get();
            variablePredicateToExpandedName.put(p, clause.prop.relName);
            clause.prop.relName = p;
        });
        return copy;
    }

    @Override
    public Rule copy() {
        return new Rule(this);
    }
}
