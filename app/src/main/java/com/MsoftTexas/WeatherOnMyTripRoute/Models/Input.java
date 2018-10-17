
package com.MsoftTexas.WeatherOnMyTripRoute.Models;

import java.io.Serializable;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Input implements Serializable
{

    @SerializedName("origin")
    @Expose
    private LatLng1 origin;
    @SerializedName("destination")
    @Expose
    private LatLng1 destination;
    @SerializedName("route")
    @Expose
    private Long route;
    @SerializedName("interval")
    @Expose
    private Long interval;
    @SerializedName("timeZone")
    @Expose
    private String timeZone;
    @SerializedName("time")
    @Expose
    private Long time;
    private final static long serialVersionUID = -784466641420971157L;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Input() {
    }

    /**
     * 
     * @param time
     * @param interval
     * @param route
     * @param origin
     * @param timeZone
     * @param destination
     */
    public Input(LatLng1 origin, LatLng1 destination, Long route, Long interval, String timeZone, Long time) {
        super();
        this.origin = origin;
        this.destination = destination;
        this.route = route;
        this.interval = interval;
        this.timeZone = timeZone;
        this.time = time;
    }

    public LatLng1 getOrigin() {
        return origin;
    }

    public void setOrigin(LatLng1 origin) {
        this.origin = origin;
    }

    public LatLng1 getDestination() {
        return destination;
    }

    public void setDestination(LatLng1 destination) {
        this.destination = destination;
    }

    public Long getRoute() {
        return route;
    }

    public void setRoute(Long route) {
        this.route = route;
    }

    public Long getInterval() {
        return interval;
    }

    public void setInterval(Long interval) {
        this.interval = interval;
    }

    public String getTimeZone() {
     return timeZone;
    }

    public void setTimeZone(String timeZone) {
        if(timeZone.contains("/")) {
            this.timeZone= timeZone.replace("/", ".");
        }else {
            this.timeZone= timeZone;
        }

    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

}
