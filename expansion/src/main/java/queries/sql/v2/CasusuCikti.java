package queries.sql.v2;

import queries.sql.v1.WhereEqValueCondition;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class CasusuCikti {

    private final List<WhereEqValueCondition> selectSpecificERTypes;
    private final List<HashMap<String, String>> selectArgumentToValue;
    public final List<String> tableRenamings;

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

    public CasusuCikti(List<WhereEqValueCondition> selectSpecificERTypes, List<HashMap<String, String>> selectArgumentToValue, List<String> tableRenamings) {
        this.selectSpecificERTypes = selectSpecificERTypes;
        this.selectArgumentToValue = selectArgumentToValue;
        this.tableRenamings = tableRenamings;
    }


    public StringBuilder toStringBuilder(HashMap<String, String> map) {
        StringBuilder sb = new StringBuilder();
        // Getting the current conversion
        sb.append("\tWHEN ");
        // Getting the renaming associated to this clause
        sb.append(
        selectSpecificERTypes.stream().map(x -> {
            String key = x.value.replace("'","");
            String exValue = map.get(key);
            return new WhereEqValueCondition(x.tableLeft, x.argLeft, "'"+exValue+"'", false).toString();
        }).collect(Collectors.joining(" AND ")));
        // Specifying how to represent the data
        sb.append("\n\t\t\tTHEN ARRAY[");

        String retType = properties.getProperty("type");

        sb.append(
        selectArgumentToValue.stream().map(map2 -> {
            String newType = map2.get(retType).replace("'","");
            ArrayList<String> toReturn = new ArrayList<>();
            arguments.forEach(x -> {
                if (x.equals(retType)) {
                    if (("'"+map.get(newType)+"'").equals("'null'")) {
                        if (newType.equals("_bot_"))
                            toReturn.add("'_bot_'");
                        else
                            System.err.println("ERROR: type information not found.");
                    } else
                        toReturn.add("'"+map.get(newType)+"'");
                } else {
                    String got = map2.get(x);
                    if (got == null)
                        toReturn.add(properties.getProperty("emptyarray"));
                    else
                        toReturn.add(map2.get(x));
                }
            });

            // Overwriting the arguments with specific needs:
            // 1) provenance information
            toReturn.set(0, tableRenamings.stream().map(x -> x+".eid").collect(Collectors.joining(" || ")));
            // 2) weight as the conjunction of all the elements that were combined
            toReturn.set(2, tableRenamings.stream().map(x -> x+".weight").collect(Collectors.joining(" * ")));
            //map2.put(retType, "'"+map.get(newType)+"'");
            return toReturn.stream().collect(Collectors.joining(", ", "json_build_array(", ")"));
        }).collect(Collectors.joining(",   "))
        );

        sb.append("]\n");
        return sb;
    }
}
