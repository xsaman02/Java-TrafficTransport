package vut.fit.ija.main.model.map.impl;

import vut.fit.ija.main.model.map.Coordinate;

import java.util.Objects;

/**
 * Coordinate implementation
 * @author xkarpi06
 * @version 1.0
 * @since 1.0
 * created: 20-3-2020, xkarpi06
 * updated: 27-3-2020, xkarpi06
 */
public class CoordinateImpl implements Coordinate {

    /** value on x-axis */
    private int x;

    /** value on y-axis */
    private int y;

    /**
     * Constructor
     * @param x input
     * @param y input
     */
    private CoordinateImpl(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * static Constructor
     * @param x input
     * @param y input
     * @return instance
     */
    public static CoordinateImpl create(int x, int y) {
        return new CoordinateImpl(x,y);
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int diffX(Coordinate c) {
        return this.x - c.getX();
    }

    @Override
    public int diffY(Coordinate c) {
        return this.y - c.getY();
    }

    @Override
    public boolean between(Coordinate start, Coordinate finish) {
        if (start == null || finish == null) {
            return false;
        }
        int diffX = start.diffX(finish);
        int diffY = start.diffY(finish);

        if (diffX < 0) {
            return this.y == start.getY()
                    && this.x >= start.getX()
                    && this.x <= finish.getX();
        } else if (diffX == 0) {
            if (diffY < 0) {
                return this.x == start.getX()
                        && this.y >= start.getY()
                        && this.y <= finish.getY();
            } else if (diffY == 0) {
                // start equals finish
                return this.equals(start);
            } else { // diffY > 0
                return this.x == start.getX()
                        && this.y >= finish.getY()
                        && this.y <= start.getY();
            }
        } else { //diffX > 0
            return this.y == start.getY()
                    && this.x >= finish.getX()
                    && this.x <= start.getX();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CoordinateImpl)) return false;
        CoordinateImpl that = (CoordinateImpl) o;
        return x == that.x &&
                y == that.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "[" + x +"," + y + "]";
    }
}
