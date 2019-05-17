package org.ufl.hypogator.jackb.streamutils.operations;

public interface Split<T> {
    /**
     * Splitting function allowing to detect the headers from the header line
     *
     * @param toSplit String representing the header
     * @return Each String represents one element value
     */
    public T[] splitter(T toSplit);
}
