package com.javacodegeeks.android.bluetoothtest;

import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Handler;
import android.os.Message;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.Parse;
import com.parse.ParseObject;

import java.util.UUID;
import java.util.logging.LogRecord;

public class MainActivity extends Activity {
    public Handler myHandler = new Handler(){
        @Override
        public void handleMessage(Message msg){
            String data = msg.obj.toString();
            TextView t = (TextView)findViewById(R.id.textView);
            t.setText(data);
            Button b = (Button)findViewById(R.id.button);
            b.setBackgroundColor(Color.CYAN);
            ParseObject testObject = new ParseObject("EmployeeData");
            testObject.put("empid", data);
            testObject.saveInBackground();
        }
    };
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
    private String name="Server";
    private String message;
    private ArrayAdapter<String> messageAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

// Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "ecNYEdsTREI9Mwzx5gWOoh2HB9V78KvVWe8W8iIA", "YHuKHkJdjm4gSdl6lrZavY9Sdx06Da1DPNNXy40p");

        // take an instance of BluetoothAdapter - Bluetooth radio
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter == null) {

            Toast.makeText(getApplicationContext(),"Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        } else {
            //writre onclick listener here
            on();
            while(!myBluetoothAdapter.isEnabled());  //wait till bluetooth is on
            //send it to a particular mac address
            //check for mac address of the main server enter it first

            ConnectThread mConnect = new ConnectThread();
            mConnect.start();
            // off();
        }
    }

    public void fname(View v){
        TextView t = (TextView)findViewById(R.id.textView);
        t.setText(message);
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
        BluetoothDevice d=null;
        return d;
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
        private final BluetoothServerSocket mmSocket;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

        public ConnectThread(){
            BluetoothServerSocket temp=null;
            try{
                temp = myBluetoothAdapter.listenUsingRfcommWithServiceRecord(name, MY_UUID);
            }catch(IOException e){}
            mmSocket = temp;
        }

        public void run(){
            BluetoothSocket socket=null;
            while(true){
                try{
                    socket=mmSocket.accept();
                }catch(IOException e){System.out.println(e);}
                if(socket!=null){

                    ConnectedThread mConnection = new ConnectedThread(socket);
                    mConnection.start();
                    /*try{
                        mmSocket.close();
                    }catch(IOException e){}*/
                }
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }

    private class ConnectedThread extends Thread{
        private final InputStream mmInStrteam;
        private final OutputStream mmOutStream;
        private final BluetoothSocket mmSocket;

        public ConnectedThread(BluetoothSocket socket){
            mmSocket = socket;
            InputStream tempIn=null;
            OutputStream tempOut=null;
            try{
                tempIn = socket.getInputStream();
                tempOut=socket.getOutputStream();
            }catch (IOException e){}
            mmInStrteam = tempIn;
            mmOutStream = tempOut;
        }

        public void run(){
            byte [] buffer = new byte[1024];
            int bytes=0;
            while(true){
                try{
                    bytes=mmInStrteam.read(buffer);
                    String m = new String(buffer);
                    Message msg = myHandler.obtainMessage(1,m);
                    msg.sendToTarget();
                }catch(IOException e){break;}
            }

        }

        public void cancel(){
            try{
                mmSocket.close();
            }catch(IOException e){}
        }
    }

}