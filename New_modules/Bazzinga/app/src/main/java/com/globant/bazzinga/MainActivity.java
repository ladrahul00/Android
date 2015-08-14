package com.globant.bazzinga;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;


public class MainActivity extends ActionBarActivity {

    private ProgressBar spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "ecNYEdsTREI9Mwzx5gWOoh2HB9V78KvVWe8W8iIA", "YHuKHkJdjm4gSdl6lrZavY9Sdx06Da1DPNNXy40p");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("EmpData", 0);
        SharedPreferences.Editor editor = pref.edit();
        String empID = pref.getString("EmployeeIDKey", "blank");//receive from preference
        editor.commit();
        assert empID != null;
        if(empID.equals("blank")){
            finish();
            Intent intent = new Intent(MainActivity.this, AuthenticateEmployee.class);
            startActivity(intent);
        }
        else{
            finish();
            Intent intent = new Intent(MainActivity.this, CheckInOut.class);
            startActivity(intent);
        }
    }

}