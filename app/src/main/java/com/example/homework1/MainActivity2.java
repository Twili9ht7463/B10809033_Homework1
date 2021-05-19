package com.example.homework1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;

import android.Manifest;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity2 extends AppCompatActivity {

    private LinearLayoutManager mLinearLayoutManager;
    private BluetoothManager mBluetoothManager = null;
    private BluetoothAdapter mBluetoothAdapter = null;
    private BluetoothLeScanner mBluetoothLeScanner = null;
    private RecyclerView mRecyclerView;
    private DeviceAdapter mResultAdapter;
    private boolean scanning = false;
    private Button button;
    private static final int PERMISSION_REQUEST_CODE = 420;

    private final static String[] permissionsWeNeed = new String[]{
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    private void setupPermissions(){
        boolean isGranted = true;
        for (String permission : permissionsWeNeed){
            isGranted &= ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        }

        if (!isGranted){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                requestPermissions(permissionsWeNeed, PERMISSION_REQUEST_CODE);
            }
            else{
                Toast.makeText(this, "No permission", Toast.LENGTH_LONG).show();
                finishAndRemoveTask();
            }
        }
        else{
            initBluetooth();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults){
        switch (requestCode){
            case PERMISSION_REQUEST_CODE:{
                boolean isGranted = grantResults.length > 0;
                for (int grantResult : grantResults){
                    isGranted &= grantResult == PackageManager.PERMISSION_GRANTED;
                }
                if (isGranted){
                    initBluetooth();
                }
                else{
                    Toast.makeText(this, "No permission",Toast.LENGTH_LONG).show();
                    finishAndRemoveTask();
                }
            }
        }
    }

    private void initBluetooth(){
        boolean success = false;
        mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        if (mBluetoothManager != null){
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter != null){
                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                Toast.makeText(this, "Bluetooth function started",Toast.LENGTH_LONG).show();
                success = true;
            }
        }

        if (!success){
            Toast.makeText(this, "Can't start bluetooth function",Toast.LENGTH_LONG).show();
            finishAndRemoveTask();
        }
    }

    private final ScanCallback startScanCallback = new ScanCallback(){
        @Override
        public void onScanResult(int callbackType, ScanResult result){
            BluetoothDevice device = result.getDevice();
            ScanRecord mScanRecord = result.getScanRecord();
            String address = device.getAddress();
            byte[] content = mScanRecord.getBytes();
            int flag = mScanRecord.getAdvertiseFlags();
            int mRssi = result.getRssi();
            String dataS = byteArrayToHexString(content);
            if (address == null || address.trim().length() == 0) return;
            mResultAdapter.addDevice(address, ""+mRssi, dataS);
            Log.d("Test","devices added");
            mResultAdapter.notifyDataSetChanged();
        }
    };


    public static String byteArrayToHexString(byte[] bytes){
        final char[] hexArray = {'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
        char[] hexChars = new char[bytes.length * 2]; // Each byte has two hex characters (nibbles)
        int x;
        for (int j = 0; j < bytes.length; j++) {
            x = bytes[j] & 0xFF; // Cast bytes[j] to int, treating as unsigned value
            hexChars[j * 2] = hexArray[x >>> 4]; // Select hex character from upper nibble
            hexChars[j * 2 + 1] = hexArray[x & 0x0F]; // Select hex character from lower nibble
        }
        return new String(hexChars);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        setupPermissions();
        mRecyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        mResultAdapter = new DeviceAdapter();
        mLinearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setAdapter(mResultAdapter);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        button = (Button)findViewById(R.id.button3);
        button.setOnClickListener(v -> {
            if (scanning == false){
                scanning = true;
                mBluetoothLeScanner.startScan(startScanCallback);
                button.setText("STOP");
            }
            else{
                scanning = false;
                mBluetoothLeScanner.stopScan(startScanCallback);
                button.setText("SCAN");
            }
        });
    }
}