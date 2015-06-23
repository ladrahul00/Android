package com.javacodegeeks.android.bluetoothtest;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.*;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import android.util.Log;

public class MainActivity extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;
    private Button onBtn;
    private Button offBtn;
    private Button listBtn;
    private Button findBtn;
    private TextView text;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView myListView;
    private ArrayAdapter<String> BTArrayAdapter;
    private BluetoothDevice mdevice;
    private String name="Client";
    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void sendMessage(View v){
        // take an instance of BluetoothAdapter - Bluetooth radio
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        } else {
            //write onclick listener here
            on();
            //send it to a particular mac address
            //check for mac address of the main server enter it first
            while(!myBluetoothAdapter.isEnabled());  //wait till bluetooth is on
            String macAdd="40:78:6A:BB:E3:64";
            EditText e = (EditText)findViewById(R.id.empID);
            String employeeid = e.getText().toString();
            mdevice = search(macAdd);
            ConnectThread mConnect = new ConnectThread(mdevice,employeeid);
            mConnect.start();
            //try {
             //   mConnect.join();
            //} catch (InterruptedException e1) { }
        }
    }

    public void on(){
        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

            Toast.makeText(getApplicationContext(),"Bluetooth turned on" ,
                    Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if(requestCode == REQUEST_ENABLE_BT){
            if(myBluetoothAdapter.isEnabled()) {
                text.setText("Status: Enabled");
            } else {
                text.setText("Status: Disabled");
            }
        }
    }

    public void list(){
        // get paired devices
        pairedDevices = myBluetoothAdapter.getBondedDevices();
        //find a device with server devices's mac address
        // put it's one to the adapter
        for(BluetoothDevice device : pairedDevices)
            BTArrayAdapter.add(device.getName()+ "\n" + device.getAddress());

        Toast.makeText(getApplicationContext(),"Show Paired Devices",
                Toast.LENGTH_SHORT).show();

    }

    final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name and the MAC address of the object to the arrayAdapter
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    public void find() {
        if (myBluetoothAdapter.isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            myBluetoothAdapter.cancelDiscovery();
        }
        else {
            BTArrayAdapter.clear();
            myBluetoothAdapter.startDiscovery();
            registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        }
    }

    public BluetoothDevice search(String macAdd){
        pairedDevices = myBluetoothAdapter.getBondedDevices();
        BluetoothDevice dev=null;
        //find a device with server devices's mac address
        // put it's one to the adapter
        for(BluetoothDevice device : pairedDevices) {
            if (device.getAddress().toString().equals(macAdd)) {
                dev = device;
                break;
            }
        }
        return dev;
    }

    public void off(){
        myBluetoothAdapter.disable();
        text.setText("Status: Disconnected");

        Toast.makeText(getApplicationContext(),"Bluetooth turned off",
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(bReceiver);
    }

    //Thread to establish connection
    private class ConnectThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        private final String employeeID;

        public ConnectThread(BluetoothDevice device,String empData){
            BluetoothSocket temp=null;
            mmDevice = device;
            employeeID=empData;
            try{
                temp = device.createRfcommSocketToServiceRecord(MY_UUID);
            }catch(IOException e){Log.d(TAG, "socket not created",e);}
            mmSocket = temp;
        }

        public void run(){
            myBluetoothAdapter.cancelDiscovery();
            try{
                mmSocket.connect();
            }catch(IOException e){
                try{
                    mmSocket.close();
                }catch(IOException ee){Log.d(TAG, "socket not closed",ee);}
                return;
            }
            ConnectedThread c = new ConnectedThread(mmSocket,employeeID);
            c.start();
            //try {
            //    c.join();
            //}catch(Exception e){}
            return;
        }

        public void cancel(){
            try{
                mmSocket.close();
            }catch(IOException e){Log.d(TAG, "socket not closed of connect thread",e);}

        }

    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private String empData;
        public ConnectedThread(BluetoothSocket socket,String data){
            mmSocket=socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;
            empData = data;

            try{
                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
            }catch(IOException e){Log.d(TAG, "temp in or temp out not created",e);}

            mmInStream = tempIn;
            mmOutStream = tempOut;
        }

        public void run(){
            byte[] buffer = empData.getBytes();
            int bytes;
            while(true){
                try{
                    mmOutStream.write(buffer);
                    break;
                }   catch (IOException e){Log.d(TAG, "write error",e); }
            }
            cancel();
            off();

        }

        public void cancel(){
            try{
                mmSocket.close();
            }catch(IOException e){Log.d(TAG, "socket not closed",e);}

        }

    }

  }

