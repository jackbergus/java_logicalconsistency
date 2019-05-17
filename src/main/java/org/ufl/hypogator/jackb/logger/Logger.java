package org.ufl.hypogator.jackb.logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

    public boolean isOn;
    private FileWriter file;

    public Logger(boolean isOn, File fileWriter) {
        this.isOn = isOn;
        if (fileWriter != null) {
            try {
                file = new FileWriter(fileWriter);
            } catch (IOException ignored) {
                System.err.println("Cannot find/open/write file "+fileWriter);
            }
        }
    }

    public void out(String debug) {
        if (isOn) {
            System.out.println(debug);
            if (file != null) {
                try {
                    file.append(debug).append("\n");
                    file.flush();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public void debug(String debug) {
        if (isOn) {
            System.err.println(debug);
            if (file != null) {
                try {
                    file.append(debug).append("\n");
                    file.flush();
                } catch (IOException ignored) {
                }
            }
        }
    }

    @Override
    public void finalize() {
        close();
    }

    public void close() {if (file != null) {
        if (file != null) try {
            file.close();
        } catch (IOException ignored) {
        }
        }
    }
}
