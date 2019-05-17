package org.ufl.hypogator.jackb.logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * Class that registres the loggers from the configuration file
 */
public class LoggerFactory {

    HashMap<String, Logger> loggerMap;
    private LoggerFactory() throws IOException {
        loggerMap = new HashMap<>();
        Properties properties = new Properties();
        properties.load(new FileReader(new File("conf/logging.properties")));
        for (String key : properties.stringPropertyNames()) {
            boolean isOn = false;
            File logger = null;
            Class clazz = null;
            String value = properties.getProperty(key, "on");
            if (value.contains(":")) {
                String[] opt = value.split(":");
                if (opt.length >= 2) {
                    if (opt[0].equals("on")) {
                        isOn = true;
                    } else {
                        isOn = false;
                    }
                    value = (opt[1]);
                    logger = new File(value);
                }
            } else {
                if (!value.equals("on")) {
                    logger = new File(value);
                    isOn = false;
                } else {
                    isOn = true;
                }
            }
            this.loggerMap.put(key, new Logger(isOn, logger));
        }
    }

    private static LoggerFactory self;
    private final static Logger noLogging = new Logger(false, null);

    /**
     * Getting the customized logger for the given class
     * @param clazz     Custom class decleared in the
     * @return
     */
    public static Logger getLogger(Class clazz) {
        if (self == null) {
            try {
                self = new LoggerFactory();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        Logger toret = self.loggerMap.get(clazz.getCanonicalName());
        return toret == null ? noLogging : toret;
    }

}
