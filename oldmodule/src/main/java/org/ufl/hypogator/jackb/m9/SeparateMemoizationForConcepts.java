package org.ufl.hypogator.jackb.m9;

import org.ufl.aida.ldc.dbloader.tmpORM.withReflection.dbms.Database;
import org.ufl.hypogator.jackb.ontology.TtlOntology;
import org.ufl.hypogator.jackb.traversers.conceptnet.Concept5ClientConfigurations;
import org.ufl.hypogator.jackb.utils.adt.HashMultimapSerializer;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Files;
import java.util.Scanner;

public class SeparateMemoizationForConcepts extends StaticDatabaseClass {

    public static void main(String argo[]) throws Exception {
        FileWriter fileWriter = new FileWriter(argo[0]+"_missiedConfs");
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.print("Some String");
        printWriter.printf("Product name is %s and its price is %d $", "iPhone", 1000);
        TtlOntology ontology = new TtlOntology("data/SeedlingOntology.ttl");
        loadProperties();
        Database opt = Database.openOrCreate(engine, dbname, username, password).get();
        if (argo.length == 1) {
            Files.readAllLines(new File(argo[0]).toPath()).forEach(x -> {
                String args[] = x.split("\\s");
                String folder = args[0];
                String tuplesType = args[1];
                String dimension = args[2];
                File folderFile = new File(folder);
                if (!folderFile.exists())
                    folderFile.mkdirs();
                File nistDimensionFile = new File(folderFile, tuplesType);
                System.out.println(folder+"\t"+tuplesType+"\t"+dimension);
                if (!nistDimensionFile.exists())
                    nistDimensionFile.mkdirs();
                CompareOnce.selectiveTypeMemoizationToDisk(opt, dimension, nistDimensionFile, tuplesType);
            });
        }
        printWriter.close();
        opt.close();
    }

}
