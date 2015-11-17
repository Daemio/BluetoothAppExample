package com.example.damian.bluetoothapp.view;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.example.damian.bluetoothapp.R;
import com.example.damian.bluetoothapp.Utils;
import com.example.damian.bluetoothapp.data.BluetoothManager;
import com.example.damian.bluetoothapp.view.adapter.MyArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ListView lvMain;
    Button btnRefresh;
    Button btnListen;
    Button btnTurn; // turn on/off bluetooth
    MyArrayAdapter listAdapter;
    List<BluetoothDevice> deviceList;
    private FrameLayout progressBar;
    BluetoothManager bluetoothManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        lvMain = (ListView) findViewById(R.id.lvMain);
        btnListen = (Button) findViewById(R.id.btnListen);
        btnRefresh = (Button) findViewById(R.id.btnRefresh);
        btnTurn = (Button) findViewById(R.id.btnTurn);
        btnListen.setOnClickListener(this);
        btnRefresh.setOnClickListener(this);
        btnTurn.setOnClickListener(this);
        progressBar = (FrameLayout) findViewById(R.id.loadingPanel);
        listAdapter = new MyArrayAdapter(this,R.layout.list_item);
        lvMain.setAdapter(listAdapter);
        progressBar.setVisibility(View.GONE);
        deviceList = new ArrayList<>();

        bluetoothManager = new BluetoothManager(this, new BluetoothManager.SearchListener() {
            @Override
            public void onSearchStarted() {
                deviceList.clear();
                listAdapter.clear();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSearchFinished(List<BluetoothDevice> devices) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onDeviceFound(BluetoothDevice device) {
                deviceList.add(device);
                listAdapter.add(device);
            }
        });

        bluetoothManager.init();

        if(bluetoothManager.bluetoothIsEnabled()){
            btnTurn.setText("Turn Off");
        }else {
            btnTurn.setText("Turn On");
        }

    }


    @Override
    protected void onDestroy() {
        bluetoothManager.unregisterReceiver();
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnListen:
                break;
            case R.id.btnRefresh:
                if(bluetoothManager.bluetoothIsEnabled()) {
                    bluetoothManager.scanBluetoothDevices();
                }else{
                    Toast.makeText(this, "Error, Bluetooth is turned off", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnTurn:
                if (bluetoothManager.bluetoothIsEnabled()) {
                    bluetoothManager.setBluetoothEnabled(false);
                    btnTurn.setText("Turn on");
                } else {
                    bluetoothManager.setBluetoothEnabled(true);
                    btnTurn.setText("Turn off");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Utils.REQUEST_ENABLE_BT) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "Error, couldn't turn Bluetooth on", Toast.LENGTH_LONG).show();
            }
        }
    }

}
