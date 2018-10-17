package com.MsoftTexas.WeatherOnMyTripRoute.Models;


import java.io.Serializable;

/** A place on Earth, represented by a latitude/longitude pair. */
public class LatLng1 implements  Serializable {

    private static final long serialVersionUID = 1L;

    /** The latitude of this location. */
    public double latitude;

    /** The longitude of this location. */
    public double longitude;

    /**
     * Constructs a location with a latitude/longitude pair.
     *
     * @param latitude The latitude of this location.
     * @param longitude The longitude of this location.
     */
    public LatLng1(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /** Serialisation constructor. */
    public LatLng1() {}



}