package com.example.damian.bluetoothapp.data;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.damian.bluetoothapp.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 13.11.2015.
 */
public class BluetoothManager {
    Activity activity;
    BroadcastReceiver br;
    BluetoothAdapter bluetoothAdapter;
    SearchListener callBackBluetoothDeviceAction;

    public BluetoothManager(Activity activity, SearchListener callBackBluetoothDeviceAction) {
        this.callBackBluetoothDeviceAction = callBackBluetoothDeviceAction;
        this.activity = activity;
    }


    public void init() {
        br = new BroadcastReceiver() {
            ArrayList deviceList = new ArrayList();
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                    //Toast.makeText(TheApplication.getInstance().getApplicationContext(), "Start scanning", Toast.LENGTH_LONG).show();
                    //discovery starts, we can show progress dialog or perform other tasks
                    deviceList.clear();
                    callBackBluetoothDeviceAction.onSearchStarted();
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                    callBackBluetoothDeviceAction.onSearchFinished(deviceList);
                    //Toast.makeText(TheApplication.getInstance().getApplicationContext(), "Finished scanning", Toast.LENGTH_LONG).show();
                    //discovery finishes, dismis progress dialog
                    //listAdapter.notifyDataSetChanged();
                } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    //bluetooth device found
                    //Toast.makeText(TheApplication.getInstance().getApplicationContext(), "Found device", Toast.LENGTH_SHORT).show();
                    BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (!deviceList.contains(device)) {
                        deviceList.add(device);
                        callBackBluetoothDeviceAction.onDeviceFound(device);
                    }
                }
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(br, filter);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    public void unregisterReceiver() {
        activity.unregisterReceiver(br);
    }

    public void setBluetoothEnabled(boolean state) {
        Intent intent;
        if (state) {
            if (!bluetoothAdapter.isEnabled()) {
                intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(intent, Utils.REQUEST_ENABLE_BT);
            }
        } else {
            bluetoothAdapter.disable();
        }
    }

    public void scanBluetoothDevices() {
        bluetoothAdapter.startDiscovery();
    }

    public boolean bluetoothIsEnabled() {
        return bluetoothAdapter.isEnabled();
    }


    public interface SearchListener {
        void onSearchStarted();

        void onSearchFinished(List<BluetoothDevice> devices);

        void onDeviceFound(BluetoothDevice device);
    }
}


