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
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
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

import com.example.anonymous.server.AddData;
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
            text.setTextColor(Color.DKGRAY);
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
            myBluetoothAdapter.enable();
            while (!myBluetoothAdapter.isEnabled()) ;  //wait till bluetooth is on
            //send it to a particular mac address
            //check for mac address of the main server enter it first
            ConnectThread mConnect = new ConnectThread();
            mConnect.start();
            // off();
        }
    }

    public void fname(View v) {
        Intent intent = new Intent(this, AddData.class);
        startActivity(intent);
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
        private String empid;
        private String status;
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

        void parseMsg(String msg){
            char m[]=msg.toCharArray();
            char emparray[] = new char [100];
            char st='\0';
            for (int i=0;i<m.length;i++){
                if(m[i]=='#'){
                    st=m[i+1];
                    break;
                }
                else
                    emparray[i]=m[i];
            }
            empid=String.valueOf(emparray);
            if(st=='0')
                status="OUT";
            else
                status="IN";
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes = 0;
            while (true) {
                try {
                    bytes = mmInStrteam.read(buffer);
                    String m = new String(buffer);
                    parseMsg(m);
                    ParseObject testObject = new ParseObject("EmployeeLog");
                    testObject.put("EmployeeID", empid);
                    Message msg = myHandler.obtainMessage(1, empid);
                    msg.sendToTarget();
                    String dType = "dd / MM / yyyy";
                    String tType = "HH:mm:ss";
                    //String s="MMM d, y, HH:mm";
                    SimpleDateFormat sdf = new SimpleDateFormat(dType);
                    SimpleDateFormat stf = new SimpleDateFormat(tType);
                    String Date = sdf.format(new Date());
                    String time = stf.format(new Date());
                    testObject.put("Date",Date);
                    testObject.put("Time",time);
                    testObject.put("Status",status);
                    testObject.saveInBackground();
                    testObject.saveEventually();
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