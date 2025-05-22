package com.example.weatherapp.weatherAPI;

public class LocationInfo {
    public double latitude;
    public double longitude;
    public String timezone;
    public double elevation;
    public String timezoneAbbreviation;

    public LocationInfo(double latitude, double longitude, String timezone, double elevation, String timezoneAbbreviation) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.timezone = timezone;
        this.elevation = elevation;
        this.timezoneAbbreviation = timezoneAbbreviation;
    }
}
