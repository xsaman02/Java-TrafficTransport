package vut.fit.ija.main.data.parse.strategy;

import vut.fit.ija.main.Log;
import vut.fit.ija.main.model.map.Line;
import vut.fit.ija.main.model.vehicles.TransitType;

import java.util.logging.Level;

/**
 * Strategy for strategy design pattern
 * reads line and returns ParsedStop
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 28-3-2020, xkarpi06
 * updated:
 */
public class LineParser implements ParsingStrategy{

    // format: lineId,transitType

    /** Amount of variables per line */
    public static final int VARIABLE_COUNT = 2;

    /** Delimiter */
    public static final String DELIMITER = ",";

    /** Order in input file line */
    public static final int LINE_ID_INDEX = 0;

    /** Order in input file line */
    public static final int TRANSIT_TYPE_INDEX = 1;

    @Override
    public Line parseLine(String inputLine) {
        Line newLine = null;
        int var2 = 0;
        String[] variables = inputLine.split(DELIMITER);
        if (variables.length == VARIABLE_COUNT) {
            String var1 = variables[LINE_ID_INDEX];
            try {
                var2 = Integer.parseInt(variables[TRANSIT_TYPE_INDEX]);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                return null;
            }
            TransitType tt = TransitType.valueOf(var2);
            if (tt != null) {   // can be null in case of incorrect input var2
                newLine = Line.defaultInstance(var1, tt);
                Log.LOGGER.log(Level.FINEST, "new {0}", newLine.toString2());
            }
        }
        return newLine;
    }
}
