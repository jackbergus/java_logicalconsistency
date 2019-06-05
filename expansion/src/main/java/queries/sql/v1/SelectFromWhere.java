/*
 * SelectFromWhere.java
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

import algos.CopyConstructor;
import queries.bitmaps.BitMap;
import queries.sql.SqlUtils;
import queries.sql.v2.MultipleNestedCases;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SelectFromWhere implements CopyConstructor<SelectFromWhere> {

    private static Properties properties = null;
    private static List<String> arguments = null;

    // SELECT
    HashMap<String, String> selectArgumentToValue;

    // FROM
    List<String> tablesWithRenaming, renamings;

    // WHERE
    List<WhereJoinCondition> clauseJoins;
    List<WhereEqValueCondition> selectSpecificERTypes;
    HashMap<String, BitMap> notNullMaps;
    HashMap<String, BitMap> negatedMaps;
    QueryCollection generateIfNotExists;

    public SelectFromWhere(HashMap<String, String> selectionMap,
                           List<String> fromTables,
                           List<String> tableRenamings,
                           List<WhereJoinCondition> wjcLsAND,
                           List<WhereEqValueCondition> selectSpecificERTypes,
                           HashMap<String, BitMap> notNullMaps,
                           HashMap<String, BitMap> negatedMaps,
                           int nexExpectedSize) {

        if (properties == null || arguments == null) {
            try {
                properties = new Properties();
                properties.load(new FileReader("query_generation.properties"));
                String[] split = properties.getProperty("argumentsTable").split(",");
                arguments = Arrays.asList(split);
            } catch (IOException e) {
                e.printStackTrace();
                properties = null;
                arguments = null;
            }
        }

        this.selectArgumentToValue = selectionMap;
        this.tablesWithRenaming = fromTables;
        this.renamings = tableRenamings;
        this.clauseJoins = wjcLsAND;
        this.selectSpecificERTypes = selectSpecificERTypes;
        this.notNullMaps = notNullMaps;
        this.negatedMaps = negatedMaps;
        this.generateIfNotExists = new QueryCollection(SetOperations.UNION, false, nexExpectedSize);
    }

    public MultipleNestedCases transformFromLegacy() {
        return new MultipleNestedCases(selectArgumentToValue, tablesWithRenaming, renamings, clauseJoins, selectSpecificERTypes, notNullMaps, negatedMaps, generateIfNotExists);
    }

    public SelectFromWhere(HashMap<String, String> selectionMap,
                           List<String> fromTables,
                           List<String> tableRenamings,
                           List<WhereJoinCondition> wjcLsAND,
                           List<WhereEqValueCondition> selectSpecificERTypes,
                           HashMap<String, BitMap> notNullMaps,
                           HashMap<String, BitMap> negatedMaps,
                           QueryCollection generateIfNotExists) {

        if (properties == null || arguments == null) {
            try {
                properties = new Properties();
                properties.load(new FileReader("query_generation.properties"));
                String[] split = properties.getProperty("argumentsTable").split(",");
                arguments = Arrays.asList(split);
            } catch (IOException e) {
                e.printStackTrace();
                properties = null;
                arguments = null;
            }
        }

        this.selectArgumentToValue = selectionMap;
        this.tablesWithRenaming = fromTables;
        this.renamings = tableRenamings;
        this.clauseJoins = wjcLsAND;
        this.selectSpecificERTypes = selectSpecificERTypes;
        this.notNullMaps = notNullMaps;
        this.negatedMaps = negatedMaps;
        this.generateIfNotExists = generateIfNotExists;
    }

    /**
     *
     * @param map       Map associating the element from the general clause to the specific instantiation given by the associated rule
     * @return
     */
    public SelectFromWhere instantiateQuery(HashMap<String, String> map) {
        selectSpecificERTypes.forEach(x -> {
            String key = x.value.replace("'","");
            String exValue = map.get(key);
            x.value = "'"+exValue+"'";
        });
        String retType = properties.getProperty("type");
        String newType = selectArgumentToValue.get(retType).replace("'","");
        selectArgumentToValue.put(retType, "'"+map.get(newType)+"'");
        generateIfNotExists.distinctQueries.forEach(x -> x.instantiateQuery(map));
        return this;
    }

    public void addNotExistsResultSet(SelectFromWhere sfw) {
        this.generateIfNotExists.add(sfw);
    }

    @Override
    public String toString() {
        List<String> selectStatement = generateSelectArguments(selectArgumentToValue);

        // Defining the AND predicates
        List<String> whereJoinAndConditions = SqlUtils.getWhereJoinAndConditions(properties, selectSpecificERTypes, notNullMaps, negatedMaps, clauseJoins, generateIfNotExists);

        return "SELECT " + (selectStatement.isEmpty() ? "*" : String.join(",\n       ", selectStatement)) +
                // Selecting the table concurring in the join
                "\n\tFROM "+(String.join(",", tablesWithRenaming))+
                // Join conditions
                (whereJoinAndConditions.isEmpty() ? "" : "\n\tWHERE ")+(String.join("\n\t  AND ", whereJoinAndConditions))
                +"\n\n";
    }

    public static String compileUnion(Collection<SelectFromWhere> sfw, boolean all) {
        return sfw.stream().map(SelectFromWhere::toString).collect(Collectors.joining("\n\t\t\tUNION "+(all ? "ALL" : "")+"\n"));
    }

    private List<String> generateSelectArguments(HashMap<String, String> map) {
        ArrayList<String> toReturn = new ArrayList<>();
        arguments.forEach(x -> toReturn.add(map.get(x)+" AS "+x));
        toReturn.set(0, renamings.stream().map(x -> x+".eid").collect(Collectors.joining(" || "))+" AS eid");
        return toReturn;
    }

    @Override
    public SelectFromWhere copy() {
        HashMap<String, String> sa = new HashMap<>();
        sa.putAll(selectArgumentToValue);
        return new SelectFromWhere(sa,tablesWithRenaming, renamings, clauseJoins, CopyConstructor.listCopy(selectSpecificERTypes),notNullMaps, negatedMaps, generateIfNotExists.copy());
    }
}
