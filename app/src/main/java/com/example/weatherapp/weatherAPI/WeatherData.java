package com.example.weatherapp.weatherAPI;

import java.util.ArrayList;
import java.util.List;

public class WeatherData {
    public LocationInfo location;
    public List<HourlyForecast> todayForecast;
    public List<DailyForecast> dailyForecast;

    public WeatherData() {
        this.todayForecast = new ArrayList<>();
        this.dailyForecast = new ArrayList<>();
    }
}
