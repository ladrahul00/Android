package com.globant.client;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;


public class WidgetService extends Service {
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothDevice mdevice;
    final private int SEND_ACK = 1;
    final private int SEND_STATE = 2;
    public void onCreate(){
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(getApplicationContext(), "On Start command", Toast.LENGTH_SHORT).show();
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!myBluetoothAdapter.isEnabled())
            myBluetoothAdapter.enable();
        while(!myBluetoothAdapter.isEnabled());
        SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref",0);
        SharedPreferences.Editor editor = pref.edit();
        //myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String macAdd="90:68:C3:48:EA:B1";
        mdevice = search(macAdd);
        int a=pref.getInt("key_name",0);
        editor.commit();
        String empid = pref.getString("EmployeeIDKey","blank");
        editor.commit();
        if(empid.equals("blank")){
            Toast.makeText(getApplicationContext(), "Sign-In to proceed", Toast.LENGTH_SHORT).show();
            Intent intentMain = new Intent(WidgetService.this,MainActivityClient.class);
            this.startActivity(intentMain);
        }
        else {
            String msg = empid + "#" + String.valueOf(a);
            ConnectWidgetThread connectWidgetThread = new ConnectWidgetThread(mdevice, msg);
            connectWidgetThread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BluetoothDevice search(String macAdd)
    {
        pairedDevices = myBluetoothAdapter.getBondedDevices();
        BluetoothDevice dev=null;
        for(BluetoothDevice device : pairedDevices)
        {
            if (device.getAddress().toString().equals(macAdd))
            {
                dev = device;
                return dev;
            }
        }
        throw null;
    }


    public class ConnectWidgetThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        private String employeeID;
        ConnectWidgetThread(BluetoothDevice mdevice,String msg){
            BluetoothSocket temp=null;
            mmDevice = mdevice;
            employeeID=msg;
            try{
                temp = mdevice.createRfcommSocketToServiceRecord(MY_UUID);
                Thread.sleep(10);
            }catch(IOException e){}
            catch(Exception e){}
            mmSocket = temp;
        }

        public void run(){
            myBluetoothAdapter.cancelDiscovery();
            try{
                mmSocket.connect();
            }catch(IOException e){
                try{
                    mmSocket.close();
                }catch(IOException ee){}
                BluetoothAdapter badapt = BluetoothAdapter.getDefaultAdapter();
                badapt.disable();
                Message msg = myHandler.obtainMessage(0, "Device Not connected");
                msg.sendToTarget();
                return;
            }
            ConnectedWidgetThread connectedWidgetThread = new ConnectedWidgetThread(mmSocket,employeeID);
            connectedWidgetThread.start();
            return;
        }
        public void cancel(){
            try{
                mmSocket.close();
            }catch(IOException e){}
        }
    }

    public class ConnectedWidgetThread extends Thread
    {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private volatile boolean stop = false;
        private String empData;
        public ConnectedWidgetThread(BluetoothSocket socket,String data){
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
                //msg.what=SEND_ACK;
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
    public android.os.Handler myHandler = new android.os.Handler() {
        @Override
        public void handleMessage(Message msg) {
            String data = msg.obj.toString();
            switch(msg.what) {
                case 1:
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0); //0 for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    int a = pref.getInt("key_name", 0);
                    editor.commit();
                    if (a == 0) {
                        editor.putInt("key_name", 1);
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "Check Out Acknowledged", Toast.LENGTH_SHORT).show();
                    } else {
                        editor.putInt("key_name", 0);
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "Check In Acknowledged", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 0:
                    Toast.makeText(getApplicationContext(), "Server device not found", Toast.LENGTH_SHORT).show();
                    break;
            }
            //widget.LOCK.release();
            Context context = getApplicationContext();
            int layoutID=R.layout.widget;
            RemoteViews remoteViews1 = new RemoteViews(context.getPackageName(), layoutID);
            remoteViews1.setOnClickPendingIntent(R.id.imageButton, widget.buildButtonPendingIntent(context));
            widget.pushWidgetUpdate(context, remoteViews1);
        }
    };
}