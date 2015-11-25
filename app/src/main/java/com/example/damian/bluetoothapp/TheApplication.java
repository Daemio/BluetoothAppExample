package com.example.damian.bluetoothapp;

import android.app.Application;

import com.example.damian.bluetoothapp.data.BluetoothManager;

/**
 * Created by Admin on 12.11.2015.
 */
public class TheApplication extends Application{
    public void setBluetoothManager(BluetoothManager bluetoothManager) {
        this.bluetoothManager = bluetoothManager;
    }

    public BluetoothManager getBluetoothManager() {

        return bluetoothManager;
    }

    private BluetoothManager bluetoothManager;
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
