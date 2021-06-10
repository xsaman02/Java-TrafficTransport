package vut.fit.ija.main.model.map;

import vut.fit.ija.main.model.map.impl.StreetImpl;
import vut.fit.ija.main.model.vehicles.TrafficLevel;

import java.util.List;

/**
 * Represents section in map without any cross-sections.
 * Consists of Coordinated, street must be perpendicular.
 * Supported traffic defines visual representation of segment in map.
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 27-3-2020, xkarpi06
 * updated:
 */
public interface Street {

    /**
     * Getter
     * @return street id
     */
    String getId();

    /**
     * Getter
     * @return street's traffic level
     */
    TrafficLevel getTrafficLevel();

    /**
     * Getter
     * @return immutable list of coordinates
     */
    List<Coordinate> getCoordinates();

    /**
     * Getter
     * @return immutable list of stops
     */
    List<Stop> getStops();

    /**
     * Setter
     * @param t new traffic level
     */
    void setTrafficLevel(TrafficLevel t);

    /**
     * @return first Coordinate of the Street
     */
    Coordinate begin();

    /**
     * @return last Coordinate of the Street
     */
    Coordinate end();

    /**
     * Tests if Street links with another Street
     * @param s second Street
     * @return true if either of the ends match
     */
    boolean follows(Street s);

    /**
     * Adds stop to Street, but only if the Stop is positioned on the street.
     * @param s input stop
     * @return true if the operation was successful
     */
    boolean addStop(Stop s);

    /**
     * Defualt constructor
     * @param id input
     * @param coordinates input
     * @return instance
     */
    static Street defaultInstance(String id, List<Coordinate> coordinates) {
        return StreetImpl.create(id, coordinates);
    }
}
