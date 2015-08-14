package com.globant.bazzinga;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Set;
import android.content.SharedPreferences.Editor;

public class CheckInOut extends ActionBarActivity
       {
    private static final int REQUEST_ENABLE_BT = 1;
    private BluetoothAdapter myBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothDevice mdevice;
    String employeeid;
    ProgressWheel pw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_out);


        SharedPreferences pref = getApplicationContext().getSharedPreferences("EmpData", 0); //0 for private mode
        Editor editor = pref.edit();
        //Get previous status of employee weather he is IN or OUT
        int a = pref.getInt("key_name", 0);
        editor.commit();

        employeeid = pref.getString("EmployeeIDKey", "BLANK");
        editor.commit();
        String employeename = pref.getString("EmployeeName", "BLANK");
        editor.commit();
        //Welcome EmployeeID set Text
        TextView empname = (TextView) findViewById(R.id.employeename);
        empname.setText(employeename);

        pw = (ProgressWheel) findViewById(R.id.pw_spinner1);
        pw.progress = 0;
        pw.stopSpinning();
        pw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pw.spin(true);
                sendMessage();
            }
        });
        if (a == 0) {//If Employee is Onside
            pw.setText("Exit");
            pw.setTextSize(30);
        } else//If Employee is Outside
        {
            pw.setText("Enter");
            pw.setTextSize(30);
        }
        PrintMessage();
    }

    private void PrintMessage() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("mypref", 0); //0 for private mode
        SharedPreferences.Editor editor = pref.edit();
        int a = pref.getInt("key_name", 0);
        editor.commit();

        if (a == 0) {
            String weekDay = "";
            Calendar cal = Calendar.getInstance();
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (Calendar.MONDAY == dayOfWeek) weekDay = "Okay Monday, Let’s do this!";
            else if (Calendar.TUESDAY == dayOfWeek)
                weekDay = "Its only Tuesday and you're almost done with 95% of the week!";
            else if (Calendar.WEDNESDAY == dayOfWeek)
                weekDay = "Keep calm you're halfway through!!";
            else if (Calendar.THURSDAY == dayOfWeek)
                weekDay = "Better days are just around the corner, They are Friday, Saturday and Sunday";
            else if (Calendar.FRIDAY == dayOfWeek) weekDay = "Thank god it’s Friday";
            else if (Calendar.SATURDAY == dayOfWeek)
                weekDay = "I love working “Saturday” said no one ever.";
            else if (Calendar.SUNDAY == dayOfWeek) weekDay = "Happy Sunday!";
            TextView msg = (TextView) findViewById(R.id.Message);
            msg.setText(weekDay);
        } else {
            String weekDay = "";
            Calendar cal = Calendar.getInstance();
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (Calendar.MONDAY == dayOfWeek) weekDay = "Its Monday Don’t forget to be awesome";
            else if (Calendar.TUESDAY == dayOfWeek)
                weekDay = "Take off Tuesday!!";
            else if (Calendar.WEDNESDAY == dayOfWeek)
                weekDay = "Have a bright and beautiful Wednesday";
            else if (Calendar.THURSDAY == dayOfWeek)
                weekDay = "You say Thursday, I say Its Friday eve.";
            else if (Calendar.FRIDAY == dayOfWeek) weekDay = "Ready for the weekend??";
            else if (Calendar.SATURDAY == dayOfWeek)
                weekDay = "If you can’t be bothered to work on Saturday, Don’t bother to come in on Sunday";
            else if (Calendar.SUNDAY == dayOfWeek) weekDay = "Finally time to rest :P";
            TextView msg = (TextView) findViewById(R.id.Message);
            msg.setText(weekDay);
        }
    }

    private void sendMessage() {
        myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (myBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        } else {
            SharedPreferences pref = getApplicationContext().getSharedPreferences("EmpData", 0); //0 for private mode
            SharedPreferences.Editor editor = pref.edit();
            int a = pref.getInt("key_name", 0);
            editor.commit();

            //Message to be sent to server
            String msg = employeeid + "#" + String.valueOf(a);

            ConnectThread mConnect = new ConnectThread(msg);       //Bluetooth connection Thread
            mConnect.start();   //Starting server

        }
    }

    private void logOut(View view) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences("EmpData", 0);
        pref.edit().clear().commit();
        Intent intent = new Intent(this, AuthenticateEmployee.class);
        startActivity(intent);
    }
}
