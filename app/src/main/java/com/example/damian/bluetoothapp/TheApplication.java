package com.example.damian.bluetoothapp;

import android.app.Application;

/**
 * Created by Admin on 12.11.2015.
 */
public class TheApplication extends Application{
    private static TheApplication instance;
    public static TheApplication getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}
