package vut.fit.ija.main.model.map;

import vut.fit.ija.main.model.map.impl.LineImpl;
import vut.fit.ija.main.model.schedule.Direction;
import vut.fit.ija.main.model.schedule.LineIntervalScheme;
import vut.fit.ija.main.model.schedule.Trip;
import vut.fit.ija.main.model.vehicles.TransitType;

import java.util.AbstractMap;
import java.util.List;

/**
 * Represents line in public transportation
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 27-3-2020, xkarpi06
 * updated: 22-4-2020, xkarpi06 added getReverseRoute(), getTripsInDirection()
 */
public interface Line {

    /**
     * Getter
     * @return line id
     */
    String getId();

    /**
     * Getter
     * @return line transit type
     */
    TransitType getTransitType();

    /**
     * Gives immutable list of route elements consisting of Streets
     * with and without stops
     * @return immutable list
     */
    List<AbstractMap.SimpleImmutableEntry<Street,Stop>> getRoute();

    /**
     * Gives immutable list of route elements consisting of Streets
     * with and without stops in reverse order
     * @return immutable list
     */
    List<AbstractMap.SimpleImmutableEntry<Street,Stop>> getReverseRoute();

    /**
     * Getter
     * @return list of trips related to this line
     */
    List<Trip> getTrips();

    /**
     * Returns list of Trips that have specified direction
     * @param direction the direction
     * @return list of trips
     */
    List<Trip> getTripsInDirection(Direction direction);

    /**
     * Getter
     * @return line interval scheme
     */
    LineIntervalScheme getIntervalScheme();

    /**
     * Getter (reverse trip)
     * @return reversed line interval scheme
     */
    LineIntervalScheme getReverseIntervalScheme();

    /**
     * Insert Stop with Street
     * @param stop input
     * @return true if successful
     */
    boolean addStop(Stop stop);

    /**
     * Insert Street without Stop. Cannot be the first entry in route.
     * @param street input
     * @return true if successful
     */
    boolean addStreet(Street street);

    /**
     * Insert line interval scheme. Line must already have list of stops and scheme must correspond to amount of stops.
     * @param scheme input scheme
     * @return true if line has stops and scheme corresponds to the amount of stops, false otherwise
     */
    boolean setIntervalScheme(LineIntervalScheme scheme);

    /**
     * Add trip related to line
     * @param trip input
     * @return success of operation
     */
    boolean addTrip(Trip trip);

    /**
     * Gives list of stops in order based on line route
     * @return list of stops
     */
    List<Stop> getStops();

    /**
     * Tells if the street is part of the line
     * @param street the street
     * @return true if the street is part of line, false otherwise
     */
    boolean goesViaStreet(Street street);

    String toString2();

    /**
     * Default constructor
     * @param id input line id
     * @param t input
     * @return instance
     */
    static Line defaultInstance(String id, TransitType t) {
        return LineImpl.create(id,t);
    }
}
