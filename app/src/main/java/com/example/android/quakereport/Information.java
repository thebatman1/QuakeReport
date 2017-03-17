package com.example.android.quakereport;

/**
 * Created by HP on 2/26/2017.
 */

public class Information {

    private String location , timestamp , magnitude , url;

    public Information(String magnitude , String location , String timestamp , String url) {
        this.setMagnitude(magnitude);
        this.setLocation(location);
        this.setTimestamp(timestamp);
        this.setUrl(url);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(String magnitude) {
        this.magnitude = magnitude;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
