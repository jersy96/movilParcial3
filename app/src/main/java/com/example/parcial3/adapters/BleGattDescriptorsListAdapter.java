package com.example.parcial3.adapters;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
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

public class BleGattDescriptorsListAdapter extends ArrayAdapter<BluetoothGattDescriptor> {
    private final Context context;
    private MainActivity mainActivity;
    private ArrayList<BluetoothGattDescriptor> descriptors;
    private final BleManager bleManager;

    public BleGattDescriptorsListAdapter(@NonNull Context context, BleManager bleManager, MainActivity mainActivity) {
        super(context, R.layout.descriptor_list_item, bleManager.descriptors);
        this.context = context;
        this.mainActivity=mainActivity;
        this.bleManager = bleManager;
        this.descriptors = bleManager.descriptors;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = mainActivity.getLayoutInflater();

        View rowView= inflater.inflate(R.layout.descriptor_list_item, null, true);

        BluetoothGattDescriptor descriptor = descriptors.get(position);

        String name = "descriptor "+(position+1);
        setTextToTextView(rowView, R.id.descriptor_list_item_text_view, name);

        String descriptorUuid = descriptor.getUuid().toString();
        setTextToTextView(rowView, R.id.descriptor_list_item_text_view2, descriptorUuid);

        return rowView;
    }

    private void setTextToTextView(View rowView, int textViewId, String text){
        TextView txtView = rowView.findViewById(textViewId);
        txtView.setText(text);
    }
}
