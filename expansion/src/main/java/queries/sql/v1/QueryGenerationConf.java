/*
 * QueryGenerationConf.java
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

 
package queries.sql.v1;

import algos.Substitute;
import queries.bitmaps.BitMap;
import queries.sql.v1.SelectFromWhere;
import queries.sql.v1.WhereEqValueCondition;
import queries.sql.v1.WhereJoinCondition;
import types.*;
import utils.CompPair;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class QueryGenerationConf {

    Properties properties;
    List<String> arguments;

    // TODO:
    // 1) Universal Quantifier. Grounding within the programming language
    // 2) Using the paths and 

    public QueryGenerationConf() throws IOException {
        properties = new Properties();
        properties.load(new FileReader("query_generation.properties"));
        String[] split = properties.getProperty("argumentsTable").split(",");
        arguments = Arrays.asList(split);
    }

    public String getTableName(String t) {
        return t.startsWith("p_") ? t.replace("p_", "t") : t;
    }

    public String getArgumentName(int i) {
        return properties.getProperty("argumentName").replaceAll("\\{n\\}", i+"");
    }

    public String getArgumentType(int i) {
        return properties.getProperty("argumentType").replaceAll("\\{n\\}", i+"");
    }

    public CompPair<String, String> getJoinConditionForSQL(CompPair<String, Integer> cp) {
        return new CompPair<>(getTableName(cp.key), getArgumentName(cp.val));
    }

    public CompPair<String, String> getArgTypeForSQL(CompPair<String, Integer> cp) {
        return new CompPair<>(getTableName(cp.key), getArgumentType(cp.val));
    }

    private void updateArgumentSet(Set<PropArgument> arguments, Proposition prop) {
        for (PropArgument p : prop.args) {
            if (p.isVariable) {
                arguments.add(p);
            }
        }
    }

    private void updateArgumentSet(Set<PropArgument> arguments, Clause clause) {
        if (clause.prop != null)
            updateArgumentSet(arguments, clause.prop);
    }

    private void updateArgumentSet(Set<PropArgument> arguments, Rule r) {
        if (r.body != null) r.body.forEach(x -> updateArgumentSet(arguments, x));
    }

    private void createSelectAndJoinConditions(PropArgument variable, Proposition prop, List<CompPair<String, Integer>> ls) {
        if (variable.isVariable && ls != null) {
            for (int i = 0, N = prop.args.size(); i<N; i++) {
                if (prop.args.get(i).equals(variable))
                    ls.add(new CompPair<>(prop.relName, i+1));
            }
        }
    }

    private void createSelectAndJoinConditions(PropArgument variable, Clause clause, List<CompPair<String, Integer>> ls) {
        if (ls != null) {
            createSelectAndJoinConditions(variable, clause.prop, ls);
        }
    }

    private List<CompPair<String,Integer>> createSelectAndJoinConditions(PropArgument variable, Rule r) {
        List<CompPair<String,Integer>> ls = new ArrayList<>();
        if (r.head.size() == 1) {
            createSelectAndJoinConditions(variable, r.head.get(0), ls);
            if (ls.isEmpty()) {
                //ls.add(new CompPair<>(null, null));
            }
            if (r.body != null) {
                r.body.forEach(x -> createSelectAndJoinConditions(variable, x, ls));
            }
        }
        return ls;
    }

    private List<String> generateSelectArguments(HashMap<String, String> map) {
        ArrayList<String> toReturn = new ArrayList<>();
        arguments.forEach(x -> toReturn.add(map.get(x)+" AS "+x));
        return toReturn;
    }

    /**
     * Compiles a simple query which has no quantifiers whatsoever.
     */
    public SelectFromWhere compileSimpleQuery(Rule r) {
        // TODO: also implement the join predicates with time comparisons
        if (r.head.size() > 1) {
            System.err.println("Ignoring error requiring querying or disjunction...");
            return null;
        }
        if (r.body.isEmpty() || r.isFinalBottom) {
            System.err.println("Ignoring mutual exclusions...");
            return null;
        }

        // Associating into the bitmap whether the final result should be negated, too.
        BitMap resultNegated = new BitMap(Integer.valueOf(properties.getProperty("negatedS")));
        if (r.head.get(0).isNegated) {
            resultNegated.on(0);
        }


        Set<PropArgument> joinVariables = new HashSet<>();

        // Generating the not-null argument check
        HashMap<String, BitMap> notNullMaps = new HashMap<>();
        Set<String> checkIfNullVariables = new HashSet<>();
        for (PropArgument x : r.head.get(0).prop.args) {
            if (x.isVariable)
                checkIfNullVariables.add(x.value);
        }
        HashMap<String, BitMap> negatedMaps = new HashMap<>();
        for (Predicate nnpar : r.joinPredicates) {
            if (nnpar.mustNotNull && checkIfNullVariables.contains(nnpar.varName))
                checkIfNullVariables.remove(nnpar.varName);
        }
        for (Clause b : r.body) {
            BitMap nnmap = new BitMap(Integer.valueOf(properties.getProperty("nullS")));
            String tableName = getTableName(b.prop.relName);
            boolean atLeastOneSet = false;
            for (Predicate nnpar : r.joinPredicates) {
                if (nnpar.mustNotNull) {
                    ArrayList<PropArgument> args = b.prop.args;
                    for (int i = 0, argsSize = args.size(); i < argsSize; i++) {
                        PropArgument arg = args.get(i);
                        if (arg.isVariable && arg.value.equals(nnpar.varName)) {
                            nnmap.on(i);
                            atLeastOneSet = true;
                        }
                    }
                }
            }
            if (b.isNegated) {
                BitMap bm = new BitMap(Integer.valueOf(properties.getProperty("negatedS")));
                bm.on(0);
                negatedMaps.put(tableName, bm);
            }
            if (atLeastOneSet)
                notNullMaps.put(tableName, nnmap);
        }


        // Preparing the arguments for the SELECT clause
        HashMap<String, String> selectionMap = new HashMap<>();

        // Tables from which you have to extract the arguments
        List<String> fromTables = new ArrayList<>();
        List<String> tableRenamings = new ArrayList<>();
        List<WhereEqValueCondition> selectSpecificERTypes = new ArrayList<>();
        r.body.forEach(x -> {
            fromTables.add(properties.getProperty("tupleTable")+" "+getTableName(x.prop.relName));
            tableRenamings.add(getTableName(x.prop.relName));
            selectSpecificERTypes.add(new WhereEqValueCondition(getTableName(x.prop.relName), properties.getProperty("type"), "'"+x.prop.relName+"'"));
        });

        // Generating all the join conditions. All the values in the map must be linked
        HashMap<String, List<CompPair<String, Integer>>> map = new HashMap<>();
        // --> Extracting all the variables participating in both select and join conditions
        updateArgumentSet(joinVariables, r);
        for (PropArgument arg : joinVariables) {
            map.put(arg.value, createSelectAndJoinConditions(arg, r));
        }

        // Obtaining the join conditions
        List<WhereJoinCondition> wjcLsAND = new ArrayList<>();
        HashMap<Integer, String> resultMapForNullCheck = new HashMap<>();
        for (List<CompPair<String, Integer>> ls : map.values()) {
            //int N = ls.size();
            //if (N >= 2) {
                List<CompPair<String, Integer>> toJoin = new ArrayList<>(ls);
                List<CompPair<String, Integer>> inSelect = ls.stream().filter(x -> x.key.equals(r.head.get(0).prop.relName)).collect(Collectors.toList());
                toJoin.removeAll(inSelect);
            if (!inSelect.isEmpty()) {
                toJoin.add(inSelect.get(0));
            }

                CompPair<String, Integer> getSelectElementRaw = toJoin.get(0);
                CompPair<String, String> getSelectArgument = getJoinConditionForSQL(getSelectElementRaw);
                CompPair<String, String> getSelectType = getArgTypeForSQL(getSelectElementRaw);

                for (CompPair<String, Integer> getSelectPosition : inSelect) {
                    resultMapForNullCheck.put(getSelectPosition.val, getTableName(getSelectArgument.key) + "." + getSelectArgument.val);
                    selectionMap.put(getArgumentName(getSelectPosition.val), getTableName(getSelectArgument.key) + "." + getSelectArgument.val);
                    selectionMap.put(getArgumentType(getSelectPosition.val), getTableName(getSelectType.key) + "." + getSelectType.val);
                }

                int N = toJoin.size();
                //if (N>2) {
                    for (int i = 0; i < N-1; i++) {
                        CompPair<String, String> left = getJoinConditionForSQL(toJoin.get(i));
                        CompPair<String, String> right = getJoinConditionForSQL(toJoin.get(i + 1));
                        if (!((!tableRenamings.contains(left.key)) || (!tableRenamings.contains(right.key)))) {
                            //System.err.println("ERROR: join condition that is not reflected into a resulting table. Killing");
                            //return null;
                            wjcLsAND.add( new WhereJoinCondition(left.key, left.val, right.key, right.val));
                        }
                    }
                //}
            //}

        }

        // the select statement has to return a value associated to the relation that has to be produced
        selectionMap.put(properties.getProperty("type"), "'"+r.head.get(0).prop.relName+"'");

        List<String> ls = new ArrayList<>();
        ArrayList<PropArgument> args = r.head.get(0).prop.args;
        for (int i = 0, argsSize = args.size(); i < argsSize; i++) {
            PropArgument x = args.get(i);
            if (checkIfNullVariables.contains(x.value)) {
                ls.add("("+resultMapForNullCheck.get(i+1)+" IS NULL)::int::bit");
            } else {
                ls.add("B'0'");
            }
        }

        selectionMap.put(properties.getProperty("null"), "("+String.join(" || ", ls)+")::bit("+properties.getProperty("nullS")+")");
        selectionMap.put(properties.getProperty("negated"), resultNegated.toString());
        selectionMap.put(properties.getProperty("hedged"), new BitMap(Integer.valueOf(properties.getProperty("hedgedS"))).toString());


        return new SelectFromWhere(selectionMap,
                                   fromTables,
                                    tableRenamings,
                                   wjcLsAND, selectSpecificERTypes, notNullMaps, negatedMaps,
                    0);
    }

    Substitute<String, Clause> cl = new Substitute<>(null);

    /**
     * This statement generates the union of all the existentials that needs to be checked
     * @param r
     * @return
     */
    public SelectFromWhere compileExistQueryForExistentialQueryQuery(Rule r) {
        List<Clause> exList = r.head.stream().filter(x -> x.foralls.isEmpty() && !x.exists.isEmpty()).collect(Collectors.toList());
        if (exList.isEmpty())
            return null;

        List<Clause> notEx = new ArrayList<>(r.head);
        notEx.removeAll(exList);

        Rule toCompile = r.copy();
        toCompile.head.removeAll(exList);
        SelectFromWhere toReturn = compileSimpleQuery(toCompile);

        for (Clause ex : exList) {
            AtomicInteger ai = new AtomicInteger(1);
            HashMap<String, String> varReplacement = new HashMap<>();
            Rule rcp = r.copy();
            rcp.head.clear();

            Clause newEx = ex.copy();
            for (Quantifier exVar : ex.exists) {
                varReplacement.put(exVar.varName, "__"+ai.getAndIncrement());
            }
            newEx.exists.clear();
            newEx.foralls.clear();
            varReplacement.forEach((z,t)-> {
                cl.setSubstitute(z);
                cl.accept(new Substitute.SubPair<>(newEx, t));
            });
            rcp.head.add(newEx);
            rcp.body.add(newEx);
            SelectFromWhere sfwE = compileSimpleQuery(rcp);
            if (sfwE != null)
                toReturn.addNotExistsResultSet(sfwE);
        }
        return toReturn;
    }

    public SelectFromWhere compileQuery(Rule r) {
        SelectFromWhere tryWithExistential = compileExistQueryForExistentialQueryQuery(r);
        if (tryWithExistential == null) {
            return compileSimpleQuery(r);
        } else {
            return tryWithExistential;
        }
    }

}
