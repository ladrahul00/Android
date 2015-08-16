package com.globant.bazzinga;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import com.parse.Parse;


public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "ecNYEdsTREI9Mwzx5gWOoh2HB9V78KvVWe8W8iIA", "YHuKHkJdjm4gSdl6lrZavY9Sdx06Da1DPNNXy40p");
        SharedPreferences pref = getApplicationContext().getSharedPreferences("EmployeeData", 0); //0 for private mode
        SharedPreferences.Editor editor = pref.edit();

        String empID = pref.getString("EmployeeIDKey", "blank");//receive from preference
        editor.commit();
        assert empID != null;

        if(empID.equals("blank")){/*If user is not Authenticated*/
            finish();
            Intent authenticateEmployeeIntent = new Intent(MainActivity.this, AuthenticateEmployee.class);
            startActivity(authenticateEmployeeIntent);
        }
        else{/*If user is Authenticated*/
            finish();
            Intent checkInOutIntent = new Intent(MainActivity.this, CheckInOut.class);
            startActivity(checkInOutIntent);
        }
    }
}
