package com.aawhere.jts.map.place;

import com.vividsolutions.jts.geom.Point;

import java.io.Serializable;

/**
 * A point that describes an area or a spot within another area. The coordinate system used is
 * defined by the Point's SRID.
 * <p/>
 * The coordinates are relative to the origin of the ADF that was localized when this destination was
 * created.
 * <p/>
 * Created by aroller on 7/2/15.
 */
public class Place implements Serializable {

    /**
     * Uniquely identifies this destination within the scope of the map or ADF for which this destination
     * exists.
     */
    private Long id;

    /**
     * The most common word (or few words) that describes this destination.
     */
    private String name;
    /**
     * The significant point for a destination.  Some places may be well represented by only a single
     * coordinate while most will have a 2D or 3D geometry associated to it.  This represents the
     * most significant point which may be the center of the destination, but more importantly represents
     * the most significant part of the destination (i.e. town square for a town).
     */
    private Point point;

    public static class PlaceBuilder {
        private Place place;

        private PlaceBuilder() {
            this.place = new Place();
        }

        public PlaceBuilder name(String name) {
            place.name = name;
            return this;
        }

        public PlaceBuilder point(Point point) {
            place.point = point;
            return this;
        }

        public Place build() {
            place.id = System.currentTimeMillis();
            place.point.setUserData(place.id);

            //FIXME:destination must be nulled.  use builder
            return this.place;
        }
    }

    public static PlaceBuilder builder() {
        return new PlaceBuilder();
    }

    public Long id() {
        return id;
    }

    public Point point() {
        return point;
    }

    public String name() {
        return name;
    }

}
