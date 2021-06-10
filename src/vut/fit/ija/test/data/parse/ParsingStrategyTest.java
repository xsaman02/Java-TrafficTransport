package vut.fit.ija.test.data.parse;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.instanceOf;

import vut.fit.ija.main.data.auxobj.*;
import vut.fit.ija.main.data.parse.strategy.*;
import vut.fit.ija.main.model.map.Line;
import vut.fit.ija.main.model.schedule.*;
import vut.fit.ija.main.model.vehicles.TransitType;

import java.time.LocalTime;

/**
 * Test class testing Classes implementing ParsingStrategy from main.data.parse package
 */
public class ParsingStrategyTest {

    /**
     * Test class StreetCoordinateParser
     */
    @Test
    public void givenLineOfSourceParserShouldCreateParsedStreetCoordinate() {
        ParsingStrategy ps = new StreetCoordinateParser();
        String var1 = "Ticha";
        String var2 = "200";
        String var3 = "300";
        String delim = ",";
        String input = var1 + delim + var2 + delim + var3;
        Object o = ps.parseLine(input);

        assertThat(o, instanceOf(ParsedStreetCoordinate.class));

        ParsedStreetCoordinate psc = (ParsedStreetCoordinate) o;

        assertEquals(var1, psc.streetId);
        assertEquals(Integer.parseInt(var2), psc.x);
        assertEquals(Integer.parseInt(var3), psc.y);
    }

    /**
     * Test class StopParser
     */
    @Test
    public void givenLineOfSourceParserShouldCreateParsedStop() {
        ParsingStrategy ps = new StopParser();
        String var1 = "Sestidomi";
        String var2 = "200";
        String var3 = "300";
        String var4 = "Horska";
        String delim = ";";
        String input = var1 + delim + var2 + delim + var3 + delim + var4;
        Object o = ps.parseLine(input);

        assertThat(o, instanceOf(ParsedStop.class));

        ParsedStop casted = (ParsedStop) o;

        assertEquals(var1, casted.stopId);
        assertEquals(Integer.parseInt(var2), casted.x);
        assertEquals(Integer.parseInt(var3), casted.y);
        assertEquals(var4, casted.streetId);
    }

    /**
     * Test class LineComponentParser
     */
    @Test
    public void givenLineOfSourceParserShouldCreateParsedLineComponent() {
        ParsingStrategy ps = new LineComponentParser();
        String var1 = "1";
        String var2 = "Sestidomi";
        String var3 = "0";
        String delim = ";";
        String input = var1 + delim + var2 + delim + var3;
        Object o = ps.parseLine(input);

        assertThat(o, instanceOf(ParsedLineComponent.class));

        ParsedLineComponent casted = (ParsedLineComponent) o;

        assertEquals(var1, casted.lineId);
        assertEquals(var2, casted.componentId);
        assertEquals(ParsedLineComponent.ComponentType.valueOf(Integer.parseInt(var3)), casted.type);
    }

    /**
     * Test class LineParser
     */
    @Test
    public void givenLineOfSourceParserShouldCreateLine() {
        ParsingStrategy ps = new LineParser();
        String var1 = "1";
        String var2 = "1";
        String delim = ",";
        String input = var1 + delim + var2;
        Object o = ps.parseLine(input);

        assertThat(o, instanceOf(Line.class));

        Line casted = (Line) o;

        assertEquals(var1, casted.getId());
        assertEquals(TransitType.valueOf(Integer.parseInt(var2)), casted.getTransitType());
    }

    /**
     * Test class LineIntervalSchemeParser
     */
    @Test
    public void givenLineOfSourceParserShouldCreateParsedLineIntervalScheme() {
        ParsingStrategy ps = new LineIntervalSchemeParser();
        String var1 = "1";
        String var2 = "10:30:00";
        String var3 = "10:31:00";
        String delim = ",";
        String input = var1 + delim + var2 + delim + var3;
        Object o = ps.parseLine(input);

        assertThat(o, instanceOf(ParsedLineIntervalScheme.class));

        ParsedLineIntervalScheme casted = (ParsedLineIntervalScheme) o;

        assertEquals(var1, casted.lineId);
        assertEquals(LocalTime.parse(var2), casted.arrival);
        assertEquals(LocalTime.parse(var3), casted.departure);
    }

    /**
     * Test class TripParser
     */
    @Test
    public void givenLineOfSourceParserShouldCreateParsedTrip() {
        ParsingStrategy ps = new TripParser();
        String var1 = "1";
        String var2 = "0";
        String var3 = "10:31:00";
        String delim = ",";
        String input = var1 + delim + var2 + delim + var3;
        Object o = ps.parseLine(input);

        assertThat(o, instanceOf(ParsedTrip.class));

        ParsedTrip casted = (ParsedTrip) o;

        assertEquals(var1, casted.lineId);
        assertEquals(Direction.valueOf(Integer.parseInt(var2)), casted.direction);
        assertEquals(LocalTime.parse(var3), casted.departure);
    }

}
