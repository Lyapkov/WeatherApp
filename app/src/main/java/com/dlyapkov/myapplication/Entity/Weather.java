package com.dlyapkov.myapplication.Entity;

import androidx.room.ColumnInfo;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity(indices = {@Index(value = {Weather.CITY})})
public class Weather {

    public final static String ID = "id";
    public final static String CITY = "city";
    public final static String DESCRIPTION = "description";
    public final static String ICON = "icon";
    public final static String TEMP = "temperature";
    public final static String PRESSURE = "pressure";
    public final static String HUMIDITY = "humidity";
    public final static String TEMP_MIN = "temp_min";
    public final static String TEMP_MAX = "temp_max";


    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = ID)
    public long id;

    @ColumnInfo(name = CITY)
    public String city;

    @ColumnInfo(name = DESCRIPTION)
    public String description;

    @ColumnInfo(name = ICON)
    public String icon;

    @ColumnInfo(name = TEMP)
    public float temp;

    @ColumnInfo(name = PRESSURE)
    public int pressure;

    @ColumnInfo(name = HUMIDITY)
    public int humidity;

    @ColumnInfo(name = TEMP_MIN)
    public float temp_min;

    @ColumnInfo(name = TEMP_MAX)
    public float temp_max;
}
