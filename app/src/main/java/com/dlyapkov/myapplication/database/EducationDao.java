package com.dlyapkov.myapplication.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.dlyapkov.myapplication.Entity.Weather;

import java.util.List;

@Dao
public interface EducationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertWeather(Weather weather);

    @Update
    void updateWeather(Weather weather);

//    @Query("UPDATE weather SET ")
//    List<Weather> updateWeatherCity();

    @Delete
    void deleteWeather(Weather weather);

    @Query("DELETE FROM weather WHERE id = :id")
    void deleteWeatherById(long id);

    @Query("SELECT * FROM weather")
    List<Weather> getAllWeather();

    @Query("SELECT * FROM weather WHERE city = :city")
    Weather getWeatherByCity(String city);

    @Query("SELECT * FROM weather WHERE id = :id")
    Weather getWeatherById(long id);

    @Query("SELECT COUNT() FROM weather")
    long getCountWeather();

//    @Query("SELECT  " +
//            "FROM weather " +
//            "INNER JOIN  ON weather.id = .weather_id")
//    List<Weather> getWeatherWithEmail();
}
