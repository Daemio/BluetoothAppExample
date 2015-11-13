package com.example.damian.bluetoothapp.view;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.damian.bluetoothapp.R;
import com.example.damian.bluetoothapp.TheApplication;
import com.example.damian.bluetoothapp.view.adapter.MyArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ListView lvMain;
    Button btnRefresh;
    Button btnListen;
    Button btnTurn; // turn on/off bluetooth
    BluetoothAdapter bluetoothAdapter;
    MyArrayAdapter listAdapter;
    List<BluetoothDevice> deviceList;
    final int REQUEST_ENABLE_BT = 100;

    final BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                listAdapter.clear();
                deviceList.clear();
                //Toast.makeText(TheApplication.getInstance().getApplicationContext(), "Start scanning", Toast.LENGTH_LONG).show();
                //discovery starts, we can show progress dialog or perform other tasks
                progressBar.setVisibility(View.VISIBLE);
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //Toast.makeText(TheApplication.getInstance().getApplicationContext(), "Finished scanning", Toast.LENGTH_LONG).show();
                //discovery finishes, dismis progress dialog
                progressBar.setVisibility(View.GONE);
                listAdapter.addAll(deviceList);
                //listAdapter.notifyDataSetChanged();
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                //bluetooth device found
                //Toast.makeText(TheApplication.getInstance().getApplicationContext(), "Found device", Toast.LENGTH_SHORT).show();
                BluetoothDevice device = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(!deviceList.contains(device))
                    deviceList.add(device);
            }
        }
    };
    private FrameLayout progressBar;

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
        initBluetoothAdapter();
        listAdapter = new MyArrayAdapter(this,R.layout.list_item);
        lvMain.setAdapter(listAdapter);
        progressBar.setVisibility(View.GONE);
        deviceList = new ArrayList<>();

        if(bluetoothAdapter.isEnabled()){
            btnTurn.setText("Turn Off");
        }else {
            btnTurn.setText("Turn On");
        }

    }


    @Override
    protected void onDestroy() {
        unregisterReceiver(br);
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnListen:
                break;
            case R.id.btnRefresh:
                if(bluetoothAdapter.isEnabled()) {
                    bluetoothAdapter.startDiscovery();
                }else{
                    Toast.makeText(this, "Error, Bluetooth is turned off", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btnTurn:
                if (bluetoothAdapter.isEnabled()) {
                    setBluetoothEnabled(false);
                } else {
                    setBluetoothEnabled(true);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode != RESULT_OK) {
                Toast.makeText(this, "Error, couldn't turn Bluetooth on", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setBluetoothEnabled(boolean state) {
        Intent intent;
        if (state) {
            if (!bluetoothAdapter.isEnabled()) {
                intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(intent, REQUEST_ENABLE_BT);
                btnTurn.setText("Turn off");
            }
        } else {
            bluetoothAdapter.disable();
            btnTurn.setText("Turn on");
        }
    }

    private void initBluetoothAdapter(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(br, filter);
    }

}
