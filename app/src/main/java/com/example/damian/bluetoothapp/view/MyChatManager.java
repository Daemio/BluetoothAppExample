package com.example.damian.bluetoothapp.view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.Scanner;

/**
 * Created by Admin on 20.11.2015.
 */
public class MyChatManager {
    PrintWriter printWriter;
    Scanner scanner;

    public MyChatManager(InputStream is, OutputStream os) {
        this.printWriter = new PrintWriter(os);
        this.scanner = new Scanner(is);
    }

    public String getMessage(){
        return scanner.nextLine();
    }

    public boolean hasNextLine(){
        return scanner.hasNextLine();
    }

    public void writeMessage(String s){
        printWriter.append(s);
    }
}
