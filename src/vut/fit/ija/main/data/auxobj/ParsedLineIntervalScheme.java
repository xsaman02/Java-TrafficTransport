package vut.fit.ija.main.data.auxobj;

import java.time.LocalTime;

/**
 * Auxiliary class
 */
public class ParsedLineIntervalScheme {
    public String lineId;
    public LocalTime arrival;
    public LocalTime departure;

    public ParsedLineIntervalScheme(String lineId, LocalTime arrival, LocalTime departure) {
        this.lineId = lineId;
        this.arrival = arrival;
        this.departure = departure;
    }

    @Override
    public String toString() {
        return "ParsedLineIntervalScheme{" +
                "tripId='" + lineId + '\'' +
                ", arrival=" + arrival +
                ", departure=" + departure +
                '}';
    }
}
