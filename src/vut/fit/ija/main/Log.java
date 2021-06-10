package vut.fit.ija.main;

import vut.fit.ija.main.data.load.DataLoader;
import vut.fit.ija.main.data.load.ProjectFiles;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Manages creating log files and setting Logger.
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * last updated: 22-3-2020
 */
public class Log {

    /** Logger instance */
    public static final Logger LOGGER = Logger.getLogger( DataLoader.class.getName() );

    /** Level determines what to log */
    public static final Level LOG_LEVEL = Level.INFO;

    /**
     * Sets Logger properties in whole project
     */
    public static void setLogger() {
//        String workingDirectory = System.getProperty("user.dir");
//        createLogDirectory();
//        try {
//            FileHandler fh = new FileHandler(ProjectFiles.LOGS_FILE);
            // ensures human readable logs, alternative (and default) is new XMLFormatter()
//            fh.setFormatter(new SimpleFormatter());
            Log.LOGGER.setLevel(LOG_LEVEL);
//            Log.LOGGER.addHandler(fh);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Creates directory for saving logs
     */
    private static void createLogDirectory() {
        File directoryPath = new File(ProjectFiles.LOGS_DIRECTORY);
        directoryPath.mkdirs();
    }
}
