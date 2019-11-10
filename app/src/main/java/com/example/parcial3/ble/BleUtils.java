package com.example.parcial3.ble;

import android.content.Context;
import android.content.pm.PackageManager;

public class BleUtils {
    public static boolean CheckIfBLEIsSupportedOrNot(Context context){
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
}
