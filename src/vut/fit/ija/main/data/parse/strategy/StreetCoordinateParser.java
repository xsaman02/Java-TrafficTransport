package vut.fit.ija.main.data.parse.strategy;

import vut.fit.ija.main.Log;
import vut.fit.ija.main.data.auxobj.ParsedStreetCoordinate;

import java.util.logging.Level;

/**
 * Strategy for strategy design pattern
 * reads line and returns ParsedStreetCoordinate
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 28-3-2020, xkarpi06
 * updated:
 */
public class StreetCoordinateParser implements ParsingStrategy {

    // format: streetId,x,y

    /** Amount of variables per line */
    public static final int VARIABLE_COUNT = 3;

    /** Delimiter */
    public static final String DELIMITER = ",";

    /** Order in input file line */
    public static final int STREET_ID_INDEX = 0;

    /** Order in input file line */
    public static final int X_INDEX = 1;

    /** Order in input file line */
    public static final int Y_INDEX = 2;

    @Override
    public ParsedStreetCoordinate parseLine(String line) {
        ParsedStreetCoordinate streetCoordinate = null;
        int var2 = 0, var3 = 0;
        String[] variables = line.split(DELIMITER);
        if (variables.length == VARIABLE_COUNT) {
            String var1 = variables[STREET_ID_INDEX];
            try {
                var2 = Integer.parseInt(variables[X_INDEX]);
                var3 = Integer.parseInt(variables[Y_INDEX]);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                return null;
            }
            streetCoordinate = new ParsedStreetCoordinate(var1, var2, var3);
            Log.LOGGER.log(Level.FINEST, "new {0}", streetCoordinate);
        }
        return streetCoordinate;
    }
}
