package com.example.anonymous.adddata;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseObject;


public class addemployeee extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addemployeee);

    }

    public void addData(View v){
        ParseObject pobj = new ParseObject("EmployeeData");
        EditText empid = (EditText)findViewById(R.id.EmployeeID);
        EditText macadd = (EditText)findViewById(R.id.macadd);
        EditText cardno = (EditText)findViewById(R.id.cardno);
        EditText empname = (EditText)findViewById(R.id.empname);
        String empNameStr = empname.getText().toString();
        String empIdStr = empid.getText().toString();
        String empCardNo = cardno.getText().toString();
        String empMacAddress = macadd.getText().toString();
        if(empNameStr.equals("") || empIdStr.equals("") || empCardNo.equals("") || empMacAddress.equals("")){
            Toast.makeText(getApplicationContext(), "Enter all Details of Employee", Toast.LENGTH_SHORT).show();
        }
        else {
            pobj.put("EmployeeName", empNameStr);
            pobj.put("EmployeeID", empIdStr);
            pobj.put("CardNo", empCardNo);
            pobj.put("MacAddress", empMacAddress);
            pobj.saveInBackground();
            Toast.makeText(getApplicationContext(), "Employee Added to Database", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, DisplayActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_addemployeee, menu);
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
