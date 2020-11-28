package com.dlyapkov.myapplication;

import com.dlyapkov.myapplication.Entity.Weather;
import com.dlyapkov.myapplication.database.EducationSource;
import com.dlyapkov.myapplication.model.WeatherRequest;
import com.dlyapkov.myapplication.interfaces.OpenWeather;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Http {
    private static OpenWeather openWeather;
    private static MainActivity activity;

    private static final String TAG = "WEATHER";
    private static final String BASE_URL = "https://api.openweathermap.org/";

    public static void initRetrofit(MainActivity activ) {
        Retrofit retrofit;
        activity = activ;
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        openWeather = retrofit.create(OpenWeather.class);
    }

    public static void requestRetrofit(double latitude, double longitude, String keyApi) {
        openWeather.loadWeatherCoordinates(Double.toString(latitude), Double.toString(longitude), keyApi)
                .enqueue(new Callback<WeatherRequest>() {
                    Weather weather;

                    @Override
                    public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                        if (response.body() != null) {
                            weather = new Weather();
                            weather.city = response.body().getName();
                            weather.description = response.body().getWeather()[0].getDescription();
                            weather.humidity = response.body().getMain().getHumidity();
                            weather.pressure = response.body().getMain().getPressure();
                            weather.temp = response.body().getMain().getTemp();
                            weather.temp_min = response.body().getMain().getTemp_min();
                            weather.temp_max = response.body().getMain().getTemp_max();
                            weather.icon = "https://images.unsplash.com/photo-1567449303183-ae0d6ed1498e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80";
                            activity.addWeather(weather);
                            //activity.displayWeather(response.body().getName(), Float.toString(response.body().getMain().getTemp()));
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherRequest> call, Throwable t) {
                        activity.displayError("Проверьте подключение к интернету!");
                    }
                });
    }

                    public static void requestRetrofit(String city, String keyApi) {
        openWeather.loadWeather(city, keyApi)
                .enqueue(new Callback<WeatherRequest>() {
                    Weather weather;

                    @Override
                    public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                        if (response.body() != null) {
                            weather = new Weather();
                            weather.city = response.body().getName();
                            weather.description = response.body().getWeather()[0].getDescription();
                            weather.humidity = response.body().getMain().getHumidity();
                            weather.pressure = response.body().getMain().getPressure();
                            weather.temp = response.body().getMain().getTemp();
                            weather.temp_min = response.body().getMain().getTemp_min();
                            weather.temp_max = response.body().getMain().getTemp_max();
                            weather.icon = "https://images.unsplash.com/photo-1567449303183-ae0d6ed1498e?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&auto=format&fit=crop&w=634&q=80";
                            activity.addWeather(weather);
                            //activity.displayWeather(response.body().getName(), Float.toString(response.body().getMain().getTemp()));
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherRequest> call, Throwable t) {
                        activity.displayError("Проверьте подключение к интернету!");
                    }
                });
    }
}