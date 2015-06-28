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
    public Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String data = msg.obj.toString();
            text = (TextView) findViewById(R.id.textView);
            text.setText(data);
            Button b = (Button) findViewById(R.id.button);
            b.setBackgroundColor(Color.CYAN);
        }
    };
    private static final int REQUEST_ENABLE_BT = 1;
    private TextView text;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothDevice mdevice;
    private String name = "Server";
    private String message;
    //private String status = "Successful!";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        text = (TextView) findViewById(R.id.textView);
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "ecNYEdsTREI9Mwzx5gWOoh2HB9V78KvVWe8W8iIA", "YHuKHkJdjm4gSdl6lrZavY9Sdx06Da1DPNNXy40p");
        // take an instance of BluetoothAdapter - Bluetooth radio
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        } else {
            //write onclick listener here
            on();
            while (!myBluetoothAdapter.isEnabled()) ;  //wait till bluetooth is on
            //send it to a particular mac address
            //check for mac address of the main server enter it first
            ConnectThread mConnect = new ConnectThread();
            mConnect.start();
            ConnectThread mConnect1 = new ConnectThread();
            mConnect1.start();
            // off();
        }
    }

    public void fname(View v) {
        TextView t = (TextView) findViewById(R.id.textView);
        t.setText(message);
    }

    public void on() {
        if (!myBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);

            Toast.makeText(getApplicationContext(), "Bluetooth turned on",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Bluetooth is already on",
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void off() {
        myBluetoothAdapter.disable();
        text.setText("Status: Disconnected");

        Toast.makeText(getApplicationContext(), "Bluetooth turned off",
                Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
//        unregisterReceiver(bReceiver);
    }

    //Thread to establish connection
    private class ConnectThread extends Thread {
        private final BluetoothServerSocket mmSocket;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        public ConnectThread() {
            BluetoothServerSocket temp = null;
            try {
                temp = myBluetoothAdapter.listenUsingRfcommWithServiceRecord(name, MY_UUID);
            } catch (IOException e) {
            }
            mmSocket = temp;
        }
        public void run() {
            BluetoothSocket socket = null;
            while (true) {
                try {
                    socket = mmSocket.accept();
                } catch (IOException e) {
                    System.out.println(e);
                }
                if (socket != null) {
                    ConnectedThread mConnection = new ConnectedThread(socket);
                    mConnection.start();
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

    private class ConnectedThread extends Thread {
        private InputStream mmInStrteam;
        private OutputStream mmOutStream;
        private final BluetoothSocket mmSocket;
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tempIn = null;
            OutputStream tempOut = null;
            try {
                tempIn = socket.getInputStream();
                tempOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStrteam = tempIn;
            mmOutStream = tempOut;
        }
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes = 0;
            while (true) {
                try {
                    bytes = mmInStrteam.read(buffer);
                    String m = new String(buffer);
                    Message msg = myHandler.obtainMessage(1, m);
                    msg.sendToTarget();
                    ParseObject testObject = new ParseObject("xEmpData");
                    testObject.put("empid", m);
                    testObject.saveInBackground();
                    byte [] xyz = "Successful".getBytes();
                    try {
                        mmOutStream.write(xyz);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    break;
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
}