package org.ufl.hypogator.jackb.utils.debuggers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class PrintAsEdges {

    public static void main(String args[]) throws FileNotFoundException {
        FileReader f = new FileReader("/home/giacomo/Scrivania/manually/nojibo.txt");
        Scanner s = new Scanner(f);
        while (s.hasNextLine()) {
            String line = s.nextLine();

        }
    }

}
