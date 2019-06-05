package org.ufl.hypogator.jackb.m9;

import java.io.*;
import java.nio.file.Files;

public class MergingResult {

    public static void writeFilesInParallel() {
        // File appending for the concurrent threads
        File dir = new File(".");
        //fileWriter = new FileWriter("inconsistencies.json");
        //PrintWriter printWriter = new PrintWriter(fileWriter);
        //printWriter.write("{ \"inconsistencies\": [");
        File[] files = dir.listFiles((dir1, name) -> name.toLowerCase().endsWith(".out.txt"));
            /*for (int i = 0, filesLength = files.length; i < filesLength; i++) {
                File f = files[i];
                String inco = String.join(" ", Files.readAllLines(f.toPath()));
                if (inco.endsWith(",") && i == filesLength -1) {
                    inco = inco.substring(0, inco.length()-1);
                }
                f.delete(); // freeing some disk space: removing the data before writing that again.
                printWriter.write(inco);
            }*/
        mergeFiles(files, new File("inconsistencies_bbn.json"));
        //printWriter.write("]}");
        //printWriter.close();
    }

    public static void mergeFiles(File[] files, File target) {
        OutputStream fos = null;
        try {
            fos = new FileOutputStream(target);
            fos.write("{ \"inconsistencies\": [".getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (File f : files) {
            InputStream fis = null;
            try {
                fis = new FileInputStream(f);
                byte[] buf = new byte[4096];
                int i;
                while ((i = fis.read(buf)) != -1) {
                    fos.write(buf, 0, i);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            f.delete();
        }
        try {
            //writing a bogus couple
            fos.write("[\"0\",\"0\"]]}".getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
