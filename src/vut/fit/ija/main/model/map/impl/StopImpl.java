package vut.fit.ija.main.model.map.impl;

import vut.fit.ija.main.model.map.Coordinate;
import vut.fit.ija.main.model.map.Stop;
import vut.fit.ija.main.model.map.Street;

/**
 * Stop implementation
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 27-3-2020, xkarpi06
 * updated:
 */
public class StopImpl implements Stop {

    /** stop identifier */
    private String id;

    /** stop location */
    private Coordinate coordinate;

    /** street, where the stop is */
    private Street street;

    /**
     * Constructor
     * @param id input
     * @param coordinate input
     */
    private StopImpl(String id, Coordinate coordinate) {
        this.id = id;
        this.coordinate = coordinate;
    }

    /**
     * static Constructor
     * @param id input
     * @param coordinate input
     * @return instance
     */
    public static StopImpl create(String id, Coordinate coordinate) {
        if (coordinate != null) {
            return new StopImpl(id, coordinate);
        } else {
            return null;
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Street getStreet() {
        return street;
    }

    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public void setStreet(Street s) {
        this.street = s;
    }

    @Override
    public String toString() {
        return "stop(" + id + ")" + coordinate;
    }
}
