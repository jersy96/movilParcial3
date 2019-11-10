package com.example.parcial3.ble;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class BleManager extends ScanCallback {
    Context context;

    BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    public List<ScanResult> scanResults;
    BleManagerCallerInterface caller;

    public BleManager(Context context, BleManagerCallerInterface caller) {
        this.context = context;
        initializeBluetoothManager();
        this.caller = caller;
    }


    public void initializeBluetoothManager(){
        try{
            bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
            scanResults = new ArrayList();
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

    public void requestLocationPermissions(final Activity activity,int REQUEST_CODE){
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                boolean gps_enabled = false;
                boolean network_enabled = false;

                LocationManager locationManager=(LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
                try {
                    gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                } catch(Exception ex) {}

                try {
                    network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                } catch(Exception ex) {}

                if(!((gps_enabled)||(network_enabled))){

                    android.app.AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setMessage("In order to BLE connection be successful please proceed to enable the GPS")
                            .setTitle("Settings");

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            context.startActivity(intent);

                        }
                    });

                    builder.create().show();
                }
            }
            if (ContextCompat.checkSelfPermission(this.context.getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            } else {
                activity.requestPermissions( new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_CODE);

            }
        }catch (Exception error){

        }

    }

    public void enableBluetoothDevice(Activity activity,int REQUEST_ENABLE_BT){
        try{
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }catch (Exception error){

        }
    }

    public void scanDevices(){
        try{
            scanResults.clear();
            bluetoothLeScanner=bluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.startScan(this);
            caller.scanStartedSuccessfully();
        }catch (Exception error){

        }
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        if(!isResultAlreadyAtList(result)) {
            scanResults.add(result);
        }
        caller.newDeviceDetected();

    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {

    }

    @Override
    public void onScanFailed(int errorCode) {
        caller.scanFailed(errorCode);
    }

    public boolean isResultAlreadyAtList(ScanResult newResult){
        for (ScanResult current : scanResults){
            if(current.getDevice().getAddress().equals(newResult.getDevice().getAddress())){
                return true;
            }
        }
        return false;
    }

    public BluetoothDevice getByAddress(String targetAddress){
        for(ScanResult current : scanResults){
            if(current!=null){
                if(current.getDevice().getAddress().equals(targetAddress)){
                    return current.getDevice();
                }
            }
        }
        return null;
    }
}
