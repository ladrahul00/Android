package com.example.anonymous.client2706;

import android.app.IntentService;

import android.content.Intent;
import android.content.SharedPreferences;


import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
import com.parse.ParseObject;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Listener for geofence transition changes.
 *
 * Receives geofence transition events from Location Services in the form of an Intent containing
 * the transition type and geofence id(s) that triggered the transition. Creates a notification
 * as the output.
 */
public class GeofenceTransitionsIntentService extends IntentService {

    protected static final String TAG = "geofence-transitions-service";

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public GeofenceTransitionsIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
    /**
     * Handles incoming intents.
     *
     * @param intent sent by Location Services. This Intent is provided to Location
     *               Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref" , 0); //0 for private mode
        SharedPreferences.Editor editor = pref.edit();
        if (geofencingEvent.hasError()) {
            //  String errorMessage = GeofenceErrorMessages.getErrorString(this,
            //     geofencingEvent.getErrorCode());
            // Log.e(TAG, errorMessage);
            return;
        }

        // Get the transition type.
        int geofenceTransition = geofencingEvent.getGeofenceTransition();
        String empid = pref.getString("EmployeeIDKey", "blank");
        editor.commit();
        // Test that the reported transition was of interest.
        if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            editor.putInt("key_name", 0);
            editor.commit();
            //show_status.setText("You got in");
            //button.setText("OUT");

            ParseObject testObject = new ParseObject("EmployeeLog");
            testObject.put("EmployeeID", empid);
            String dType = "dd / MM / yyyy";
            String tType = "HH:mm:ss";
            //String s="MMM d, y, HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(dType);
            SimpleDateFormat stf = new SimpleDateFormat(tType);
            String Date = sdf.format(new Date());
            String time = stf.format(new Date());
            testObject.put("Date",Date);
            testObject.put("Time",time);
            testObject.put("Status","IN-GEO");
            testObject.saveInBackground();
            testObject.saveEventually();
        } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
            // Get the geofences that were triggered. A single event can trigger multiple geofences.
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            editor.putInt("key_name", 1);
            editor.commit();
           // show_status.setText("You got out");
            //button.setText("IN");

            ParseObject testObject = new ParseObject("EmployeeLog");
            testObject.put("EmployeeID", empid);
            String dType = "dd / MM / yyyy";
            String tType = "HH:mm:ss";
            //String s="MMM d, y, HH:mm";
            SimpleDateFormat sdf = new SimpleDateFormat(dType);
            SimpleDateFormat stf = new SimpleDateFormat(tType);
            String Date = sdf.format(new Date());
            String time = stf.format(new Date());
            testObject.put("Date",Date);
            testObject.put("Time",time);
            testObject.put("Status","OUT-GEO");
            testObject.saveInBackground();
            testObject.saveEventually();
        }
    }
}