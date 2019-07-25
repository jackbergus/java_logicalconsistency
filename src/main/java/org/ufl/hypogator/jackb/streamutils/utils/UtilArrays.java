package org.ufl.hypogator.jackb.streamutils.utils;

public class UtilArrays {

    public static int[] range(int min, int max) {
        int[] list = new int[max - min + 1];
        int j = 0;
        for (int i = min; i <= max; i++) {
            list[j++] = i;
        }
        return list;
    }

}

