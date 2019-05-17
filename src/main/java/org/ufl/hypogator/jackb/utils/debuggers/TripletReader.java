package org.ufl.hypogator.jackb.utils.debuggers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.ufl.hypogator.jackb.traversers.conceptnet.jOOQ.conceptnet.queries.answerFormat.EdgeVertex;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.Scanner;

public class TripletReader {

    public static HashSet<String> toRemove = new HashSet<>();

    public static void main(String args[]) throws IOException {
        File directory = new File("/media/giacomo/Biggus/project_dir/data/hierarchies/");
        ObjectMapper om = new ObjectMapper();
        ObjectReader reader = om.readerFor(EdgeVertex.class);

        if (directory.exists() && directory.isDirectory()) {
            for (File f : directory.listFiles()) {
                if (f.isFile() && f.getName().endsWith(".json")) {
                    File n = new File(f.getAbsolutePath()+".bak");
                    System.out.println(f.getName());
                    Scanner s = new Scanner(new FileReader(f));
                    while (s.hasNextLine()) {
                        String[] line = s.nextLine().split("\t");
                        EdgeVertex src = reader.readValue(line[0]);
                        EdgeVertex dst = reader.readValue(line[1]);
                        String toadd = equivalencePath(src);
                        if (toadd != null) toRemove.add(toadd);
                        toadd = equivalencePath(dst);
                        if (toadd != null) toRemove.add(toadd);
                    }
                    s.close();
                    Files.copy(f.toPath(), n.toPath(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);

                    s = new Scanner(new FileReader(n));
                    PrintWriter w = new PrintWriter(f);
                    while (s.hasNextLine()) {
                        String actualLine = s.nextLine();
                        String[] line = actualLine.split("\t");
                        EdgeVertex src = reader.readValue(line[0]);
                        EdgeVertex dst = reader.readValue(line[1]);
                        String toadd = equivalencePath(src);
                        if (toRemove.contains(toadd)) continue;
                        toadd = equivalencePath(dst);
                        if (toRemove.contains(toadd)) continue;
                        w.println(actualLine);
                    }
                    s.close();
                    w.close();
                    System.out.println(f.getName()+" rectified ");
                    if (n.exists()) n.delete();
                }
            }
        }

    }

    private static String equivalencePath(EdgeVertex src) {
        if (src.getSemanticId().endsWith("/v") || src.getSemanticId().endsWith("/a")) {
            String[] id = src.getSemanticId().split("/");
            return "/"+id[1]+"/"+id[2]+"/"+id[3];
        }
        return null;
    }

}
