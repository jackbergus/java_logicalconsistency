package it.giacomobergami.m18.graph_run;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.Database;
import queries.DependencyGraph;
import queries.graph.GraphDissectPaths;
import queries.sql.v1.QueryGenerationConf;
import queries.sql.v1.SelectFromWhere;
import ref.RuleListener;
import ref.schemaLexer;
import ref.schemaParser;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class RunQuery {

    /**
     * Dependency Graph
     */
    DependencyGraph dependencyGraph;

    /**
     *
     */
    QueryGenerationConf qgc;


    public RuleListener classListener;

    public RunQuery(final Reader code) throws IOException {
        final org.antlr.v4.runtime.CharStream input = new ANTLRInputStream(code);
        schemaLexer lexer = new schemaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        schemaParser parser = new schemaParser(tokens);
        schemaParser.ProgramContext tree = parser.program(); // parse a compilationUnit
        classListener = new RuleListener();
        ParseTreeWalker.DEFAULT.walk(classListener, tree);
        //System.out.println("# event/relation: "+classListener.schema.size());
        //System.out.println("# MVD: "+classListener.countArrayListMVD());
        //System.out.println("# generateRules: "+classListener.countAllRules());
        //System.out.println("TAB PRINTING (ltd.)");
        //System.out.println("===================");
        qgc = new QueryGenerationConf();
        //System.out.println("===================\n");
        //System.err.println("INFO: detecting cycles");
        dependencyGraph = new DependencyGraph(classListener, qgc);
        //dg.plot();
    }

    /**
     * Return all the graph id nodes associated to the query graph
     * @return
     */
    public Set<Integer> getGraphNodeId() {
        return classListener.idToRuleTab.keySet();
    }

    /**
     * Return the query associated to the query node
     * @param nodeId       Node id
     * @return
     */
    public String returnAssociatedQuery(Integer nodeId) {
        SelectFromWhere compiledQuery = qgc.compileQuery(classListener.idToRuleTab.get(nodeId));
        return compiledQuery.transformFromLegacy().toString(classListener.ruleTabClassification4DB.get(nodeId).keySet().stream().map(classListener.ruleToResolvedPredicates::get));
    }

    /**
     * Runs the query associated to the graph node
     *
     * @param db            Database over which run the query
     * @param nodeId        Node for which run the  query
     * @return              Returns zero if the update had no success, and else non-zero.
     */
    int runQueryFromNode(Database db, Integer nodeId) {
        String query = "INSERT INTO expansions (eid,type_event,weight,arg1,arg2,arg3,arg4,arg5,arg6,arg7,bitmap_null,bitmap_neg,bitmap_hed)\n"+
                returnAssociatedQuery(nodeId)+
                "\non conflict (eid,type_event,weight,arg1,arg2,arg3,arg4,arg5,arg6,arg7,bitmap_null,bitmap_neg,bitmap_hed) do nothing";
        int numberOfUpdates = 0;
        int numberOfIterations = -1;
        do {
            numberOfUpdates = db.rawSqlUpdate(query);
            numberOfIterations++;
        } while (numberOfUpdates > 0);
        return numberOfIterations;
    }

    /**
     * Runs a path containing all the nodes.
     *
     * @param db            Database over which run the queries
     * @param ls            List of all the nodes that needs to be run in sequence to
     * @return              Returns zero if all the queries in the paths had no success, and else non-zero value.
     */
    int runQueryPath(Database db, List<String> ls) {
        return ls.stream().mapToInt(x -> runQueryFromNode(db, Integer.valueOf(x))).sum();
    }

    /**
     * Runs runQueryPath for all the set of paths
     *
     * @param db            Database over which run the queries
     * @param lsOfList      Set of all the paths that needs to be run
     * @return              Returns zero if all the paths had no success, and else non-zero value
     */
    int runMultiplePaths(Database db, Set<List<String>> lsOfList) {
        return lsOfList.stream().mapToInt(x -> runQueryPath(db, x)).sum();
    }

    public void doExpansion(Database db) {
        GraphDissectPaths paths = dependencyGraph.generatePathsForExpansionModule();

        // First, run the rules directly pointing to the cycles
        runMultiplePaths(db, paths.pathFromStartingToCycles);

        // Then, run the rules directly pointing to terminal nodes.
        runMultiplePaths(db, paths.pathDirecltyTerminal);

        // Run the loops with some "fegatelli" (bitter bites) messing up the elegant loop semantics
        int numberOfUpdates = 0;
        do {
            numberOfUpdates = runMultiplePaths(db, dependencyGraph.cycles);
            numberOfUpdates += runMultiplePaths(db, paths.fegatelli);
        } while (numberOfUpdates > 0); // The loop will terminate where no new expansions will be produced.

        // At the end, run the terminal paths
        runMultiplePaths(db, paths.pathFromCyclesToEnding);
    }

    public void debugPlot() {
        dependencyGraph.plot();
    }

}
