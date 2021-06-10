package vut.fit.ija.main.data.parse;

import vut.fit.ija.main.data.load.ProjectFiles;
import vut.fit.ija.main.data.parse.strategy.*;

/**
 * Context for strategy design pattern
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 22-3-2020, xkarpi06
 * updated: 29-3-2020, xkarpi06
 */
public class StringParser {

    private ParsingStrategy strategy;

    /**
     * Constructor
     * @param strategy input
     */
    public StringParser(ParsingStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * Smart Constructor
     * Chooses strategy based on file name
     * @param fileName input
     */
    public StringParser(String fileName) {
        switch (fileName) {
            case ProjectFiles.SRC_STREET_COORDINATES:
                this.strategy = new StreetCoordinateParser();
                break;
            case ProjectFiles.SRC_STOPS:
                this.strategy = new StopParser();
                break;
            case ProjectFiles.SRC_LINES:
                this.strategy = new LineParser();
                break;
            case ProjectFiles.SRC_LINE_COMPONENTS:
                this.strategy = new LineComponentParser();
                break;
            case ProjectFiles.SRC_LINE_INTERVAL_SCHEMES:
                this.strategy = new LineIntervalSchemeParser();
                break;
            case ProjectFiles.SRC_TRIPS:
                this.strategy = new TripParser();
                break;
            default:
                this.strategy = null;
                break;
        }
    }

    /**
     * Executes particular strategy how to parse line to Object
     * @param line input
     * @return Object based on the strategy, can be null
     */
    public Object exectueStrategy(String line) {
        return (this.strategy == null) ? null : this.strategy.parseLine(line);
    }
}
