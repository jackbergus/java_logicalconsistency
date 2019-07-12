package it.giacomobergami.m18.configuration;

import it.giacomobergami.m18.TTLOntology2;
import it.giacomobergami.m18.graph_run.RunQuery;
import types.Schema;

import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;

public class QueryGenerationConfiguration {

    private static QueryGenerationConfiguration self;
    private Properties properties;
    private TTLOntology2 ontology2;
    private RunQuery expansionRunner;

    private QueryGenerationConfiguration(FileReader propertiesFile) throws IOException {
        properties.load(propertiesFile);

        System.err.println("Loading the ontology file");
        ontology2 = new TTLOntology2(properties.getProperty("ttlOntology2","data/SeedlingOntology2.ttl"));

        System.err.println("Loading the RunQuery module");
        expansionRunner = new RunQuery(new FileReader(properties.getProperty("additionalSchemaDefinition", "data/schema_definition.txt")));
    }

    public static QueryGenerationConfiguration getInstance() {
        if (self == null) {
            try {
                self = new QueryGenerationConfiguration(new FileReader("query_generation.properties"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return self;
    }

    public TTLOntology2 getOntology2() {
        return ontology2;
    }

    public RunQuery getExpansionRunner() {
        return expansionRunner;
    }

    public Optional<Schema> getSchemaDefinition(String key) {
        Schema ret = expansionRunner.classListener.schema.get(key);
        return ret == null ? Optional.empty() : Optional.of(ret);
    }

    public HashSet<String> variadicArguments() {
        return new HashSet<>(Arrays.asList(properties.getProperty("schemasDemultiplexAB").split(",")));
    }

    public String startTimeName() {
        return properties.getProperty("TimeStartFromRules","TStart");
    }

    public String endTimeName() {
        return properties.getProperty("TimeEndFromRules","TEnd");
    }

    public Integer getMaxInferenceTupleArguments() {
        return Integer.valueOf(properties.getProperty("nullS", "7"));
    }

}
