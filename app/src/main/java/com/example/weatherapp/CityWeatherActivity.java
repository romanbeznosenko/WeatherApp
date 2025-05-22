package com.example.weatherapp;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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
    private ImageButton buttonFavorite;

    private HourlyWeatherAdapter hourlyAdapter;
    private DailyWeatherAdapter dailyAdapter;
    private FavoritesManager favoritesManager;

    private String cityName, adminName, countryName;
    private double latitude, longitude;
    private City currentCity;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_weather);

        initializeViews();
        extractIntentData();
        setupRecyclerViews();
        setupFavoriteButton();
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
        buttonFavorite = findViewById(R.id.buttonFavorite);

        favoritesManager = FavoritesManager.getInstance(this);
    }

    private void extractIntentData() {
        cityName = getIntent().getStringExtra("CITY_NAME");
        adminName = getIntent().getStringExtra("ADMIN_NAME");
        countryName = getIntent().getStringExtra("COUNTRY_NAME");
        latitude = getIntent().getDoubleExtra("LATITUDE", 0.0);
        longitude = getIntent().getDoubleExtra("LONGITUDE", 0.0);

        currentCity = new City();
        currentCity.setName(cityName);
        currentCity.setAdminName(adminName);
        currentCity.setCountry(countryName);
        currentCity.setLat(latitude);
        currentCity.setLng(longitude);
        currentCity.setId((long) (cityName + latitude + longitude).hashCode());
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

    private void setupFavoriteButton() {
        updateFavoriteIcon();

        buttonFavorite.setOnClickListener(v -> {
            if (favoritesManager.isFavorite(currentCity)) {
                favoritesManager.removeFromFavorites(currentCity);
                Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
            } else {
                favoritesManager.addToFavorites(currentCity);
                Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
            }
            updateFavoriteIcon();
        });
    }

    private void updateFavoriteIcon() {
        if (favoritesManager.isFavorite(currentCity)) {
            buttonFavorite.setImageResource(R.drawable.ic_star_filled);
        } else {
            buttonFavorite.setImageResource(R.drawable.ic_star_empty);
        }
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