package it.giacomobergami.m18.schemas;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import ref.RuleListener;
import ref.schemaLexer;
import ref.schemaParser;
import types.Schema;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;

public class LoadSchemas {
    private static LoadSchemas self = null;
    private RuleListener classListener;
    private static Properties properties;

    static {
        try {
            properties = new Properties();
            properties.load(new FileReader("query_generation.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public LoadSchemas(FileReader code) throws IOException {
        final org.antlr.v4.runtime.CharStream input = new ANTLRInputStream(code);
        schemaLexer lexer = new schemaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        schemaParser parser = new schemaParser(tokens);
        schemaParser.ProgramContext tree = parser.program(); // parse a compilationUnit
        classListener = new RuleListener(); // TODO: two step expansion with the other element
        ParseTreeWalker.DEFAULT.walk(classListener, tree);
    }

    /**
     * Instantiates the new schema parser, that is also its extension to support the rule rewriting
     * @return
     */
    public static Optional<LoadSchemas> newInstance() {
        if (self == null) {
            try {
                self = new LoadSchemas(new FileReader(properties.getProperty("schemaDefinitions")));
            } catch (IOException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }
        return Optional.of(self);
    }

    /**
     * Getting the schema definition from the key, that is the type, associated to the element.
     * @param key
     * @return
     */
    public static Optional<Schema> getSchemaDefinition(String key) {
        return newInstance().flatMap(x -> {
            Schema ret = x.classListener.schema.get(key);
            return ret == null ? Optional.empty() : Optional.of(ret);
        });
    }

    public static HashSet<String> variadicArguments() {
        return new HashSet<>(Arrays.asList(properties.getProperty("schemasDemultiplexAB").split(",")));
    }

    public static String startTimeName() {
        return properties.getProperty("TimeStartFromRules","TStart");
    }

    public static String endTimeName() {
        return properties.getProperty("TimeEndFromRules","TEnd");
    }

}
