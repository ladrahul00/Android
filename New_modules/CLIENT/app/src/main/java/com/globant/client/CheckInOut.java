package com.globant.client;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

//imports for shared preferences
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.w3c.dom.Text;

public class CheckInOut extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothDevice mdevice;
    private String employeeid;
    private ProgressWheel pw;
    private TextView message;
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mGeofencePendingIntent;
    protected ArrayList<Geofence> mGeofenceList;
    Location mLastLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_out);
        mGeofenceList  = new ArrayList<Geofence>();
        mGeofencePendingIntent=null;

        populateGeofenceList();
        buildGoogleAPIClient();

       // while(!mGoogleApiClient.isConnected());

        getGeofencingRequest();

        SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0); //0 for private mode
        Editor editor = pref.edit();

        int a = pref.getInt("key_name", 0);
        editor.commit();

        employeeid = pref.getString("EmployeeIDKey", "BLANK");
        editor.commit();
        String employeename = pref.getString("EmployeeName", "BLANK");
        editor.commit();

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
        if (a == 0) {//If Employee is Inside
            pw.setText("Exit");
            pw.setTextSize(30);
        } else//If Employee is Outside
        {
            pw.setText("Enter");
            pw.setTextSize(30);
        }
        message = (TextView)findViewById(R.id.Message);
        message.setVisibility(View.INVISIBLE);
    }

    protected synchronized void buildGoogleAPIClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    public void PrintMessage(){
        SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0); //0 for private mode
        SharedPreferences.Editor editor = pref.edit();
        int a = pref.getInt("key_name", 0);
        editor.commit();

        String weekDay = "";
        if(a==0) {
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
        }
        else
        {
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
        }
        message.setVisibility(View.VISIBLE);
        message.setText(weekDay);
    }

    public void sendMessage(){
        addGeofencesButtonHandler();
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
        Intent intent = new Intent(this,AuthenticateEmployee.class);
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
                        pw.setTextSize(50);
                        PrintMessage();
                    } else {
                        editor.putInt("key_name", 0);
                        editor.commit();
                        pw.setText("Exit");
                        pw.setTextSize(50);
                        PrintMessage();
                        Toast.makeText(getApplicationContext(), "Check In Acknowledged", Toast.LENGTH_SHORT).show();
            }
                    break;
                case 2:
                    pw.stopSpinning();
                    Toast.makeText(getApplicationContext(), "Server device not found", Toast.LENGTH_SHORT).show();
                    break;
                case 3:
                    pw.stopSpinning();
                    Toast.makeText(getApplicationContext(), "Server device Not Paired", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void populateGeofenceList() {
        for (Map.Entry<String, LatLng> entry : Constants.PUNE_AREA_LANDMARKS.entrySet()) {

            mGeofenceList.add(new Geofence.Builder()
                    // Set the request ID of the geofence. This is a string to identify this
                    // geofence.
                    .setRequestId(entry.getKey())

                            // Set the circular region of this geofence.
                    .setCircularRegion(
                            entry.getValue().latitude,
                            entry.getValue().longitude,
                            Constants.GEOFENCE_RADIUS_IN_METERS
                    )

                            // Set the expiration duration of the geofence. This geofence gets automatically
                            // removed after this period of time.
                    .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)

                            // Set the transition types of interest. Alerts are only generated for these
                            // transition. We track entry and exit transitions in this sample.
                    .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER |
                            Geofence.GEOFENCE_TRANSITION_EXIT)
                            // .setLoiteringDelay(300000)
                            // Create the geofence.
                    .build());
        }
    }

    public void addGeofencesButtonHandler() {
        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, "GPS not started", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            LocationServices.GeofencingApi.addGeofences(
                    mGoogleApiClient,
                    // The GeofenceRequest object.
                    getGeofencingRequest(),
                    // A pending intent that that is reused when calling removeGeofences(). This
                    // pending intent is used to generate an intent when a matched geofence
                    // transition is observed.
                    getGeofencePendingIntent()
            ).setResultCallback(this); // Result processed in onResult().

        } catch (SecurityException securityException) {
            // Catch exception generated if the app does not use ACCESS_FINE_LOCATION permission.
            //logSecurityException(securityException);
        }
    }

    /**
     * Gets a PendingIntent to send with the request to add or remove Geofences. Location Services
     * issues the Intent inside this PendingIntent whenever a geofence transition occurs for the
     * current list of geofences.
     *
     * @return A PendingIntent for the IntentService that handles geofence transitions.
     */
    private PendingIntent getGeofencePendingIntent() {
        // Reuse the PendingIntent if we already have it.
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mGoogleApiClient.disconnect();
    }


    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    private GeofencingRequest getGeofencingRequest() {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

        // The INITIAL_TRIGGER_ENTER flag indicates that geofencing service should trigger a
        // GEOFENCE_TRANSITION_ENTER notification when the geofence is added and if the device
        // is already inside that geofence.
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER |
                GeofencingRequest.INITIAL_TRIGGER_EXIT);

        // Add the geofences to be monitored by geofencing service.
        builder.addGeofences(mGeofenceList);
        // Return a GeofencingRequest.
        return builder.build();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void onResult(Status status) {

    }

}