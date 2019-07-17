package org.ufl.hypogator.jackb.server.handlers.concrete;

import com.google.common.collect.HashMultimap;
import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.Database;
import org.ufl.hypogator.jackb.m18.LoadFact;
import org.ufl.hypogator.jackb.m9.configuration.StaticDatabaseClass;
import org.ufl.hypogator.jackb.server.handlers.abstracts.SimplePostRequest;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class LoadDatabaseViaRequest extends SimplePostRequest {

    @Override
    public String handleContent(String content, HashMultimap<String, String> requestParameters) {
        Set<String> dbName_string = requestParameters.get("dbname");
        if (dbName_string == null || dbName_string.isEmpty()) {
            return "ERROR: no database was created because the dbname argument was not set up";
        }
        String dbName = dbName_string.iterator().next();
        StaticDatabaseClass.loadProperties();
        LoadFact lf = new LoadFact();
        Database opt = Database.openOrCreate(StaticDatabaseClass.engine, dbName, StaticDatabaseClass.username, StaticDatabaseClass.password).get();
        String toBeReturned = "Error on closing the database";
        try {
            File f = File.createTempFile("temporary_database_file", ".tsv");
            PrintWriter pw = new PrintWriter(f, StandardCharsets.UTF_8.toString());
            pw.print(content);
            pw.flush();
            pw.close();
            lf.loadForcefully(opt, f);
            f.delete(); // explicitely removing the temp file.
            toBeReturned = "Database " + dbName +" has been created";
        } catch (Exception e) {
            toBeReturned = Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.joining("\n"));
        } finally {
            try {
                opt.close();
            } catch (Exception e) {
                e.printStackTrace();
                toBeReturned += ". Also, error on closing the database.";
            }
        }
        return toBeReturned;
    }
}
