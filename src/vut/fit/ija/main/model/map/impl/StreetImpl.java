package vut.fit.ija.main.model.map.impl;

import vut.fit.ija.main.Log;
import vut.fit.ija.main.model.map.Coordinate;
import vut.fit.ija.main.model.map.Stop;
import vut.fit.ija.main.model.map.Street;
import vut.fit.ija.main.model.vehicles.TrafficLevel;

import java.util.*;
import java.util.logging.Level;

/**
 * Street implementation
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 22-3-2020, xkarpi06
 * updated: 27-3-2020, xkarpi06
 */
public class StreetImpl implements Street {

    /** Street identifier */
    private String id;

    /** traffic intensity for this Street only, influences vehicle speed, on scale from ZERO to FATAL */
    private TrafficLevel trafficLevel;

    /** Ordered list of coordinates that create the street*/
    private List<Coordinate> coordinates;

    /** List of stops that are on the street */
    private List<Stop> stops;

    /**
     * Constructor
     */
    private StreetImpl(String id, List<Coordinate> coordinates) {
        this.id = id;
        this.trafficLevel = TrafficLevel.ZERO;
        this.coordinates = coordinates;
        this.stops = new ArrayList<>();
    }

    /**
     * static Constructor
     * @param id input
     * @param coordinates input
     * @return StreetImpl if coordinates meet condition for object, null otherwise
     */
    public static StreetImpl create(String id, List<Coordinate> coordinates) {
        if (perpendicular(coordinates)) {
            return new StreetImpl(id, coordinates);
        } else {
            return null;
        }
    }

    /**
     * tests perpendicularity of each of two following coordinates
     * @param coordinates input
     * @return true if success, false otherwise
     */
    private static boolean perpendicular(List<Coordinate> coordinates) {
        if (coordinates.size() < 2) {
            return false;
        }
        Coordinate tmp = coordinates.get(0);
        for (int i = 1; i < coordinates.size(); i++) {
            if (coordinates.get(i).equals(tmp)) {
                return false;
            } else if (coordinates.get(i).diffX(tmp) != 0 && coordinates.get(i).diffY(tmp) != 0) {
                return false;
            } else {
                tmp = coordinates.get(i);
            }
        }
        return true;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public TrafficLevel getTrafficLevel() {
        return trafficLevel;
    }

    @Override
    public List<Coordinate> getCoordinates() {
        return Collections.unmodifiableList(coordinates);
    }

    @Override
    public List<Stop> getStops() {
        return Collections.unmodifiableList(stops);
    }

    @Override
    public void setTrafficLevel(TrafficLevel t) {
        this.trafficLevel = t;
        Log.LOGGER.log(Level.FINE, String.format("Traffic level set to %s on '%s'\n", t, id));
    }

    @Override
    public Coordinate begin() {
        return coordinates.get(0);
    }

    @Override
    public Coordinate end() {
        return coordinates.get(coordinates.size()-1);
    }

    @Override
    public boolean follows(Street that) {
        return that != null
                && (this.begin().equals(that.begin())
                || this.begin().equals(that.end())
                || this.end().equals(that.begin())
                || this.end().equals(that.end()));
    }

    @Override
    public boolean addStop(Stop s) {
        if (liesOnStreet(s)) {
            stops.add(s);
            s.setStreet(this);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Finds if stop lies on street
     * @param stop input
     * @return true if stop lies on street, false otherwise
     */
    private boolean liesOnStreet(Stop stop) {
        if (stop == null || stop.getCoordinate() == null) {
            return false;
        } else {
            for (int i = 1; i < coordinates.size(); i++) {
                Coordinate start = coordinates.get(i-1);
                Coordinate finish = coordinates.get(i);
                if (stop.getCoordinate().between(start,finish)) {
                    return true;
                }
            }
            return false;
        }
    }

    @Override
    public String toString() {
        String s =  "Street '" + id + "', traffic(" + trafficLevel.getValue() + "): ";
        for (Coordinate c : coordinates) {
            s += c;
        }
        s += "\nStops: ";
        for (Stop st : stops) {
            s += String.format("%s%s ", st.getId(), st.getCoordinate());
        }
        s += "\n";
        return s;
    }
}
