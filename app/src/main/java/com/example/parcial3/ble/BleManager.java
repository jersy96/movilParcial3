package com.example.parcial3.ble;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
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
    public static final int CHARACTERISTIC_OPERATION_UNAVAILABLE = 0;
    public static final int CHARACTERISTIC_OPERATION_START = 1;
    public static final int CHARACTERISTIC_OPERATION_NULL = 2;

    Context context;

    BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    public List<ScanResult> scanResults;
    public ArrayList<BluetoothGattService> services;
    public ArrayList<BluetoothGattCharacteristic> characteristics;
    public ArrayList<BluetoothGattDescriptor> descriptors;
    private BluetoothGatt lastGatt;
    public BluetoothGattCharacteristic lastCharacteristic;
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
            services = new ArrayList();
            characteristics = new ArrayList();
            descriptors = new ArrayList();
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

    public void stopScan(){
        this.scanResults.clear();
        this.services.clear();
        bluetoothLeScanner.stopScan(this);
        lastCharacteristic = null;
        lastGatt = null;
        caller.scanStoped();
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        processNewScanResult(result);
        caller.newDeviceDetected();
    }

    @Override
    public void onBatchScanResults(List<ScanResult> results) {

    }

    @Override
    public void onScanFailed(int errorCode) {
        caller.scanFailed(errorCode);
    }

    private void processNewScanResult(ScanResult newScanResult){
        String newAddress = newScanResult.getDevice().getAddress();
        int pos = getDevicePositionByAddress(newAddress);
        if (pos == -1){
            scanResults.add(newScanResult);
        } else {
            scanResults.set(pos, newScanResult);
        }
    }

    public int getDevicePositionByAddress(String targetAddress){
        for (int i=0; i < scanResults.size(); i++){
            ScanResult  current = scanResults.get(i);
            if(current.getDevice().getAddress().equals(targetAddress)){
                return i;
            }
        }
        return -1;
    }

    public BluetoothDevice getDeviceByAddress(String targetAddress){
        int pos = getDevicePositionByAddress(targetAddress);
        if(pos == -1){
            return null;
        } else {
          return scanResults.get(pos).getDevice();
        }

    }

    public void connectToGattServer(String targetAddress){
        BluetoothDevice device = getDeviceByAddress(targetAddress);
        try{
            device.connectGatt(this.context, false, new BluetoothGattCallback() {
                @Override
                public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                    super.onPhyUpdate(gatt, txPhy, rxPhy, status);
                }

                @Override
                public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
                    super.onPhyRead(gatt, txPhy, rxPhy, status);
                }

                @Override
                public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                    super.onConnectionStateChange(gatt, status, newState);
                    if(newState==BluetoothGatt.STATE_CONNECTED){
                        lastGatt = gatt;
                        caller.connectedToGattServer();
                        gatt.discoverServices();
                    }
                }

                @Override
                public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                    super.onServicesDiscovered(gatt, status);
                    services = (ArrayList)gatt.getServices();
                    searchAndSetAllNotifyAbleCharacteristics(gatt);
                    caller.servicesDiscovered();
                }

                @Override
                public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicRead(gatt, characteristic, status);
                    caller.onCharacteristicRead(characteristic);
                }

                @Override
                public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
                    super.onCharacteristicWrite(gatt, characteristic, status);
                    caller.onCharacteristicWrite(characteristic);
                }

                @Override
                public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                    super.onCharacteristicChanged(gatt, characteristic);
                    caller.onCharacteristicChanged(characteristic);
                }

                @Override
                public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorRead(gatt, descriptor, status);
                }

                @Override
                public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
                    super.onDescriptorWrite(gatt, descriptor, status);
                }

                @Override
                public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
                    super.onReliableWriteCompleted(gatt, status);
                }

                @Override
                public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                    super.onReadRemoteRssi(gatt, rssi, status);
                }

                @Override
                public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
                    super.onMtuChanged(gatt, mtu, status);
                }
            },BluetoothDevice.TRANSPORT_LE);
        }catch (Exception error){

        }
    }

    public int getServicePositionByUuid(String targetUuid){
        for (int i=0; i < services.size(); i++){
            BluetoothGattService current = services.get(i);
            if(current.getUuid().toString().equals(targetUuid)){
                return i;
            }
        }
        return -1;
    }

    public BluetoothGattService getServiceByUuid(String targetUuid){
        int pos = getServicePositionByUuid(targetUuid);
        if(pos == -1){
            return null;
        } else {
            return services.get(pos);
        }

    }

    public void setCharacteristics(String serviceUuid){
        lastCharacteristic = null;
        BluetoothGattService service = getServiceByUuid(serviceUuid);
        characteristics = (ArrayList) service.getCharacteristics();
    }

    public int getCharacteristicPositionByUuid(String targetUuid){
        for (int i=0; i < characteristics.size(); i++){
            BluetoothGattCharacteristic current = characteristics.get(i);
            if(current.getUuid().toString().equals(targetUuid)){
                return i;
            }
        }
        return -1;
    }

    public BluetoothGattCharacteristic getCharacteristicByUuid(String targetUuid){
        int pos = getCharacteristicPositionByUuid(targetUuid);
        if(pos == -1){
            return null;
        } else {
            return characteristics.get(pos);
        }

    }

    public void setDescriptors(String characteristicUuid){
        BluetoothGattCharacteristic characteristic = getCharacteristicByUuid(characteristicUuid);
        lastCharacteristic = characteristic;
        descriptors = (ArrayList) characteristic.getDescriptors();
    }

    private void searchAndSetAllNotifyAbleCharacteristics(BluetoothGatt gatt) {
        for(BluetoothGattService currentService: services){
            for(BluetoothGattCharacteristic currentCharacteristic:currentService.getCharacteristics()){
                enableNotifiableCharacteristic(currentCharacteristic);
            }
        }
    }

    private void enableNotifiableCharacteristic(BluetoothGattCharacteristic characteristic){
        if(isCharacteristicNotifiable(characteristic)){
            lastGatt.setCharacteristicNotification(characteristic, true);
            for(BluetoothGattDescriptor currentDescriptor:characteristic.getDescriptors()){
                currentDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                lastGatt.writeDescriptor(currentDescriptor);
            }

        }
    }

    public boolean isCharacteristicWriteable(BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() &
                (BluetoothGattCharacteristic.PROPERTY_WRITE
                        | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
                        | BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE)) != 0;
    }

    public boolean isCharacteristicReadable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0);
    }

    public boolean isCharacteristicNotifiable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0);
    }

    public boolean isCharacteristicIndicable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0);
    }

    public boolean isCharacteristicExtendable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) != 0);
    }

    public boolean isCharacteristicBroadcastable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_BROADCAST) != 0);
    }

    public int readLastCharacteristic(){
        if(lastCharacteristic == null){
            return CHARACTERISTIC_OPERATION_NULL;
        } else {
            if (isCharacteristicReadable(lastCharacteristic)){
                readCharacteristic(lastGatt, lastCharacteristic);
                return CHARACTERISTIC_OPERATION_START;
            } else {
                return CHARACTERISTIC_OPERATION_UNAVAILABLE;
            }
        }
    }

    public int writeLastCharacteristic(byte[] data){
        if(lastCharacteristic == null){
            return CHARACTERISTIC_OPERATION_NULL;
        } else {
            if(isCharacteristicWriteable(lastCharacteristic)){
                writeCharacteristic(lastGatt, lastCharacteristic, data);
                return CHARACTERISTIC_OPERATION_START;
            } else {
                return CHARACTERISTIC_OPERATION_UNAVAILABLE;
            }
        }
    }

    private boolean readCharacteristic(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic){
        return gatt.readCharacteristic(characteristic);
    }

    public boolean writeCharacteristic(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, byte[] data){
        characteristic.setValue(data);
        return gatt.writeCharacteristic(characteristic);
    }
}
