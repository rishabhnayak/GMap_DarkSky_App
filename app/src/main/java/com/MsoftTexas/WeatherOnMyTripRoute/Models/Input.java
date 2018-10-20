
package com.MsoftTexas.WeatherOnMyTripRoute.Models;


import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Input {

    @SerializedName("origin")
    @Expose
    private LatLng origin;
    @SerializedName("destination")
    @Expose
    private LatLng destination;
    @SerializedName("route")
    @Expose
    private Integer route;
    @SerializedName("interval")
    @Expose
    private Long interval;
    @SerializedName("timeZone")
    @Expose
    private String timeZone;
    @SerializedName("time")
    @Expose
    private Long time;
    @SerializedName("travelmode")
    @Expose
    private Integer travelmode;
    @SerializedName("restrictions")
    @Expose
    private String restrictions;

    public LatLng getOrigin() {
        return origin;
    }

    public void setOrigin(LatLng origin) {
        this.origin = origin;
    }

    public LatLng getDestination() {
        return destination;
    }

    public void setDestination(LatLng destination) {
        this.destination = destination;
    }

    public Integer getRoute() {
        return route;
    }

    public void setRoute(Integer route) {
        this.route = route;
    }

    public Long getInterval() {
        return interval;
    }

    public void setInterval(long interval2) {
        this.interval = interval2;
    }

    public String getTimeZone() {

    	
    		return timeZone;
    }

    public void setTimeZone(String timeZone) {
        if(timeZone.contains("/"))
            this.timeZone =timeZone.replace("/", ".");

    }

    public Long getTime() {
        return time;
    }

    public void setTime(long starttime) {
        this.time = starttime;
    }

    public Integer getTravelmode() {
        return travelmode;
    }

    public void setTravelmode(Integer travelmode) {
        this.travelmode = travelmode;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }

}
