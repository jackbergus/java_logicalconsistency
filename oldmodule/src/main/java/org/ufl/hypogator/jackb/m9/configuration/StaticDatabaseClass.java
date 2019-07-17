package org.ufl.hypogator.jackb.m9.configuration;

import com.google.common.collect.HashMultimap;
import javafx.util.Pair;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.Database;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.rdbms.DBMSInterface;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.rdbms.DBMSInterfaceFactory;
import org.ufl.hypogator.jackb.ConfigurationEntrypoint;
import org.ufl.hypogator.jackb.fuzzymatching.MultiWordSimilarity;
import org.ufl.hypogator.jackb.inconsistency.legacy.TupleComparator;
import org.ufl.hypogator.jackb.inconsistency.typecomparisonpolicy.FieldComparisonPolicyFactory;
import org.ufl.hypogator.jackb.logger.LoggerFactory;
import org.ufl.hypogator.jackb.server.handlers.concrete.InconsistencyDetection;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

public class StaticDatabaseClass {

    public static Properties properties = new Properties();
    public static final TupleComparator comparator = TupleComparator.getDefaultTupleComparator();
    public static String dbname, username, password;
    public static Integer batchSize;
    public static DBMSInterface engine;
    //private HashMultimap<String, String> dictionary_ToKBID;
    public HashMap<String, String> kbidToHandle;
    public HashMultimap<String, String> kbidToTerms;
    public HashMap<String, Pair<Long, String>> kbtoNistType;
    public static MultiWordSimilarity sim = new MultiWordSimilarity();
    //private static JsonOntologyLoader ontologyEntrypoint = JsonOntologyLoader.getInstance();
    public final static org.ufl.hypogator.jackb.logger.Logger LOGGER = LoggerFactory.getLogger(InconsistencyDetection.class);
    //public final static boolean doMemoization = FieldComparisonPolicyFactory.getInstance().doesFieldComparisonPolicyRequireDimensionMemoization(ConfigurationEntrypoint.getInstance().typingPolicy);

    public static boolean loadProperties() {
        if (properties.isEmpty()) try {
            properties.load(new FileInputStream("conf/postgresql.properties"));
            engine = DBMSInterfaceFactory.generate(properties.getProperty("engine", "PostgreSQL"));
            dbname = properties.getProperty("ta2name", "ldc");
            username = properties.getProperty("ta2username", System.getProperty("user.name"));
            password = properties.getProperty("ta2password", "password");
            batchSize = Integer.valueOf(properties.getProperty("batchSize", "1000"));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } else
            return true;
    }

    public static Optional<Database> databaseConnection() {
        if (!loadProperties()) return Optional.empty();
        return Database.open(engine, dbname, username, password);
    }

    public static Database openDatabase() {
        return Database.openOrCreate(engine, dbname, username, password).get();
    }
}
