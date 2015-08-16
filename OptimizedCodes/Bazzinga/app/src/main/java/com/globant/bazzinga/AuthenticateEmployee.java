package com.globant.bazzinga;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Map;


public class AuthenticateEmployee extends ActionBarActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {
    private GoogleApiClient mGoogleApiClient;
    private PendingIntent mGeofencePendingIntent;
    protected ArrayList<Geofence> mGeofenceList;
    private Location mLastLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mGeofenceList  = new ArrayList<Geofence>();
        mGeofencePendingIntent=null;
        populateGeofenceList();
        buildGoogleAPIClient();

        // while(!mGoogleApiClient.isConnected());

        getGeofencingRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_authenticate_employee);

        final ProgressWheel progressWheel1 = (ProgressWheel)findViewById(R.id.pw_spinner1);
        progressWheel1.spin(false);//stop spinning custom view 1

        progressWheel1.setText("Authenticating");
        progressWheel1.setTextSize(30);

        final ProgressWheel progressWheel2 = (ProgressWheel)findViewById(R.id.pw_spinner2);
        progressWheel2.spin(true);//stop spinning custom view 2

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        String empMac = bluetoothAdapter.getAddress();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("EmployeeData");
        query.whereEqualTo("MacAddress", empMac);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject == null) {
                    Log.d("score", "The getFirst request failed.");
                } else {
                    addGeofencesButtonHandler();
                    String empidString = parseObject.get("EmployeeID").toString();
                    String empName = parseObject.get("EmployeeName").toString();
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("EmployeeData", 0); //0 for private mode
                    final SharedPreferences.Editor editor = pref.edit();
                    progressWheel1.stopSpinning();
                    progressWheel2.stopSpinning();

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("EmployeeLog");
                    query.whereEqualTo("EmployeeID", empidString);
                    query.getFirstInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (parseObject == null) {
                                editor.putInt("Status", Constants.OUTSIDE);//Employee Current state Outside
                            } else {
                                String status = parseObject.get("Status").toString();
                                if (status.equals("OUT")) {
                                    editor.putInt("Status", Constants.OUTSIDE);//Employee Current state Outside
                                } else {
                                    editor.putInt("Status", Constants.INSIDE);//Employee Current state Inside
                                }
                                editor.commit();
                            }
                        }
                    });
                    editor.putString("EmployeeIDKey", empidString);//store Employee Name into preferences
                    editor.commit();
                    editor.putString("EmployeeName", empName);//store Employee ID into preferences
                    editor.commit();
                    finish();
                    Intent checkInOutIntent = new Intent(AuthenticateEmployee.this, CheckInOut.class);
                    startActivity(checkInOutIntent);
                }
            }
        });

    }

    protected synchronized void buildGoogleAPIClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

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
