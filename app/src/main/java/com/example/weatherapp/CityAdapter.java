package com.example.weatherapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityViewHolder> {

    private Context context;
    private List<City> cities;
    private CityClickListener clickListener;

    public interface CityClickListener {
        void onCityClick(City city);
    }

    public CityAdapter(Context context, List<City> cities, CityClickListener clickListener) {
        this.context = context;
        this.cities = new ArrayList<>(cities);
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public CityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_city, parent, false);
        return new CityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CityViewHolder holder, int position) {
        City city = cities.get(position);
        holder.textViewCityName.setText(city.getName());
        holder.textViewCountryName.setText(city.getCountry());

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onCityClick(city);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public void updateList(List<City> newList) {
        cities.clear();
        cities.addAll(newList);
        notifyDataSetChanged();
    }

    public static class CityViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewCityName;
        public TextView textViewCountryName;

        public CityViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewCityName = itemView.findViewById(R.id.textViewCityName);
            textViewCountryName = itemView.findViewById(R.id.textViewCountryName);
        }
    }
}