package com.globant.bazzinga;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Message;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by ANONYMOUS on 8/15/2015.
 */
public class ConnectThread extends Thread {
    private BluetoothSocket mmSocket;
    private BluetoothAdapter myBluetoothAdapter;
    private BluetoothDevice mmDevice;
    Set<BluetoothDevice> pairedDevices;
    private Context context;
    private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private final String employeeID;
    public ConnectThread(String empData, Context context) {
        employeeID = empData;
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
    }

    //Search function to search Paired Server Device
    public BluetoothDevice search(String macAdd){
        pairedDevices = myBluetoothAdapter.getBondedDevices();
        BluetoothDevice dev=null;
        //find a device with server devices's mac address
        for(BluetoothDevice device : pairedDevices) {
            if (device.getAddress().toString().equals(macAdd)) {
                dev = device;
                return dev;
            }
        }
        throw null;
    }

    public void run(){
        if (!myBluetoothAdapter.isEnabled())
            myBluetoothAdapter.enable();

        while(!myBluetoothAdapter.isEnabled());//Waiting for bluetooth to turn on

        String macAdd="90:68:C3:48:EA:B1";  //Setting MAC Address of server
        BluetoothDevice mdevice;
        try {
            mdevice = search(macAdd);   //Searching for paired server device
        }catch(NullPointerException ne){
            Message msg = myHandler.obtainMessage(3, "Device Not connected");
            msg.sendToTarget();
            return;
        }

        BluetoothSocket temp=null;
        mmDevice = mdevice;

        //Creating Socket for connection
        try{
            temp = mdevice.createRfcommSocketToServiceRecord(MY_UUID);
            Thread.sleep(10);
        }catch(IOException e){}
        catch(Exception e){}

        mmSocket = temp;
        BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        myBluetoothAdapter.cancelDiscovery();
        try{
            mmSocket.connect();
        }catch(IOException e){
            try{
                mmSocket.close();
            }catch(IOException ee){}
            BluetoothAdapter bdapt = BluetoothAdapter.getDefaultAdapter();
            bdapt.disable();
            Message msg = myHandler.obtainMessage(2, "Device Not connected");
            msg.sendToTarget();
            return;
        }
        ConnectedThread c = new ConnectedThread(mmSocket,employeeID);
        c.start();
        try {
            c.join();
        }catch(Exception e){}

        return;
    }

    private class ConnectedThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private volatile boolean stop = false;
        private String empData;
        public ConnectedThread(BluetoothSocket socket,String data){
            mmSocket=socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;
            empData = data;

            try{
                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
            }catch(IOException e){}

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
                }   catch (Exception e){ }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while(mmInStream==null);
            try {
                byte [] xyz = new byte[1024];
                bytes = mmInStream.read(xyz);
                String m = new String(xyz);
                Message msg = myHandler.obtainMessage(1, m);
                msg.sendToTarget();

            } catch (IOException e) {
                e.printStackTrace();
            }
            BluetoothAdapter badapt = BluetoothAdapter.getDefaultAdapter();
            badapt.disable();
            return;
        }
        public void cancel(){
            try{
                mmSocket.close();
            }catch(IOException e){}
        }
    }

    public void cancel(){
        try{
            mmSocket.close();
        }catch(IOException e){}
    }

    private android.os.Handler myHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            String data = msg.obj.toString();
            switch(msg.what){
                case 1:
                    SharedPreferences pref = context.getSharedPreferences("EmployeeData", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    int a = pref.getInt("Status", 0);
                    editor.commit();
                    if (a == 0) {
                        editor.putInt("Status", 1);
                        editor.commit();
                        Toast.makeText(context, "Check Out Acknowledged", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        editor.putInt("Status", 0);
                        editor.commit();
                        Toast.makeText(context, "Check In Acknowledged", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    Toast.makeText(context, "Server device not found", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    Toast.makeText(context, "Server device not Paired", Toast.LENGTH_SHORT).show();
                    break;
            }
            super.handleMessage(msg);
        }
    };

}
