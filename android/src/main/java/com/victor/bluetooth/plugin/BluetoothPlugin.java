package com.victor.bluetooth.plugin;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@CapacitorPlugin(name = "Bluetooth")
public class BluetoothPlugin extends Plugin {

  private BluetoothAdapter bluetoothAdapter;
  private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

  @Override
  public void load() {
    super.load();
    bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
  }

  @PluginMethod
  public void echo(PluginCall call) {
    String value = call.getString("value");
    JSObject ret = new JSObject();
    ret.put("value", value);
    call.resolve(ret);
  }

  @PluginMethod
  public void startClassicScan(PluginCall call) {
    if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
      call.reject("Bluetooth is not enabled");
      return;
    }

    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
      // Request the necessary permissions
      requestPermissions(call, new String[]{
        Manifest.permission.BLUETOOTH_SCAN
      });
      return;
    }

    bluetoothAdapter.startDiscovery();
    BroadcastReceiver receiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
          BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
          if (device != null) {
            JSObject ret = new JSObject();
            ret.put("address", device.getAddress());
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
              // Request the necessary permissions
              requestPermissions(call, new String[]{
                Manifest.permission.BLUETOOTH_CONNECT
              });
              return;
            }
            ret.put("name", device.getName());
            notifyListeners("classicDeviceFound", ret);
          }
        }
      }
    };
    getContext().registerReceiver(receiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    call.resolve();
  }

  @PluginMethod
  public void connectToDevice(PluginCall call) {
    String address = call.getString("address");
    if (address == null) {
      call.reject("No device address provided");
      return;
    }

    if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
      // Request the necessary permissions
      requestPermissions(call, new String[]{
        Manifest.permission.BLUETOOTH_CONNECT
      });
      return;
    }

    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
    try {
      BluetoothSocket socket = device.createRfcommSocketToServiceRecord(MY_UUID);
      socket.connect();
      manageConnectedSocket(socket, call);
    } catch (IOException e) {
      call.reject("Connection failed", e);
    }
  }

  private void manageConnectedSocket(BluetoothSocket socket, PluginCall call) {
    InputStream inputStream;
    try {
      inputStream = socket.getInputStream();
    } catch (IOException e) {
      call.reject("Error getting input stream", e);
      return;
    }

    new Thread(() -> {
      byte[] buffer = new byte[1024];
      int bytes;
      while (true) {
        try {
          bytes = inputStream.read(buffer);
          String receivedData = new String(buffer, 0, bytes);
          JSObject ret = new JSObject();
          ret.put("data", receivedData);
          notifyListeners("dataReceived", ret);
        } catch (IOException e) {
          Log.e("BluetoothPlugin", "Disconnected", e);
          break;
        }
      }
    }).start();

    call.resolve();
  }

  @Override
  protected void handleRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    super.handleRequestPermissionsResult(requestCode, permissions, grantResults);

    PluginCall savedCall = getSavedCall();
    if (savedCall == null) {
      return;
    }

    for (int result : grantResults) {
      if (result == PackageManager.PERMISSION_DENIED) {  // Check if any permission was denied
        savedCall.reject("Permission denied");
        return;
      }
    }

    switch (savedCall.getMethodName()) {
      case "startClassicScan":
        startClassicScan(savedCall);
        break;
      case "connectToDevice":
        connectToDevice(savedCall);
        break;
    }
  }
}
