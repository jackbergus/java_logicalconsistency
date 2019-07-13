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
    private static Set<String> withDoubles;
    private static String dField;
    private static String array_of_jsonarray_fieldname;
    private static String unnested_fieldname, nested_table_name, unnested_table;
    private static Set<String> withBitmapSize;

    static {
        try {
            properties.load(new FileReader("query_generation.properties"));
            arguments = Arrays.asList(properties.getProperty("argumentsTable").split(","));
            withDoubles = new HashSet<>();
            Arrays.spliterator(properties.getProperty("doubleArgs").split(",")).forEachRemaining(withDoubles::add);
            dField = properties.getProperty("unnested_fieldname")+"->>";
            array_of_jsonarray_fieldname = properties.getProperty("array_of_jsonarray_fieldname");
            unnested_fieldname = properties.getProperty("unnested_fieldname");
            nested_table_name = properties.getProperty("nested_table_name");
            unnested_table = properties.getProperty("unnested_table");
            withBitmapSize = new HashSet<>();
            withBitmapSize.add("hed");
            withBitmapSize.add("null");
            withBitmapSize.add("neg");
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
        notNullMaps.forEach((x,y)-> whereJoinAndConditions.add("((~"+x+"."+ properties.getProperty("null", "bitmap_null")+") & "+y.toString()+") = "+y.toString()));
        negatedMaps.forEach((x,y)-> whereJoinAndConditions.add("(("+x+"."+ properties.getProperty("neg", "bitmap_neg")+") & "+y.toString()+") = "+y.toString()));
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
        return new StringBuilder().append("SELECT unnest(").append(array_of_jsonarray_fieldname).append(") AS ").append(unnested_fieldname).append("\nFROM (").append(EMap).append(") AS ").append(nested_table_name).toString();
    }

    public static String getFieldWithTypecastForRecursion(int columnId) {
        String argument = arguments.get(columnId);
        for (String withS : withBitmapSize) {
            String associatedElement = properties.getProperty(withS);
            if (argument.equals(associatedElement)) {
                String essed = withS+"S";
                Integer size = Integer.valueOf(properties.getProperty(essed));
                return "("+dField+columnId+")::bit("+properties.getProperty(essed)+") as "+argument;
            }
        }
        if (argument.equals(properties.getProperty("type"))) {
            return "("+dField+columnId+")::varchar as "+argument;
        } else if (withDoubles.contains(argument)) {
            return "("+dField+columnId+")::double precision as "+argument;
        } else {
            return "translate("+dField+columnId+", '[]', '{}')::varchar[] as "+argument;
        }
    }

    public static String globalV2(StringBuilder EMap) {
        return new StringBuilder().append("SELECT ")

                // Selecting all the fields that need to be extracted
                .append(IntStream.rangeClosed(0, arguments.size()-1).mapToObj(SqlUtils::getFieldWithTypecastForRecursion).collect(Collectors.joining(", ")))

                // Unnesting query, also containing the expansion one
                .append("\nFROM\n(").append(unnestingV2(EMap)).append("\n) AS ").append(unnested_table)
                .toString();

    }

}
