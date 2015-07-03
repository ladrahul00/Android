package com.example.anonymous.client2706;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class MainActivityClient extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity_client);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "ecNYEdsTREI9Mwzx5gWOoh2HB9V78KvVWe8W8iIA", "YHuKHkJdjm4gSdl6lrZavY9Sdx06Da1DPNNXy40p");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref" , 0); //0 for private mode
        SharedPreferences.Editor editor = pref.edit();

        String empID = pref.getString("EmployeeIDKey", "blank");//receive from preference
        editor.commit();

        if(!empID.equals("blank")){
            Intent intent = new Intent(this,CheckInOut.class);
            intent.putExtra("EmployeeID", empID);
            startActivity(intent);
        }
    }

    public void loginCheck(View v){
        EditText empId = (EditText)findViewById(R.id.employeeid);
        final String empidString = empId.getText().toString();
        EditText passwd = (EditText)findViewById(R.id.password);
        final String passcheck = passwd.getText().toString();
        BluetoothAdapter myBluetoothAdapter;
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        final String macDevice = myBluetoothAdapter.getAddress();

        ParseQuery<ParseObject> query = ParseQuery.getQuery("EmployeeData");
        query.whereEqualTo("EmployeeID",empidString);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject == null) {
                    Log.d("score", "The getFirst request failed.");
                }
                else {
                   // if(macDevice.equals(parseObject.get("EmployeeMAC").toString())) {
                        if (passcheck.equals(parseObject.get("Password").toString())) {
                            //redirect this to checkin checkout button
                            SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref" , 0); //0 for private mode
                            SharedPreferences.Editor editor = pref.edit();

                            editor.putString("EmployeeIDKey", empidString);//store empid into preferences
                            editor.commit();

                            Intent intent = new Intent(MainActivityClient.this,CheckInOut.class);
                            intent.putExtra("EmployeeID", empidString);
                            startActivity(intent);
                        } else {
                            TextView errtxt = (TextView)findViewById(R.id.errorText);
                            errtxt.setText("Password Incorrect!!!");
                            errtxt.setVisibility(View.VISIBLE);
                        }
                   /* }
                    else{
                        //loginerror
                        TextView errtxt = (TextView)findViewById(R.id.errorText);
                        errtxt.setText("MAC Address Not Registered!!!");
                        errtxt.setVisibility(View.VISIBLE);
                    }*/
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity_client, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return super.onOptionsItemSelected(item);
    }
}
