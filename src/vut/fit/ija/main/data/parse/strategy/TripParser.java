package vut.fit.ija.main.data.parse.strategy;

import vut.fit.ija.main.Log;
import vut.fit.ija.main.data.auxobj.ParsedTrip;
import vut.fit.ija.main.model.schedule.Direction;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.logging.Level;

/**
 * Strategy for strategy design pattern
 * reads line and returns ParsedTrip
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 29-3-2020, xkarpi06
 * updated:
 */
public class TripParser implements ParsingStrategy {

    // format: lineId,direction,departure

    /** Amount of variables per line */
    public static final int VARIABLE_COUNT = 3;

    /** Delimiter */
    public static final String DELIMITER = ",";

    /** Order in input file line */
    public static final int LINE_ID_INDEX = 0;

    /** Order in input file line */
    public static final int DIRECTION_INDEX = 1;

    /** Order in input file line */
    public static final int DEPARTURE_INDEX = 2;

    @Override
    public ParsedTrip parseLine(String inputLine) {
        ParsedTrip newTrip = null;
        String[] variables = inputLine.split(DELIMITER);
        if (variables.length == VARIABLE_COUNT) {
            String var1 = variables[LINE_ID_INDEX];
            int var2 = 0;
            LocalTime var3 = null;
            try {
                var2 = Integer.parseInt(variables[DIRECTION_INDEX]);
            } catch (NumberFormatException ex) {
                Log.LOGGER.log(Level.FINEST, "NumberFormatException {0}", variables[DIRECTION_INDEX]);
                return null;
            }
            try {
                var3 = LocalTime.parse(variables[DEPARTURE_INDEX]);
            } catch (DateTimeParseException ex) {
                Log.LOGGER.log(Level.FINEST, "DateTimeParseException {0}", variables[DEPARTURE_INDEX]);
            }
            Direction dir = Direction.valueOf(var2);
            if (dir != null) {   // can be null in case of incorrect input var2
                newTrip = new ParsedTrip(var1, dir, var3);
                Log.LOGGER.log(Level.FINEST, "new {0}", newTrip);
            }
        }
        return newTrip;
    }
}
