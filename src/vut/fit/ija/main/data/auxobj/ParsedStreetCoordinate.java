package vut.fit.ija.main.data.auxobj;

/**
 * Auxiliary class
 */
public class ParsedStreetCoordinate {
    public String streetId;
    public int x;
    public int y;

    public ParsedStreetCoordinate(String streetId, int x, int y) {
        this.streetId = streetId;
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "ParsedStreetCoordinate{" +
                "streetId='" + streetId + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
