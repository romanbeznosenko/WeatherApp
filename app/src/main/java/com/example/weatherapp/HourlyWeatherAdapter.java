package com.example.weatherapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import java.util.Locale;
import com.example.weatherapp.weatherAPI.HourlyForecast;

public class HourlyWeatherAdapter extends RecyclerView.Adapter<HourlyWeatherAdapter.HourlyViewHolder> {

    private List<HourlyForecast> hourlyForecasts;

    public HourlyWeatherAdapter(List<HourlyForecast> hourlyForecasts) {
        this.hourlyForecasts = hourlyForecasts;
    }

    @NonNull
    @Override
    public HourlyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hourly_weather, parent, false);
        return new HourlyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HourlyViewHolder holder, int position) {
        HourlyForecast forecast = hourlyForecasts.get(position);

        holder.textViewTime.setText(forecast.time);
        holder.textViewTemperature.setText(String.format(Locale.getDefault(),
                "%.1f%s", forecast.temperature, forecast.temperatureUnit));
        holder.textViewWind.setText(String.format(Locale.getDefault(),
                "%.1f %s", forecast.windSpeed, forecast.windSpeedUnit));

        if (forecast.rain > 0) {
            holder.textViewRain.setText(String.format(Locale.getDefault(),
                    "%.1f %s", forecast.rain, forecast.rainUnit));
            holder.textViewRain.setVisibility(View.VISIBLE);
        } else {
            holder.textViewRain.setText("No rain");
            holder.textViewRain.setVisibility(View.VISIBLE);
        }

        double visibilityKm = forecast.visibility / 1000.0;
        holder.textViewVisibility.setText(String.format(Locale.getDefault(),
                "%.1f km", visibilityKm));
    }

    @Override
    public int getItemCount() {
        return hourlyForecasts != null ? hourlyForecasts.size() : 0;
    }

    public void updateData(List<HourlyForecast> newForecasts) {
        this.hourlyForecasts = newForecasts;
        notifyDataSetChanged();
    }

    static class HourlyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTime, textViewTemperature, textViewWind, textViewRain, textViewVisibility;

        public HourlyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewTime = itemView.findViewById(R.id.textViewTime);
            textViewTemperature = itemView.findViewById(R.id.textViewTemperature);
            textViewWind = itemView.findViewById(R.id.textViewWind);
            textViewRain = itemView.findViewById(R.id.textViewRain);
            textViewVisibility = itemView.findViewById(R.id.textViewVisibility);
        }
    }
}
