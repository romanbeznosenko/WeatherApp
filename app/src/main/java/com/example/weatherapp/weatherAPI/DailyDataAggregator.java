package com.example.weatherapp.weatherAPI;

public class DailyDataAggregator {
    private double minTemp = Double.MAX_VALUE;
    private double maxTemp = Double.MIN_VALUE;
    private boolean hasRain = false;
    private double totalWind = 0;
    private double totalVisibility = 0;
    private int count = 0;

    void addData(double temp, double rain, double wind, double visibility) {
        if (temp < minTemp) minTemp = temp;
        if (temp > maxTemp) maxTemp = temp;
        if (rain > 0) hasRain = true;

        totalWind += wind;
        totalVisibility += visibility;
        count++;
    }

    double getMinTemp() { return minTemp; }
    double getMaxTemp() { return maxTemp; }
    boolean hasRain() { return hasRain; }
    double getAvgWind() { return count > 0 ? totalWind / count : 0; }
    double getAvgVisibility() { return count > 0 ? totalVisibility / count : 0; }
}
