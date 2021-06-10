package vut.fit.ija.main.data.parse.strategy;

import vut.fit.ija.main.Log;
import vut.fit.ija.main.data.auxobj.ParsedLineComponent;

import java.util.logging.Level;

/**
 * Strategy for strategy design pattern
 * reads line and returns ParsedLineComponent
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 28-3-2020, xkarpi06
 * updated:
 */
public class LineComponentParser implements ParsingStrategy{

    // format: lineId,componentId,componentType

    /** Amount of variables per line */
    public static final int VARIABLE_COUNT = 3;

    /** Delimiter */
    public static final String DELIMITER = ";";

    /** Order in input file line */
    public static final int LINE_ID_INDEX = 0;

    /** Order in input file line */
    public static final int COMPONENT_ID_INDEX = 1;

    /** Order in input file line */
    public static final int COMPONENT_TYPE_INDEX = 2;

    @Override
    public ParsedLineComponent parseLine(String line) {
        ParsedLineComponent lineComponent = null;
        int var3 = 0;
        String[] variables = line.split(DELIMITER);
        if (variables.length == VARIABLE_COUNT) {
            String var1 = variables[LINE_ID_INDEX];
            String var2 = variables[COMPONENT_ID_INDEX];
            try {
                var3 = Integer.parseInt(variables[COMPONENT_TYPE_INDEX]);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
                return null;
            }
            ParsedLineComponent.ComponentType ct = ParsedLineComponent.ComponentType.valueOf(var3);
            if (ct != null) {   // can be null in case of incorrect input var3
                lineComponent = new ParsedLineComponent(var1, var2, ct);
                Log.LOGGER.log(Level.FINEST, "new {0}", lineComponent);
            }
        }
        return lineComponent;
    }
}
