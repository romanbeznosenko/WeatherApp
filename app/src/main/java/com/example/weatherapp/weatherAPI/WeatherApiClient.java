package com.example.weatherapp.weatherAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;

public class WeatherApiClient {
    private static final String BASE_URL = "https://api.open-meteo.com/v1/forecast";
    private static final String DEFAULT_PARAMS = "&hourly=temperature_2m,rain,wind_speed_10m,visibility";

    public static WeatherData getWeatherData(double latitude, double longitude) throws Exception {
        String jsonData = fetchWeatherJson(latitude, longitude);
        return parseWeatherData(jsonData);
    }

    private static String fetchWeatherJson(double latitude, double longitude)
            throws IOException {
        String apiUrl = BASE_URL + "?latitude=" + latitude + "&longitude=" + longitude + DEFAULT_PARAMS;

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(10000);

        try {
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("HTTP error code: " + responseCode);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } finally {
            connection.disconnect();
        }
    }

    private static WeatherData parseWeatherData(String jsonData) throws Exception {
        try {
            JSONObject weatherJson = new JSONObject(jsonData);
            WeatherData weatherData = new WeatherData();

            weatherData.location = new LocationInfo(
                    weatherJson.getDouble("latitude"),
                    weatherJson.getDouble("longitude"),
                    weatherJson.getString("timezone"),
                    weatherJson.getDouble("elevation"),
                    weatherJson.getString("timezone_abbreviation")
            );

            JSONObject hourlyData = weatherJson.getJSONObject("hourly");
            JSONArray timeArray = hourlyData.getJSONArray("time");
            JSONArray temperatureArray = hourlyData.getJSONArray("temperature_2m");
            JSONArray rainArray = hourlyData.getJSONArray("rain");
            JSONArray windArray = hourlyData.getJSONArray("wind_speed_10m");
            JSONArray visibilityArray = hourlyData.getJSONArray("visibility");

            JSONObject hourlyUnits = weatherJson.getJSONObject("hourly_units");
            String tempUnit = hourlyUnits.getString("temperature_2m");
            String rainUnit = hourlyUnits.getString("rain");
            String windUnit = hourlyUnits.getString("wind_speed_10m");
            String visUnit = hourlyUnits.getString("visibility");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String today = dateFormat.format(new Date());

            SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault());
            SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

            Map<String, DailyDataAggregator> dailyMap = new HashMap<>();
            for (int i = 0; i < timeArray.length(); i++) {
                String timeStr = timeArray.getString(i);
                String date = timeStr.substring(0, 10);

                double temp = temperatureArray.getDouble(i);
                double rain = rainArray.getDouble(i);
                double wind = windArray.getDouble(i);
                double visibility = visibilityArray.getDouble(i);

                if (date.equals(today)) {
                    Date timeDate = apiFormat.parse(timeStr);
                    String displayTime = displayFormat.format(timeDate);

                    weatherData.todayForecast.add(new HourlyForecast(displayTime, temp, rain, wind, visibility, tempUnit, rainUnit, windUnit, visUnit));
                } else {
                    DailyDataAggregator daily = dailyMap.getOrDefault(date, new DailyDataAggregator());
                    daily.addData(temp, rain, wind, visibility);
                    dailyMap.put(date, daily);
                }
            }
            for (Map.Entry<String, DailyDataAggregator> entry : dailyMap.entrySet()) {
                DailyDataAggregator daily = entry.getValue();
                weatherData.dailyForecast.add(new DailyForecast(entry.getKey(), daily.getMinTemp(), daily.getMaxTemp(), daily.hasRain(), daily.getAvgWind(), daily.getAvgVisibility(), tempUnit));

            }
            return weatherData;
        } catch (Exception e) {
            throw new Exception("Failed to parse weather data:" + e);
        }

    }
}
