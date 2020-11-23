package com.dlyapkov.myapplication.database;

import android.app.Application;

import androidx.room.Room;

public class App extends Application {
    private static App instance;
    private EducationDatabase db;

    public static App getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        db = Room.databaseBuilder(
                getApplicationContext(),
                EducationDatabase.class,
                "education_database")
                .allowMainThreadQueries()
//                .addMigrations()
                .build();
    }

    public EducationDao getEducationDao() {
        return db.getEducationDao();
    }
}
