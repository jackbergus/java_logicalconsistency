package queries.sql;

import queries.bitmaps.BitMap;
import queries.sql.v1.QueryCollection;
import queries.sql.v1.SelectFromWhere;
import queries.sql.v1.WhereEqValueCondition;
import queries.sql.v1.WhereJoinCondition;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SqlUtils {

    private static Properties properties = new Properties();
    private static List<String> arguments;

    static {
        try {
            properties.load(new FileReader("query_generation.properties"));
            String[] split = properties.getProperty("argumentsTable").split(",");
            arguments = Arrays.asList(split);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method collects all the conditions that are required join conditions
     * @param properties                Rewriting names that are required
     * @param selectSpecificERTypes     Actual table selection features
     * @param notNullMaps               Checking whether the tuple has null arguments or not
     * @param negatedMaps               Checking whether the tuple has negated arguments or not
     * @param clauseJoins               Actual join conditions
     * @param generateIfNotExists       TODO: not existance checking
     *
     * @return                          List of strings of the join conditions.
     */
    public static List<String> getWhereJoinAndConditions(Properties properties, List<WhereEqValueCondition> selectSpecificERTypes, HashMap<String, BitMap> notNullMaps, HashMap<String, BitMap> negatedMaps, List<WhereJoinCondition> clauseJoins, QueryCollection generateIfNotExists) {
        List<String> whereJoinAndConditions = new ArrayList<>();
        selectSpecificERTypes.forEach(x -> whereJoinAndConditions.add(x.toString()));
        notNullMaps.forEach((x,y)-> whereJoinAndConditions.add("(~"+x+"."+ properties.getProperty("null")+") &"+y.toString()));
        negatedMaps.forEach((x,y)-> whereJoinAndConditions.add("("+x+"."+ properties.getProperty("negated")+") &"+y.toString()));
        clauseJoins.forEach(x -> whereJoinAndConditions.add(x.toString()));
        if (!generateIfNotExists.isEmpty()) {
            whereJoinAndConditions.add("NOT EXISTS("+generateIfNotExists.toString()+")");
        }
        return whereJoinAndConditions;
    }

    public static List<String> getWhereJoinAndConditions(Properties properties, HashMap<String, BitMap> notNullMaps, HashMap<String, BitMap> negatedMaps, List<WhereJoinCondition> clauseJoins, QueryCollection generateIfNotExists) {
        return getWhereJoinAndConditions(properties, Collections.emptyList(), notNullMaps, negatedMaps, clauseJoins, generateIfNotExists);
    }

    private static String unnestingV2(StringBuilder EMap) {
        return new StringBuilder().append("SELECT unnest(").append(properties.getProperty("array_of_jsonarray_fieldname")).append(") AS ").append(properties.getProperty("unnested_fieldname")).append("\nFROM ").append(EMap).append(" AS ").append(properties.getProperty("nested_table_name")).append(")").toString();
    }

    public static String globalV2(StringBuilder EMap) {
        return new StringBuilder().append("SELECT ").append(IntStream.rangeClosed(0, arguments.size()-1).mapToObj(i -> properties.getProperty("unnested_fieldname")+"->>"+i).collect(Collectors.joining(", "))).append("\nFROM\n(").append(unnestingV2(EMap)).append("\n) AS ").append(properties.getProperty("unnested_table")).toString();

    }

}
