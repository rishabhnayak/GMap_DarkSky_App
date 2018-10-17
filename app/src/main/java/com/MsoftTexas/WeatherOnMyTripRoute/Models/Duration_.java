
package com.MsoftTexas.WeatherOnMyTripRoute.Models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Duration_ {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("value")
    @Expose
    private Long value;

    /**
     * No args constructor for use in serialization
     * 
     */
    public Duration_() {
    }

    /**
     * 
     * @param text
     * @param value
     */
    public Duration_(String text, Long value) {
        super();
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

}
