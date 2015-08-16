package com.globant.client;

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
        setContentView(R.layout.activity_authenticate_employee);

        SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0); //0 for private mode
        SharedPreferences.Editor editor = pref.edit();

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
                SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0); //0 for private mode
                SharedPreferences.Editor editor = pref.edit();
                progressWheel1.stopSpinning();
                progressWheel2.stopSpinning();
                editor.putInt("State", 1);//Employee Current state Outside
                editor.commit();
                editor.putString("EmployeeIDKey", empidString);//store Employee Name into preferences
                editor.commit();
                editor.putString("EmployeeName", empName);//store Employee ID into preferences
                editor.commit();
                finish();
                Intent intent = new Intent(AuthenticateEmployee.this, CheckInOut.class);
                startActivity(intent);
            }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_authenticate_employee, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
