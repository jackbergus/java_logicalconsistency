package org.ufl.hypogator.jackb.streamutils.data;

import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;
import org.ufl.hypogator.jackb.streamutils.utils.MapOperations;

import java.io.File;
import java.util.Iterator;
import java.util.function.Function;

public class FileLoader {
    private static LineSeparatedObjects lso = new LineSeparatedObjects();
    private static FileLoader tabSplitter = new FileLoader("\t");

    Function<String, String[]> lineSplitter;
    Function<Iterator<String>, String[]> splitterFromIterator;

    public FileLoader(String regexRowSplitter) {
        lineSplitter = string -> string.split(regexRowSplitter);
        splitterFromIterator = iterator -> iterator.hasNext() ? iterator.next().split(regexRowSplitter) : new String[0];
    }

    public static IteratorWithOperations<String> getLineIterator(File file) {
        return lso.apply(file);
    }

    public static FileLoader tabHeadedFiles() {
        return tabSplitter;
    }

    public IteratorWithOperations<Tuple> getIterator(File file) {
        return lso.apply(file)
                .extractMetadata(splitterFromIterator)
                .map((strings, s) -> Tuple.fromMap(MapOperations.chain(strings, lineSplitter.apply(s))));
    }

    public IteratorWithOperations<Tuple> getIIterator(File file) {
        return lso.apply(file)
                .extractMetadata(splitterFromIterator)
                .mapi((strings, s) -> Tuple.fromMap(MapOperations.chain(strings, lineSplitter.apply(s))))
                .map(t -> t.getValue().putStream("row#", t.getKey().toString()));
    }


}
