package vut.fit.ija.main.model.schedule;

import vut.fit.ija.main.model.map.Line;
import vut.fit.ija.main.model.map.Stop;
import vut.fit.ija.main.model.map.Street;
import vut.fit.ija.main.model.schedule.impl.TripImpl;

import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.List;

/**
 * Represents one trip during day on a line
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 27-3-2020, xkarpi06
 * updated: 22-4-2020, xkarpi06 added getRoute()
 */
public interface Trip {

    /**
     * Getter
     * @return included line
     */
    Line getLine();

    /**
     * Gives immutable list of route elements consisting of Streets
     * with and without stops, route is ordered according to trip's direction!
     * @return immutable list
     */
    List<AbstractMap.SimpleImmutableEntry<Street,Stop>> getRoute();

    /**
     * Getter
     * @return trip direction
     */
    Direction getDirection();

    /**
     * Getter
     * @return list representing schedule of trip
     */
    List<AbstractMap.SimpleImmutableEntry<Stop, StopTime>> getSchedule();

    /**
     * Get departure from first stop
     * @return time
     */
    LocalTime getDeparture();

    /**
     * Get arrival time to final stop
     * @return time
     */
    LocalTime getArrival();

    /**
     * Default constructor
     * @param line where this trip happens
     * @param d direction
     * @param departure departure time from first stop
     * @return Trip if line contains LineIntervalScheme, null otherwise
     */
    static Trip defaultInstance(Line line, Direction d, LocalTime departure) {
        return TripImpl.create(line, d, departure);
    }
}
