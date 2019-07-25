package org.ufl.aida.ldc.dbloader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides some file utility access
 */
public class FileUtils {

    /**
     * Returns all the subdirectories within the current folder
     * @param folder
     * @return
     */
    public static List<File> getSubDirectories(File folder) {
        File[] listOfFiles = folder.listFiles();
        List<File> arrayList = new ArrayList<>(listOfFiles.length);
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isDirectory()) {
                arrayList.add(listOfFiles[i]);
            }
        }
        return arrayList;
    }
}
