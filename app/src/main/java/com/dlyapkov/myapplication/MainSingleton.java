package com.dlyapkov.myapplication;

import android.app.Application;

public class MainSingleton extends Application {
    private final static MainSingleton mainSingleton = new MainSingleton();

    private String[] cities;

    private MainSingleton() {
        cities = getResources().getStringArray(R.array.cities);
    }

    public String[] getCities() {
        return cities;
    }

    public static MainSingleton getMainSingleton() {
        return mainSingleton;
    }
}
