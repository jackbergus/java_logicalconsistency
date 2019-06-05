/*
 * Main.java
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

 
package main;

import org.antlr.v4.gui.TreeViewer;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import queries.DependencyGraph;
import queries.sql.v1.QueryGenerationConf;
import ref.RuleListener;
import ref.schemaLexer;
import ref.schemaParser;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static boolean isLatex = true;

    static void showGuiTreeView(final Reader code) throws IOException {
        final org.antlr.v4.runtime.CharStream stream = new ANTLRInputStream(code);
        final schemaLexer lexer = new schemaLexer(stream);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final schemaParser parser = new schemaParser(tokens);
        final ParseTree tree = parser.program();
        final List<String> ruleNames = Arrays.asList(schemaParser.ruleNames);
        final TreeViewer view = new TreeViewer(ruleNames, tree);
        view.open();
    }



    static void parseRule(final Reader code) throws IOException {
        final org.antlr.v4.runtime.CharStream input = new ANTLRInputStream(code);
        schemaLexer lexer = new schemaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        schemaParser parser = new schemaParser(tokens);
        schemaParser.ProgramContext tree = parser.program(); // parse a compilationUnit
        RuleListener classListener = new RuleListener();
        ParseTreeWalker.DEFAULT.walk(classListener, tree);

        System.out.println("# event/relation: "+classListener.schema.size());
        System.out.println("# MVD: "+classListener.countArrayListMVD());
        System.out.println("# generateRules: "+classListener.countAllRules());

        System.out.println("TAB PRINTING (ltd.)");
        System.out.println("===================");
        QueryGenerationConf qgc = new QueryGenerationConf();
        classListener.printQueriesFromTabs(qgc);
        System.out.println("===================\n");

        /*for (Map.Entry<Rule, ArrayList<Rule>> x : classListener.ruleTabClassification.entrySet()) {
            System.out.println(x.getKey());
            for (Rule y : x.getValue()) {
                System.out.println("\t"+y);
            }
        }*/

        /* OLD: System.err.println("INFO: printing the rules in json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("rules.json"), classListener.ruleTabClassification4DB);*/
        /*Iterable<String> it = () -> classListener.ruleTabClassification4DB.values().stream().flatMap(x -> x.values().stream())
                .map(Rule::toString).iterator();
        Files.write(Paths.get("example.tex"), it);*/

        System.err.println("INFO: detecting cycles");
        DependencyGraph dg = new DependencyGraph(classListener);
        dg.plot();
    }

    public static void main(String args[]) throws IOException {
        parseRule(new FileReader("schema_definition.txt"));
    }

}
