package com.example.anonymous.client2706;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

//imports for shared preferences
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CheckInOut extends ActionBarActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothDevice mdevice;
    private String name="Client";
    private String employeeid;
    Button checkInOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_out);

        Button button = (Button)findViewById(R.id.button);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref" , 0); //0 for private mode
        Editor editor = pref.edit();

        int a = pref.getInt("key_name",0);
        editor.commit();

        TextView show_status = (TextView)findViewById(R.id.show_status);


        if(a==0){
            show_status.setText("You are inside.");
            button.setText("out");
        }
        else
        {
            show_status.setText("You are outside.");
            button.setText("in");}

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        checkInOut = (Button)findViewById(R.id.button);
        String mac = myBluetoothAdapter.getAddress();
        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
        }
        Bundle bundle = getIntent().getExtras();
        employeeid=bundle.getString("EmployeeID");
    }

    public void sendMessage(View v) throws InterruptedException {
        // take an instance of BluetoothAdapter - Bluetooth radio

//        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//        checkInOut = (Button)findViewById(R.id.button);
        String mac = myBluetoothAdapter.getAddress();
        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
        }
        Bundle bundle = getIntent().getExtras();
        employeeid=bundle.getString("EmployeeID");

        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        }
        else {
            while(!myBluetoothAdapter.isEnabled());
            String macAdd="40:78:6A:BB:E3:64";
            try {
                mdevice = search(macAdd);
            }catch(NullPointerException ne){
                TextView tv = (TextView)findViewById(R.id.ack);
                tv.setText("NOT Connected to server");
                tv.setVisibility(View.VISIBLE);
            }

            SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref" , 0); //0 for private mode
            Editor editor = pref.edit();
            int a = pref.getInt("key_name",0);
            editor.commit();
            String msg = employeeid+"#"+String.valueOf(a);
            ConnectThread mConnect = new ConnectThread(mdevice,msg);
            mConnect.start();
            try {
                mConnect.join();
            }
            catch (InterruptedException e1) { }
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
                return dev;
            }
        }
        throw null;
    }

    public void off(){
        myBluetoothAdapter.disable();
        Toast.makeText(getApplicationContext(),"Bluetooth turned off",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check_in_out, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return super.onOptionsItemSelected(item);
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
                return;
            }
            ConnectedThread c = new ConnectedThread(mmSocket,employeeID);
            c.start();
            try {
                c.join();
            }catch(Exception e){}

            return;
        }
        public void cancel(){
            try{
                mmSocket.close();
            }catch(IOException e){}
        }
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
                    //Thread.sleep(300);
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
                //       break;
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
            TextView tv = (TextView)findViewById(R.id.ack);
            tv.setText("Acknowledged by server");
            tv.setTextColor(Color.BLUE);
            tv.setVisibility(View.VISIBLE);

            Button button = (Button)findViewById(R.id.button);
            TextView show_status = (TextView)findViewById(R.id.show_status);
            show_status.setTextColor(Color.BLUE);
            show_status.setVisibility(View.VISIBLE);
            SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref" , 0); //0 for private mode
            SharedPreferences.Editor editor = pref.edit();
            int a=pref.getInt("key_name",0);
            editor.commit();
            if(a==0) {
                editor.putInt("key_name", 1);
                editor.commit();
                show_status.setText("You got out");
                button.setText("IN");
            }
            else
            {
                editor.putInt("key_name", 0);
                editor.commit();
                show_status.setText("You got in");
                button.setText("OUT");
            }
        }
    };
}
