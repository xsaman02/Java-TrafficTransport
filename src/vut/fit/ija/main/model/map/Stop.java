package vut.fit.ija.main.model.map;

import vut.fit.ija.main.model.map.impl.StopImpl;

/**
 * Represents stop in public transportation
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 27-3-2020, xkarpi06
 * updated:
 */
public interface Stop {

    /**
     * Getter
     * @return stop id
     */
    String getId();

    /**
     * Getter
     * @return street it lays on
     */
    Street getStreet();

    /**
     * Getter
     * @return coordinates of the stop
     */
    Coordinate getCoordinate();

    /**
     * Setter
     * @param s input
     */
    void setStreet(Street s);

    /**
     * Default constructor
     * @param id input
     * @param c input
     * @return instance
     */
    static Stop defaultInstance(String id, Coordinate c) {
        return StopImpl.create(id,c);
    }
}
