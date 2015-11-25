package com.example.damian.bluetoothapp.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.damian.bluetoothapp.R;
import com.example.damian.bluetoothapp.TheApplication;
import com.example.damian.bluetoothapp.Utils;
import com.example.damian.bluetoothapp.data.BluetoothManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Created by Admin on 18.11.2015.
 */
public class ChatActivity extends AppCompatActivity {
    Handler myHandler;
    Button btnDisconnect;
    Button btnSend;
    TextView tvMessages;
    EditText etNewMessage;
    BluetoothManager bluetoothManager;
    InputStream inputStream;
    OutputStream outputStream;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        btnSend = (Button) findViewById(R.id.btnSendMessage);
        tvMessages = (TextView) findViewById(R.id.tvMessages);
        etNewMessage = (EditText) findViewById(R.id.etNewMessage);

        bluetoothManager = TheApplication.getInstance().getBluetoothManager();
        inputStream = bluetoothManager.getSocketInputStream();
        outputStream = bluetoothManager.getSocketOutputStream();

        if(inputStream!=null&&outputStream!=null){
            Toast.makeText(TheApplication.getInstance().getApplicationContext(),"Streams initialized",Toast.LENGTH_LONG).show();
        }




        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()){
                    case R.id.btnSendMessage:
                        break;
                    case R.id.btnDisconnect:
                        finish();
                        break;
                }
            }
        };

        btnSend.setOnClickListener(listener);
        btnDisconnect.setOnClickListener(listener);

    }


    private void appendMessage(String author,String message){
        tvMessages.append(author+" : "+message+"\n");
    }
    @Override
    protected void onDestroy() {
        //connectedThread.interrupt();
        bluetoothManager.disconnect();
        super.onDestroy();
    }

}
