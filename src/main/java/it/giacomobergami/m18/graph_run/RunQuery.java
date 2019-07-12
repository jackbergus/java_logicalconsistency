package it.giacomobergami.m18.graph_run;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import queries.DependencyGraph;
import queries.sql.v1.QueryGenerationConf;
import ref.RuleListener;
import ref.schemaLexer;
import ref.schemaParser;

import java.io.IOException;
import java.io.Reader;

public class RunQuery {

    /**
     * Dependency Graph
     */
    DependencyGraph dependencyGraph;

    /**
     *
     */
    QueryGenerationConf qgc;

    public RunQuery(final Reader code) throws IOException {
        final org.antlr.v4.runtime.CharStream input = new ANTLRInputStream(code);
        schemaLexer lexer = new schemaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        schemaParser parser = new schemaParser(tokens);
        schemaParser.ProgramContext tree = parser.program(); // parse a compilationUnit
        RuleListener classListener = new RuleListener();
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

    public void debugPlot() {
        dependencyGraph.plot();
    }

}
