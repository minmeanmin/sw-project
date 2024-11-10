package com.example.naengjanggo;

import java.io.OutputStream;

public class BluetoothConnectionManager {
    private static BluetoothConnectionManager instance;
    private OutputStream outputStream;

    private BluetoothConnectionManager() {}

    public static synchronized BluetoothConnectionManager getInstance() {
        if (instance == null) {
            instance = new BluetoothConnectionManager();
        }
        return instance;
    }

    public void setOutputStream(OutputStream outputStream) {
        this.outputStream = outputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }
}
