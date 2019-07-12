package org.ufl.aida.ldc.dbloader.tmptables;

import org.ufl.aida.ldc.dbloader.MapIterator;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public abstract class SourceTabLoader {

    /**
     *
     * @param args
     */
    public abstract void load(String[] args);

    /**
     *
     * @param parentFolder  Parent folder where the file should be located
     * @return              File from which load the object
     */
    public abstract File getFile(File parentFolder);

    /**
     * Generates a new object.
     * @param <T>   Type of the subclass
     * @return      Each class extending this class should return an empty not initialized object
     */
    public abstract <T extends SourceTabLoader> T generateNew();

    /**
     *
     * @param self          Empty object generating sibling elements of the same type
     * @param parentFolder          parentFolder from which load the sources
     * @param <T>           Subclass extending the SourceTabLoader
     * @return              An iterator over the file, which is expressed as an iterator over T
     * @throws IOException  The file is not present
     */
    public static <T extends SourceTabLoader> Iterator<T> loadFromFolder(final T self, File parentFolder) throws IOException {
        final BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(parentFolder), StandardCharsets.UTF_8));

        // Removing the header;
        br.readLine();

        // Reading the first line after the header
        String ln = br.readLine();

        // File iterator
        Iterator<String> it = new Iterator<String>() {
            final BufferedReader file = br;
            String line = ln;

            @Override
            public boolean hasNext() {
                return line != null;
            }

            @Override
            public String next() {
                String toReturn = line;
                try {
                    line = br.readLine();
                } catch (IOException e) {
                    return null;
                }
                return toReturn;
            }
        };

        // Mapping each line into an object
        return new MapIterator<String, T>(it) {
            @Override
            public T apply(String s) {
                // Generates a new object
                T ret = self.generateNew();
                // Feeding the tab-separated tata to the object
                ret.load(s.split("\t"));

                // providing the object
                return ret.failed() ? null : ret;
            }
        };
    }

    private boolean fail = false;

    public void setFailed() {
        fail = true;
    }

    public boolean failed() {
        return fail;
    }
}
