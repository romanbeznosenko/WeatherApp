package com.example.weatherapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FavoritesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavorites;
    private TextView textViewNoFavorites;
    private CityAdapter favoritesAdapter;
    private FavoritesManager favoritesManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorites);

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        loadFavorites();
    }

    private void initializeViews() {
        recyclerViewFavorites = findViewById(R.id.recyclerViewFavorites);
        textViewNoFavorites = findViewById(R.id.textViewNoFavorites);
        favoritesManager = FavoritesManager.getInstance(this);
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Favorite Cities");
        }
    }

    private void setupRecyclerView() {
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(this));
        favoritesAdapter = new CityAdapter(this, favoritesManager.getFavoriteCities(), this::onCitySelected);
        recyclerViewFavorites.setAdapter(favoritesAdapter);
    }

    private void loadFavorites() {
        List<City> favorites = favoritesManager.getFavoriteCities();
        if (favorites.isEmpty()) {
            textViewNoFavorites.setVisibility(View.VISIBLE);
            recyclerViewFavorites.setVisibility(View.GONE);
        } else {
            textViewNoFavorites.setVisibility(View.GONE);
            recyclerViewFavorites.setVisibility(View.VISIBLE);
            favoritesAdapter.updateList(favorites);
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
    protected void onResume() {
        super.onResume();
        loadFavorites();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}