package org.ufl.hypogator.jackb.streamutils.data;

import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;
import org.ufl.hypogator.jackb.streamutils.iterators.VoidIterator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Reads a file line by line using an org.ufl.hypogator.jackb.streamutils.iterator
 */
public class LineSeparatedObjects implements LineReader {

    @Override
    public IteratorWithOperations<String> apply(File file) {
        try {
            return new IteratorWithOperations<String>() {
                Scanner scanner = new Scanner(file);

                @Override
                public boolean hasNext() {
                    return scanner.hasNextLine();
                }

                @Override
                public String next() {
                    return scanner.nextLine();
                }
            };
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return VoidIterator.get();
        }

    }

}
