package com.aawhere.jts.map;

import com.aawhere.jts.place.Place;
import com.aawhere.jts.place.PlaceUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import com.vividsolutions.jts.operation.distance.GeometryLocation;

import java.util.HashMap;

/**
 * A general purpose dynamic map that manages associated features.  This service only works with a
 * single coordinate system.
 * <p/>
 * <p/>
 * Created by aroller on 7/2/15.
 */
public class MapService {

    private GeometryFactory factory = new GeometryFactory();
    private HashMap<Long, Place> places = new HashMap<>();


    /**Provides the nearest place to the coordinate given.
     *
     * @param coordinate
     * @return the nearest place or null if no places are defined
     */
    public Place nearestPlace(Coordinate coordinate) {
        if(places.isEmpty()){
            return null;
        }

        MultiPoint multiPoint = factory.createMultiPoint(PlaceUtil.points(this.places.values()));
        Point target = factory.createPoint(coordinate);
        //finds the nearest point
        DistanceOp distanceOp = new DistanceOp(target,multiPoint);
        GeometryLocation[] nearestLocations = distanceOp.nearestLocations();
        //the location array corresponds with the inputs
        int placeIndex = 1;
        final GeometryLocation nearestLocation = nearestLocations[placeIndex];
        Point nearestPoint = (Point) nearestLocation.getGeometryComponent();
        Long idOfNearestPlace = PlaceUtil.id(nearestPoint);
        return places.get(idOfNearestPlace);
    }

    /**
     * creates a place and associates it with the map.
     */
    public Place place(String name, Coordinate coordinate) {
        Point point = factory.createPoint(coordinate);
        Place place = Place.builder().name(name).point(point).build();
        places.put(place.id(), place);
        return place;
    }
}
