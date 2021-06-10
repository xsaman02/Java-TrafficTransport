package vut.fit.ija.main.data.parse.strategy;

import vut.fit.ija.main.Log;
import vut.fit.ija.main.data.auxobj.ParsedLineIntervalScheme;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;

/**
 * Strategy for strategy design pattern
 * reads line and returns ParsedLineIntervalScheme
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 29-3-2020, xkarpi06
 * updated:
 */
public class LineIntervalSchemeParser implements ParsingStrategy {

    // format: lineId,componentId,componentType

    /** Amount of variables per line */
    public static final int VARIABLE_COUNT = 3;

    /** Delimiter */
    public static final String DELIMITER = ",";

    /** Order in input file line */
    public static final int LINE_ID_INDEX = 0;

    /** Order in input file line */
    public static final int ARRIVAL_INDEX = 1;

    /** Order in input file line */
    public static final int DEPARTURE_INDEX = 2;

    @Override
    public ParsedLineIntervalScheme parseLine(String line) {
        ParsedLineIntervalScheme intervalScheme = null;
        String[] variables = line.split(DELIMITER);
        if (variables.length == VARIABLE_COUNT) {
            String var1 = variables[LINE_ID_INDEX];
            LocalTime var2, var3;
            try {
                var2 = LocalTime.parse(variables[ARRIVAL_INDEX]);
                var3 = LocalTime.parse(variables[DEPARTURE_INDEX]);
            } catch (DateTimeParseException ex) {
                Log.LOGGER.log(Level.FINEST, "DateTimeParseException");
                return null;
            }
            intervalScheme = new ParsedLineIntervalScheme(var1, var2, var3);
            Log.LOGGER.log(Level.FINEST, "new {0}", intervalScheme);
        }
        return intervalScheme;
    }
}
