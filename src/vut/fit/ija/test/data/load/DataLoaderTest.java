package vut.fit.ija.test.data.load;

import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import vut.fit.ija.main.data.load.DataLoader;
import vut.fit.ija.main.data.load.ProjectFiles;
import vut.fit.ija.main.model.map.Coordinate;
import vut.fit.ija.main.model.map.Line;
import vut.fit.ija.main.model.map.Stop;
import vut.fit.ija.main.model.map.Street;
import vut.fit.ija.main.model.schedule.*;
import vut.fit.ija.main.model.city.City;
import vut.fit.ija.main.model.vehicles.TransitType;

import java.util.AbstractMap;
import java.util.List;

/**
 * Test class testing DataLoader class
 */
public class DataLoaderTest {

    private static DataLoader dataLoader;
    private static List<City> cities;

    @BeforeClass
    public static void init() {
        dataLoader = new DataLoader(ProjectFiles.TEST_DATA_DIRECTORY);
        cities = dataLoader.loadData();
    }

    /**
     * Test loading streets
     */
    @Test
    public void streetsShouldBeCreatedInLoadData() {
        City testedCity = getCity("street_test", cities);
        assertNotNull(testedCity);
        List<Street> streets = testedCity.getStreets();
        assertNotNull(streets);
        assertEquals(4, streets.size());
    }

    /**
     * Test loading stops
     */
    @Test
    public void stopsShouldBeCreatedInLoadData() {
        City testedCity = getCity("stop_test", cities);
        assertNotNull(testedCity);
        List<Stop> stops = testedCity.getStops();
        assertNotNull(stops);
        assertEquals(4, stops.size());
        assertNotNull(stops.get(0).getStreet());
    }
//
//    /**
//     * Test correct assigning of Coordinates to streets
//     */
//    @Test
//    public void coordinatesShouldBeAssignedToStreetsInLoadData() {
//        City testedCity = getCity("coordinate_test", cities);
//        assertNotNull(testedCity);
//        List<Street> streets = testedCity.getStreets();
//        assertNotNull(streets);
//        assertEquals(1, streets.size());
//        Street s = streets.get(0);
//        List<Coordinate> coordinates = s.getCoordinates();
//        assertNotNull(coordinates);
//        assertEquals(4, coordinates.size());
//    }

    /**
     * Test loading lines
     */
    @Test
    public void linesShouldBeCreatedInLoadData() {
        City testedCity = getCity("line_test", cities);
        assertNotNull(testedCity);
        List<Line> lines = testedCity.getLines();
        assertNotNull(lines);
        assertEquals(3, lines.size());
        assertEquals(TransitType.TRAM, lines.get(0).getTransitType());
        assertEquals(TransitType.BUS, lines.get(2).getTransitType());
    }

//    /**
//     * Test correct assigning of stops and streets to lines
//     */


    /**
     * Private helper method for getting city from list by name
     * @param cityName input
     * @param cityList input
     * @return city or null
     */
    private City getCity(String cityName, List<City> cityList) {
        for (City c : cityList) {
            if (c.getId().equals(cityName)) {
                return c;
            }
        }
        return null;
    }

}
