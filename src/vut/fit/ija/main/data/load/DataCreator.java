package vut.fit.ija.main.data.load;

import vut.fit.ija.main.model.city.City;
import vut.fit.ija.main.model.map.Coordinate;
import vut.fit.ija.main.model.map.Line;
import vut.fit.ija.main.model.map.Stop;
import vut.fit.ija.main.model.map.Street;
import vut.fit.ija.main.model.schedule.Direction;
import vut.fit.ija.main.model.schedule.LineIntervalScheme;
import vut.fit.ija.main.model.schedule.StopTime;
import vut.fit.ija.main.model.schedule.Trip;
import vut.fit.ija.main.model.vehicles.TransitType;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Auxiliary class for mocking data
 */
public class DataCreator {

    public List<City> create() {
        List<Street> streets = new ArrayList<>();
        List<Stop> stops = new ArrayList<>();
        Street sNew;

        sNew = Street.defaultInstance("Vejrostova", new ArrayList<Coordinate>() {
            {
                add(Coordinate.defaultInstance(10, 360));
                add(Coordinate.defaultInstance(30, 360));
                add(Coordinate.defaultInstance(30, 340));
                add(Coordinate.defaultInstance(60, 340));
                add(Coordinate.defaultInstance(60, 360));
            }
        });
        stops.add(Stop.defaultInstance("Bystrc,Ecerova", Coordinate.defaultInstance(30, 350)));
        stops.add(Stop.defaultInstance("Ondrouskova", Coordinate.defaultInstance(40, 340)));
        stops.add(Stop.defaultInstance("Kubickova", Coordinate.defaultInstance(60, 355)));
        sNew.addStop(stops.get(0));
        sNew.addStop(stops.get(1));
        sNew.addStop(stops.get(2));
        streets.add(sNew);

        System.out.println(sNew);

        sNew = Street.defaultInstance("Obvodova", new ArrayList<Coordinate>() {
            {
                add(Coordinate.defaultInstance(60,360));
                add(Coordinate.defaultInstance(100, 360));
            }
        });
        stops.add(Stop.defaultInstance("Pristaviste", Coordinate.defaultInstance(75, 360)));
        stops.add(Stop.defaultInstance("Zoologicka zahrada", Coordinate.defaultInstance(90, 360)));
        sNew.addStop(stops.get(3));
        sNew.addStop(stops.get(4));
        streets.add(sNew);

        sNew = Street.defaultInstance("Kninicska", new ArrayList<Coordinate>() {
            {
                add(Coordinate.defaultInstance(100, 360));
                add(Coordinate.defaultInstance(100, 340));
                add(Coordinate.defaultInstance(120, 340));
                add(Coordinate.defaultInstance(120, 320));
                add(Coordinate.defaultInstance(140, 320));
                add(Coordinate.defaultInstance(140, 300));
                add(Coordinate.defaultInstance(160, 300));
                add(Coordinate.defaultInstance(160, 280));
            }
        });
        stops.add(Stop.defaultInstance("Kamenolom", Coordinate.defaultInstance(110, 340)));
        stops.add(Stop.defaultInstance("Podlesi", Coordinate.defaultInstance(120, 335)));
        stops.add(Stop.defaultInstance("Branka", Coordinate.defaultInstance(125, 320)));
        stops.add(Stop.defaultInstance("Svratecka", Coordinate.defaultInstance(140, 315)));
        stops.add(Stop.defaultInstance("Vozovna komin", Coordinate.defaultInstance(152, 300)));
        stops.add(Stop.defaultInstance("Stranskeho", Coordinate.defaultInstance(160, 285)));
        sNew.addStop(stops.get(5));
        sNew.addStop(stops.get(6));
        sNew.addStop(stops.get(7));
        sNew.addStop(stops.get(8));
        sNew.addStop(stops.get(9));
        sNew.addStop(stops.get(10));

        Line line1 = Line.defaultInstance("1", TransitType.TRAM);
        for (Stop s : stops) {
            line1.addStop(s);
        }

        boolean success = true;
        success = line1.setIntervalScheme(LineIntervalScheme.create(
                LocalTime.parse("00:10:00"),
                new ArrayList<StopTime>() {
                    {
                        add(new StopTime(LocalTime.parse("00:10:40"),LocalTime.parse("00:11:00")));
                        add(new StopTime(LocalTime.parse("00:12:40"),LocalTime.parse("00:13:00")));
                        add(new StopTime(LocalTime.parse("00:13:40"),LocalTime.parse("00:14:00")));
                        add(new StopTime(LocalTime.parse("00:16:40"),LocalTime.parse("00:17:00")));
                        add(new StopTime(LocalTime.parse("00:17:40"),LocalTime.parse("00:18:00")));
                        add(new StopTime(LocalTime.parse("00:18:40"),LocalTime.parse("00:19:00")));
                        add(new StopTime(LocalTime.parse("00:21:40"),LocalTime.parse("00:22:00")));
                        add(new StopTime(LocalTime.parse("00:22:40"),LocalTime.parse("00:23:00")));
                        add(new StopTime(LocalTime.parse("00:23:40"),LocalTime.parse("00:24:00")));
                    }
                },
                LocalTime.parse("00:24:40")
        ));
        System.out.println(success);

        Trip t0 = Trip.defaultInstance(line1, Direction.WAY_THERE, LocalTime.parse("10:00"));
        Trip t1 = Trip.defaultInstance(line1, Direction.WAY_BACK, LocalTime.parse("10:00"));
        Trip t3 = Trip.defaultInstance(line1, Direction.WAY_THERE, LocalTime.parse("14:05"));

        System.out.println(line1);
        System.out.println(t3);

        return null;
    }

}
