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
    Button checkInOut;
    String employeeid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_out);
        checkInOut = (Button) findViewById(R.id.button);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0); //0 for private mode
        Editor editor = pref.edit();

        //Get previous status of employee weather he is IN or OUT
        int a = pref.getInt("key_name", 0);
        editor.commit();

        employeeid = pref.getString("EmployeeIDKey", "BLANK");
        editor.commit();

        //Welcome EmployeeID set Text
        TextView empid = (TextView) findViewById(R.id.employeeId);
        empid.setText(employeeid);

        TextView show_status = (TextView) findViewById(R.id.show_status);
        Button button = (Button) findViewById(R.id.button);

        if (a == 0) {//If Employee is Onside
            show_status.setText("You are inside.");
            show_status.setTextColor(Color.WHITE);
            button.setText("out");
        } else//If Employee is Outside
        {
            show_status.setText("You are outside.");
            show_status.setTextColor(Color.WHITE);
            button.setText("in");
        }
    }

    public void sendMessage(View v) throws InterruptedException {
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        }
        else {
            if (!myBluetoothAdapter.isEnabled())
                myBluetoothAdapter.enable();

            while(!myBluetoothAdapter.isEnabled());//Waiting for bluetooth to turn on

            String macAdd="90:68:C3:48:EA:B1";  //Setting MAC Address of server

            try {
                mdevice = search(macAdd);   //Searching for paired server device
            }catch(NullPointerException ne){
                Toast.makeText(getApplicationContext(), "Not Connected to server",
                        Toast.LENGTH_LONG).show();
            }

            SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0); //0 for private mode
            SharedPreferences.Editor editor = pref.edit();
            int a = pref.getInt("key_name", 0);
            editor.commit();

            //Message to be sent to server
            String msg = employeeid+"#"+String.valueOf(a);

            ConnectThread mConnect = new ConnectThread(mdevice,msg);       //Bluetooth connection Thread
            mConnect.start();   //Starting server

            try {
                mConnect.join();
            }
            catch (InterruptedException e1) { }
        }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_check_in_out, menu);
        return true;
    }


    //Thread to establish connection
    public class ConnectThread extends Thread{
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        private final String employeeID;
        public ConnectThread(BluetoothDevice device,String empData){
            BluetoothSocket temp=null;
            mmDevice = device;
            employeeID=empData;

            //Creating Socket for connection
            try{
                temp = device.createRfcommSocketToServiceRecord(MY_UUID);
                Thread.sleep(10);
            }catch(IOException e){}
            catch(Exception e){}
            mmSocket = temp;
        }
        public void run(){
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

    public class ConnectedThread extends Thread{
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
 /*           TextView tv = (TextView)findViewById(R.id.ack);
            tv.setText("Acknowledged by server");
            tv.setTextColor(Color.WHITE);

            tv.setVisibility(View.VISIBLE);
*/
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
                show_status.setTextColor(Color.WHITE);

                button.setText("IN");
            }
            else
            {
                editor.putInt("key_name", 0);
                editor.commit();
                show_status.setText("You got in");
                show_status.setTextColor(Color.WHITE);
                button.setText("OUT");
            }
        }
    };
}
