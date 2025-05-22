package com.example.weatherapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherapp.weatherAPI.DailyWeatherAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.example.weatherapp.weatherAPI.*;

public class CityWeatherActivity extends AppCompatActivity {

    private TextView textViewCityName, textViewAdminName, textViewCountry,
            textViewCoordinates, textViewTimezone, textViewError;
    private RecyclerView recyclerViewHourly, recyclerViewDaily;
    private ProgressBar progressBar;

    private HourlyWeatherAdapter hourlyAdapter;
    private DailyWeatherAdapter dailyAdapter;

    private String cityName, adminName, countryName;
    private double latitude, longitude;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_weather);

        initializeViews();
        extractIntentData();
        setupRecyclerViews();
        displayCityInfo();
        fetchWeatherData();
    }

    private void initializeViews() {
        textViewCityName = findViewById(R.id.textViewCityName);
        textViewAdminName = findViewById(R.id.textViewAdminName);
        textViewCountry = findViewById(R.id.textViewCountry);
        textViewCoordinates = findViewById(R.id.textViewCoordinates);
        textViewTimezone = findViewById(R.id.textViewTimezone);
        textViewError = findViewById(R.id.textViewError);
        recyclerViewHourly = findViewById(R.id.recyclerViewHourly);
        recyclerViewDaily = findViewById(R.id.recyclerViewDaily);
        progressBar = findViewById(R.id.progressBar);
    }

    private void extractIntentData() {
        cityName = getIntent().getStringExtra("CITY_NAME");
        adminName = getIntent().getStringExtra("ADMIN_NAME");
        countryName = getIntent().getStringExtra("COUNTRY_NAME");
        latitude = getIntent().getDoubleExtra("LATITUDE", 0.0);
        longitude = getIntent().getDoubleExtra("LONGITUDE", 0.0);
    }

    private void setupRecyclerViews() {
        LinearLayoutManager hourlyLayoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.HORIZONTAL, false);
        recyclerViewHourly.setLayoutManager(hourlyLayoutManager);
        hourlyAdapter = new HourlyWeatherAdapter(new ArrayList<>());
        recyclerViewHourly.setAdapter(hourlyAdapter);

        LinearLayoutManager dailyLayoutManager = new LinearLayoutManager(this);
        recyclerViewDaily.setLayoutManager(dailyLayoutManager);
        dailyAdapter = new DailyWeatherAdapter(new ArrayList<>());
        recyclerViewDaily.setAdapter(dailyAdapter);
    }

    private void displayCityInfo() {
        textViewCityName.setText(cityName != null ? cityName : "Unknown City");
        textViewAdminName.setText(adminName != null ? adminName : "");
        textViewCountry.setText(countryName != null ? countryName : "Unknown Country");
        textViewCoordinates.setText(String.format("%.4f, %.4f", latitude, longitude));
    }

    private void fetchWeatherData() {
        showLoading(true);

        executorService.execute(() -> {
            try {
                WeatherData weatherData = WeatherApiClient.getWeatherData(latitude, longitude);

                runOnUiThread(() -> {
                    displayWeatherData(weatherData);
                    showLoading(false);
                });

            } catch (IOException e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showError("Network error: " + e.getMessage());
                });
            } catch (Exception e) {
                runOnUiThread(() -> {
                    showLoading(false);
                    showError("Unexpected error: " + e.getMessage());
                });
            }
        });
    }

    private void displayWeatherData(WeatherData weatherData) {
        if (weatherData.location != null) {
            textViewTimezone.setText("Timezone: " + weatherData.location.timezone);
        }

        if (weatherData.todayForecast != null && !weatherData.todayForecast.isEmpty()) {
            hourlyAdapter.updateData(weatherData.todayForecast);
        }

        if (weatherData.dailyForecast != null && !weatherData.dailyForecast.isEmpty()) {
            dailyAdapter.updateData(weatherData.dailyForecast);
        }

        hideError();
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerViewHourly.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        recyclerViewDaily.setVisibility(isLoading ? View.GONE : View.VISIBLE);
    }

    private void showError(String message) {
        textViewError.setText("Error: " + message);
        textViewError.setVisibility(View.VISIBLE);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void hideError() {
        textViewError.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}