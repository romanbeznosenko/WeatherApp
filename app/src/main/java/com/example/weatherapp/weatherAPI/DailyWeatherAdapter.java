package com.example.weatherapp.weatherAPI;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import com.example.weatherapp.R;

public class DailyWeatherAdapter extends RecyclerView.Adapter<DailyWeatherAdapter.DailyViewHolder> {

    private List<DailyForecast> dailyForecasts;
    private SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());
    private SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.getDefault());

    public DailyWeatherAdapter(List<DailyForecast> dailyForecasts) {
        this.dailyForecasts = dailyForecasts;
    }

    @NonNull
    @Override
    public DailyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_daily_weather, parent, false);
        return new DailyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyViewHolder holder, int position) {
        DailyForecast forecast = dailyForecasts.get(position);

        try {
            Date date = inputFormat.parse(forecast.date);
            if (date != null) {
                holder.textViewDate.setText(outputFormat.format(date));
                holder.textViewDayName.setText(dayFormat.format(date));
            } else {
                holder.textViewDate.setText(forecast.date);
                holder.textViewDayName.setText("");
            }
        } catch (ParseException e) {
            holder.textViewDate.setText(forecast.date);
            holder.textViewDayName.setText("");
        }

        holder.textViewTempRange.setText(String.format(Locale.getDefault(),
                "%.1f° / %.1f%s", forecast.minTemperature, forecast.maxTemperature, forecast.temperatureUnit));

        if (forecast.hasRain) {
            holder.textViewRainStatus.setText("Rain expected");
            holder.textViewRainStatus.setVisibility(View.VISIBLE);
        } else {
            holder.textViewRainStatus.setText("No rain");
            holder.textViewRainStatus.setVisibility(View.VISIBLE);
        }

        holder.textViewAvgWind.setText(String.format(Locale.getDefault(),
                "%.1f km/h", forecast.avgWindSpeed));

        double visibilityKm = forecast.avgVisibility / 1000.0;
        holder.textViewAvgVisibility.setText(String.format(Locale.getDefault(),
                "%.1f km", visibilityKm));
    }

    @Override
    public int getItemCount() {
        return dailyForecasts != null ? dailyForecasts.size() : 0;
    }

    public void updateData(List<DailyForecast> newForecasts) {
        this.dailyForecasts = newForecasts;
        notifyDataSetChanged();
    }

    static class DailyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDate, textViewDayName, textViewTempRange, textViewRainStatus,
                textViewAvgWind, textViewAvgVisibility;

        public DailyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.textViewDate);
            textViewDayName = itemView.findViewById(R.id.textViewDayName);
            textViewTempRange = itemView.findViewById(R.id.textViewTempRange);
            textViewRainStatus = itemView.findViewById(R.id.textViewRainStatus);
            textViewAvgWind = itemView.findViewById(R.id.textViewAvgWind);
            textViewAvgVisibility = itemView.findViewById(R.id.textViewAvgVisibility);
        }
    }
}

