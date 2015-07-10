package com.example.anonymous.adddata;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.parse.Parse;
import com.parse.ParseObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "ecNYEdsTREI9Mwzx5gWOoh2HB9V78KvVWe8W8iIA", "YHuKHkJdjm4gSdl6lrZavY9Sdx06Da1DPNNXy40p");
    }

    public void addData(View v){
        ParseObject pobj = new ParseObject("EmployeeData");
        EditText empid = (EditText)findViewById(R.id.EmployeeID);
        pobj.put("EmployeeID",empid.getText().toString());
        EditText passwd = (EditText)findViewById(R.id.Password);
        pobj.put("Password",passwd.getText().toString());
        String mac= BluetoothAdapter.getDefaultAdapter().getAddress();
        pobj.put("EmployeeMAC",mac);
        pobj.saveInBackground();
        String dtype = "d / m / y";
        String tType = "HH:mm";
        //String s="MMM d, y, HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(dtype);
        SimpleDateFormat stf = new SimpleDateFormat(tType);
        String Date = sdf.format(new Date());
        String time = stf.format(new Date());
        ParseObject testObject = new ParseObject("EmployeeLog");
        testObject.put("EmployeeID", empid.getText().toString());
        testObject.put("Date",Date);
        testObject.put("Time",time);
        testObject.put("Status","xyz");
        testObject.saveInBackground();
        testObject.saveEventually();

        Intent intent = new Intent(this,EmployeeLog.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
