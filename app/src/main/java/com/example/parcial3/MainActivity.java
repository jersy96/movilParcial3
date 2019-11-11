package com.example.parcial3;

import android.os.Bundle;

import com.example.parcial3.adapters.BleGattServicesListAdapter;
import com.example.parcial3.adapters.BluetoothDeviceListAdapter;
import com.example.parcial3.ble.BleManager;
import com.example.parcial3.ble.BleManagerCallerInterface;
import com.example.parcial3.logs.Logger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements BleManagerCallerInterface {
    private final static int DEVICES_ADAPTER = 1;
    private final static int SERVICES_ADAPTER = 2;

    public BleManager bleManager;
    private MainActivity mainActivity;
    private int currentAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        currentAdapter = DEVICES_ADAPTER;
        mainActivity = this;
        instantiateBleManager();
        detectIfBleIsSupported();
        detectIfBluetoothIsEnabled();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_show_devices:
                setDevicesAdapter();
                bleManager.scanDevices();
                return true;
            case R.id.action_show_services:
                setServicesAdapter();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void instantiateBleManager(){
        bleManager = new BleManager(this, this);
    }

    private void detectIfBleIsSupported(){
        boolean bleIsSupported = bleManager.checkIfBLEIsSupportedOrNot();
        if (bleIsSupported){
            handleBleSupported();
        } else {
            handleBleNotSupported();
        }
    }

    private void handleBleSupported(){
        Logger.shortToast(this, "Su dispositivo SI soporta BLE");
    }

    private void handleBleNotSupported(){
        Logger.shortToast(this, "Su dispositivo NO soporta BLE");
    }

    private void detectIfBluetoothIsEnabled(){
        if(!bleManager.isBluetoothOn()){
            bleManager.enableBluetoothDevice(this, 1001);
        }
        bleManager.requestLocationPermissions(this,1002);
    }

    @Override
    public void scanStartedSuccessfully() {

    }

    @Override
    public void scanStoped() {

    }

    @Override
    public void scanFailed(int error) {

    }

    @Override
    public void newDeviceDetected() {
        if (currentAdapter == DEVICES_ADAPTER){
            setDevicesAdapter();
        }
    }

    @Override
    public void connectedToGattServer() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Logger.shortToast(getApplicationContext(), "conectado");
            }
        });
    }

    @Override
    public void servicesDiscovered() {
        if (currentAdapter == SERVICES_ADAPTER){
            setServicesAdapter();
        }
    }

    private void setDevicesAdapter(){
        currentAdapter = DEVICES_ADAPTER;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    ListView listView=(ListView)findViewById(R.id.devices_list_id);
                    BluetoothDeviceListAdapter adapter=new BluetoothDeviceListAdapter(getApplicationContext(), bleManager, mainActivity);
                    listView.setAdapter(adapter);

                }catch (Exception error){

                }
            }
        });
    }

    public void setServicesAdapter(){
        currentAdapter = SERVICES_ADAPTER;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView=(ListView)findViewById(R.id.devices_list_id);
                BleGattServicesListAdapter adapter=new BleGattServicesListAdapter(getApplicationContext(), bleManager, mainActivity);
                listView.setAdapter(adapter);
            }
        });
    }
}
