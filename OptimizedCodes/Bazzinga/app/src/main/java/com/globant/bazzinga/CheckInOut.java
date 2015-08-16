package com.globant.bazzinga;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class CheckInOut extends ActionBarActivity {
    private ProgressWheel progreesWheelButton;
    private TextView message;
    private String employeeid;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_out);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_check_in_out);
        pref = getApplicationContext().getSharedPreferences("EmployeeData", 0); //0 for private mode
        editor = pref.edit();
        int state = pref.getInt("Status", Constants.OUTSIDE);
        editor.commit();

        employeeid = pref.getString("EmployeeIDKey", "BLANK");
        editor.commit();
        String employeename = pref.getString("EmployeeName", "BLANK");
        editor.commit();
        TextView empname = (TextView) findViewById(R.id.employeename);
        empname.setText(employeename);

        progreesWheelButton = (ProgressWheel)findViewById(R.id.pw_spinner1);
        progreesWheelButton.progress=0;
        progreesWheelButton.stopSpinning();

        progreesWheelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progreesWheelButton.spin(true);
                sendMessage();
            }
        });

        if (state == Constants.INSIDE) {//If Employee is Inside
            progreesWheelButton.setText("Going Out");
            progreesWheelButton.setTextSize(30);
        }
        else if(state==Constants.OUTSIDE)//If Employee is Outside
        {
            progreesWheelButton.setText("Going In");
            progreesWheelButton.setTextSize(30);
        }

        message = (TextView)findViewById(R.id.Message);
        message.setVisibility(View.INVISIBLE);
    }


    private void sendMessage(){
        BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        }
        else {
            int state = pref.getInt("Status", Constants.OUTSIDE);
            editor.commit();

            //Message to be sent to server
            String msg = employeeid+"#"+String.valueOf(state);
            ConnectThread mConnect = new ConnectThread(msg,getApplicationContext(),message,progreesWheelButton);       //Bluetooth connection Thread
            mConnect.start();   //Starting server
           /* try {
                mConnect.join();
                //PrintMessage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }

    public void logOut(View view){
        pref.edit().clear().commit();
        Intent authenticateEmployeeIntent = new Intent(this,AuthenticateEmployee.class);
        startActivity(authenticateEmployeeIntent);
    }

}
