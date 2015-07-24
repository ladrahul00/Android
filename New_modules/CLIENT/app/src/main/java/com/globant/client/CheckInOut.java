package com.globant.client;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
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
    //Button checkInOut;
    String employeeid;
    ProgressWheel pw;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_out);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0); //0 for private mode
        Editor editor = pref.edit();
        //Get previous status of employee weather he is IN or OUT
        int a = pref.getInt("key_name", 0);
        editor.commit();

        employeeid = pref.getString("EmployeeIDKey", "BLANK");
        editor.commit();
        String employeename = pref.getString("EmployeeName", "BLANK");
        editor.commit();
        //Welcome EmployeeID set Text
        TextView empname = (TextView) findViewById(R.id.employeename);
        empname.setText(employeename);

        pw = (ProgressWheel)findViewById(R.id.pw_spinner1);
        pw.progress=0;
        pw.stopSpinning();
        pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw.spin(true);
                    sendMessage();
            }
        });
        if (a == 0) {//If Employee is Onside
            pw.setText("Exit");
            pw.setTextSize(30);
        } else//If Employee is Outside
        {
            pw.setText("Enter");
            pw.setTextSize(30);
        }
        PrintMessage();
    }

    public void PrintMessage(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0); //0 for private mode
        SharedPreferences.Editor editor = pref.edit();
        int a = pref.getInt("key_name", 0);
        editor.commit();

        if(a==0) {
            String weekDay = "";
            Calendar cal = Calendar.getInstance();
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (Calendar.MONDAY == dayOfWeek) weekDay = "Okay Monday, Let’s do this!";
            else if (Calendar.TUESDAY == dayOfWeek)
                weekDay = "Its only Tuesday and you're almost done with 95% of the week!";
            else if (Calendar.WEDNESDAY == dayOfWeek)
                weekDay = "Keep calm you're halfway through!!";
            else if (Calendar.THURSDAY == dayOfWeek)
                weekDay = "Better days are just around the corner, They are Friday, Saturday and Sunday";
            else if (Calendar.FRIDAY == dayOfWeek) weekDay = "Thank god it’s Friday";
            else if (Calendar.SATURDAY == dayOfWeek)
                weekDay = "I love working “Saturday” said no one ever.";
            else if (Calendar.SUNDAY == dayOfWeek) weekDay = "Happy Sunday!";
            TextView msg = (TextView) findViewById(R.id.Message);
            msg.setText(weekDay);
        }
        else
        {
            String weekDay = "";
            Calendar cal = Calendar.getInstance();
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (Calendar.MONDAY == dayOfWeek) weekDay = "Its Monday Don’t forget to be awesome";
            else if (Calendar.TUESDAY == dayOfWeek)
                weekDay = "Take off Tuesday!!";
            else if (Calendar.WEDNESDAY == dayOfWeek)
                weekDay = "Have a bright and beautiful Wednesday";
            else if (Calendar.THURSDAY == dayOfWeek)
                weekDay = "You say Thursday, I say Its Friday eve.";
            else if (Calendar.FRIDAY == dayOfWeek) weekDay = "Ready for the weekend??";
            else if (Calendar.SATURDAY == dayOfWeek)
                weekDay = "If you can’t be bothered to work on Saturday, Don’t bother to come in on Sunday";
            else if (Calendar.SUNDAY == dayOfWeek) weekDay = "Finally time to rest :P";
            TextView msg = (TextView) findViewById(R.id.Message);
            msg.setText(weekDay);
        }
    }

    public void sendMessage(){
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        }
        else {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0); //0 for private mode
            SharedPreferences.Editor editor = pref.edit();
            int a = pref.getInt("key_name", 0);
            editor.commit();

            //Message to be sent to server
            String msg = employeeid+"#"+String.valueOf(a);

            ConnectThread mConnect = new ConnectThread(msg);       //Bluetooth connection Thread
            mConnect.start();   //Starting server

        }
    }

    public void logOut(View view){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0);
        pref.edit().clear().commit();
        Intent intent = new Intent(this,MainActivityClient.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }


    //Thread to establish connection
    public class ConnectThread extends Thread{
        private BluetoothSocket mmSocket;
        private BluetoothDevice mmDevice;
        private final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        private final String employeeID;
        public ConnectThread(String empData){
            employeeID=empData;
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

            try {
                mdevice = search(macAdd);   //Searching for paired server device
            }catch(NullPointerException ne){

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
            switch (msg.what) {
                case 1:
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0); //0 for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    int a = pref.getInt("key_name", 0);
                    editor.commit();
                    pw.stopSpinning();
                    if (a == 0) {
                        editor.putInt("key_name", 1);
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "Check Out Acknowledged", Toast.LENGTH_SHORT).show();
                        pw.setText("Enter");
                        pw.setTextSize(30);
                        PrintMessage();
                    } else {
                        editor.putInt("key_name", 0);
                        editor.commit();
                        pw.setText("Exit");
                        pw.setTextSize(30);
                        PrintMessage();
                        Toast.makeText(getApplicationContext(), "Check In Acknowledged", Toast.LENGTH_SHORT).show();
            }
                    break;
                case 2:
                    pw.stopSpinning();
                    Toast.makeText(getApplicationContext(), "Server device not found", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };
}