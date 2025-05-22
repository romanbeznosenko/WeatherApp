package com.example.weatherapp.weatherAPI;

public class HourlyForecast {
    public String time;
    public double temperature;
    public double rain;
    public double windSpeed;
    public double visibility;
    public String temperatureUnit;
    public String rainUnit;
    public String windSpeedUnit;
    public String visibilityUnit;

    public HourlyForecast(String time, double temperature, double rain, double windSpeed, double visibility, String temperatureUnit, String rainUnit, String windSpeedUnit, String visibilityUnit) {
        this.time = time;
        this.temperature = temperature;
        this.rain = rain;
        this.windSpeed = windSpeed;
        this.visibility = visibility;
        this.temperatureUnit = temperatureUnit;
        this.rainUnit = rainUnit;
        this.windSpeedUnit = windSpeedUnit;
        this.visibilityUnit = visibilityUnit;
    }
}
