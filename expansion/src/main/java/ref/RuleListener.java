/*
 * Listener.java
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

 
package ref;// Generated from schema.g4 by ANTLR 4.7.1

import algos.Substitute;
import algos.VariableGenerator;
import main.Main;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import queries.sql.v1.QueryGenerationConf;
import queries.sql.v1.QueryCollection;
import queries.sql.v1.SelectFromWhere;
import queries.sql.v1.SetOperations;
import types.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class provides an empty implementation of {@link schemaListener},
 * which can be extended to create a listener which only needs to handle a subset
 * of the available methods.
 */
public class RuleListener implements schemaListener {

	public HashMap<Rule, Integer> ruleTabToId;
	public HashMap<String, Schema> schema;
	public HashMap<String, Substitute.SubPair<Schema, Rule>> macros;
	public HashMap<Schema, ArrayList<MVD>> mvdMap;
	public HashMap<Rule, ArrayList<Rule>> ruleTabClassification;
	public HashMap<Integer, HashMap<Integer, Rule>> ruleTabClassification4DB;
	public HashMap<String, ArrayList<String>> schemaClassificationType;
	public HashMap<String, EventAssociation> eventAssociation;
	public HashMap<Schema, ArrayList<MVDRelaxationForTransfer>> multipleRelaxationForEvent;
	public HashMap<Integer, HashMap<String, String>> ruleToResolvedPredicates;
    private String previousDeclaration = null;
    AtomicInteger tabNo, ruleNo;

	public long countArrayListMVD() {
		return mvdMap.values().stream().mapToLong(ArrayList::size).sum();
	}

	public long countAllRules() {
		return ruleTabClassification.values().stream().mapToLong(ArrayList::size).sum();
	}

	public void printQueriesFromTabs(QueryGenerationConf qgc) {
		for (Rule r : ruleTabClassification.keySet()) {
			SelectFromWhere macroQuery = qgc.compileQuery(r);
			if (macroQuery == null)
				continue; // TODO: implement this case
			int tabId = ruleTabToId.get(r);

			Stream<HashMap<String, String>> xyz = ruleTabClassification4DB.get(ruleTabToId.get(r)).keySet().stream().map(ruleToResolvedPredicates::get);



			/*HashMap<Integer, Rule> m = ruleTabClassification4DB.get(tabId);
			QueryCollection qc = new QueryCollection(SetOperations.UNION, false, m.size());
			for (Integer ruleId : ruleTabClassification4DB.get(tabId).keySet()) {
				HashMap<String, String> conversion = ruleToResolvedPredicates.get(ruleId);
				qc.add(macroQuery.copy().instantiateQuery(conversion));
			}*/
			System.out.println(tabId+") "+r+"\n================================================\n"+macroQuery.transformFromLegacy().toString(xyz)+"\n\n\n");

		}
	}

	/**
	 * Initializes the class for visiting the declaration
	 */
	@Override public void enterProgram(schemaParser.ProgramContext ctx) {
		if (schema == null || mvdMap == null) {
			schema = new HashMap<>();
			mvdMap = new HashMap<>();
			ruleTabToId = new HashMap<>();
			ruleTabClassification = new HashMap<>();
			macros = new HashMap<>();
			schemaClassificationType = new HashMap<>();
			eventAssociation = new HashMap<>();
			multipleRelaxationForEvent = new HashMap<>();
			ruleTabClassification4DB = new HashMap<>();
			tabNo = new AtomicInteger(1);
			ruleNo = new AtomicInteger(1);
			ruleToResolvedPredicates = new HashMap<>();
		} else {
			macros.clear();
			schema.clear();
			mvdMap.clear();
			ruleTabClassification.clear();
			schemaClassificationType.clear();
			eventAssociation.clear();
			multipleRelaxationForEvent.clear();
			ruleTabClassification4DB.clear();
			ruleTabToId.clear();
			ruleToResolvedPredicates.clear();
		}
	}


	/**
	 * Add some schema definition of the event/relationship
	 */
	@Override public void enterRel_delcare(schemaParser.Rel_delcareContext ctx) {
		previousDeclaration = ctx.STRING().getText();
		Schema s = new Schema(ctx.STRING(), ctx.stringlist().angestrengend());
		Schema val = schema.put(ctx.STRING().getText(), s);
		if (val != null) {
			System.err.println("ERROR: relation "+ctx.STRING().getText()+" was already defined");
			System.exit(1);
		}
		String type = ctx.EVENT() != null ? "event" : "relation";
		ArrayList<String> ls = schemaClassificationType.get(type);
		if (ls == null) {
			ls = new ArrayList<>();
			ls.add(previousDeclaration);
			schemaClassificationType.put(type, ls);
		} else {
			ls.add(previousDeclaration);
		}
	}

	/**
	 * Reads the declaration of a multivalued functional dependency, plus generates and stores the associated rules
	 */
	@Override public void enterFdep_declare(schemaParser.Fdep_declareContext ctx) {
		String relName = ctx.STRING().getText();
		if (relName.equals("owned"))
			System.err.println("DEBUG");
		Schema s = schema.get(relName);
		if (s == null) {
			System.err.println("ERROR: schema element '" + relName + "' has not been delcared");
			System.exit(1);
		} else {
			if (!mvdMap.containsKey(s))
				mvdMap.put(s, new ArrayList<>());

			// Creating the new functional dependency
			MVD mvd = new MVD(s, ctx.stringlist(0).angestrengend(), ctx.stringlist(1).angestrengend());

			// Associating the dependency to the current element of the schema
			mvdMap.get(s).add(mvd);

			// Generating and compiling the rules
			mvd.generateFDRules(this.multipleRelaxationForEvent.get(s)).forEach(this::addRule);
		}
	}

	/**
	 * Feeds the parsed rule to the compiler
	 */
	@Override public void enterRule(schemaParser.RuleContext ctx) {
		Rule r = generateRuleFromArguments(ctx.BOT(), ctx.moreClauses().size(), ctx.moreClauses(0), ctx.moreClauses(1), ctx.predicates());
		addRule(r);
	}

	private void addRule(Rule r) {
		// Definition: syntax checking
		for (Clause ch : r.body) {
			if (!schema.containsKey(ch.prop.relName)) {
				System.err.println("Error: body relation '"+ ch.prop.relName + "' has not been previously declared");
				System.exit(1);
			}
		}
		for (Clause ch : r.head) {
			if (!schema.containsKey(ch.prop.relName)) {
				System.err.println("Error: head relation '"+ ch.prop.relName + "' has not been previously declared");
				System.exit(1);
			}
		}

		// Generating the key for tabbing the rules
		HashMap<String, String> m = new HashMap<>();
		Rule key = r.normalizeForDetection(m);
		ArrayList<Rule> ls = ruleTabClassification.get(key);
		// Inserting the relation to the multihashmap
		if (ls == null) {
			ArrayList<Rule> rl = new ArrayList<>();
			rl.add(r);
			HashMap<Integer, Rule> ir = new HashMap<>();
			int ruleId = ruleNo.getAndIncrement();
			ir.put(ruleId, r);
			ruleToResolvedPredicates.put(ruleId, m);
			int tabN = tabNo.getAndIncrement();
			ruleTabClassification.put(key, rl);
			ruleTabClassification4DB.put(tabN, ir);
			ruleTabToId.put(key, tabN);
		} else {
			ls.add(r);
			Integer tabId = ruleTabToId.get(key);
			HashMap<Integer, Rule> mapToUpdate = ruleTabClassification4DB.get(tabId);
			int ruleId = ruleNo.getAndIncrement();
			ruleToResolvedPredicates.put(ruleId, m);
			mapToUpdate.put(ruleId, r);
		}
	}

	/**
	 * This command generates the existential rules for all the entities/fillers involved within the given rule
	 *
	 * @param ctx the parse tree
	 */
	@Override
	public void enterEnexists(schemaParser.EnexistsContext ctx) {
		Schema s = schema.get(previousDeclaration);
		List<String> time = (ctx.intime() != null && !ctx.intime().stringlist().angestrengend().isEmpty()) ?
				ctx.intime().stringlist().angestrengend().stream().map(RuleContext::getText).collect(Collectors.toList()) : new ArrayList<>();
		for (String t : time) {
			if (!s.arguments.contains(t)) {
				System.err.println("ERROR: time argument '"+ t +"' required by the rule is not declared in the schema of '"+s.relationName+"' = "+s.arguments);
				System.exit(1);
			}
		}
		List<String> space = (ctx.inspace() != null && !ctx.inspace().stringlist().angestrengend().isEmpty()) ?
				ctx.inspace().stringlist().angestrengend().stream().map(RuleContext::getText).collect(Collectors.toList()) : new ArrayList<>();
		for (String t : time) {
			if (!s.arguments.contains(t)) {
				System.err.println("ERROR: time argument '"+ t +"' required by the rule is not declared in the schema of '"+s.relationName+"' = "+s.arguments);
				System.exit(1);
			}
		}
		for (String t : space) {
			if (!s.arguments.contains(t)) {
				System.err.println("ERROR: time argument '"+ t +"' required by the rule is not declared in the schema of '"+s.relationName+"' = "+s.arguments);
				System.exit(1);
			}
		}

		List<String> toHaveFields = (ctx.stringlist() != null && !ctx.stringlist().angestrengend().isEmpty())  ? ctx.stringlist().angestrengend().stream().map(RuleContext::getText).collect(Collectors.toList()) : new ArrayList<>(s.arguments);
		List<String> toRemoveFileds = (ctx.except() != null && !ctx.except().stringlist().angestrengend().isEmpty()) ?
				ctx.except().stringlist().angestrengend().stream().map(RuleContext::getText).collect(Collectors.toList()) : new ArrayList<>();
		toRemoveFileds.addAll(space);
		toRemoveFileds.addAll(time);
		toHaveFields.removeAll(toRemoveFileds);
		if (previousDeclaration == null) {
			System.err.println("ERROR: no previous event/relationship declaration was provided");
			System.exit(1);
		}
		if (!s.arguments.containsAll(toHaveFields)) {
			for (String toCheck : toHaveFields) {
				if (!s.arguments.contains(toCheck))
					System.err.println("ERROR: argument '"+ toCheck +"' required by the rule is not declared in the schema of '"+s.relationName+"' = "+s.arguments);
			}
			System.exit(1);
		}

		ArrayList<Clause> clauses = new ArrayList<>();
		clauses.add(s.asClause());
		ArrayList<Rule> rules = new ArrayList<>();
		for (String fieldVAr : toHaveFields) {
			for (String tVar : time) {
				ArrayList<Predicate> isNotNull = new ArrayList<>();
				isNotNull.add(new Predicate(tVar));
				isNotNull.add(new Predicate(fieldVAr));
				{
					ArrayList<Clause> inPresentExist = new ArrayList<>();
					inPresentExist.add(new Clause("ex", fieldVAr, tVar));
					rules.add(new Rule(clauses, isNotNull, inPresentExist));
				}

				for (String sp : space) {
					isNotNull.add(new Predicate(sp));
					ArrayList<Clause> inPresentExist = new ArrayList<>();
					inPresentExist.add(new Clause("be", fieldVAr, sp, tVar));
					rules.add(new Rule(clauses, isNotNull, inPresentExist));
				}
			}
		}
		rules.forEach(this::addRule);
	}

	/**
	 * A macro is just a rule that is associated to a function, which is the argument to be replaced within the rule.
	 * The rule will be generated for each application of the function to an existing event/relationship
	 *
	 * @param ctx the parse tree
	 */
	@Override
	public void enterMacro_definition(schemaParser.Macro_definitionContext ctx) {
		String macroName = ctx.STRING(1).getText();
		if (macros.containsKey(macroName)) {
			System.err.println("Macro with name '"+macroName+"' already exists");
			System.exit(1);
		} else {
			Schema s = new Schema(ctx.STRING(0), ctx.stringlist().angestrengend());
			Rule r = generateRuleFromArguments(ctx.BOT(), ctx.moreClauses().size(), ctx.moreClauses(0), ctx.moreClauses(1), ctx.predicates());
			macros.put(macroName, new Substitute.SubPair<>(s, r));
		}
	}

	private Rule generateRuleFromArguments(TerminalNode botTerminal, int moreClausesContexts,
										   schemaParser.MoreClausesContext rawBodyClauses,
										   schemaParser.MoreClausesContext rawHeadClauses,
										   List<schemaParser.PredicatesContext> predicates) {
		boolean isFinalBottom = botTerminal != null || moreClausesContexts == 1;

		ArrayList<Clause> bodyClauses;
		schemaParser.MoreClausesContext mc = rawBodyClauses;
		if (mc != null) {
			bodyClauses = new ArrayList<>(mc.clause().size());
			for (schemaParser.ClauseContext bodyElement : mc.clause()) {
				Clause headClause = new Clause(bodyElement.forall(), bodyElement.exists(), bodyElement.NEG() != null, new Proposition(bodyElement.STRING(), bodyElement.stringlist()), new ArrayList<>());
				bodyClauses.add(headClause);
			}
		} else {
			bodyClauses = new ArrayList<>();
		}

		ArrayList<Clause> headClauses;
		if (!isFinalBottom) {
			mc = rawHeadClauses;
			headClauses = new ArrayList<>(mc.clause().size());
			for (schemaParser.ClauseContext bodyElement : mc.clause()) {
				Clause headClause = new Clause(bodyElement.forall(), bodyElement.exists(), bodyElement.NEG() != null, new Proposition(bodyElement.STRING(), bodyElement.stringlist()), new ArrayList<>());
				headClauses.add(headClause);
			}
		} else {
			headClauses = new ArrayList<>();
		}


		ArrayList<Predicate> joinPredicates;
		List<schemaParser.PredicatesContext> preds = predicates;
		if (preds != null) {
			joinPredicates = new ArrayList<>(preds.size());
			for (schemaParser.PredicatesContext ctx2 : preds) {
				joinPredicates.add(new Predicate(ctx2));
			}
		} else {
			joinPredicates = new ArrayList<>();
		}

		return new Rule(isFinalBottom, bodyClauses, joinPredicates, headClauses);
	}

	/**
	 * This function provides the macro expansion, that is the generation of all the rules
	 * @param ctx the parse tree
	 */
	@Override
	public void enterMacro_expand(schemaParser.Macro_expandContext ctx) {
		String macroName /*= ctx.STRING(0).getText()*/;
		List<String> iter;
		if (ctx.RELATION() != null) {
			iter = this.schemaClassificationType.get("relation");
		} else if (ctx.EVENT() != null) {
			iter = this.schemaClassificationType.get("event");
		} else {
			iter = Collections.singletonList(previousDeclaration);
		}
		for (int i1 = 0, iterSize = iter.size(); i1 < iterSize; i1++) {
			String evRelName = iter.get(i1);
			for (schemaParser.AngestrengendContext macroNameNode : ctx.stringlist().angestrengend()) {
				macroName = macroNameNode.getText();
				Substitute.SubPair<Schema, Rule> macro = macros.get(macroName);
				if (macro == null) {
					System.err.println("ERROR: macro '" + macroName + "' has not been delcared");
					System.exit(1);
				}
				Schema s = (evRelName != null) ? schema.get(evRelName) : null;
				if (s == null) {
					System.err.println("ERROR: schema element '" + evRelName + "' has not been delcared");
					System.exit(1);
				}
				if (s.arguments.size() != macro.first.arguments.size()) {
					System.err.println("WARNING: macro '" + macroName + "' will not be applied to '" + evRelName + "'");
					continue;
				}
				Rule copy = macro.second.copy();
				Substitute<String, Rule> sr = new Substitute<>(null);

				copy.body.forEach(clause -> {
					if (clause.prop.relName.equals(macro.first.relationName))
						clause.prop.relName = evRelName;
				});
				copy.head.forEach(clause -> {
					if (clause.prop.relName.equals(macro.first.relationName))
						clause.prop.relName = evRelName;
				});
				ArrayList<String> arguments = macro.first.arguments;
				for (int i = 0, argumentsSize = arguments.size(); i < argumentsSize; i++) {
					String x = arguments.get(i);
					sr.setSubstitute(x);
					sr.accept(new Substitute.SubPair<>(copy, s.arguments.get(i)));
				}
				addRule(copy);
			}
		}

	}

	public static ArrayList<String> fromAngestrengend(List<schemaParser.AngestrengendContext> ls) {
		ArrayList<String> toret = new ArrayList<>(ls.size());
		for (int i = 0, lsSize = ls.size(); i < lsSize; i++) {
			schemaParser.AngestrengendContext x = ls.get(i);
			toret.add(x.getText());
		}
		return toret;
	}

	@Override
	public void enterBeginend_declare(schemaParser.Beginend_declareContext ctx) {
	    // Relation describing the event
		String relName = ctx.STRING().getText();
		ArrayList<String> relArguments = fromAngestrengend(ctx.orig.angestrengend());
		ArrayList<String> timeArgs = fromAngestrengend(ctx.intime().stringlist().angestrengend());

		// Event describing either the beginning or the end of the relationship

		boolean isUnique = ctx.UNIQUE() != null;
		boolean isBegin = ctx.BEGIN() != null;
		boolean isEnd = ctx.END() != null;

		extractBeginOrEnd(relName, relArguments, timeArgs, isUnique, isBegin, isEnd, false);
	}

	private void extractBeginOrEnd(String associatedRelation, ArrayList<String> fromAngestrengend, ArrayList<String> timeArgs, boolean isUnique, boolean isBegin, boolean isEnd, boolean isTransfer) {
		if (isBegin && isEnd) {
			System.err.println("ERROR: the same declaration cannot be from a beginning and for an ending event at the same time (1)");
			System.exit(1);
		}

		Schema sourceSchema = schema.get(previousDeclaration);
		if (previousDeclaration == null || sourceSchema == null) {
			System.err.println("ERROR: no previous event/relationship declaration was provided");
			System.exit(1);
		}

		EventAssociation relaxed = eventAssociation.get(associatedRelation);
		if (relaxed == null) {
			relaxed = new EventAssociation(associatedRelation);
			eventAssociation.put(associatedRelation, relaxed);
		}

		EventAssociation.BeginEndSpecification argument = isBegin ? relaxed.begin : (isEnd ? relaxed.end : null);
		if (argument != null) {
			System.err.println("ERROR: the same declaration cannot be from a beginning and for an ending event at the same time (2)");
			System.exit(1);
		}
		if (isBegin) {
			argument = relaxed.begin = new EventAssociation.BeginEndSpecification();
		} else if (isEnd) {
			argument = relaxed.end = new EventAssociation.BeginEndSpecification();
		}
		relaxed.isTransfer = isTransfer;
		argument.isUnique = isUnique;
		argument.associatedEventName = previousDeclaration;
		argument.schema = new Schema(associatedRelation, fromAngestrengend);
		argument.timeArgs = timeArgs;
	}

	/**
	 * All the transfer event are not unique, because transfer can occur many several other times
	 * @param ctx the parse tree
	 */
	@Override
	public void enterTransfer_macro(schemaParser.Transfer_macroContext ctx) {
		// Defining the object that will contain all the relaxations
		MVDRelaxationForTransfer relax = new MVDRelaxationForTransfer();

		// 1) Representing the transfer as the end of an ownership and the begin of a new one.
		String relName = ctx.STRING().getText();
		ArrayList<String> relOrigArguments = fromAngestrengend(ctx.orig.angestrengend());
		ArrayList<String> relDestArguments = fromAngestrengend(ctx.dest.angestrengend());
		ArrayList<String> timeArgs = fromAngestrengend(ctx.intime().stringlist().angestrengend());
		extractBeginOrEnd(relName, relOrigArguments, timeArgs, false, false, true, true);
		extractBeginOrEnd(relName, relDestArguments, timeArgs, false, true, false, true);

		// 2) This representation shall tolerate inconsistency for the sole time when the transfer happens
		relax.notToExQuantifyVars = new HashSet<>(relDestArguments);
		relax.notToExQuantifyVars.addAll(relOrigArguments);
		relax.originalEvent = schema.get(previousDeclaration);
		relax.rewrittenRelation = schema.get(relName);

		// Setting the arguments that should be relaxed during the generation of the rules from the MVDs
		// The elements that do not have any swap are the ones that do not differ in the relation/event subdeclaration.
		relax.relaxation = new HashMap<>();
		// The other elements that will not switch, may be still differently assigned
		relax.fixedToSwitch = new HashMap<>();
		for (int i = 0, relDestArgumentsSize = relDestArguments.size(); i < relDestArgumentsSize; i++) {
			String x = relOrigArguments.get(i);
			String y = relDestArguments.get(i);
			String orig = relax.rewrittenRelation.arguments.get(i);
			if (!x.equals(y)) {
				relax.relaxation.put(orig, new Substitute.SubPair<>(x, y));
			} else {
				relax.fixedToSwitch.put(orig, x);
			}
		}

		ArrayList<MVDRelaxationForTransfer> m = multipleRelaxationForEvent.get(relax.rewrittenRelation);
		if (m == null) {
			m = new ArrayList<>();
			multipleRelaxationForEvent.put(relax.rewrittenRelation, m);
		}
		m.add(relax);
	}

    @Override public void exitProgram(schemaParser.ProgramContext ctx) {
        System.err.println("INFO: generating all the temporal rules");

        for (EventAssociation association : eventAssociation.values()) {
        	// We provide the rule declaration for the begin iff we have the
            if (association.end != null && association.begin != null) {
            	ArrayList<Predicate> joinPredicates = new ArrayList<>();
                if (!association.end.schema.relationName.equals(association.begin.schema.relationName)) {
                    System.err.println("ERROR: the two events do not refer to the same rewritten relationship.");
                    System.exit(1);
                }
                if (association.end.schema.arguments.size() != association.begin.schema.arguments.size()) {
                    System.err.println("ERROR: the begin and end relationships do not have the same argument size.");
                    System.exit(1);
                }

                // Retrieving the clause for the main event
                Clause beginClause = schema.get(association.begin.associatedEventName).asClause();
                Clause beginClauseRW = association.begin.schema.asClause().copy();
                Substitute<String, Clause> sc = new Substitute<>(null);
				VariableGenerator vg = new VariableGenerator();
                HashMap<Integer, String> alreadySubstituted = new HashMap<>();
				Set<String> beginContinued = beginClause.getFreeVariables();
				beginContinued.removeAll(association.begin.schemaArgumentWithNoTime().values());
				beginContinued.removeAll(association.begin.timeArgs);
				Map<Integer, String> ls = association.begin.schemaArgumentWithNoTime();
				ArrayList<String> notexisting = new ArrayList<>(ls.size());

                for (Map.Entry<Integer, String> args : ls.entrySet()) {
                    sc.setSubstitute(args.getValue());
                    String newVar = vg.get();
                    notexisting.add(newVar);
                    alreadySubstituted.put(args.getKey(), newVar);
                    sc.accept(new Substitute.SubPair<>(beginClause, newVar));
					sc.accept(new Substitute.SubPair<>(beginClauseRW, newVar));
					beginContinued.remove(newVar);
                }

                // This part has to be set only if we have a explicit declaration for the end event
				Set<String> endContinued;
				Clause endClause;
				Clause endClauseRW;
                if (association.end != null) {
					endClause = schema.get(association.end.associatedEventName).asClause();
					endClauseRW = association.end.schema.asClause().copy();
					endContinued = beginClause.getFreeVariables();
					endContinued.removeAll(association.end.schemaArgumentWithNoTime().values());
					endContinued.removeAll(association.end.timeArgs);

					for (Map.Entry<Integer, String> args : association.end.schemaArgumentWithNoTime().entrySet()) {
						sc.setSubstitute(args.getValue());
						String newVar = alreadySubstituted.get(args.getKey());
						if (newVar == null)
							newVar = vg.get();
						sc.accept(new Substitute.SubPair<>(endClause, newVar));
						sc.accept(new Substitute.SubPair<>(endClauseRW, newVar));
						endContinued.remove(newVar);
					}
				} else {
                	endContinued = Collections.emptySet();
                	endClause = null;
                	endClauseRW = null;
				}

				for (String x: beginContinued) {
					sc.setSubstitute(x);
					sc.accept(new Substitute.SubPair<>(beginClause, vg.get()));
				}

				if (association.end != null) {
					for (String x : endContinued) {
						sc.setSubstitute(x);
						sc.accept(new Substitute.SubPair<>(endClause, vg.get()));
						sc.accept(new Substitute.SubPair<>(endClauseRW, vg.get()));
					}
				}

				if (association.end != null) {
					// I can generate the rule that states that an element exists from the begin event towards the end event iff. I have both declarations for beginning and end.
					generateBasicIntervalRule(association, new ArrayList<>(joinPredicates), beginClause, beginClauseRW, sc, endClause, endClauseRW);

					// I can generate the end event if I have some information that one event does not exist, then the relationship will be terminated by not-existance of one of the two arguments.
					generateEventEndWithNoExistance(association, new ArrayList<>(joinPredicates), beginClause, sc, endClause, endClauseRW);
				}
				boolean isUnique = association.begin.isUnique || association.end.isUnique;

				if (isUnique) {
					// If the event is unique, then before its creation it cannot exist
					generateBasicUniqueNotExistBefore(association, new ArrayList<>(joinPredicates), beginClause, beginClauseRW, sc);

					// If the event is unique, then after its deletion it cannot exist
					if (association.end != null)
						generateBasicUniqueNotExistsAfter(association, new ArrayList<>(joinPredicates), sc, endClause, endClauseRW);
				} else {
					// The entity does not exist within an end event and a new beginning event,
					if (association.end != null)
						generateBasicNonexistanceIntervalRuleForNotUnique(association, new ArrayList<>(joinPredicates), beginClause, beginClauseRW, sc, endClause, endClauseRW);
				}

				// If an end event does not exist, it means that it did not end according to the fact that there is no source
				endEventOnEntityLoss(association, new ArrayList<>(joinPredicates), beginClause, sc, endClause, notexisting);
			}
        }
    }

    public static String underP = Main.isLatex ? "\\_p" : "_p";
	public static String underPT = Main.isLatex ? "\\_p\\_t" : "_p_t";
	public static String under = Main.isLatex ? "\\_" : "_";
	public static String underT = Main.isLatex ? "\\_t" : "_t";


	/**
	 * I can generate the rule that states that an element exists from the begin event towards the end event iff. I have both declarations for beginning and end.
	 */
	private void generateBasicIntervalRule(EventAssociation asc, ArrayList<Predicate> joinPredicates, Clause beginClause, Clause beginClauseRW, Substitute<String, Clause> sc, Clause endClause, Clause endClauseRW) {
		ArrayList<Quantifier> forallQuantifiers = new ArrayList<>();
		ArrayList<String> timeArgs = asc.end != null ? asc.end.timeArgs : asc.begin.timeArgs;
		for (int i = 0, timeArgsSize = timeArgs.size(); i < timeArgsSize; i++) {
			String x = timeArgs.get(i);
			sc.setSubstitute(x);
			joinPredicates.add(Predicate.lt(asc.begin.timeArgs.get(i), asc.begin.timeArgs.get(i)+under+x+underP));
			sc.accept(new Substitute.SubPair<>(endClause, asc.begin.timeArgs.get(i)+under+x+underP));
			sc.accept(new Substitute.SubPair<>(beginClauseRW, asc.begin.timeArgs.get(i)+under+x+underPT));
			final int j = i;
			if (beginClauseRW.prop.args.stream().anyMatch(y -> y.value.equals(asc.begin.timeArgs.get(j)+under+x+underPT))) {
				ArrayList<Predicate> forallResultPredicates = new ArrayList<>();
				forallResultPredicates.add(Predicate.interval(asc.begin.timeArgs.get(i), asc.begin.timeArgs.get(i)+under+x+underPT, asc.begin.timeArgs.get(i)+under+x+underP));
				forallQuantifiers.add(new Quantifier(true, asc.begin.timeArgs.get(i) + under + x + underPT, "time", forallResultPredicates));
			}
		}
		ArrayList<Clause> header = new ArrayList<>(2);
		header.add(beginClause);
		header.add(endClause);

		Clause ending = new Clause(forallQuantifiers, new ArrayList<>(), false, beginClauseRW.prop, new ArrayList<>());
		ArrayList<Clause> footer = new ArrayList<>(1);
		footer.add(ending);
		Rule r = new Rule(header, joinPredicates, footer);
		addRule(r);
	}

	/**
	 * I can generate the end event if I have some information that one event does not exist, then the relationship will be terminated by not-existance of one of the two arguments.
	 */
	private void generateEventEndWithNoExistance(EventAssociation asc, ArrayList<Predicate> joinPredicates, Clause beginClause, Substitute<String, Clause> sc, Clause ec, Clause endClauseRW) {
		ArrayList<Quantifier> forallQuantifiers = new ArrayList<>();
		ArrayList<String> timeArgs = asc.end != null ? asc.end.timeArgs : asc.begin.timeArgs;
		Clause endClause = ec.copy();
		for (int i = 0, timeArgsSize = timeArgs.size(); i < timeArgsSize; i++) {
			String x = timeArgs.get(i);
			sc.setSubstitute(x);
			//joinPredicates.add(Predicate.lt(asc.begin.timeArgs.get(i), asc.begin.timeArgs.get(i)+under+x+underP));
			sc.accept(new Substitute.SubPair<>(endClause, asc.begin.timeArgs.get(i)+under+x+underP));
			sc.accept(new Substitute.SubPair<>(endClauseRW, asc.begin.timeArgs.get(i)+under+x+underPT));
			final int j = i;
			if (endClauseRW.prop.args.stream().anyMatch(y -> y.value.equals(asc.begin.timeArgs.get(j)+under+x+underPT))) {
				ArrayList<Predicate> forallResultPredicates = new ArrayList<>();
				forallResultPredicates.add(Predicate.lt( asc.begin.timeArgs.get(i) + under + x + underP, asc.begin.timeArgs.get(i) + under + x + underPT));
				forallQuantifiers.add(new Quantifier(true, asc.begin.timeArgs.get(i) + under + x + underP, "time", forallResultPredicates));
			}
		}
		ArrayList<Clause> header = new ArrayList<>(2);
		header.add(beginClause);
		Clause ending = new Clause(new ArrayList<Quantifier>(), new ArrayList<>(), true, endClauseRW.prop, new ArrayList<>());
		header.add(ending);

		ArrayList<Clause> footer = new ArrayList<>(1);
		endClause.foralls = forallQuantifiers;
		footer.add(endClause);
		Rule r = new Rule(header, joinPredicates, footer);
		addRule(r);
	}


	/**
	 * If I'm describing a transfer, then the non-existance shall not be mapped to an end event, but shall negate the relationship
	 */
	private void endEventOnEntityLoss(EventAssociation asc, ArrayList<Predicate> jp, Clause beginClause, Substitute<String, Clause> sc, Clause ec, Collection<String> exargs) {
		boolean isTransfer = asc.isTransfer;
		for (String args : exargs) {
			Clause nex = new Schema("nex", args, underT).asClause();
			ArrayList<Predicate> joinPredicates = new ArrayList<>(jp);

			Clause endClause = ec.copy();
			ArrayList<Quantifier> forallQuantifiers = new ArrayList<>();
			ArrayList<String> timeArgs = asc.begin.timeArgs;
            ArrayList<String> pt = new ArrayList<>(timeArgs.size());
			for (int i = 0, timeArgsSize = timeArgs.size(); i < timeArgsSize; i++) {
				String x = timeArgs.get(i);
				sc.setSubstitute(x);
				joinPredicates.add(Predicate.lt(asc.begin.timeArgs.get(i), underT));

				if (!isTransfer) {
					sc.accept(new Substitute.SubPair<>(endClause, asc.begin.timeArgs.get(i) + under + x + underP));
					ArrayList<Predicate> forallResultPredicates = new ArrayList<>();
					forallResultPredicates.add(Predicate.geq(asc.begin.timeArgs.get(i) + under + x + underP, underT));
					forallQuantifiers.add(new Quantifier(true, asc.begin.timeArgs.get(i) + under + x + underP, "time", forallResultPredicates));
					pt.add(asc.begin.timeArgs.get(i) + under + x + underP);
				}
			}
			ArrayList<Clause> header = new ArrayList<>(2);
			header.add(beginClause);
			//Clause ending = new Clause(forallQuantifiers, new ArrayList<>(), true, beginClauseRW.prop, new ArrayList<>());
			header.add(nex);

			ArrayList<Clause> footer = new ArrayList<>(2);

			if (!isTransfer) {
				HashSet<String> exEnd = new HashSet<>(endClause.getFreeVariables());
				exEnd.removeAll(beginClause.getFreeVariables());
				ArrayList<Quantifier> q = new ArrayList<>(exEnd.size());
				Clause withNulls = endClause.copy();
				for (String x : exEnd) {
					q.add(new Quantifier(false, x, "?", new ArrayList<>()));
				}

				exEnd.removeAll(pt);
				for (String x : exEnd) {
					sc.setSubstitute(x);
					sc.accept(new Substitute.SubPair<>(withNulls, "null"));
				}
				endClause.exists = q;
				endClause.foralls = forallQuantifiers;
				withNulls.foralls = forallQuantifiers;
				footer.add(endClause);
				footer.add(withNulls);
			} else {
				Clause invert = beginClause.copy();
				invert.isNegated = true;
				footer.add(invert);
			}
			Rule r = new Rule(header, joinPredicates, footer);
			addRule(r);
		}
	}

	/**
	 * The entity does not exist within an end event and a new beginning event,
	 */
	private void generateBasicNonexistanceIntervalRuleForNotUnique(EventAssociation asc, ArrayList<Predicate> joinPredicates, Clause beginClause, Clause beginClauseRW, Substitute<String, Clause> sc, Clause endClause, Clause endClauseRW) {
		ArrayList<Quantifier> forallQuantifiers = new ArrayList<>();
		ArrayList<String> timeArgs = asc.end.timeArgs ;
		for (int i = 0, timeArgsSize = timeArgs.size(); i < timeArgsSize; i++) {
			String x = timeArgs.get(i);
			sc.setSubstitute(x);
			joinPredicates.add(Predicate.gt(asc.begin.timeArgs.get(i), asc.begin.timeArgs.get(i) + under + x + underP));
			sc.accept(new Substitute.SubPair<>(endClause, asc.begin.timeArgs.get(i) + under + x + underP));
			sc.accept(new Substitute.SubPair<>(endClauseRW, asc.begin.timeArgs.get(i) + under + x + underPT));
			final int j = i;
			if (endClauseRW.prop.args.stream().anyMatch(y -> y.value.equals(asc.begin.timeArgs.get(j) + under + x + underPT))) {
				ArrayList<Predicate> forallResultPredicates = new ArrayList<>();
				forallResultPredicates.add(Predicate.interval(asc.begin.timeArgs.get(i) + under + x + underP, asc.begin.timeArgs.get(i) + under + x + underPT, asc.begin.timeArgs.get(i)));
				forallQuantifiers.add(new Quantifier(true, asc.begin.timeArgs.get(i) + under + x + underPT, "time", forallResultPredicates));
			}
		}
		ArrayList<Clause> header = new ArrayList<>(2);
		header.add(beginClause);
		header.add(endClause);

		Clause ending = new Clause(forallQuantifiers, new ArrayList<>(), true, beginClauseRW.prop, new ArrayList<>());
		ArrayList<Clause> footer = new ArrayList<>(1);
		footer.add(ending);
		Rule r = new Rule(header, joinPredicates, footer);
		addRule(r);

		ArrayList<Clause> headerMutex = new ArrayList<>(2);
		headerMutex.add(new Clause(new ArrayList<Quantifier>(), new ArrayList<Quantifier>(), true, beginClauseRW.prop, new ArrayList<>()));
		headerMutex.add(new Clause(new ArrayList<Quantifier>(), new ArrayList<Quantifier>(), false, beginClauseRW.prop, new ArrayList<>()));
		Rule mutualExclusion = new Rule(true, headerMutex, new ArrayList<>(), new ArrayList<>());
		addRule(mutualExclusion);
	}

	/**
	 * If the event is unique, then after its deletion it cannot exist
	 */
	private void generateBasicUniqueNotExistsAfter(EventAssociation asc, ArrayList<Predicate> joinPredicates, Substitute<String, Clause> sc, Clause endClause, Clause endClauseRW) {
		ArrayList<Quantifier> forallQuantifiers = new ArrayList<>();
		ArrayList<String> timeArgs = asc.end.timeArgs;
		for (int i = 0, timeArgsSize = timeArgs.size(); i < timeArgsSize; i++) {
			String x = timeArgs.get(i);
			sc.setSubstitute(x);
			joinPredicates.add(Predicate.lt(asc.begin.timeArgs.get(i), asc.begin.timeArgs.get(i)+under+x+underP));
			sc.accept(new Substitute.SubPair<>(endClause, asc.begin.timeArgs.get(i)+under+x+underP));
			sc.accept(new Substitute.SubPair<>(endClauseRW, asc.begin.timeArgs.get(i)+under+x+underPT));
			final int j = i;
			if (endClauseRW.prop.args.stream().anyMatch(y -> y.value.equals(asc.begin.timeArgs.get(j) + under + x + underPT))) {
				ArrayList<Predicate> forallResultPredicates = new ArrayList<>();
				forallResultPredicates.add(Predicate.gt(asc.begin.timeArgs.get(i) + under + x + underPT, asc.begin.timeArgs.get(i) + under + x + underP));
				forallQuantifiers.add(new Quantifier(true, asc.begin.timeArgs.get(i) + under + x + underPT, "time", forallResultPredicates));
			}
		}
		ArrayList<Clause> header = new ArrayList<>(2);
		header.add(endClause);

		Clause ending = new Clause(forallQuantifiers, new ArrayList<>(), true, endClauseRW.prop, new ArrayList<>());
		ArrayList<Clause> footer = new ArrayList<>(1);
		footer.add(ending);
		Rule r = new Rule(header, joinPredicates, footer);
		addRule(r);
	}

	/**
	 * If the event is unique, then before its creation it cannot exist
	 */
	private void generateBasicUniqueNotExistBefore(EventAssociation asc, ArrayList<Predicate> joinPredicates, Clause beginClause, Clause beginClauseRW, Substitute<String, Clause> sc) {
		ArrayList<Quantifier> forallQuantifiers = new ArrayList<>();
		ArrayList<String> timeArgs = asc.end.timeArgs;
		for (int i = 0, timeArgsSize = timeArgs.size(); i < timeArgsSize; i++) {
			String x = timeArgs.get(i);
			sc.setSubstitute(x);
			//joinPredicates.add(Predicate.lt(asc.begin.timeArgs.get(i), asc.begin.timeArgs.get(i)+under+x+underP));
			final int j = i;
			if (beginClauseRW.prop.args.stream().anyMatch(y -> y.value.equals(asc.begin.timeArgs.get(j)+under+x+underPT))) {
				ArrayList<Predicate> forallResultPredicates = new ArrayList<>();
				forallResultPredicates.add(Predicate.lt(asc.begin.timeArgs.get(i) + under + x + underPT, asc.begin.timeArgs.get(i)));
				forallQuantifiers.add(new Quantifier(true, asc.begin.timeArgs.get(i) + under + x + underPT, "time", forallResultPredicates));
			}
		}
		ArrayList<Clause> header = new ArrayList<>(2);
		header.add(beginClause);

		Clause ending = new Clause(forallQuantifiers, new ArrayList<>(), true, beginClauseRW.prop, new ArrayList<>());
		ArrayList<Clause> footer = new ArrayList<>(1);
		footer.add(ending);
		Rule r = new Rule(header, new ArrayList<>(), footer);
		addRule(r);
	}

	                        /////////////
							// Useless //
							/////////////

	@Override public void exitBeginend_declare(schemaParser.Beginend_declareContext ctx) 	{}
	@Override public void exitTransfer_macro(schemaParser.Transfer_macroContext ctx)        {}


	@Override public void enterSet_relation(schemaParser.Set_relationContext ctx) 			{}
	@Override public void exitSet_relation(schemaParser.Set_relationContext ctx) 			{}
	@Override public void enterSet_event(schemaParser.Set_eventContext ctx) 				{}
	@Override public void exitSet_event(schemaParser.Set_eventContext ctx) 					{}
	@Override public void enterSet_entity(schemaParser.Set_entityContext ctx) 				{}
	@Override public void exitSet_entity(schemaParser.Set_entityContext ctx) 				{}
	@Override public void enterParamDeclared(schemaParser.ParamDeclaredContext ctx) 		{}
	@Override public void exitParamDeclared(schemaParser.ParamDeclaredContext ctx) 			{}
	@Override public void enterClause(schemaParser.ClauseContext ctx) 						{}
	@Override public void exitClause(schemaParser.ClauseContext ctx) 						{}
	@Override public void enterForall(schemaParser.ForallContext ctx) 						{}
	@Override public void exitForall(schemaParser.ForallContext ctx) 						{}
	@Override public void enterExists(schemaParser.ExistsContext ctx) 						{}
	@Override public void exitExists(schemaParser.ExistsContext ctx) 						{}
	@Override public void enterVar_notnull(schemaParser.Var_notnullContext ctx) 			{}
	@Override public void exitVar_notnull(schemaParser.Var_notnullContext ctx) 				{}
	@Override public void enterQuantification(schemaParser.QuantificationContext ctx) 		{}
	@Override public void exitQuantification(schemaParser.QuantificationContext ctx) 		{}
	@Override public void enterIsstring(schemaParser.IsstringContext ctx)  					{}
	@Override public void exitIsstring(schemaParser.IsstringContext ctx) 					{}
	@Override public void enterIsvalue(schemaParser.IsvalueContext ctx) 					{}
	@Override public void exitIsvalue(schemaParser.IsvalueContext ctx) 						{}
	@Override public void enterLbound(schemaParser.LboundContext ctx) 						{}
	@Override public void exitLbound(schemaParser.LboundContext ctx) 						{}
	@Override public void enterUbound(schemaParser.UboundContext ctx)                       {}
	@Override public void exitUbound(schemaParser.UboundContext ctx) 						{}
	@Override public void visitErrorNode(ErrorNode node)                         			{}
	@Override public void visitTerminal(TerminalNode node)                       			{}
	@Override public void exitEveryRule(ParserRuleContext ctx)                   			{}
	@Override public void enterEveryRule(ParserRuleContext ctx)                  			{}
	@Override public void exitEnexists(schemaParser.EnexistsContext ctx)         			{}
	@Override public void exitFdep_declare(schemaParser.Fdep_declareContext ctx) 			{}

	@Override public void exitRel_delcare(schemaParser.Rel_delcareContext ctx)   			{}
	@Override public void enterStringlist(schemaParser.StringlistContext ctx)    			{}
	@Override public void exitStringlist(schemaParser.StringlistContext ctx)     			{}
	@Override public void enterMoreClauses(schemaParser.MoreClausesContext ctx)  			{}
	@Override public void exitMoreClauses(schemaParser.MoreClausesContext ctx)   			{}
	@Override public void enterExcept(schemaParser.ExceptContext ctx)            			{}
	@Override public void exitExcept(schemaParser.ExceptContext ctx)             			{}
	@Override public void enterIntime(schemaParser.IntimeContext ctx)            			{}
	@Override public void exitIntime(schemaParser.IntimeContext ctx)             			{}
	@Override public void enterInspace(schemaParser.InspaceContext ctx)          			{}
	@Override public void exitInspace(schemaParser.InspaceContext ctx)           			{}
	@Override public void exitRule(schemaParser.RuleContext ctx)                			{}
	@Override public void exitMacro_expand(schemaParser.Macro_expandContext ctx) 			{}
	@Override public void exitMacro_definition(schemaParser.Macro_definitionContext ctx)   	{}
}
