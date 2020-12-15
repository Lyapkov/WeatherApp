package com.dlyapkov.myapplication;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.dlyapkov.myapplication.Entity.Weather;
import com.dlyapkov.myapplication.database.EducationSource;
import com.squareup.picasso.Picasso;

import java.util.List;

public class WeatherRecyclerAdapter extends RecyclerView.Adapter<WeatherRecyclerAdapter.ViewHolder> {

    private Activity activity;
    private EducationSource dataSource;
    //private long menuPosition;

    public WeatherRecyclerAdapter(EducationSource dataSource, Activity activity) {
        this.dataSource = dataSource;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        List<Weather> weathers = dataSource.getWeather();
        Weather weather = weathers.get(position);
        holder.city.setText(weather.city);
        holder.temperature.setText(Integer.toString((int) weather.temp - 273) + " \u00B0C");
        holder.pressure.setText(Integer.toString(weather.pressure) + " hpa");
        holder.humidity.setText(Integer.toString(weather.humidity) + " %");
        holder.temp_min.setText(Integer.toString((int) weather.temp_min - 273) + " \u00B0C");
        holder.temp_max.setText(Integer.toString((int) weather.temp_max - 273) + " \u00B0C");
        holder.description.setText(weather.description);
        //holder.icon.setText(weather.icon);

        Picasso.get()
                .load(weather.icon)
                .transform(new CircleTransformation())
                .into(holder.icon);


        if (activity != null)
            activity.registerForContextMenu(holder.cardView);
    }

    @Override
    public int getItemCount() {
        return (int) dataSource.getCountWeather();
    }

//    public long getMenuPosition() {
//        return menuPosition;
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
    TextView city;
    TextView temperature;
    TextView pressure;
    TextView humidity;
    TextView temp_min;
    TextView temp_max;
    TextView description;
    ImageView icon;

    View cardView;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = itemView;
        city = cardView.findViewById(R.id.text_city);
        temperature = cardView.findViewById(R.id.temperature_view);
        pressure = cardView.findViewById(R.id.pressure_view);
        humidity = cardView.findViewById(R.id.humidity_view);
        temp_min = cardView.findViewById(R.id.temp_min_view);
        temp_max = cardView.findViewById(R.id.temp_max_view);
        description = cardView.findViewById(R.id.description_view);
        icon = cardView.findViewById(R.id.imageView);
    }
}
}
