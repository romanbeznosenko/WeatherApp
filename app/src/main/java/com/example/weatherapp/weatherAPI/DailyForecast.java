package com.example.weatherapp.weatherAPI;

public class DailyForecast {
    public String date;
    public double minTemperature;
    public double maxTemperature;
    public boolean hasRain;
    public double avgWindSpeed;
    public double avgVisibility;
    public String temperatureUnit;

    public DailyForecast(String date, double minTemperature, double maxTemperature, boolean hasRain, double avgWindSpeed, double avgVisibility, String temperatureUnit) {
        this.date = date;
        this.minTemperature = minTemperature;
        this.maxTemperature = maxTemperature;
        this.hasRain = hasRain;
        this.avgWindSpeed = avgWindSpeed;
        this.avgVisibility = avgVisibility;
        this.temperatureUnit = temperatureUnit;
    }
}
