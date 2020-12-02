package com.dlyapkov.myapplication.database;

import com.dlyapkov.myapplication.Entity.Weather;

import java.util.List;

public class EducationSource {
    private final EducationDao educationDao;
    private List<Weather> weather;

    public EducationSource(EducationDao educationDao) {
        this.educationDao = educationDao;
    }

    public List<Weather> getWeather() {
        if (weather == null)
            LoadWeather();
        return weather;
    }

    private void LoadWeather() {
        weather = educationDao.getAllWeather();
    }

    public long getCountWeather() {
        return educationDao.getCountWeather();
    }

    public long getIdWeather(Weather weather) {
        Weather receivedWeather = educationDao.getWeatherById(weather.id);
        return receivedWeather.id;
    }

    public void addWeather(Weather weather) {
        long id = educationDao.insertWeather(weather);

        LoadWeather();
    }

    public void updateOrAddWeather(Weather weather) {
        Weather receivedWeather = educationDao.getWeatherByCity(weather.city);
        if (receivedWeather != null)
            educationDao.updateWeather(receivedWeather);
        else
            educationDao.insertWeather(weather);
        LoadWeather();
    }

    public void removeWeather(long id) {
        educationDao.deleteWeatherById(id);
        LoadWeather();
    }
}
