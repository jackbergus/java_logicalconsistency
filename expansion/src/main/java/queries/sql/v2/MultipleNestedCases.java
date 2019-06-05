package queries.sql.v2;

import queries.bitmaps.BitMap;
import queries.sql.SqlUtils;
import queries.sql.v1.QueryCollection;
import queries.sql.v1.WhereEqValueCondition;
import queries.sql.v1.WhereJoinCondition;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

public class MultipleNestedCases {
    private final  HashMap<String, BitMap> notNullMaps;
    private final  HashMap<String, BitMap> negatedMaps;
    private final  QueryCollection generateIfNotExists;
    private final  List<WhereJoinCondition> clauseJoins;
    private final  CasusuCikti cc;
    private final  List<String> tablesWithRenaming;


    public MultipleNestedCases(HashMap<String, String> selectionMap,
                               List<String> tablesWithRenaming,
                               List<String> tableRenamings,
                               List<WhereJoinCondition> wjcLsAND,
                               List<WhereEqValueCondition> selectSpecificERTypes,
                               HashMap<String, BitMap> notNullMaps,
                               HashMap<String, BitMap> negatedMaps, QueryCollection generateIfNotExists) {
        this.clauseJoins = wjcLsAND;
        this.tablesWithRenaming = tablesWithRenaming;
        this.notNullMaps = notNullMaps;
        this.negatedMaps = negatedMaps;
        this.cc = new CasusuCikti(selectSpecificERTypes, new ArrayList<HashMap<String, String>>(){{add(selectionMap);}}, tableRenamings);
        this.generateIfNotExists = generateIfNotExists;
    }

    /*
     *
    public MultipleNestedCases(//List<String> tablesWithRenaming,
                               HashMap<String, BitMap> notNullMaps,
                               HashMap<String, BitMap> negatedMaps,
                               QueryCollection generateIfNotExists,
                               List<WhereJoinCondition> clauseJoins,
                               CasusuCikti cc)  {
        //this.tablesWithRenaming = tablesWithRenaming;
        this.notNullMaps = notNullMaps;
        this.negatedMaps = negatedMaps;
        this.generateIfNotExists = generateIfNotExists;
        this.clauseJoins = clauseJoins;
        this.cc = cc;
    }*/

    public String toString(Stream<HashMap<String, String>> shm) {
        Properties properties = new Properties();
        try {
            properties.load(new FileReader("query_generation.properties"));
            StringBuilder sb = new StringBuilder();


            // Starting the inner E table, providing the rewriting rules.
            sb.append("SELECT CASE\n");
            // Getting all the cases from the stream
            shm.forEach(map -> sb.append(cc.toStringBuilder(map)));
            sb
                    // If none of the cases is matched, provide no expansion
                    .append("\t\tELSE ARRAY[]::json[]\n")
                    // Closing all the cases, and creating the one element
                    .append("\tEND AS ").append(properties.getProperty("array_of_jsonarray_fieldname"));

            // Getting all the tables to be joined
            sb.append("\nFROM ").append(String.join(",", tablesWithRenaming));
            // Getting the actual join conditions

            List<String> whereJoinAndConditions = SqlUtils.getWhereJoinAndConditions(properties, notNullMaps, negatedMaps, clauseJoins, generateIfNotExists);
            if (!whereJoinAndConditions.isEmpty()) {
                sb.append("\nWHERE ").append(String.join("\n\t  AND ", whereJoinAndConditions));
            }

            return SqlUtils.globalV2(sb);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
