File contains project documentation for IJA project in 2019/2020.

Name: Traffic Simulation

Authors: Šamánek Jan (xsaman02), Karpíšek Jakub (xkarpi06)

Usage:

from command line in folder with build.xml file type:
    ant compile
    ant run

Depenencies:
    java 1.8

Application description:

Application simulates public transportation in cities. The data is loaded from files in folder "data" and a map of
streets and stops is displayed for chosen city. Simulated data can only span over a period of time one day. It cannot
distinguish between different week days. Public transportation is represented by moving vehicles (circles) on the map.
The user can click on vehicles, stops or streets to display additional information. After clicking on a street user can
change street's traffic intensity, which will influence vehicles passing through that street by increasing/reducing their
delay. Vehicle can catch up with schedule again, if it passes through empty streets.
The simulation can be rewinded, accelerated/decelerated or paused.

Application flaws:

1) Application can not zoom or translate map.
2) Streets can not be closed and detours can not be defined.

Data modification:

The data is stored in "data" folder, where one folder needs to be created for each city. The city is named after the folder.
A city is represented by a set of 6 files, similar to the GTFS. Naming of the files and following formats of each file is mandatory.
The files and their formats are following:

street_coordinates.txt - defines shapes of streets
    format: streetId,coordinateX,coordinateY

stops.txt - defines stop position and street the stop belongs to
    format: stopId;coordinateX;coordinateY;streetId

line_components.txt - defines route of line in main direction by adding stops or empty streets without stops
    format: lineId;stopId/streetId;type     where type is 0 for stop, 1 for street

lines.txt - defines transit type of lines
    format: lineId,transitType      where transitType is 0 for subway, 1 for tram, 2 for bus

line_interval_schemes.txt - defines ordered arrival and departure times of line for whole route in main direction
    format: lineId,arrival,departure

trips.txt - defines trips on lines with unique departure time and direction
    format: lineId,direction,departure      where direction is 0 for main direction and 1 for return trip

