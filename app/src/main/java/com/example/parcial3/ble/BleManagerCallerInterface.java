package com.example.parcial3.ble;

public interface BleManagerCallerInterface {

    void scanStartedSuccessfully();
    void scanStoped();
    void scanFailed(int error);
    void newDeviceDetected();

}
