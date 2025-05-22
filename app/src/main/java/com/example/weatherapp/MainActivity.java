package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private SearchView searchViewCity;
    private RecyclerView recyclerViewCities;
    private TextView textViewNoResults;
    private ProgressBar progressBar;

    private CityAdapter cityAdapter;
    private List<City> allCities = new ArrayList<>();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        setupToolbar();
        initializeViews();
        setupRecyclerView();
        loadCitiesFromCSV();
        setupSearchView();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Weather App");
        }
    }

    private void initializeViews() {
        searchViewCity = findViewById(R.id.searchViewCity);
        recyclerViewCities = findViewById(R.id.recyclerViewCities);
        textViewNoResults = findViewById(R.id.textViewNoResults);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupRecyclerView() {
        recyclerViewCities.setLayoutManager(new LinearLayoutManager(this));
        cityAdapter = new CityAdapter(this, new ArrayList<>(), this::onCitySelected);
        recyclerViewCities.setAdapter(cityAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_favorites) {
            Intent intent = new Intent(this, FavoritesActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSearchView() {
        searchViewCity.setIconifiedByDefault(false);

        searchViewCity.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filterCities(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterCities(newText);
                return true;
            }
        });

        searchViewCity.requestFocus();
    }

    private void loadCitiesFromCSV() {
        showLoading(true);

        executorService.execute(() -> {
            List<City> cities = new ArrayList<>();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("worldcities.csv"))
            )) {
                String line = reader.readLine();

                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",");
                    if (tokens.length >= 11) {
                        String name = removeQuotes(tokens[0].trim());
                        String cityAscii = removeQuotes(tokens[1].trim());
                        Double lat = Double.parseDouble(removeQuotes(tokens[2].trim()));
                        Double lng = Double.parseDouble(removeQuotes(tokens[3].trim()));
                        String country = removeQuotes(tokens[4].trim());
                        String iso2 = removeQuotes(tokens[5].trim());
                        String iso3 = removeQuotes(tokens[6].trim());
                        String adminName = removeQuotes(tokens[7].trim());
                        String capital = removeQuotes(tokens[8].trim());

                        long population = 0;
                        String popStr = removeQuotes(tokens[9].trim());
                        if (popStr.matches("\\d+")) {
                            population = Long.parseLong(popStr);
                        }

                        long id = 0;
                        String idStr = removeQuotes(tokens[10].trim());
                        if (idStr.matches("\\d+")) {
                            id = Long.parseLong(idStr);
                        }

                        City city = new City(name, cityAscii, lat, lng, country, iso2, iso3, adminName, capital, population, id);
                        cities.add(city);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            mainThreadHandler.post(() -> {
                allCities.clear();
                allCities.addAll(cities);
                showLoading(false);
                if (searchViewCity.getQuery().toString().isEmpty()) {
                    cityAdapter.updateList(new ArrayList<>());
                } else {
                    filterCities(searchViewCity.getQuery().toString());
                }
            });
        });
    }

    private String removeQuotes(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        if (input.startsWith("\"") && input.endsWith("\"") && input.length() >= 2) {
            return input.substring(1, input.length() - 1);
        }

        return input;
    }

    private void filterCities(String query) {
        List<City> filteredCities = new ArrayList<>();

        if (query == null || query.isEmpty()) {
            cityAdapter.updateList(filteredCities);
            textViewNoResults.setVisibility(View.GONE);
            return;
        }

        String filterPattern = query.toLowerCase().trim();

        for (City city : allCities) {
            if (city.getName().toLowerCase().contains(filterPattern) ||
                    city.getCountry().toLowerCase().contains(filterPattern) ||
                    city.getCityAscii().toLowerCase().contains(filterPattern)) {
                filteredCities.add(city);
            }
        }

        cityAdapter.updateList(filteredCities);

        if (filteredCities.isEmpty()) {
            textViewNoResults.setVisibility(View.VISIBLE);
        } else {
            textViewNoResults.setVisibility(View.GONE);
        }
    }

    private void showLoading(boolean isLoading) {
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (isLoading) {
            textViewNoResults.setVisibility(View.GONE);
        }
    }

    private void onCitySelected(City city) {
        Intent intent = new Intent(this, CityWeatherActivity.class);

        intent.putExtra("CITY_NAME", city.getName());
        intent.putExtra("ADMIN_NAME", city.getAdminName());
        intent.putExtra("COUNTRY_NAME", city.getCountry());
        intent.putExtra("LATITUDE", city.getLat());
        intent.putExtra("LONGITUDE", city.getLng());

        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}