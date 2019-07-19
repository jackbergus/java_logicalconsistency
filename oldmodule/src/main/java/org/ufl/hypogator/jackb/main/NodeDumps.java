package org.ufl.hypogator.jackb.main;

import java.io.File;
import java.io.IOException;

public class NodeDumps {

    public static void main(String args[]) throws IOException {
        ConceptNet5Postgres.getInstance().dumpNodestoFile(new File("nodes.csv"));
    }

}
