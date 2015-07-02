package com.aawhere.jts.place;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Point;

/**
 * Created by aroller on 7/2/15.
 */
public class PlaceUtil {


    public static Point[] points(Iterable<Place> places) {
        Iterable<Point> points = Iterables.transform(places, pointFunction());
        return Iterables.toArray(points, Point.class);
    }

    public static Function<Place, Point> pointFunction() {
        return new Function<Place, Point>() {
            @Override
            public Point apply(Place input) {
                return input.point();
            }
        };
    }

    /**
     * If the point was assigned to a Place this will provide the Place id or null otherwise.
     *
     * @param point
     * @return the Place id or null if the point wasn't associated with a place.
     */
    public static Long id(Point point) {
        return (Long) point.getUserData();
    }

}
