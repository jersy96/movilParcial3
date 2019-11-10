package com.example.parcial3.ble;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;

public class BleManager {
    Context context;

    BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;

    public BleManager(Context context) {
        this.context = context;
        initializeBluetoothManager();
    }


    public void initializeBluetoothManager(){
        try{
            bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        }catch (Exception error){

        }
    }

    public boolean checkIfBLEIsSupportedOrNot(){
        try {
            if (!context.getPackageManager().
                    hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                return false;
            }
            return true;
        }catch (Exception error){

        }
        return false;
    }

    public boolean isBluetoothOn(){
        try{
            return bluetoothManager.getAdapter().isEnabled();
        }catch (Exception error){

        }
        return false;
    }
}
