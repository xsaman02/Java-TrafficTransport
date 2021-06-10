package vut.fit.ija.main.model.city;

import vut.fit.ija.main.model.city.impl.CityImpl;
import vut.fit.ija.main.model.map.Line;
import vut.fit.ija.main.model.map.Stop;
import vut.fit.ija.main.model.map.Street;
import vut.fit.ija.main.model.map.impl.LineImpl;

import java.util.List;

/**
 * Represents a city with its own map and traffic schedule
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 27-3-2020, xkarpi06
 * updated: 29-3-2020, xkarpi06
 */
public interface City {

    /**
     * Getter
     * @return city id
     */
    String getId();

    /**
     * Getter
     * @return immutable list
     */
    List<Street> getStreets();

    /**
     * Getter
     * @return immutable list
     */
    List<Stop> getStops();

    /**
     * Getter
     * @return immutable list
     */
    List<Line> getLines();

    /**
     * Setter
     * @param streets input
     */
    void setStreets(List<Street> streets);

    /**
     * Setter
     * @param stops input
     */
    void setStops(List<Stop> stops);

    /**
     * Setter
     * @param lines input
     */
    void setLines(List<Line> lines);

    /**
     * Default Constructor
     * @param id input
     * @return instance
     */
    static City defaultInstance(String id) {
        return CityImpl.create(id);
    }
}
