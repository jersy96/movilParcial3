package com.example.parcial3;

import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Bundle;

import com.example.parcial3.adapters.BleGattCharacteristicsListAdapter;
import com.example.parcial3.adapters.BleGattDescriptorsListAdapter;
import com.example.parcial3.adapters.BleGattServicesListAdapter;
import com.example.parcial3.adapters.BluetoothDeviceListAdapter;
import com.example.parcial3.ble.BleManager;
import com.example.parcial3.ble.BleManagerCallerInterface;
import com.example.parcial3.logs.Logger;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements BleManagerCallerInterface {
    private final static int DEVICES_ADAPTER = 1;
    private final static int SERVICES_ADAPTER = 2;
    private final static int CHARACTERISTICS_ADAPTER = 3;
    private final static int DESCRIPTORS_ADAPTER = 4;

    public BleManager bleManager;
    private MainActivity mainActivity;
    private int currentAdapter;
    private boolean scanning;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        scanning = false;
        currentAdapter = DEVICES_ADAPTER;
        mainActivity = this;
        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.openOptionsMenu();
            }
        });
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
            case R.id.action_start_scan:
                if (!scanning){
                    bleManager.scanDevices();
                    scanning = true;
                    setDevicesAdapter();
                } else {
                    Logger.shortToast(this, "Ya estas escaneando");
                }
                return true;
            case R.id.action_stop_scan:
                scanning = false;
                bleManager.stopScan();
                setDevicesAdapter();
                return true;
            case R.id.action_show_devices:
                setDevicesAdapter();
                return true;
            case R.id.action_show_services:
                setServicesAdapter();
                return true;
            case R.id.action_show_characteristics:
                setCharacteristicsAdapter();
                return true;
            case R.id.action_show_descriptors:
                setDescriptorsAdapter();
            case R.id.action_read_last_characteristic:
                readLastCharacteristic();
            case R.id.action_write_last_characteristic:
                writeLastCharacteristic();
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

    @Override
    public void onCharacteristicChanged(final BluetoothGattCharacteristic characteristic) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Logger.shortToast(getApplicationContext(), "characteristic changed"+characteristic.getUuid().toString());
            }
        });
    }

    @Override
    public void onCharacteristicRead(final BluetoothGattCharacteristic characteristic) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Logger.shortToast(getApplicationContext(), "characteristic read"+characteristic.getUuid().toString());
            }
        });
    }

    @Override
    public void onCharacteristicWrite(final BluetoothGattCharacteristic characteristic) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Logger.shortToast(getApplicationContext(), "characteristic write"+characteristic.getUuid().toString());
            }
        });
    }

    private void setDevicesAdapter(){
        currentAdapter = DEVICES_ADAPTER;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try{
                    ListView listView=(ListView)findViewById(R.id.adapters_list_id);
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
                ListView listView=findViewById(R.id.adapters_list_id);
                BleGattServicesListAdapter adapter=new BleGattServicesListAdapter(getApplicationContext(), bleManager, mainActivity);
                listView.setAdapter(adapter);
            }
        });
    }

    public void setCharacteristicsAdapter(){
        currentAdapter = CHARACTERISTICS_ADAPTER;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView=findViewById(R.id.adapters_list_id);
                BleGattCharacteristicsListAdapter adapter=new BleGattCharacteristicsListAdapter(getApplicationContext(), bleManager, mainActivity);
                listView.setAdapter(adapter);
            }
        });
    }

    public void setDescriptorsAdapter(){
        currentAdapter = DESCRIPTORS_ADAPTER;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView=findViewById(R.id.adapters_list_id);
                BleGattDescriptorsListAdapter adapter=new BleGattDescriptorsListAdapter(getApplicationContext(), bleManager, mainActivity);
                listView.setAdapter(adapter);
            }
        });
    }

    private void showCurrentAdapter(){
        switch (currentAdapter){
            case DEVICES_ADAPTER:
                setDevicesAdapter();
                break;
            case SERVICES_ADAPTER:
                setServicesAdapter();
                break;
            case CHARACTERISTICS_ADAPTER:
                setCharacteristicsAdapter();
                break;
            case DESCRIPTORS_ADAPTER:
                setDescriptorsAdapter();
                break;
        }
    }

    private void readLastCharacteristic(){
        if (!bleManager.readLastCharacteristic()){
            Logger.shortToast(this, "characteristic not set");
        }
    }

    private void writeLastCharacteristic(){
        byte[] data = "hola".getBytes();
        if (!bleManager.writeLastCharacteristic(data)){
            Logger.shortToast(this, "characteristic not set");
        }
    }
}
