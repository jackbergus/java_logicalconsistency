/*
 * MVD.java
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

import algos.Substitute;
import org.antlr.v4.runtime.tree.TerminalNode;
import ref.MVDRelaxationForTransfer;
import ref.schemaParser;

import java.util.*;

public class MVD {
    public Schema rel;
    public ArrayList<String> head, tail;

    public MVD(Schema s, List<schemaParser.AngestrengendContext> heads, List<schemaParser.AngestrengendContext> tails) {
        rel = s;
        head = new ArrayList<>(heads.size());
        for (int i = 0, string1Size = heads.size(); i < string1Size; i++) {
            TerminalNode tn = ((schemaParser.IsstringContext)heads.get(i)).STRING();
            if (!s.arguments.contains(tn.getText())) {
                System.err.println("ERROR: head field '"+tn.getText()+"' is not delcared in the schema for "+s.relationName);
                System.exit(1);
            }
            head.add(tn.getText());
        }

        tail = new ArrayList<>(tails.size());
        for (int i = 0, string1Size = tails.size(); i < string1Size; i++) {
            TerminalNode tn = ((schemaParser.IsstringContext)tails.get(i)).STRING();if (!s.arguments.contains(tn.getText())) {
                System.err.println("ERROR: tail field '"+tn.getText()+"' is not delcared in the schema for "+s.relationName);
                System.exit(1);
            }
            tail.add(tn.getText());
        }
        this.s = new Substitute<>(null);
    }

    @Override
    public String toString() { return String.join(",", head) + "->>" + String.join(",", tail); }

    Substitute<String, Proposition> s;

    public List<Rule> generateFDRules(ArrayList<MVDRelaxationForTransfer> relaxations) {
        if (relaxations == null || relaxations.isEmpty()) {
            return generateFDRulesFromSingleRelaxation(null);
        } else {
            ArrayList<Rule> rules = new ArrayList<>();
            for (MVDRelaxationForTransfer t : relaxations)
                rules.addAll(generateFDRulesFromSingleRelaxation(t));
            return rules;
        }
    }

    private List<Rule> generateFDRulesFromSingleRelaxation(MVDRelaxationForTransfer relaxation) {
        if (head.isEmpty())
            return Collections.emptyList();
        Proposition p1 = rel.asProposition(),
                p2 = rel.asProposition();
        ArrayList<Clause> headProps = new ArrayList<>(head.size()+2);
        Set<String> fv = p1.getFreeVariables();

        for (String x : fv) {
            s.setSubstitute(x);
            s.accept(new Substitute.SubPair<>(p1, x+"1"));
            s.accept(new Substitute.SubPair<>(p2, x+"2"));
            PropArgument args1 = PropArgument.var(x+"1");
            PropArgument args2 = PropArgument.var(x+"2");
            ArrayList<PropArgument> ls = new ArrayList<>(2);
            ls.add(args1);
            ls.add(args2);
            //headProps.add(new Clause(new Proposition("isA", ls)));
        }

        for (String x : head) {
            PropArgument args1 = PropArgument.var(x+"1");
            PropArgument args2 = PropArgument.var(x+"2");
            ArrayList<PropArgument> ls = new ArrayList<>(2);
            ls.add(args1);
            ls.add(args2);
            headProps.add(new Clause(new Proposition("isA", ls)));
        }

        headProps.add(0, new Clause(p2));
        headProps.add(0, new Clause(p1));
        List<Rule> rules = new ArrayList<>(tail.size());
        List<Proposition> fixpoint = null;
        for (String y : tail) {
            PropArgument args1 = PropArgument.var(y+"1");
            PropArgument args2 = PropArgument.var(y+"2");
            ArrayList<PropArgument> ls = new ArrayList<>(2);
            ls.add(args1);
            ls.add(args2);
            ArrayList<Clause> tailProps = new ArrayList<>(1);
            tailProps.add(new Clause(new Proposition("isA", ls)));
            ArrayList<Predicate> predicates = new ArrayList<>();
            predicates.add(new Predicate(y+"1"));
            predicates.add(new Predicate(y+"2"));

            if (relaxation != null) {
                if (fixpoint == null)
                    fixpoint = relaxation.prepareFixpointInit();
                for (String z : fv) {
                    fixpoint = relaxation.substitute(fixpoint, z, z+"1",z+"2");
                }

                for (Proposition prop : fixpoint) {
                    tailProps.add(relaxation.fromPropositionWithExProj(prop));
                }
            }
            rules.add(new Rule(headProps, predicates, tailProps));
        }

        return rules;
    }

}
