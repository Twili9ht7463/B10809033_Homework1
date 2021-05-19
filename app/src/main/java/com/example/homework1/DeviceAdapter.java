package com.example.homework1;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeviceAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<DeviceAdapter.BLEDeviceViewHolder>{
    private ArrayList<BLEDevice> list;
    private HashMap<String, BLEDevice> hashMap;

    public DeviceAdapter(){
        list = new ArrayList<BLEDevice>();
        hashMap = new HashMap<String, BLEDevice>();
    }

    public void addDevice(String mac, String rssi, String content){
        if (hashMap.containsKey(mac)){
            return;
        }
        BLEDevice device = new BLEDevice();
        device.deviceName = mac;
        device.RSSI = rssi;
        device.content = content;
        Log.d("BLE",device.deviceName);
        list.add(0,device);
    }

    @NonNull
    @Override
    public BLEDeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        BLEDeviceViewHolder viewHolder = new BLEDeviceViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull BLEDeviceViewHolder holder, int position){
        Log.d("BLE", "bind" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount(){
        return list.size();
    }

    class BLEDeviceViewHolder extends RecyclerView.ViewHolder{
        TextView t1;
        TextView t2;
        TextView t3;

        public BLEDeviceViewHolder(@NonNull View itemView){
            super(itemView);
            t1 = itemView.findViewById(R.id.textView1);
            t2 = itemView.findViewById(R.id.textView2);
            t3 = itemView.findViewById(R.id.textView3);
        }

        public void bind(int index){
            BLEDevice device = list.get(index);
            t1.setText(device.deviceName);
            t2.setText(device.RSSI);
            t3.setText(device.content);

        }
    }
}
