package com.globant.bazzinga;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;


public class AuthenticateEmployee extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate_employee);
        SharedPreferences pref = getApplicationContext().getSharedPreferences("EmpData", 0); //0 for private mode
        SharedPreferences.Editor editor = pref.edit();
        final ProgressWheel pw = (ProgressWheel)findViewById(R.id.pw_spinner1);
        pw.spin(false);
        pw.setText("Authenticating");
        pw.setTextSize(30);
        final ProgressWheel pwin = (ProgressWheel)findViewById(R.id.pw_spinner2);
        pwin.spin(true);

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
                    SharedPreferences pref = getApplicationContext().getSharedPreferences("EmpData", 0); //0 for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    pw.stopSpinning();
                    pwin.stopSpinning();
                    editor.putString("EmployeeIDKey", empidString);//store empid into preferences
                    editor.commit();
                    editor.putString("EmployeeName", empName);//store empid into preferences
                    editor.commit();
                    finish();
                    Intent intent = new Intent(AuthenticateEmployee.this, CheckInOut.class);
                    startActivity(intent);
                }
            }
        });

    }

}
