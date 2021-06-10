package vut.fit.ija.main.model.map;

import vut.fit.ija.main.model.map.impl.CoordinateImpl;

/**
 * Represents one point in map
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 27-3-2020, xkarpi06
 * updated:
 */
public interface Coordinate {

    /**
     * Getter
     * @return x coordinate
     */
    int getX();

    /**
     * Getter
     * @return y coordinate
     */
    int getY();

    /**
     * Difference in x coordinates
     * @param c second coordinate
     * @return value
     */
    int diffX(Coordinate c);

    /**
     * Difference in y coordinates
     * @param c second coordinate
     * @return value
     */
    int diffY(Coordinate c);

    /**
     * Tells if coordinate lies between two other coordinates
     * (horizontal or vertical vectors only)
     * @param start input
     * @param finish input
     * @return true if lies between points or is equal to end point, false otherwise
     */
    boolean between(Coordinate start, Coordinate finish);

    /**
     * Default constructor
     * @param x input
     * @param y input
     * @return instance
     */
    static Coordinate defaultInstance(int x, int y) {
        return CoordinateImpl.create(x,y);
    }
}
