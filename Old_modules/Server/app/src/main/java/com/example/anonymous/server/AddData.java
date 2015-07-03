package com.example.anonymous.server;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.javacodegeeks.android.bluetoothtest.R;
import com.parse.ParseObject;


public class AddData extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_data);
    }

    public void addData(View v){
        ParseObject pobj = new ParseObject("EmployeeData");
        EditText empid = (EditText)findViewById(R.id.EmployeeID);
        pobj.put("EmployeeID",empid.getText().toString());
        EditText passwd = (EditText)findViewById(R.id.Password);
        pobj.put("Password",passwd.getText().toString());
        EditText mac = (EditText)findViewById(R.id.DeviceMac);
        pobj.put("EmployeeMAC",mac.getText().toString());
        pobj.saveInBackground();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_data, menu);
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
