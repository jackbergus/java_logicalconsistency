package org.ufl.hypogator.jackb.streamutils.functions;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class FilePrinter<T> extends Printer<T> {

    private String file;
    PrintWriter out;

    public FilePrinter(String file) throws FileNotFoundException {
        out = new PrintWriter(file);
    }

    @Override
    public void accept(T t) {
        out.println(t);
    }

    public void close() {
        out.close();
    }
}
