package org.ufl.hypogator.jackb.streamutils.data;

import org.ufl.hypogator.jackb.streamutils.iterators.IteratorWithOperations;

import java.io.File;
import java.util.function.Function;

/**
 * A line reader maps a file to an iteration over its lines
 */
public interface LineReader extends Function<File, IteratorWithOperations<String>> {
}
