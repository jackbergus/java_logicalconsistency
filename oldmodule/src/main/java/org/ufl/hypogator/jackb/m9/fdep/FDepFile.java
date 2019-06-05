package org.ufl.hypogator.jackb.m9.fdep;

import com.google.common.collect.HashMultimap;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

/**
 * This class parses the list of functional dependencies over a file.
 */
public class FDepFile {

    public HashMultimap<String, FDep> fDepMap;

    /**
     *
     * @param f             File where to load the functional dependencies from
     * @throws IOException
     */
    public FDepFile(File f)  {
        fDepMap = HashMultimap.create();
        try {
            Files.readAllLines(f.toPath()).forEach(x -> {
                if (x.startsWith("#")) return;
                if ((!x.isEmpty()) && x.contains(":=")) {
                    int indexOf = x.indexOf(":=");
                    String header = x.substring(0, indexOf).trim();
                    if (!header.isEmpty()) {
                        String fd = x.substring(indexOf+2).trim();
                        if (!fd.isEmpty()) {
                            fDepMap.put(header, new FDep(fd));
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*public Set<FDep> getFunctionalDependenciesFor(String type) {
        return fDepMap.get(type);
    }*/
    /*public static void main(String args[]) {
        System.out.println(new FDepFile(new File("data/fdep.txt")).fDepMap);
    }*/
}
