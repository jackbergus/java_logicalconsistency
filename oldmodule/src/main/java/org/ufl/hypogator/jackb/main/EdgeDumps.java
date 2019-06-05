package org.ufl.hypogator.jackb.main;

import org.ufl.hypogator.jackb.traversers.conceptnet.ConceptNet5Postgres;

import java.io.File;
import java.io.IOException;

public class EdgeDumps {

    public static void main(String args[]) throws IOException {
        ConceptNet5Postgres.getInstance().dumpEdgestoFile(new File("edges.csv"));
    }

}
