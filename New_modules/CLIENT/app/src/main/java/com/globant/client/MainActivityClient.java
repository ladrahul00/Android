package com.globant.client;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import android.widget.ProgressBar;


public class MainActivityClient extends ActionBarActivity {
    private ProgressBar spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_client);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "ecNYEdsTREI9Mwzx5gWOoh2HB9V78KvVWe8W8iIA", "YHuKHkJdjm4gSdl6lrZavY9Sdx06Da1DPNNXy40p");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0); //0 for private mode
        SharedPreferences.Editor editor = pref.edit();
        final ProgressWheel pw = (ProgressWheel)findViewById(R.id.pw_spinner1);
        pw.spin(false);
        pw.setText("Authenticating");
        pw.setTextSize(30);
        final ProgressWheel pwin = (ProgressWheel)findViewById(R.id.pw_spinner2);
        pwin.spin(true);
        String empID = pref.getString("EmployeeIDKey", "blank");//receive from preference
        editor.commit();

        assert empID != null;
        if(empID.equals("blank")){
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            String empMac = bluetoothAdapter.getAddress();

            ParseQuery<ParseObject> query = ParseQuery.getQuery("EmployeeData");
            query.whereEqualTo("MacAddress",empMac);
            query.getFirstInBackground(new GetCallback<ParseObject>() {
                @Override
                public void done(ParseObject parseObject, ParseException e) {
                    if (parseObject == null) {
                        Log.d("score", "The getFirst request failed.");
                    } else {
                        String empidString=parseObject.get("EmployeeID").toString();
                        SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0); //0 for private mode
                        SharedPreferences.Editor editor = pref.edit();
                        pw.stopSpinning();
                        editor.putString("EmployeeIDKey", empidString);//store empid into preferences
                        editor.commit();

                        Intent intent = new Intent(MainActivityClient.this, CheckInOut.class);
                        startActivity(intent);
                    }
                }
            });
        }
        else{
            pw.stopSpinning();
            Intent intent = new Intent(MainActivityClient.this, CheckInOut.class);
            startActivity(intent);
        }
    }
}