package com.example.parcial3.ble;

import android.bluetooth.BluetoothGattCharacteristic;

public interface BleManagerCallerInterface {

    void scanStartedSuccessfully();
    void scanStoped();
    void scanFailed(int error);
    void newDeviceDetected();
    void connectedToGattServer();
    void servicesDiscovered();
    void onCharacteristicChanged(BluetoothGattCharacteristic characteristic);
    void onCharacteristicRead(BluetoothGattCharacteristic characteristic);
    void onCharacteristicWrite(BluetoothGattCharacteristic characteristic, int status);

}
