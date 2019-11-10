package com.example.parcial3.ble;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AlertDialog;

import com.example.parcial3.R;

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

    public boolean requestBluetoothDeviceEnable(final Activity activity){
        try{
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                AlertDialog.Builder builder=new AlertDialog.Builder(activity)
                        .setTitle("Bluetooth")
                        .setMessage("The bluetooth device must be enabled in order to connect the device")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                activity.startActivityForResult(enableBtIntent, 1001);
                            }
                        });
                builder.show();

            }else {
                return true;
            }
        }catch (Exception error){

        }
        return false;
    }
}
