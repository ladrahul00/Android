package com.globant.bazzinga;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Message;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
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
    private TextView messageTextView;
    private ProgressWheel wheelButton;
    private final String macAdd="5C:51:88:66:57:78";  //Setting MAC Address of server
    MediaPlayer mediaPlayer;
    public ConnectThread(String empData, Context context) {
        employeeID = empData;
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
        messageTextView=null;
        wheelButton=null;
    }

    public ConnectThread(String empData, Context context, TextView textView, ProgressWheel progressWheel){
        employeeID=empData;
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.context = context;
        messageTextView=textView;
        wheelButton=progressWheel;
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
        private void PrintMessage(){
            SharedPreferences pref = context.getSharedPreferences("EmployeeData", 0);
            SharedPreferences.Editor editor = pref.edit();
            int state = pref.getInt("Status", Constants.OUTSIDE);
            editor.commit();

            String weekDay = "";
            if(state==Constants.INSIDE) {
                Calendar cal = Calendar.getInstance();
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                if (Calendar.MONDAY == dayOfWeek) weekDay = "Okay Monday, Let's do this!";
                else if (Calendar.TUESDAY == dayOfWeek)
                    weekDay = "Its only Tuesday and you're almost done with 95% of the week!";
                else if (Calendar.WEDNESDAY == dayOfWeek)
                    weekDay = "Keep calm you're halfway through!!";
                else if (Calendar.THURSDAY == dayOfWeek)
                    weekDay = "Better days are just around the corner, They are Friday, Saturday and Sunday";
                else if (Calendar.FRIDAY == dayOfWeek) weekDay = "Thank god it's Friday";
                else if (Calendar.SATURDAY == dayOfWeek)
                    weekDay = "I love working \"Saturday\" said no one ever.";
                else if (Calendar.SUNDAY == dayOfWeek) weekDay = "Happy Sunday!";
            }
            else if(state==Constants.OUTSIDE)
            {
                Calendar cal = Calendar.getInstance();
                int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
                if (Calendar.MONDAY == dayOfWeek) weekDay = "Its Monday Don't forget to be awesome";
                else if (Calendar.TUESDAY == dayOfWeek)
                    weekDay = "Take off Tuesday!!";
                else if (Calendar.WEDNESDAY == dayOfWeek)
                    weekDay = "Have a bright and beautiful Wednesday";
                else if (Calendar.THURSDAY == dayOfWeek)
                    weekDay = "You say Thursday, I say Its Friday eve.";
                else if (Calendar.FRIDAY == dayOfWeek) weekDay = "Ready for the weekend??";
                else if (Calendar.SATURDAY == dayOfWeek)
                    weekDay = "If you can't be bothered to work on Saturday, Don't bother to come in on Sunday";
                else if (Calendar.SUNDAY == dayOfWeek) weekDay = "Finally time to rest :P";
            }
            if(messageTextView!=null) {
                messageTextView.setVisibility(View.VISIBLE);
                messageTextView.setText(weekDay);
            }
        }

        @Override
        public void handleMessage(Message msg) {
            String data = msg.obj.toString();
            switch(msg.what){
                case 1:
                    int layoutID=R.layout.new_app_widget_2;
                    SharedPreferences pref = context.getSharedPreferences("EmployeeData", 0);
                    SharedPreferences.Editor editor = pref.edit();
                    int state = pref.getInt("Status", Constants.OUTSIDE);
                    editor.commit();

                    if(wheelButton!=null){
                        if(state==Constants.INSIDE) {
                            wheelButton.setText("Going in");
                        }
                        else if (state==Constants.OUTSIDE){
                            wheelButton.setText("Going out");
                        }
                        wheelButton.stopSpinning();
                    }
                    PrintMessage();
                    if (state == Constants.INSIDE) {
                        layoutID=R.layout.new_app_widget_2;
                        editor.putInt("Status", Constants.OUTSIDE);
                        editor.commit();
                        Toast.makeText(context, "Check Out Acknowledged", Toast.LENGTH_SHORT).show();
                    }
                    else if(state==Constants.OUTSIDE) {
                        layoutID=R.layout.new_app_widget;
                        editor.putInt("Status", Constants.INSIDE);
                        editor.commit();
                        Toast.makeText(context, "Check In Acknowledged", Toast.LENGTH_SHORT).show();
                    }
                    RemoteViews remoteViews1 = new RemoteViews(context.getPackageName(), layoutID);
                    if(state==Constants.INSIDE) {
                        remoteViews1.setTextViewText(R.id.textView2, "Outside");
                    }
                    else if (state==Constants.OUTSIDE){
                        remoteViews1.setTextViewText(R.id.textView2, "Inside");
                    }
                    remoteViews1.setOnClickPendingIntent(R.id.imageButton, NewAppWidget.buildButtonPendingIntent(context));
                    NewAppWidget.pushWidgetUpdate(context, remoteViews1);
                    try {
                        mmSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 2:
                    Toast.makeText(context, "Server device not found", Toast.LENGTH_SHORT).show();
                    try {
                        mmSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case 3:
                    Toast.makeText(context, "Server device not Paired", Toast.LENGTH_SHORT).show();
                    break;
            }
            if(wheelButton!=null){
                wheelButton.stopSpinning();
            }
            super.handleMessage(msg);
        }
    };
}
