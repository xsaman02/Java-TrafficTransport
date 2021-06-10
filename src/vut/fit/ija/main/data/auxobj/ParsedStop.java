package vut.fit.ija.main.data.auxobj;

/**
 * Auxiliary class
 */
public class ParsedStop {
    public String stopId;
    public int x;
    public int y;
    public String streetId;

    public ParsedStop(String stopId, int x, int y, String streetId) {
        this.stopId = stopId;
        this.x = x;
        this.y = y;
        this.streetId = streetId;
    }

    @Override
    public String toString() {
        return "ParsedStop{" +
                "stopId='" + stopId + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", streetId='" + streetId + '\'' +
                '}';
    }
}
