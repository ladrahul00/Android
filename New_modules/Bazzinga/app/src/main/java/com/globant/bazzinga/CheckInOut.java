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
import java.util.concurrent.Semaphore;


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
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_check_in_out);
        pref = getApplicationContext().getSharedPreferences("EmployeeData", 0); //0 for private mode
        editor = pref.edit();
        int a = pref.getInt("Status", 0);
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

        if (a == 0) {//If Employee is Inside
            progreesWheelButton.setText("Going Out");
            progreesWheelButton.setTextSize(30);
        }
        else//If Employee is Outside
        {
            progreesWheelButton.setText("Going In");
            progreesWheelButton.setTextSize(30);
        }
        message = (TextView)findViewById(R.id.Message);
        message.setVisibility(View.INVISIBLE);
    }

  /*  private void PrintMessage(){
        int a = pref.getInt("Status", 0);
        editor.commit();

        String weekDay = "";
        if(a==0) {
            progreesWheelButton.setText("Going Out");
            Calendar cal = Calendar.getInstance();
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (Calendar.MONDAY == dayOfWeek) weekDay = "Okay Monday, Let's do this!";
            else if (Calendar.TUESDAY == dayOfWeek)
                weekDay = "Its only Tuesday and you're almost done with 95% of the week!";
            else if (Calendar.WEDNESDAY == dayOfWeek)
                weekDay = "Keep calm you're halfway through!!";
            else if (Calendar.THURSDAY == dayOfWeek)
                weekDay = "Better days are just around the corner, They are Friday, Saturday and Sunday";
            else if (Calendar.FRIDAY == dayOfWeek) weekDay = "Thank god it's Friday";
            else if (Calendar.SATURDAY == dayOfWeek)
                weekDay = "I love working \"Saturday\" said no one ever.";
            else if (Calendar.SUNDAY == dayOfWeek) weekDay = "Happy Sunday!";
        }
        else
        {
            progreesWheelButton.setText("Going In");
            Calendar cal = Calendar.getInstance();
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if (Calendar.MONDAY == dayOfWeek) weekDay = "Its Monday Don't forget to be awesome";
            else if (Calendar.TUESDAY == dayOfWeek)
                weekDay = "Take off Tuesday!!";
            else if (Calendar.WEDNESDAY == dayOfWeek)
                weekDay = "Have a bright and beautiful Wednesday";
            else if (Calendar.THURSDAY == dayOfWeek)
                weekDay = "You say Thursday, I say Its Friday eve.";
            else if (Calendar.FRIDAY == dayOfWeek) weekDay = "Ready for the weekend??";
            else if (Calendar.SATURDAY == dayOfWeek)
                weekDay = "If you can't be bothered to work on Saturday, Don't bother to come in on Sunday";
            else if (Calendar.SUNDAY == dayOfWeek) weekDay = "Finally time to rest :P";
        }
        message.setVisibility(View.VISIBLE);
        message.setText(weekDay);
    }*/

    private void sendMessage(){
        BluetoothAdapter myBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(myBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Your device does not support Bluetooth",
                    Toast.LENGTH_LONG).show();
        }
        else {
            int a = pref.getInt("Status", 0);
            editor.commit();

            //Message to be sent to server
            String msg = employeeid+"#"+String.valueOf(a);

            ConnectThread mConnect = new ConnectThread(msg,getApplicationContext());       //Bluetooth connection Thread
            mConnect.start();   //Starting server
            try {
                mConnect.join();
                //PrintMessage();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void logOut(View view){
        pref.edit().clear().commit();
        Intent authenticateEmployeeIntent = new Intent(this,AuthenticateEmployee.class);
        startActivity(authenticateEmployeeIntent);
    }
}
