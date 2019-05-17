package org.ufl.aida.ldc;

import au.com.bytecode.opencsv.CSVReader;
import org.ufl.hypogator.jackb.fuzzymatching.ldc.LDCMatching;

import java.io.*;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

@Deprecated
public class Extractor {

    public static void main(String[] args) throws IOException {
        System.err.println("Loading the matcher---");
        Set<String> writed = new HashSet<>();
        FileWriter writer = new FileWriter("/home/giacomo/Scrivania/manually/man_edges.txt", true);
        LDCMatching matching = LDCMatching.getInstance();
        Scanner in = new Scanner(System.in);
        CSVReader reader = new CSVReader(new FileReader(new File("/home/giacomo/Scrivania/manually/man.csv")));
        String[] elems = null;
        while ((elems = reader.readNext()) != null) {
            System.out.println("Term : " + elems[0]);
            System.out.println("Type : " + elems[1]);
            System.out.println("Description : " + elems[2]);
            if (!writed.add(elems[0])) continue;

            System.out.println("\nAggiungere una relazione (S/n)?");
            Boolean addRelationship = in.nextLine().trim().toLowerCase().equals("s");
            if (addRelationship) {
                do {
                    System.out.println("\nLa relazione della descrizione appartiene alla gerarchia (S/n)?");
                    Boolean hierarchy = in.nextLine().trim().toLowerCase().equals("s");
                    String matched;

                    System.out.println("\nL'elemento appartiene a LDC? (S/n)?");
                    if (in.nextLine().trim().toLowerCase().equals("s")) {
                        boolean isWhatSaid = false;
                        do {
                            System.out.println("Insert a stirng identifying the element: ");
                            matched = matching.bestFuzzyMatch(in.nextLine()).resolved;
                            System.out.println("\nDid you mean '" + matched + "' (S/n)?");
                            isWhatSaid = in.nextLine().trim().toLowerCase().equals("s");
                        } while (!isWhatSaid);
                    } else {
                        matched = in.nextLine();
                    }

                    writer.write(elems[0] + "," + elems[1] + "," + hierarchy + "," + matched + "\n");
                    writer.flush();
                    System.out.println("\nAggiungere un'altra relazione (S/n)?");
                    addRelationship = in.nextLine().trim().toLowerCase().equals("s");
                } while (addRelationship);
            }
        }
        writer.close();
        reader.close();
    }

}
