package com.example.parcial3.adapters;

import android.bluetooth.BluetoothGattService;
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

public class BleGattServicesListAdapter extends ArrayAdapter<BluetoothGattService> {
    private final Context context;
    private MainActivity mainActivity;
    private ArrayList<BluetoothGattService> services;
    private final BleManager bleManager;

    public BleGattServicesListAdapter(@NonNull Context context, BleManager bleManager, MainActivity mainActivity) {
        super(context, R.layout.device_list_item, bleManager.services);
        this.context = context;
        this.mainActivity=mainActivity;
        this.bleManager = bleManager;
        this.services = bleManager.services;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = mainActivity.getLayoutInflater();

        View rowView= inflater.inflate(R.layout.service_list_item, null, true);

        String serviceUuid = services.get(position).getUuid().toString();
        setTextToTextView(rowView, R.id.service_list_item_text_view, serviceUuid);

        return rowView;
    }

    private void setTextToTextView(View rowView, int textViewId, String text){
        TextView txtView = (TextView) rowView.findViewById(textViewId);
        txtView.setText(text);
    }
}
