package com.example.parcial3.adapters;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.graphics.Color;
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
        boolean colored = isCharacteristicLastOne(characteristic);

        String name = "char "+(position+1);
        setTextToTextView(rowView, R.id.characteristic_list_item_text_view, name, colored);

        String properties = getCharacteristicProperties(characteristic);
        setTextToTextView(rowView, R.id.characteristic_list_item_text_view2, properties, colored);

        String characteristicUuid = characteristic.getUuid().toString();
        setTextToTextView(rowView, R.id.characteristic_list_item_text_view3, characteristicUuid, colored);

        setOnLongClickListenerToTextView(rowView, R.id.characteristic_list_item_text_view);
        setOnLongClickListenerToTextView(rowView, R.id.characteristic_list_item_text_view2);
        setOnLongClickListenerToTextView(rowView, R.id.characteristic_list_item_text_view3);

        return rowView;
    }

    private void setTextToTextView(View rowView, int textViewId, String text, boolean colored){
        TextView txtView = rowView.findViewById(textViewId);
        txtView.setText(text);
        if (colored){
            txtView.setTextColor(Color.RED);
        }
    }

    private void setOnLongClickListenerToTextView(View rowView, int textViewId){
        rowView.findViewById(textViewId).setOnLongClickListener(getOnLongClickListener());
    }

    private View.OnLongClickListener getOnLongClickListener(){
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String characteristicUuid = ((TextView) ((View)view.getParent()).findViewById(R.id.characteristic_list_item_text_view3)).getText()+"";
                bleManager.setDescriptors(characteristicUuid);
                mainActivity.setDescriptorsAdapter();
                return true;
            }
        };
    }

    private String getCharacteristicProperties(BluetoothGattCharacteristic characteristic){
        String properties = "";
        if(bleManager.isCharacteristicWriteable(characteristic)){
            properties += "W";
        }
        if(bleManager.isCharacteristicReadable(characteristic)){
            properties += "R";
        }
        if(bleManager.isCharacteristicNotifiable(characteristic)){
            properties += "N";
        }
        if(bleManager.isCharacteristicIndicable(characteristic)){
            properties += "I";
        }
        if(bleManager.isCharacteristicExtendable(characteristic)){
            properties += "E";
        }
        if(bleManager.isCharacteristicBroadcastable(characteristic)){
            properties += "B";
        }
        if(properties.equals("")){
            return characteristic.getProperties()+"";
        }else{
            return properties;
        }
    }

    private boolean isCharacteristicLastOne(BluetoothGattCharacteristic characteristic){
        BluetoothGattCharacteristic lastCharacteristic = bleManager.lastCharacteristic;
        if (lastCharacteristic == null){
            return false;
        } else {
            return lastCharacteristic.getUuid().toString().equals(characteristic.getUuid().toString());
        }
    }
}