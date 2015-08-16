package com.globant.bazzinga;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class AuthenticateEmployee extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        query.whereEqualTo("MacAddress",empMac);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject == null) {
                    Log.d("score", "The getFirst request failed.");
                } else {
                    String empidString=parseObject.get("EmployeeID").toString();
                    String empName=parseObject.get("EmployeeName").toString();
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
                                editor.putInt("Status", 1);//Employee Current state Outside
                            } else {
                                String status = parseObject.get("Status").toString();
                                if (status.equals("OUT")) {
                                    editor.putInt("Status", 1);//Employee Current state Outside
                                } else {
                                    editor.putInt("Status", 0);//Employee Current state Outside
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

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}
