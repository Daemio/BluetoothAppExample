package com.example.damian.bluetoothapp.data;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.example.damian.bluetoothapp.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Admin on 13.11.2015.
 */
public class BluetoothManager implements Serializable{

    private static final String CONNECTION_NAME = "MyBtAppConnection";
    private static final UUID MY_UUID_SECURE = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final int LISTENING_WAITING_TIME_SECONDS = 20; //time to wait for connection
    private static final int DISCOVERABLE_DURATION_SECONDS = 300;

    Activity activity;
    BroadcastReceiver br;
    BluetoothAdapter bluetoothAdapter;
    SearchListener callBackBluetoothDeviceAction;
    BluetoothSocket socket;


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

    public void setDiscoverable(){
        Intent intent;
        intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,DISCOVERABLE_DURATION_SECONDS);
        activity.startActivityForResult(intent, Utils.REQUEST_ENABLE_BT);
    }


    public void scanBluetoothDevices() {
        bluetoothAdapter.startDiscovery();
    }

    public boolean bluetoothIsEnabled() {
        return bluetoothAdapter.isEnabled();
    }


    public void listen() {
        setDiscoverable();
        socket = connectToServerSocket(createServerSocket());
    }

    public void disconnect() {
        try {
            socket.close();
        } catch (IOException e) {
            Log.d(BluetoothManager.class.getName(), "Could not close socket");
        }
        socket = null;
    }

    public void connect(BluetoothDevice device) {
        try {
            socket = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
        } catch (IOException e) {
            Log.d(BluetoothManager.class.getName(), "Could not create socket");
            socket = null;
        }
        if(socket!=null){
            try {
                socket.connect();
            } catch (IOException e) {
                Log.d(BluetoothManager.class.getName(), "Could not connect to device socket");
            }
        }
    }

    public boolean isConnectedToSocket(){
        if(socket == null)
            return false;
        return socket.isConnected();
    }

    public InputStream getSocketInputStream() {
        if (socket != null) {
            try {
                return socket.getInputStream();
            } catch (IOException e) {
                Log.d(BluetoothManager.class.getName(), "Could not get InputStream from socket");
            }
        }
        Log.d(BluetoothManager.class.getName(), "Socket is not created");
        return null;
    }

    public OutputStream getSocketOutputStream() {
        if (socket != null) {
            try {
                return socket.getOutputStream();
            } catch (IOException e) {
                Log.d(BluetoothManager.class.getName(), "Could not get InputStream from socket");
            }
        }
        Log.d(BluetoothManager.class.getName(), "Socket is not created");
        return null;
    }

    public String getDeviceName(){
        return bluetoothAdapter.getName();
    }

    public String getConnectedDeviceName(){
        if(socket!=null){
            if(socket.isConnected()){
                return socket.getRemoteDevice().getName();
            }
        }
        Log.d(BluetoothManager.class.getName(), "Could not get remote device name? no socket created");
        return null;
    }

    private BluetoothServerSocket createServerSocket() {
        try {
            return bluetoothAdapter.listenUsingRfcommWithServiceRecord(CONNECTION_NAME, MY_UUID_SECURE);
        } catch (IOException e) {
            Log.d(BluetoothManager.class.getName(), "Could not create server socket");
        }
        return null;
    }

    private BluetoothSocket connectToServerSocket(BluetoothServerSocket serverSocket) {
        BluetoothSocket socket;

        try {
            socket = serverSocket.accept(LISTENING_WAITING_TIME_SECONDS * 1000);
        } catch (IOException e) {
            Log.d(BluetoothManager.class.getName(), "Could not accept() to connect to server socket");
            return null;
        }

        try {
            serverSocket.close();
        } catch (IOException e) {
            Log.d(BluetoothManager.class.getName(), "Could not close server socket");
        }

        try {
            socket.connect();
        } catch (IOException e) {
            Log.d(BluetoothManager.class.getName(), "Could not connect to server socket");
        }
        return socket;
    }


    public interface SearchListener {
        void onSearchStarted();

        void onSearchFinished(List<BluetoothDevice> devices);

        void onDeviceFound(BluetoothDevice device);
    }
}


