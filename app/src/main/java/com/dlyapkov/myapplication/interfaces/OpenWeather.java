package com.dlyapkov.myapplication.interfaces;

import retrofit2.Call;
import retrofit2.http.Path;
import retrofit2.http.Query;
import com.dlyapkov.myapplication.Entity.WeatherRequest;
import retrofit2.http.GET;

public interface OpenWeather {
    @GET("data/2.5/weather")
    Call<WeatherRequest> loadWeather(@Query("q") String cityCountry, @Query("appid") String keyApi);

    @GET("data/2.5/weather")
    Call<WeatherRequest> load();
}
