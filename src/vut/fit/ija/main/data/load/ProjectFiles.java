package vut.fit.ija.main.data.load;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Keeps track of location for neccesary files and directories
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 23-3-2020, xkarpi06
 * updated: 29-3-2020, xkarpi06
 */
public class ProjectFiles {

    /** Current working directory */
    public static final String WORKING_DIRECTORY = System.getProperty("user.dir");

    /** Directory containing input city data */
    public static final String DATA_DIRECTORY = WORKING_DIRECTORY.concat("/").concat("data");

    /** Directory for saving logs */
    public static final String LOGS_DIRECTORY = WORKING_DIRECTORY.concat("/").concat("logs");

    /** File for saving logs */
    public static final String LOGS_FILE = LOGS_DIRECTORY.concat("/").concat("log.txt");

    /** Name of source file for ParsedStreetCoordinate */
    public static final String SRC_STREET_COORDINATES = "street_coordinates.txt";

    /** Name of source file for ParsedStops */
    public static final String SRC_STOPS = "stops.txt";

    /** Name of source file for ParsedLine */
    public static final String SRC_LINES = "lines.txt";

    /** Name of source file for ParsedLineComponent */
    public static final String SRC_LINE_COMPONENTS = "line_components.txt";

    /** Name of source file for ParsedLineComponent */
    public static final String SRC_LINE_INTERVAL_SCHEMES = "line_interval_schemes.txt";

    /** Name of source file for ParsedTrips */
    public static final String SRC_TRIPS = "trips.txt";

    /** Directory containing test data for unit tests */
    public static final String TEST_DATA_DIRECTORY = WORKING_DIRECTORY.concat("/").concat("test_data");

    public static final Set<String> SRC_FILES = new HashSet<>(Arrays.asList(
            SRC_STREET_COORDINATES,
            SRC_STOPS,
            SRC_LINES,
            SRC_LINE_COMPONENTS,
            SRC_LINE_INTERVAL_SCHEMES,
            SRC_TRIPS
    ));

}
