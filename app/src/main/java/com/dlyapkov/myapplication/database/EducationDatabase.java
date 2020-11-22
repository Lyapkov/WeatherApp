package com.dlyapkov.myapplication.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.dlyapkov.myapplication.Entity.Weather;

@Database(entities = {Weather.class}, version = 1)
public abstract class EducationDatabase extends RoomDatabase {
    public abstract EducationDao getEducationDao();
}
