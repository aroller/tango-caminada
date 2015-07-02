package com.aawhere.jts.map;

import android.util.Log;

import com.aawhere.jts.place.Place;
import com.aawhere.jts.place.PlaceRepository;
import com.aawhere.jts.place.PlaceUtil;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.operation.distance.DistanceOp;
import com.vividsolutions.jts.operation.distance.GeometryLocation;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * A general purpose dynamic map that manages associated features.  This service only works with a
 * single coordinate system which should is identified by map id.
 * <p/>
 * <p/>
 * Created by aroller on 7/2/15.
 */
public class MapService {

    private static final String TAG = MapService.class.getSimpleName();
    private GeometryFactory factory = new GeometryFactory();
    private PlaceRepository placeRepository;
    private String mapId;

    public MapService(String mapId, File filesDir){
        this.mapId = mapId;
        this.placeRepository = new PlaceRepository(this.mapId,filesDir);
    }

    /**Provides the nearest place to the coordinate given.
     *
     * @param coordinate
     * @return the nearest place or null if no places are defined
     */
    public Place nearestPlace(Coordinate coordinate) {
        if(placeRepository.all().isEmpty()){
            return null;
        }

        MultiPoint multiPoint = factory.createMultiPoint(PlaceUtil.points(this.placeRepository.all()));
        Point target = factory.createPoint(coordinate);
        //finds the nearest point
        DistanceOp distanceOp = new DistanceOp(target,multiPoint);
        GeometryLocation[] nearestLocations = distanceOp.nearestLocations();
        //the location array corresponds with the inputs
        int placeIndex = 1;
        final GeometryLocation nearestLocation = nearestLocations[placeIndex];
        Point nearestPoint = (Point) nearestLocation.getGeometryComponent();
        Long idOfNearestPlace = PlaceUtil.id(nearestPoint);
        return placeRepository.place(idOfNearestPlace);
    }

    /**
     * creates a place and associates it with the map.
     */
    public Place place(String name, Coordinate coordinate) {
        Point point = factory.createPoint(coordinate);
        Place place = Place.builder().name(name).point(point).build();
            placeRepository.add(place);
        return place;
    }
}
