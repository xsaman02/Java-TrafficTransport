package vut.fit.ija.main.data.auxobj;

import vut.fit.ija.main.model.schedule.Direction;

import java.time.LocalTime;

/**
 * Auxiliary class
 */
public class ParsedTrip {
    public String lineId;
    public Direction direction;
    public LocalTime departure;

    public ParsedTrip(String lineId, Direction direction, LocalTime departure) {
        this.lineId = lineId;
        this.direction = direction;
        this.departure = departure;
    }

    @Override
    public String toString() {
        return "ParsedTrip{" +
                "lineId='" + lineId + '\'' +
                ", direction=" + direction +
                ", departure=" + departure +
                '}';
    }
}
