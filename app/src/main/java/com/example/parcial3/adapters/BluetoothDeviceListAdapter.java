package com.example.parcial3.adapters;

import android.bluetooth.le.ScanResult;
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

import java.util.List;


public class BluetoothDeviceListAdapter extends ArrayAdapter<ScanResult> {
    private final Context context;
    private MainActivity mainActivity;
    private List<ScanResult> scanResultList;
    private final BleManager bleManager;

    public BluetoothDeviceListAdapter(@NonNull Context context, BleManager bleManager, MainActivity mainActivity) {
        super(context, R.layout.device_list_item, bleManager.scanResults);
        this.context = context;
        this.mainActivity=mainActivity;
        this.bleManager = bleManager;
        this.scanResultList = bleManager.scanResults;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = mainActivity.getLayoutInflater();

        View rowView= inflater.inflate(R.layout.device_list_item, null, true);

        String deviceAddress = scanResultList.get(position).getDevice().getAddress();
        setTextToTextView(rowView, R.id.device_list_item_text_view, deviceAddress);

        String deviceName = scanResultList.get(position).getDevice().getName();
        setTextToTextView(rowView, R.id.device_list_item_text_view2, deviceName);

        int deviceRssi = scanResultList.get(position).getRssi();
        setTextToTextView(rowView, R.id.device_list_item_text_view3, deviceRssi+" dBm");

        ((TextView)rowView.findViewById(R.id.device_list_item_text_view)).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String address=((TextView) view.findViewById(R.id.device_list_item_text_view)).getText()+"";
                bleManager.connectToGattServer(address);
                return true;
            }
        });

        return rowView;
    }

    private void setTextToTextView(View rowView, int textViewId, String text){
        TextView txtView = (TextView) rowView.findViewById(textViewId);
        txtView.setText(text);
    }
}