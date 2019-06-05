package org.ufl.hypogator.jackb.traversers.conceptnet;

import cz.adamh.utils.NativeUtils;

import java.io.IOException;

/**
 * This class provides the interface to the C++ library that reads the graph in a binary format
 */
public class JNIEntryPoint {
    static {
        try {
            NativeUtils.loadLibraryFromJar("/libjni.so");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public native String[] pgObjectOut(long offset, boolean ingoing, long relMap);
    public native synchronized void dispose();
    public native synchronized void init();

    private JNIEntryPoint() {}
    private static JNIEntryPoint self;
    public static final synchronized JNIEntryPoint getInstance() {
        if (self == null) {
            self = new JNIEntryPoint();
            self.init();
        }
        return self;
    }

    public static void main(String args[]) {
        JNIEntryPoint element = new JNIEntryPoint();
        element.init();
        for (String x : element.pgObjectOut(125438203L, true, 549755813892L))
            System.out.println(x);
        element.dispose();
    }
}
