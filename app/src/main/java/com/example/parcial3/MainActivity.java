package com.example.parcial3;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.DialogInterface;
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

import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
                if (scanning){
                    Logger.shortToast(this, "Already scanning");
                } else {
                    bleManager.scanDevices();
                    scanning = true;
                    setDevicesAdapter();
                    Logger.shortToast(this, "Scan started");
                }
                return true;
            case R.id.action_stop_scan:
                if (scanning){
                    scanning = false;
                    bleManager.stopScan();
                    Logger.shortToast(this, "Scan stopped");
                    setDevicesAdapter();
                } else {
                    Logger.shortToast(this, "Not scanning");
                }
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
                return true;
            case R.id.action_read_last_characteristic:
                readLastCharacteristic();
                return true;
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
        Logger.shortToast(this, "Your device support BLE");
    }

    private void handleBleNotSupported(){
        Logger.shortToast(this, "Your device does NOT support BLE");
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
                Logger.shortToast(getApplicationContext(), "Connected to Gatt, discovering services..");
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
                int pos = bleManager.getCharacteristicPositionByUuid(characteristic.getUuid().toString());
                byte[] bytes = characteristic.getValue();
                String s = new String(bytes);
                String hexString = byteArrayToHexString(bytes);
                String message = "String = "+s+"\n"+"HexString = "+hexString;
                Logger.showAlert(mainActivity, "Characteristic "+(pos+1)+" change", message);
            }
        });
    }

    @Override
    public void onCharacteristicRead(final BluetoothGattCharacteristic characteristic) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int pos = bleManager.getCharacteristicPositionByUuid(characteristic.getUuid().toString());
                Logger.shortToast(getApplicationContext(), "characteristic read "+(pos+1));
            }
        });
    }

    @Override
    public void onCharacteristicWrite(final BluetoothGattCharacteristic characteristic, final int status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                int pos = bleManager.getCharacteristicPositionByUuid(characteristic.getUuid().toString());
                Logger.shortToast(getApplicationContext(), "characteristic write "+(pos+1)+", status: "+status);
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
        int result = bleManager.readLastCharacteristic();
        switch (result){
            case BleManager.CHARACTERISTIC_OPERATION_NULL:
                Logger.shortToast(this, "characteristic not set");
                break;
            case BleManager.CHARACTERISTIC_OPERATION_UNAVAILABLE:
                Logger.shortToast(this, "characteristic not readable");
                break;
        }
    }

    private void writeLastCharacteristic(){
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        Logger.showTextInputDialog(this, "Write to characteristic", "Write", input, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                String text = input.getText().toString();
                byte[] data = hexStringToByteArray(text);
                int result = bleManager.writeLastCharacteristic(data);
                switch (result){
                    case BleManager.CHARACTERISTIC_OPERATION_NULL:
                        Logger.shortToast(mainActivity, "characteristic not set");
                        break;
                    case BleManager.CHARACTERISTIC_OPERATION_UNAVAILABLE:
                        Logger.shortToast(mainActivity, "characteristic not writable");
                        break;
                }
            }
        });
    }

    private String byteArrayToHexString(final byte[] data) {
        String hexString = "";
        if (data != null && data.length > 0) {
            final StringBuilder stringBuilder = new StringBuilder(data.length);
            for (byte byteChar : data)
                stringBuilder.append(String.format("%02X ", byteChar));
            hexString = stringBuilder.toString();
        }
        return hexString;
    }

    public static byte[] hexStringToByteArray(String s) {
        if((s.length()%2)==1){
            s="0"+s;
        }
        int len = s.length();
        if(len==1){
            return new byte[]{Byte.parseByte(s,16)};
        }
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2){
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1),16));
        }
        return data;
    }
}
