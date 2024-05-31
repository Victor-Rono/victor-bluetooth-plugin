package com.victor.bluetooth.plugin;

import android.util.Log;

public class BluetoothPlugin {

    public String echo(String value) {
        Log.i("Echo", value);
        return value;
    }
}
