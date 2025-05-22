package com.example.weatherapp;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoritesManager {
    private static final String PREFS_NAME = "weather_favorites";
    private static final String FAVORITES_KEY = "favorite_cities";
    private static FavoritesManager instance;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private List<City> favoriteCities;

    private FavoritesManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
        loadFavorites();
    }

    public static synchronized FavoritesManager getInstance(Context context) {
        if (instance == null) {
            instance = new FavoritesManager(context.getApplicationContext());
        }
        return instance;
    }

    private void loadFavorites() {
        String json = sharedPreferences.getString(FAVORITES_KEY, "");
        if (!json.isEmpty()) {
            Type type = new TypeToken<List<City>>(){}.getType();
            favoriteCities = gson.fromJson(json, type);
        } else {
            favoriteCities = new ArrayList<>();
        }
    }

    private void saveFavorites() {
        String json = gson.toJson(favoriteCities);
        sharedPreferences.edit().putString(FAVORITES_KEY, json).apply();
    }

    public void addToFavorites(City city) {
        if (!isFavorite(city)) {
            favoriteCities.add(city);
            saveFavorites();
        }
    }

    public void removeFromFavorites(City city) {
        favoriteCities.removeIf(c -> c.getId().equals(city.getId()));
        saveFavorites();
    }

    public boolean isFavorite(City city) {
        return favoriteCities.stream().anyMatch(c -> c.getId().equals(city.getId()));
    }

    public List<City> getFavoriteCities() {
        return new ArrayList<>(favoriteCities);
    }

    public int getFavoritesCount() {
        return favoriteCities.size();
    }
}