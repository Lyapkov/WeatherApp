package com.dlyapkov.myapplication;

import android.os.Handler;

import com.dlyapkov.myapplication.Entity.WeatherRequest;
import com.dlyapkov.myapplication.interfaces.OpenWeather;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Http {
    private static OpenWeather openWeather;
    //private static WeatherRequest request;
    //private static Handler handler;
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

    public static void requestRetrofit(String city, String keyApi) {
        openWeather.loadWeather(city, keyApi)
                .enqueue(new Callback<WeatherRequest>() {
                    @Override
                    public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                        if (response.body() != null) {
                            activity.displayWeather(response.body().getName(), Float.toString(response.body().getMain().getTemp()));
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherRequest> call, Throwable t) {
                        activity.displayError("Проверьте подключение к интернету!");
                    }
                });
    }
}