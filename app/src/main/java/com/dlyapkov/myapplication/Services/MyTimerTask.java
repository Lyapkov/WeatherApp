package com.dlyapkov.myapplication.Services;

import android.util.Log;

import com.dlyapkov.myapplication.BuildConfig;
import com.dlyapkov.myapplication.Http;
import com.dlyapkov.myapplication.MainActivity;

import java.util.TimerTask;

public class MyTimerTask extends TimerTask {
    @Override
    public void run() {
        Http.requestRetrofit("moscow", BuildConfig.WEATHER_API_KEY);
        Log.d("RUN", "Эта херня работает!");
    }
}
