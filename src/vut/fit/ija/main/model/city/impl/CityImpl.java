package vut.fit.ija.main.model.city.impl;

import vut.fit.ija.main.model.city.City;
import vut.fit.ija.main.model.map.Line;
import vut.fit.ija.main.model.map.Stop;
import vut.fit.ija.main.model.map.Street;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * City implementation
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 21-3-2020, xkarpi06
 * updated: 29-3-2020, xkarpi06
 */
public class CityImpl implements City {

    /** city identifier, also name */
    private String id;

    /** streets in city */
    private List<Street> streets;

    /** stops in city */
    private List<Stop> stops;

    /** public transportation lines in city */
    private List<Line> lines;

    /**
     * Constructor
     * @param id input
     */
    private CityImpl(String id) {
        this.id = id;
    }

    /**
     * static Constructor
     * @param id input
     * @return instance
     */
    public static CityImpl create(String id) {
        return new CityImpl(id);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public List<Street> getStreets() {
        return Collections.unmodifiableList(streets);
    }

    @Override
    public List<Stop> getStops() {
        return Collections.unmodifiableList(stops);
    }

    @Override
    public List<Line> getLines() {
        return Collections.unmodifiableList(lines);
    }

    @Override
    public void setStreets(List<Street> streets) {
        this.streets = new ArrayList<>(streets);
    }

    @Override
    public void setStops(List<Stop> stops) {
        this.stops = new ArrayList<>(stops);
    }

    @Override
    public void setLines(List<Line> lines) {
        this.lines = new ArrayList<>(lines);
    }
}
