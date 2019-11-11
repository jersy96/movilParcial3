package com.example.parcial3.adapters;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.parcial3.MainActivity;
import com.example.parcial3.R;
import com.example.parcial3.ble.BleManager;

import java.util.ArrayList;


public class BleGattCharacteristicsListAdapter extends ArrayAdapter<BluetoothGattCharacteristic> {
    private final Context context;
    private MainActivity mainActivity;
    private ArrayList<BluetoothGattCharacteristic> characteristics;
    private final BleManager bleManager;

    public BleGattCharacteristicsListAdapter(@NonNull Context context, BleManager bleManager, MainActivity mainActivity) {
        super(context, R.layout.characteristic_list_item, bleManager.characteristics);
        this.context = context;
        this.mainActivity=mainActivity;
        this.bleManager = bleManager;
        this.characteristics = bleManager.characteristics;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = mainActivity.getLayoutInflater();

        View rowView= inflater.inflate(R.layout.characteristic_list_item, null, true);

        BluetoothGattCharacteristic characteristic = characteristics.get(position);

        String properties = getCharacteristicProperties(characteristic);
        setTextToTextView(rowView, R.id.characteristic_list_item_text_view, properties);

        String characteristicUuid = characteristic.getUuid().toString();
        setTextToTextView(rowView, R.id.characteristic_list_item_text_view2, characteristicUuid);

        setOnLongClickListenerToTextView(rowView, R.id.characteristic_list_item_text_view);
        setOnLongClickListenerToTextView(rowView, R.id.characteristic_list_item_text_view2);

        return rowView;
    }

    private void setTextToTextView(View rowView, int textViewId, String text){
        TextView txtView = rowView.findViewById(textViewId);
        txtView.setText(text);
    }

    private void setOnLongClickListenerToTextView(View rowView, int textViewId){
        rowView.findViewById(textViewId).setOnLongClickListener(getOnLongClickListener());
    }

    private View.OnLongClickListener getOnLongClickListener(){
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String characteristicUuid = ((TextView) ((View)view.getParent()).findViewById(R.id.characteristic_list_item_text_view2)).getText()+"";
                bleManager.setDescriptors(characteristicUuid);
                mainActivity.setDescriptorsAdapter();
                return true;
            }
        };
    }

    private boolean isCharacteristicWriteable(BluetoothGattCharacteristic characteristic) {
        return (characteristic.getProperties() &
                (BluetoothGattCharacteristic.PROPERTY_WRITE
                        | BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE
                        | BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE)) != 0;
    }

    private boolean isCharacteristicReadable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_READ) != 0);
    }

    private boolean isCharacteristicNotifiable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0);
    }

    private boolean isCharacteristicIndicable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0);
    }

    private boolean isCharacteristicExtendable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS) != 0);
    }

    private boolean isCharacteristicBroadcastable(BluetoothGattCharacteristic characteristic) {
        return ((characteristic.getProperties() & BluetoothGattCharacteristic.PROPERTY_BROADCAST) != 0);
    }

    private String getCharacteristicProperties(BluetoothGattCharacteristic characteristic){
        String properties = "";
        if(isCharacteristicWriteable(characteristic)){
            properties += "W";
        }
        if(isCharacteristicReadable(characteristic)){
            properties += "R";
        }
        if(isCharacteristicNotifiable(characteristic)){
            properties += "N";
        }
        if(isCharacteristicIndicable(characteristic)){
            properties += "I";
        }
        if(isCharacteristicExtendable(characteristic)){
            properties += "E";
        }
        if(isCharacteristicBroadcastable(characteristic)){
            properties += "B";
        }
        if(properties.equals("")){
            return characteristic.getProperties()+"";
        }else{
            return properties;
        }
    }
}